package minikin.projects.coding.challenges.loadbalancer;

import minikin.projects.coding.challenges.loadbalancer.be.BackEndServers;
import minikin.projects.coding.challenges.loadbalancer.lb.LoadBalancer;

/**
 * 
 * Main method that drives the entire application
 * @author Sourabh Ninawe
 * 
 */

public class Main {
	public static void main(String[] args) {
		try {
			BackEndServers.initializeServers(3);
		}
		catch(Exception e) {
			System.err.println("Unable to initialize servers with initial capacity");
			e.printStackTrace();
			System.exit(1);
		}
		try {
			LoadBalancer.startLoadBalancer();
		}
		catch(Exception e) {
			System.err.println("Unable to initialize load balancer");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
