package com.jade.RoboCupRescueProject.behaviours.police;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.AID;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.jade.RoboCupRescueProject.agents.AgentPolice;

/**
 * Behavior responsible for managing traffic and securing transport routes.
 * This behavior continuously monitors the state of road infrastructure,
 * updates the road status map, and coordinates the flow of emergency vehicles.
 */
public class GererCirculationBehaviour extends CyclicBehaviour {
    // Road status constants
    public static final String ROAD_OPEN = "OPEN";
    public static final String ROAD_CLOSED = "CLOSED";
    public static final String ROAD_RESTRICTED = "RESTRICTED"; // Only emergency vehicles
    public static final String ROAD_DAMAGED = "DAMAGED";

    private Random random = new Random();
    private Map<String, String> roadMap = new HashMap<>(); // Map of road ID to road name

    // Simulated roads
    private final String[] ROADS = {
        "ROAD_1:Main Highway",
        "ROAD_2:Central Avenue",
        "ROAD_3:Bridge North",
        "ROAD_4:East Road",
        "ROAD_5:South Boulevard"
    };

    public GererCirculationBehaviour(Agent a) { 
        super(a); 
        System.out.println(a.getLocalName() + ": Starting traffic management behavior");

        // Initialize road map
        for (String road : ROADS) {
            String[] parts = road.split(":");
            roadMap.put(parts[0], parts[1]);

            // Initialize all roads as open in the agent's road status map
            ((AgentPolice)myAgent).updateRoadStatus(parts[0], ROAD_OPEN);
        }
    }

    @Override
    public void action() {
        // Check for messages about road status
        MessageTemplate mt = MessageTemplate.or(
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
            MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            // Process message
            String content = msg.getContent();

            if (content.startsWith("ROAD_STATUS_REQUEST:")) {
                // Handle request for road status
                String roadId = content.substring("ROAD_STATUS_REQUEST:".length());
                handleRoadStatusRequest(msg.getSender(), roadId);
            } else if (content.startsWith("ROAD_DAMAGE_REPORT:")) {
                // Handle report of road damage
                String[] parts = content.substring("ROAD_DAMAGE_REPORT:".length()).split(",");
                if (parts.length >= 2) {
                    String roadId = parts[0];
                    String damageLevel = parts[1];
                    handleRoadDamageReport(roadId, damageLevel);
                }
            } else if (content.startsWith("EMERGENCY_VEHICLE_PASSAGE:")) {
                // Handle request for emergency vehicle passage
                String[] parts = content.substring("EMERGENCY_VEHICLE_PASSAGE:".length()).split(",");
                if (parts.length >= 3) {
                    String vehicleType = parts[0];
                    String fromLocation = parts[1];
                    String toLocation = parts[2];
                    handleEmergencyVehiclePassage(msg.getSender(), vehicleType, fromLocation, toLocation);
                }
            }
        } else {
            // Periodically monitor road conditions
            if (random.nextDouble() < 0.05) { // 5% chance each cycle
                monitorRoadConditions();
            }

            block(1000); // Block for 1 second
        }
    }

    /**
     * Handle a request for road status
     * @param sender The AID of the sender
     * @param roadId The ID of the road
     */
    private void handleRoadStatusRequest(AID sender, String roadId) {
        System.out.println(myAgent.getLocalName() + ": Received road status request for " + roadId + " from " + sender.getLocalName());

        // Get the road status from the agent
        String status = ((AgentPolice)myAgent).getRoadStatus(roadId);

        // Send response
        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
        reply.addReceiver(sender);
        reply.setContent("ROAD_STATUS:" + roadId + "," + status);
        myAgent.send(reply);

        System.out.println(myAgent.getLocalName() + ": Sent road status for " + roadId + ": " + status);
    }

    /**
     * Handle a report of road damage
     * @param roadId The ID of the road
     * @param damageLevel The level of damage (LOW, MEDIUM, HIGH)
     */
    private void handleRoadDamageReport(String roadId, String damageLevel) {
        System.out.println(myAgent.getLocalName() + ": Received road damage report for " + roadId + ": " + damageLevel);

        // Update road status based on damage level
        String newStatus;
        if (damageLevel.equals("HIGH")) {
            newStatus = ROAD_CLOSED;
        } else if (damageLevel.equals("MEDIUM")) {
            newStatus = ROAD_RESTRICTED;
        } else {
            newStatus = ROAD_DAMAGED;
        }

        // Update the agent's road status map
        ((AgentPolice)myAgent).updateRoadStatus(roadId, newStatus);
        ((AgentPolice)myAgent).updateStatus("MANAGING_ROAD:" + roadId);

        // Inform the command center about the road status change
        informCommandCenter(roadId, newStatus);

        System.out.println(myAgent.getLocalName() + ": Updated status of " + roadId + " to " + newStatus);
    }

    /**
     * Handle a request for emergency vehicle passage
     * @param sender The AID of the sender
     * @param vehicleType The type of emergency vehicle
     * @param fromLocation The starting location
     * @param toLocation The destination location
     */
    private void handleEmergencyVehiclePassage(AID sender, String vehicleType, String fromLocation, String toLocation) {
        System.out.println(myAgent.getLocalName() + ": Received emergency passage request for " + vehicleType + 
                           " from " + fromLocation + " to " + toLocation);

        // Determine which roads need to be cleared
        String[] roadsToClear = determineRoadsToClear(fromLocation, toLocation);

        // Update road status to give priority to emergency vehicles
        for (String roadId : roadsToClear) {
            String currentStatus = ((AgentPolice)myAgent).getRoadStatus(roadId);
            if (!currentStatus.equals(ROAD_CLOSED)) {
                ((AgentPolice)myAgent).updateRoadStatus(roadId, ROAD_RESTRICTED);
                System.out.println(myAgent.getLocalName() + ": Restricted " + roadId + " for emergency vehicle passage");
            }
        }

        // Send response with cleared route
        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
        reply.addReceiver(sender);
        StringBuilder route = new StringBuilder("EMERGENCY_ROUTE:");
        for (String roadId : roadsToClear) {
            route.append(roadId).append(",");
        }
        reply.setContent(route.toString());
        myAgent.send(reply);

        // Update agent status
        ((AgentPolice)myAgent).updateStatus("COORDINATING_EMERGENCY_VEHICLE");
        ((AgentPolice)myAgent).incrementRouteAssistance();

        System.out.println(myAgent.getLocalName() + ": Sent emergency route to " + sender.getLocalName());
    }

    /**
     * Determine which roads need to be cleared for an emergency vehicle
     * @param fromLocation The starting location
     * @param toLocation The destination location
     * @return An array of road IDs that need to be cleared
     */
    private String[] determineRoadsToClear(String fromLocation, String toLocation) {
        // In a real implementation, this would use a pathfinding algorithm
        // For simplicity, we'll just return a predefined route
        return new String[]{"ROAD_1", "ROAD_3", "ROAD_4"};
    }

    /**
     * Periodically monitor road conditions
     */
    private void monitorRoadConditions() {
        System.out.println(myAgent.getLocalName() + ": Monitoring road conditions");

        // Simulate detecting a road issue
        if (random.nextDouble() < 0.3) { // 30% chance to detect an issue
            // Select a random road
            String roadId = "ROAD_" + (random.nextInt(5) + 1);

            // Determine the issue
            String[] issues = {ROAD_DAMAGED, ROAD_RESTRICTED, ROAD_CLOSED};
            String issue = issues[random.nextInt(issues.length)];

            // Update road status
            ((AgentPolice)myAgent).updateRoadStatus(roadId, issue);
            ((AgentPolice)myAgent).updateStatus("DETECTED_ROAD_ISSUE:" + roadId);

            System.out.println(myAgent.getLocalName() + ": Detected issue with " + roadId + ": " + issue);

            // Inform the command center
            informCommandCenter(roadId, issue);
        }
    }

    /**
     * Inform the command center about a road status change
     * @param roadId The ID of the road
     * @param status The new status of the road
     */
    private void informCommandCenter(String roadId, String status) {
        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
        inform.addReceiver(new AID("CentreCommande", AID.ISLOCALNAME));
        inform.setContent("ROAD_STATUS_CHANGE:" + roadId + "," + status);
        myAgent.send(inform);

        System.out.println(myAgent.getLocalName() + ": Informed command center about status change of " + roadId);
    }
}
