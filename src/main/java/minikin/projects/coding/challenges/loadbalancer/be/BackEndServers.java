package minikin.projects.coding.challenges.loadbalancer.be;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * 
 * This class is for all the back-end servers and related operations
 * 
 * @author Sourabh Ninawe
 */

public class BackEndServers {
	private static final int SERVER_MIN_PORT = 8081;

	public enum Status {
		UP, DOWN
	};

	public static class Server {
		private int port;
		private Status status;
		private HttpServer server;

		public Server(int port, HttpServer server) {
			this.port = port;
			this.server = server;
		}
		
		public void setStatus(Status status){
			this.status = status;
		}
		
		public int getPort() {
			return this.port;
		}
		
		public Status getStatus() {
			return this.status;
		}
		public HttpServer getServer() {
			return this.server;
		}
	}

	public static List<Server> servers = new ArrayList<Server>();

	public static void initializeServers(int capacity) throws IOException {
		for (int i = 0; i < capacity; i++) {
			int serverPort = SERVER_MIN_PORT + i;
			HttpServer httpServer = HttpServer.create(new InetSocketAddress(serverPort), 0);
			httpServer.createContext("/health_check", new HttpHandler() {
				@Override
				public void handle(HttpExchange exchange) throws IOException {
					System.out.println("Health check invoked on server:"+serverPort);
					String response = "OK";
					exchange.sendResponseHeaders(200, response.getBytes().length);
					OutputStream os = exchange.getResponseBody();
			        os.write(response.getBytes());
			        os.close();
				}
			});
			httpServer.createContext("/ping", new HttpHandler() {
				@Override
				public void handle(HttpExchange exchange) throws IOException {
					System.out.println("Ping service invoked on server:"+serverPort);
					String response = "You have pinged server:" + (serverPort);
					exchange.sendResponseHeaders(200, response.getBytes().length);
					OutputStream os = exchange.getResponseBody();
			        os.write(response.getBytes());
			        os.close();
				}
			});
			httpServer.createContext("/stop", new HttpHandler() {
				@Override
				public void handle(HttpExchange exchange) throws IOException {
					System.out.println("Stop service invoked on server:"+serverPort);
					String response = "You have stopped server:" + (serverPort);
					exchange.sendResponseHeaders(200, response.getBytes().length);
					OutputStream os = exchange.getResponseBody();
			        os.write(response.getBytes());
			        os.close();
			        servers.get(serverPort-8081).setStatus(Status.DOWN);;
				}
			});
			httpServer.createContext("/start", new HttpHandler() {
				@Override
				public void handle(HttpExchange exchange) throws IOException {
					System.out.println("Start service invoked on server:"+serverPort);
					String response = "You have restarted server:" + (serverPort);
					exchange.sendResponseHeaders(200, response.getBytes().length);
					OutputStream os = exchange.getResponseBody();
			        os.write(response.getBytes());
			        os.close();
			        servers.get(serverPort-8081).setStatus(Status.UP);;
				}
			});
			httpServer.setExecutor(null);
			Server server = new Server(serverPort, httpServer);
			server.setStatus(Status.UP);
			servers.add(server);
			System.out.println("Starting http server on port:"+serverPort);
			httpServer.start();
		}
	}
	
	

}
