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
 * Behavior responsible for establishing security perimeters around dangerous areas.
 * This behavior receives information about dangerous areas (fires, collapses, explosions),
 * establishes security perimeters, and controls access to these areas.
 */
public class BloquerZoneDangerBehaviour extends CyclicBehaviour {
    // Security zone status constants
    public static final String ZONE_DANGER = "DANGER";
    public static final String ZONE_RESTRICTED = "RESTRICTED";
    public static final String ZONE_EVACUATED = "EVACUATED";
    public static final String ZONE_SECURED = "SECURED";

    private Random random = new Random();
    private Map<String, String> zoneTypes = new HashMap<>(); // Map of zone ID to zone type (FIRE, COLLAPSE, etc.)

    public BloquerZoneDangerBehaviour(Agent a) { 
        super(a); 
        System.out.println(a.getLocalName() + ": Starting security perimeter behavior");
    }

    @Override
    public void action() {
        // Check for messages about dangerous areas
        MessageTemplate mt = MessageTemplate.or(
            MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchContent("DANGER_ZONE:.*")
            ),
            MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.MatchContent("ACCESS_REQUEST:.*")
            )
        );
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            // Process message
            String content = msg.getContent();

            if (content.startsWith("DANGER_ZONE:")) {
                // Handle information about a dangerous area
                String[] parts = content.substring("DANGER_ZONE:".length()).split(",");
                if (parts.length >= 3) {
                    String zoneId = parts[0];
                    String zoneType = parts[1];
                    String dangerLevel = parts[2];
                    handleDangerZone(zoneId, zoneType, dangerLevel);
                }
            } else if (content.startsWith("ACCESS_REQUEST:")) {
                // Handle request for access to a secured zone
                String[] parts = content.substring("ACCESS_REQUEST:".length()).split(",");
                if (parts.length >= 3) {
                    String zoneId = parts[0];
                    String agentType = parts[1];
                    String reason = parts[2];
                    handleAccessRequest(msg.getSender(), zoneId, agentType, reason);
                }
            }
        } else {
            // Periodically check security zones
            if (random.nextDouble() < 0.05) { // 5% chance each cycle
                checkSecurityZones();
            }

            block(1000); // Block for 1 second
        }
    }

    /**
     * Handle information about a dangerous area
     * @param zoneId The ID of the zone
     * @param zoneType The type of danger (FIRE, COLLAPSE, EXPLOSION, etc.)
     * @param dangerLevel The level of danger (LOW, MEDIUM, HIGH)
     */
    private void handleDangerZone(String zoneId, String zoneType, String dangerLevel) {
        System.out.println(myAgent.getLocalName() + ": Received information about dangerous zone " + 
                           zoneId + " of type " + zoneType + " with danger level " + dangerLevel);

        // Store the zone type
        zoneTypes.put(zoneId, zoneType);

        // Determine the appropriate security status based on danger level
        String securityStatus;
        if (dangerLevel.equals("HIGH")) {
            securityStatus = ZONE_EVACUATED;
        } else if (dangerLevel.equals("MEDIUM")) {
            securityStatus = ZONE_RESTRICTED;
        } else {
            securityStatus = ZONE_DANGER;
        }

        // Establish security perimeter
        establishSecurityPerimeter(zoneId, securityStatus);

        // Update agent status
        ((AgentPolice)myAgent).updateStatus("SECURING_ZONE:" + zoneId);

        // Inform other agents about the security perimeter
        informAgentsAboutPerimeter(zoneId, zoneType, securityStatus);
    }

    /**
     * Establish a security perimeter around a dangerous area
     * @param zoneId The ID of the zone
     * @param securityStatus The security status to set
     */
    private void establishSecurityPerimeter(String zoneId, String securityStatus) {
        System.out.println(myAgent.getLocalName() + ": Establishing security perimeter around zone " + 
                           zoneId + " with status " + securityStatus);

        // Add the security zone to the agent's map
        ((AgentPolice)myAgent).addSecurityZone(zoneId, securityStatus);

        // Simulate setting up physical barriers, redirecting traffic, etc.
        try {
            Thread.sleep(500); // Simulate time taken to set up perimeter
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(myAgent.getLocalName() + ": Security perimeter established around zone " + zoneId);
    }

    /**
     * Handle a request for access to a secured zone
     * @param sender The AID of the sender
     * @param zoneId The ID of the zone
     * @param agentType The type of agent requesting access
     * @param reason The reason for the access request
     */
    private void handleAccessRequest(AID sender, String zoneId, String agentType, String reason) {
        System.out.println(myAgent.getLocalName() + ": Received access request for zone " + zoneId + 
                           " from " + sender.getLocalName() + " (" + agentType + ") for reason: " + reason);

        // Get the security status of the zone
        String securityStatus = ((AgentPolice)myAgent).getSecurityZoneStatus(zoneId);

        // Determine if access should be granted
        boolean accessGranted = false;

        if (securityStatus == null) {
            // Zone not found
            System.out.println(myAgent.getLocalName() + ": Zone " + zoneId + " not found");
        } else if (securityStatus.equals(ZONE_SECURED)) {
            // Zone is secured, access can be granted
            accessGranted = true;
        } else if (securityStatus.equals(ZONE_RESTRICTED)) {
            // Zone is restricted, access depends on agent type and reason
            if (agentType.equals("pompier") && reason.equals("FIRE_FIGHTING")) {
                accessGranted = true;
            } else if (agentType.equals("ambulancier") && reason.equals("VICTIM_RESCUE")) {
                accessGranted = true;
            }
        } else if (securityStatus.equals(ZONE_DANGER)) {
            // Zone is dangerous, access only for firefighters
            if (agentType.equals("pompier")) {
                accessGranted = true;
            }
        }
        // ZONE_EVACUATED: No access for anyone

        // Send response
        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
        reply.addReceiver(sender);
        reply.setContent("ACCESS_RESPONSE:" + zoneId + "," + (accessGranted ? "GRANTED" : "DENIED"));
        myAgent.send(reply);

        System.out.println(myAgent.getLocalName() + ": Access to zone " + zoneId + " " + 
                           (accessGranted ? "granted" : "denied") + " for " + sender.getLocalName());
    }

    /**
     * Periodically check security zones
     */
    private void checkSecurityZones() {
        System.out.println(myAgent.getLocalName() + ": Checking security zones");

        // In a real implementation, this would involve checking if the danger has passed
        // For simplicity, we'll just simulate a chance of updating a zone's status

        // Simulate detecting that a danger has passed
        if (random.nextDouble() < 0.2 && !zoneTypes.isEmpty()) { // 20% chance if there are zones
            // Select a random zone
            String[] zoneIds = zoneTypes.keySet().toArray(new String[0]);
            String zoneId = zoneIds[random.nextInt(zoneIds.length)];

            // Get the current status
            String currentStatus = ((AgentPolice)myAgent).getSecurityZoneStatus(zoneId);

            // Determine if the status should be updated
            if (currentStatus != null && !currentStatus.equals(ZONE_SECURED)) {
                // Update to a safer status
                String newStatus;
                if (currentStatus.equals(ZONE_EVACUATED)) {
                    newStatus = ZONE_RESTRICTED;
                } else if (currentStatus.equals(ZONE_RESTRICTED)) {
                    newStatus = ZONE_DANGER;
                } else {
                    newStatus = ZONE_SECURED;
                }

                // Update the zone status
                ((AgentPolice)myAgent).addSecurityZone(zoneId, newStatus);

                System.out.println(myAgent.getLocalName() + ": Updated status of zone " + zoneId + 
                                   " from " + currentStatus + " to " + newStatus);

                // Inform other agents about the status change
                informAgentsAboutPerimeter(zoneId, zoneTypes.get(zoneId), newStatus);
            }
        }
    }

    /**
     * Inform other agents about a security perimeter
     * @param zoneId The ID of the zone
     * @param zoneType The type of danger
     * @param securityStatus The security status
     */
    private void informAgentsAboutPerimeter(String zoneId, String zoneType, String securityStatus) {
        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);

        // Add receivers (other agents that might be interested in security perimeters)
        inform.addReceiver(new AID("CentreCommande", AID.ISLOCALNAME));
        inform.addReceiver(new AID("Pompier", AID.ISLOCALNAME));
        inform.addReceiver(new AID("Ambulancier", AID.ISLOCALNAME));

        inform.setContent("SECURITY_PERIMETER:" + zoneId + "," + zoneType + "," + securityStatus);
        myAgent.send(inform);

        // Increment the alerts sent counter
        ((AgentPolice)myAgent).incrementAlertsSent();

        System.out.println(myAgent.getLocalName() + ": Informed other agents about security perimeter for zone " + zoneId);
    }
}
