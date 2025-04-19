package com.jade.RoboCupRescueProject.behaviours.centrecommande;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import com.jade.RoboCupRescueProject.agents.AgentCentreCommande;

/**
 * Behavior responsible for collecting and aggregating data from different agents.
 * This behavior receives information from Firefighters, Ambulance, and Police agents,
 * processes this information, and updates the command center's knowledge base.
 */
public class CollecterInfosBehaviour extends CyclicBehaviour {
    // Message types
    private static final String FIRE_REPORT = "FIRE_REPORT";
    private static final String VICTIM_REPORT = "VICTIM_REPORT";
    private static final String ROAD_STATUS_CHANGE = "ROAD_STATUS_CHANGE";
    private static final String SECURITY_PERIMETER = "SECURITY_PERIMETER";
    private static final String SUMMARY_REPORT = "SUMMARY_REPORT";
    private static final String SITUATION_REPORT = "SITUATION_REPORT";
    private static final String VICTIM_TRANSPORTED = "VICTIM_TRANSPORTED";
    private static final String TRANSPORT_REPORT = "TRANSPORT_REPORT";

    public CollecterInfosBehaviour(Agent a) { 
        super(a); 
        System.out.println(a.getLocalName() + ": Starting information collection behavior");
    }

    @Override
    public void action() {
        // Create a template to receive INFORM messages
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            // Process the message
            String content = msg.getContent();
            String sender = msg.getSender().getLocalName();

            System.out.println(myAgent.getLocalName() + ": Received information from " + sender + ": " + content);

            // Update agent status
            ((AgentCentreCommande)myAgent).updateStatus("PROCESSING_INFO:" + sender);

            // Process different types of information
            if (content.startsWith(FIRE_REPORT)) {
                processFireReport(content, sender);
            } else if (content.startsWith(VICTIM_REPORT)) {
                processVictimReport(content, sender);
            } else if (content.startsWith(ROAD_STATUS_CHANGE)) {
                processRoadStatusChange(content, sender);
            } else if (content.startsWith(SECURITY_PERIMETER)) {
                processSecurityPerimeter(content, sender);
            } else if (content.startsWith(SUMMARY_REPORT)) {
                processSummaryReport(content, sender);
            } else if (content.startsWith(SITUATION_REPORT)) {
                processSituationReport(content, sender);
            } else if (content.startsWith(VICTIM_TRANSPORTED) || content.startsWith(TRANSPORT_REPORT)) {
                processTransportReport(content, sender);
            } else {
                // Unknown message type
                System.out.println(myAgent.getLocalName() + ": Received unknown message type from " + sender);
            }

            // Send acknowledgement
            sendAcknowledgement(msg.getSender(), content);
        } else {
            block(); // Block until a message is received
        }
    }

    /**
     * Process a fire report
     * @param content The message content
     * @param sender The sender of the message
     */
    private void processFireReport(String content, String sender) {
        // Extract fire information
        String[] parts = content.substring(FIRE_REPORT.length() + 1).split(",");
        if (parts.length >= 2) {
            String location = parts[0];
            String status = parts[1];

            // Update the command center's knowledge base
            ((AgentCentreCommande)myAgent).addFireReport(location, status);

            System.out.println(myAgent.getLocalName() + ": Processed fire report from " + sender + 
                               " - Location: " + location + ", Status: " + status);
        }
    }

    /**
     * Process a victim report
     * @param content The message content
     * @param sender The sender of the message
     */
    private void processVictimReport(String content, String sender) {
        // Extract victim information
        String[] parts = content.substring(VICTIM_REPORT.length() + 1).split(",");
        if (parts.length >= 2) {
            String location = parts[0];
            String status = parts[1];

            // Update the command center's knowledge base
            ((AgentCentreCommande)myAgent).addVictimReport(location, status);

            System.out.println(myAgent.getLocalName() + ": Processed victim report from " + sender + 
                               " - Location: " + location + ", Status: " + status);
        }
    }

    /**
     * Process a road status change
     * @param content The message content
     * @param sender The sender of the message
     */
    private void processRoadStatusChange(String content, String sender) {
        // Extract road information
        String[] parts = content.substring(ROAD_STATUS_CHANGE.length() + 1).split(",");
        if (parts.length >= 2) {
            String roadId = parts[0];
            String status = parts[1];

            // Update the command center's knowledge base
            ((AgentCentreCommande)myAgent).addRoadReport(roadId, status);

            System.out.println(myAgent.getLocalName() + ": Processed road status change from " + sender + 
                               " - Road: " + roadId + ", Status: " + status);
        }
    }

    /**
     * Process a security perimeter report
     * @param content The message content
     * @param sender The sender of the message
     */
    private void processSecurityPerimeter(String content, String sender) {
        // Extract security zone information
        String[] parts = content.substring(SECURITY_PERIMETER.length() + 1).split(",");
        if (parts.length >= 3) {
            String zoneId = parts[0];
            String zoneType = parts[1];
            String status = parts[2];

            // Update the command center's knowledge base
            ((AgentCentreCommande)myAgent).addSecurityZoneReport(zoneId, status);

            System.out.println(myAgent.getLocalName() + ": Processed security perimeter from " + sender + 
                               " - Zone: " + zoneId + ", Type: " + zoneType + ", Status: " + status);
        }
    }

    /**
     * Process a summary report
     * @param content The message content
     * @param sender The sender of the message
     */
    private void processSummaryReport(String content, String sender) {
        // Extract summary information
        String[] parts = content.substring(SUMMARY_REPORT.length() + 1).split(",");

        // Process each key-value pair
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];

                System.out.println(myAgent.getLocalName() + ": Processed summary data from " + sender + 
                                   " - " + key + ": " + value);

                // Add to priority missions if needed
                if (key.equals("critique") && Integer.parseInt(value) > 0) {
                    ((AgentCentreCommande)myAgent).addPriorityMission("RESCUE_CRITICAL_VICTIMS:" + sender);
                }
            }
        }
    }

    /**
     * Process a situation report
     * @param content The message content
     * @param sender The sender of the message
     */
    private void processSituationReport(String content, String sender) {
        // Extract situation information
        String[] parts = content.substring(SITUATION_REPORT.length() + 1).split(",");

        // Process each key-value pair
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];

                System.out.println(myAgent.getLocalName() + ": Processed situation data from " + sender + 
                                   " - " + key + ": " + value);

                // Add to priority missions if needed
                if (key.equals("closed_roads") && Integer.parseInt(value) > 0) {
                    ((AgentCentreCommande)myAgent).addPriorityMission("CLEAR_ROADS:" + sender);
                }

                if (key.equals("security_zones") && Integer.parseInt(value) > 0) {
                    ((AgentCentreCommande)myAgent).addPriorityMission("MONITOR_SECURITY_ZONES:" + sender);
                }
            }
        }
    }

    /**
     * Process a transport report
     * @param content The message content
     * @param sender The sender of the message
     */
    private void processTransportReport(String content, String sender) {
        // Extract transport information
        String messageType = content.startsWith(VICTIM_TRANSPORTED) ? VICTIM_TRANSPORTED : TRANSPORT_REPORT;
        String[] parts = content.substring(messageType.length() + 1).split(",");

        if (parts.length >= 3) {
            String location = parts[0];
            String facility = parts[1];
            String status = parts[2];

            System.out.println(myAgent.getLocalName() + ": Processed transport report from " + sender + 
                               " - Location: " + location + ", Facility: " + facility + ", Status: " + status);

            // Update victim status to transported
            ((AgentCentreCommande)myAgent).addVictimReport(location, "TRANSPORTED:" + facility);
        }
    }

    /**
     * Send an acknowledgement message
     * @param receiver The receiver of the acknowledgement
     * @param originalContent The original message content
     */
    private void sendAcknowledgement(jade.core.AID receiver, String originalContent) {
        ACLMessage ack = new ACLMessage(ACLMessage.INFORM);
        ack.addReceiver(receiver);

        // Create an acknowledgement ID based on the original content
        String ackId = "ACK_" + System.currentTimeMillis();

        ack.setContent("ACKNOWLEDGEMENT:" + ackId);
        myAgent.send(ack);

        System.out.println(myAgent.getLocalName() + ": Sent acknowledgement " + ackId + " to " + receiver.getLocalName());
    }
}
