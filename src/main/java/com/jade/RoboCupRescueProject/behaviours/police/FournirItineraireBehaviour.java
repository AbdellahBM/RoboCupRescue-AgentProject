package com.jade.RoboCupRescueProject.behaviours.police;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.AID;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.jade.RoboCupRescueProject.agents.AgentPolice;

/**
 * Behavior responsible for providing route assistance to emergency vehicles.
 * This behavior receives requests for route assistance, calculates safe routes
 * based on the current road status, and provides route information to requesting agents.
 */
public class FournirItineraireBehaviour extends CyclicBehaviour {
    private Random random = new Random();

    // Map of locations to their coordinates (x,y)
    private Map<String, int[]> locationCoordinates = new HashMap<>();

    // Map of road segments connecting locations
    private Map<String, List<String>> roadNetwork = new HashMap<>();

    public FournirItineraireBehaviour(Agent a) { 
        super(a); 
        System.out.println(a.getLocalName() + ": Starting route assistance behavior");

        // Initialize location coordinates (simulated)
        locationCoordinates.put("Hospital", new int[]{100, 200});
        locationCoordinates.put("FireStation", new int[]{300, 150});
        locationCoordinates.put("PoliceStation", new int[]{250, 300});
        locationCoordinates.put("DisasterZone1", new int[]{400, 400});
        locationCoordinates.put("DisasterZone2", new int[]{150, 450});
        locationCoordinates.put("CityCenter", new int[]{250, 250});

        // Initialize road network (simulated)
        addRoadConnection("Hospital", "CityCenter", "ROAD_1");
        addRoadConnection("FireStation", "CityCenter", "ROAD_2");
        addRoadConnection("PoliceStation", "CityCenter", "ROAD_3");
        addRoadConnection("CityCenter", "DisasterZone1", "ROAD_4");
        addRoadConnection("CityCenter", "DisasterZone2", "ROAD_5");
        addRoadConnection("Hospital", "DisasterZone1", "ROAD_6");
        addRoadConnection("FireStation", "DisasterZone2", "ROAD_7");
    }

    @Override
    public void action() {
        // Check for route requests
        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
            MessageTemplate.MatchContent("ROUTE_REQUEST:.*")
        );
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            // Process route request
            String content = msg.getContent();
            String[] parts = content.substring("ROUTE_REQUEST:".length()).split(",");

            if (parts.length >= 2) {
                String fromLocation = parts[0];
                String toLocation = parts[1];

                // Optional parameters
                String vehicleType = parts.length >= 3 ? parts[2] : "standard";
                String priority = parts.length >= 4 ? parts[3] : "normal";

                // Calculate and provide route
                provideRoute(msg.getSender(), fromLocation, toLocation, vehicleType, priority);
            }
        } else {
            block(); // Block until a message is received
        }
    }

    /**
     * Add a road connection between two locations
     * @param location1 The first location
     * @param location2 The second location
     * @param roadId The ID of the road connecting the locations
     */
    private void addRoadConnection(String location1, String location2, String roadId) {
        // Add connection from location1 to location2
        if (!roadNetwork.containsKey(location1)) {
            roadNetwork.put(location1, new ArrayList<>());
        }
        roadNetwork.get(location1).add(location2 + ":" + roadId);

        // Add connection from location2 to location1 (roads are bidirectional)
        if (!roadNetwork.containsKey(location2)) {
            roadNetwork.put(location2, new ArrayList<>());
        }
        roadNetwork.get(location2).add(location1 + ":" + roadId);
    }

    /**
     * Calculate and provide a route from one location to another
     * @param sender The AID of the sender
     * @param fromLocation The starting location
     * @param toLocation The destination location
     * @param vehicleType The type of vehicle (ambulance, fire_truck, police_car, etc.)
     * @param priority The priority of the request (normal, urgent, emergency)
     */
    private void provideRoute(AID sender, String fromLocation, String toLocation, 
                             String vehicleType, String priority) {
        System.out.println(myAgent.getLocalName() + ": Calculating route from " + fromLocation + 
                           " to " + toLocation + " for " + vehicleType + " with priority " + priority);

        // Update agent status
        ((AgentPolice)myAgent).updateStatus("CALCULATING_ROUTE");

        // Calculate the route
        List<String> route = calculateRoute(fromLocation, toLocation, vehicleType, priority);

        // Send the route to the requester
        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
        reply.addReceiver(sender);

        StringBuilder routeStr = new StringBuilder("ROUTE_RESPONSE:");
        routeStr.append(fromLocation).append(",").append(toLocation).append(",");

        if (route.isEmpty()) {
            routeStr.append("NO_ROUTE_AVAILABLE");
        } else {
            for (String segment : route) {
                routeStr.append(segment).append(";");
            }
        }

        reply.setContent(routeStr.toString());
        myAgent.send(reply);

        // Increment route assistance counter
        ((AgentPolice)myAgent).incrementRouteAssistance();

        System.out.println(myAgent.getLocalName() + ": Route provided to " + sender.getLocalName());
    }

    /**
     * Calculate a route from one location to another
     * @param fromLocation The starting location
     * @param toLocation The destination location
     * @param vehicleType The type of vehicle
     * @param priority The priority of the request
     * @return A list of road segments forming the route
     */
    private List<String> calculateRoute(String fromLocation, String toLocation, 
                                      String vehicleType, String priority) {
        List<String> route = new ArrayList<>();

        // Check if both locations exist in our network
        if (!roadNetwork.containsKey(fromLocation) || !roadNetwork.containsKey(toLocation)) {
            System.out.println(myAgent.getLocalName() + ": Location not found in road network");
            return route;
        }

        // In a real implementation, this would use a pathfinding algorithm like A*
        // For simplicity, we'll use a simulated route calculation

        // Check if there's a direct connection
        List<String> connections = roadNetwork.get(fromLocation);
        for (String connection : connections) {
            String[] parts = connection.split(":");
            String connectedLocation = parts[0];
            String roadId = parts[1];

            if (connectedLocation.equals(toLocation)) {
                // Direct connection found
                String roadStatus = ((AgentPolice)myAgent).getRoadStatus(roadId);

                // Check if the road is usable
                if (roadStatus == null || roadStatus.equals(GererCirculationBehaviour.ROAD_OPEN) || 
                    (roadStatus.equals(GererCirculationBehaviour.ROAD_RESTRICTED) && 
                     (vehicleType.equals("ambulance") || vehicleType.equals("fire_truck") || 
                      vehicleType.equals("police_car")))) {

                    route.add(roadId);
                    return route;
                }
            }
        }

        // No direct connection or direct road is closed
        // In a real implementation, we would search for an alternative route
        // For simplicity, we'll just simulate finding a route through CityCenter if it's not already one of our locations
        if (!fromLocation.equals("CityCenter") && !toLocation.equals("CityCenter")) {
            // Try route through CityCenter
            List<String> routeToCenter = calculateRoute(fromLocation, "CityCenter", vehicleType, priority);
            List<String> routeFromCenter = calculateRoute("CityCenter", toLocation, vehicleType, priority);

            if (!routeToCenter.isEmpty() && !routeFromCenter.isEmpty()) {
                route.addAll(routeToCenter);
                route.addAll(routeFromCenter);
            }
        }

        // If still no route, try to find any alternative
        if (route.isEmpty() && random.nextBoolean()) { // 50% chance to find an alternative
            // Simulate finding an alternative route
            System.out.println(myAgent.getLocalName() + ": Finding alternative route");

            // Add some random roads to simulate an alternative route
            int numSegments = 2 + random.nextInt(3); // 2-4 segments
            for (int i = 0; i < numSegments; i++) {
                route.add("ROAD_" + (random.nextInt(7) + 1));
            }
        }

        return route;
    }
}
