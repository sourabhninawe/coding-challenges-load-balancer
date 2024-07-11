package minikin.projects.coding.challenges.loadbalancer.lb;

import minikin.projects.coding.challenges.loadbalancer.be.BackEndServers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Class for load balancer and all related operations
 * 
 * @author Sourabh Ninawe
 */
public class LoadBalancer {
	private static int currentCount = 0;
	private static final int LOAD_BALANCER_PORT = 8080;

	public static void startLoadBalancer() throws IOException {
		HttpServer loadBalancer = HttpServer.create(new InetSocketAddress(8080), 0);
		loadBalancer.createContext("/", new RequestHandler());
		loadBalancer.setExecutor(null);
		loadBalancer.start();
		System.out.println("Load balancer is running on port 8080");
	}

	static class RequestHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			BackEndServers.Server server = getBackendServer();
			System.out.println("Exchange request URI:" + exchange.getRequestURI());
			URL url = new URL("http://localhost:" + server.getPort() + exchange.getRequestURI());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(exchange.getRequestMethod());
			connection.setDoOutput(true);

			// Copy request headers from the original request
			exchange.getRequestHeaders()
					.forEach((key, value) -> connection.setRequestProperty(key, String.join(",", value)));

			// Copy request body if present (for POST/PUT requests)
			if (exchange.getRequestBody() != null) {
				connection.getOutputStream().write(exchange.getRequestBody().readAllBytes());
			}

			// Forward request
			connection.connect();

			// Copy response headers
			connection.getHeaderFields().forEach((key, value) -> {
				if (key != null) {
					List<String> values = new ArrayList<>(value);
					exchange.getResponseHeaders().put(key, values);
				}
			});

			// Copy response body
			exchange.sendResponseHeaders(connection.getResponseCode(), 0);
			exchange.getResponseBody().write(connection.getInputStream().readAllBytes());

			// Close resources
			exchange.getResponseBody().close();
			connection.disconnect();
		}
	}

	private static synchronized BackEndServers.Server getBackendServer() {
		System.out.println("Trying to get the next available server");
		BackEndServers.Server server = BackEndServers.servers.get(currentCount);
		if (server.getStatus() != BackEndServers.Status.UP) {
			System.out.println("The status of server:" + server.getPort() + " is not UP");
			while (server.getStatus() != BackEndServers.Status.UP) {
				System.out.println("Server:" + server.getPort() + " is down trying the next available server");
				currentCount = (currentCount + 1) % BackEndServers.servers.size();
				server = BackEndServers.servers.get(currentCount);
			}
			currentCount = (currentCount + 1) % BackEndServers.servers.size();
		} else {
			System.out.println("The status of server:" + server.getPort() + " is UP");
			currentCount = (currentCount + 1) % BackEndServers.servers.size();
		}
			
		System.out.println("Redirecting the traffic to server:" + server.getPort());
		return server;
	}

}
