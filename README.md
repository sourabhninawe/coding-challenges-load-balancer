# coding-challenges-load-balancer

## Getting Started
This repository holds the coding challenges solution for load balancer.

Coding Challenge link-

https://codingchallenges.fyi/challenges/challenge-load-balancer/

The loadbalancer has been implemented in a round-robin static routing path.

### Prerequisites

Fair knowledge of Java.

Java installed on your local machine.

Here is the brief overview of the project.

* Main method starts the required number of servers starting with port 8081.
* Once these servers are started it starts the load balancer to redirect the traffic.
* The traffic is redirected in a round robin fashion.
* The state of server can be toggled by invoking /start, /stop end point of the respective server.
* When the request arrives to the load balancer next available server with UP status would be checked and the traffic would be redirected to the same.

*** This is a beta version of the documentation which will soon be updated ***