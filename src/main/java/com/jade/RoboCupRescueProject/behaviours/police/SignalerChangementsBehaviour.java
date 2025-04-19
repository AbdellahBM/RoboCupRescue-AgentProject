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
 * Behavior responsible for alerting other agents about obstacles or imminent risks.
 * This behavior detects changes in the environment (road conditions, building collapses, etc.),
 * alerts other agents about these changes, and updates the command center on the feasibility
 * of routes, crowd state, and population movements.
 */
public class SignalerChangementsBehaviour extends CyclicBehaviour {
    // Alert types
    public static final String ALERT_ROAD_BLOCKED = "ROAD_BLOCKED";
    public static final String ALERT_BUILDING_COLLAPSE = "BUILDING_COLLAPSE";
    public static final String ALERT_CROWD_MOVEMENT = "CROWD_MOVEMENT";
    public static final String ALERT_TRAFFIC_JAM = "TRAFFIC_JAM";
    public static final String ALERT_BRIDGE_DAMAGE = "BRIDGE_DAMAGE";

    private Random random = new Random();
    private Map<String, Long> lastAlertTimes = new HashMap<>(); // Map of alert ID to last alert time
    private List<String> activeAlerts = new ArrayList<>(); // List of active alerts

    // Simulated areas to monitor
    private final String[] MONITORED_AREAS = {
        "CityCenter",
        "MainBridge",
        "ShoppingMall",
        "Stadium",
        "IndustrialZone"
    };

    public SignalerChangementsBehaviour(Agent a) { 
        super(a); 
        System.out.println(a.getLocalName() + ": Starting alert behavior");
    }

    @Override
    public void action() {
        // Check for messages about environmental changes
        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.or(
                MessageTemplate.MatchContent("ENVIRONMENT_CHANGE:.*"),
                MessageTemplate.MatchContent("ALERT_ACKNOWLEDGEMENT:.*")
            )
        );
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            // Process message
            String content = msg.getContent();

            if (content.startsWith("ENVIRONMENT_CHANGE:")) {
                // Handle information about an environmental change
                String[] parts = content.substring("ENVIRONMENT_CHANGE:".length()).split(",");
                if (parts.length >= 3) {
                    String changeId = parts[0];
                    String changeType = parts[1];
                    String changeLocation = parts[2];
                    handleEnvironmentChange(changeId, changeType, changeLocation);
                }
            } else if (content.startsWith("ALERT_ACKNOWLEDGEMENT:")) {
                // Handle acknowledgement of an alert
                String alertId = content.substring("ALERT_ACKNOWLEDGEMENT:".length());
                handleAlertAcknowledgement(alertId, msg.getSender());
            }
        } else {
            // Periodically monitor the environment for changes
            if (random.nextDouble() < 0.05) { // 5% chance each cycle
                monitorEnvironment();
            }

            // Periodically update the command center on the overall situation
            if (random.nextDouble() < 0.02) { // 2% chance each cycle
                updateCommandCenter();
            }

            block(1000); // Block for 1 second
        }
    }

    /**
     * Handle information about an environmental change
     * @param changeId The ID of the change
     * @param changeType The type of change
     * @param changeLocation The location of the change
     */
    private void handleEnvironmentChange(String changeId, String changeType, String changeLocation) {
        System.out.println(myAgent.getLocalName() + ": Received information about environmental change " + 
                           changeId + " of type " + changeType + " at location " + changeLocation);

        // Create an alert based on the change
        String alertId = "ALERT_" + System.currentTimeMillis();
        String alertType;

        switch (changeType) {
            case "ROAD_DAMAGE":
                alertType = ALERT_ROAD_BLOCKED;
                break;
            case "BUILDING_DAMAGE":
                alertType = ALERT_BUILDING_COLLAPSE;
                break;
            case "CROWD_GATHERING":
                alertType = ALERT_CROWD_MOVEMENT;
                break;
            case "TRAFFIC_CONGESTION":
                alertType = ALERT_TRAFFIC_JAM;
                break;
            case "BRIDGE_DAMAGE":
                alertType = ALERT_BRIDGE_DAMAGE;
                break;
            default:
                alertType = changeType;
        }

        // Send alert to relevant agents
        sendAlert(alertId, alertType, changeLocation);

        // Add to active alerts
        activeAlerts.add(alertId + ":" + alertType + ":" + changeLocation);

        // Update agent status
        ((AgentPolice)myAgent).updateStatus("SENDING_ALERT:" + alertType);
        ((AgentPolice)myAgent).incrementAlertsSent();
    }

    /**
     * Handle acknowledgement of an alert
     * @param alertId The ID of the alert
     * @param sender The AID of the sender
     */
    private void handleAlertAcknowledgement(String alertId, AID sender) {
        System.out.println(myAgent.getLocalName() + ": Received acknowledgement for alert " + 
                           alertId + " from " + sender.getLocalName());

        // Record the acknowledgement time
        lastAlertTimes.put(alertId, System.currentTimeMillis());
    }

    /**
     * Periodically monitor the environment for changes
     */
    private void monitorEnvironment() {
        System.out.println(myAgent.getLocalName() + ": Monitoring environment for changes");

        // Simulate detecting an environmental change
        if (random.nextDouble() < 0.3) { // 30% chance to detect a change
            // Select a random area to monitor
            String area = MONITORED_AREAS[random.nextInt(MONITORED_AREAS.length)];

            // Determine the type of change
            String[] changeTypes = {
                "ROAD_DAMAGE", "BUILDING_DAMAGE", "CROWD_GATHERING", 
                "TRAFFIC_CONGESTION", "BRIDGE_DAMAGE"
            };
            String changeType = changeTypes[random.nextInt(changeTypes.length)];

            // Generate a change ID
            String changeId = "CHANGE_" + System.currentTimeMillis();

            // Handle the change
            handleEnvironmentChange(changeId, changeType, area);
        }
    }

    /**
     * Send an alert to relevant agents
     * @param alertId The ID of the alert
     * @param alertType The type of alert
     * @param location The location of the alert
     */
    private void sendAlert(String alertId, String alertType, String location) {
        System.out.println(myAgent.getLocalName() + ": Sending alert " + alertId + 
                           " of type " + alertType + " for location " + location);

        // Determine which agents should receive the alert
        List<AID> recipients = new ArrayList<>();

        // Command center always receives alerts
        recipients.add(new AID("CentreCommande", AID.ISLOCALNAME));

        // Add other recipients based on alert type
        if (alertType.equals(ALERT_ROAD_BLOCKED) || alertType.equals(ALERT_BRIDGE_DAMAGE) || 
            alertType.equals(ALERT_TRAFFIC_JAM)) {
            // Traffic-related alerts go to ambulance and firefighters
            recipients.add(new AID("Ambulancier", AID.ISLOCALNAME));
            recipients.add(new AID("Pompier", AID.ISLOCALNAME));
        }

        if (alertType.equals(ALERT_BUILDING_COLLAPSE)) {
            // Building collapse alerts go to firefighters and ambulance
            recipients.add(new AID("Pompier", AID.ISLOCALNAME));
            recipients.add(new AID("Ambulancier", AID.ISLOCALNAME));
        }

        if (alertType.equals(ALERT_CROWD_MOVEMENT)) {
            // Crowd movement alerts go to all agents
            recipients.add(new AID("Ambulancier", AID.ISLOCALNAME));
            recipients.add(new AID("Pompier", AID.ISLOCALNAME));
            recipients.add(new AID("Logistique", AID.ISLOCALNAME));
        }

        // Send the alert
        ACLMessage alert = new ACLMessage(ACLMessage.INFORM);
        for (AID recipient : recipients) {
            alert.addReceiver(recipient);
        }

        alert.setContent("ALERT:" + alertId + "," + alertType + "," + location);
        myAgent.send(alert);

        System.out.println(myAgent.getLocalName() + ": Alert sent to " + recipients.size() + " agents");
    }

    /**
     * Periodically update the command center on the overall situation
     */
    private void updateCommandCenter() {
        System.out.println(myAgent.getLocalName() + ": Updating command center on overall situation");

        // Create a situation report
        StringBuilder report = new StringBuilder("SITUATION_REPORT:");

        // Add active alerts
        report.append("active_alerts=").append(activeAlerts.size()).append(",");

        // Add road status information
        int openRoads = 0;
        int closedRoads = 0;
        int restrictedRoads = 0;

        for (int i = 1; i <= 7; i++) {
            String roadId = "ROAD_" + i;
            String status = ((AgentPolice)myAgent).getRoadStatus(roadId);

            if (status == null || status.equals(GererCirculationBehaviour.ROAD_OPEN)) {
                openRoads++;
            } else if (status.equals(GererCirculationBehaviour.ROAD_CLOSED)) {
                closedRoads++;
            } else if (status.equals(GererCirculationBehaviour.ROAD_RESTRICTED)) {
                restrictedRoads++;
            }
        }

        report.append("open_roads=").append(openRoads).append(",");
        report.append("closed_roads=").append(closedRoads).append(",");
        report.append("restricted_roads=").append(restrictedRoads).append(",");

        // Add security zone information
        int securityZones = ((AgentPolice)myAgent).getSecurityPerimetersCount();
        report.append("security_zones=").append(securityZones);

        // Send the report to the command center
        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
        inform.addReceiver(new AID("CentreCommande", AID.ISLOCALNAME));
        inform.setContent(report.toString());
        myAgent.send(inform);

        System.out.println(myAgent.getLocalName() + ": Situation report sent to command center");
    }
}
