package com.jade.RoboCupRescueProject.behaviours.chefequipepompier;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignerTachesPompiersBehaviour extends OneShotBehaviour {
    private Map<String, List<AID>> zoneAssignments = new HashMap<>();
    private List<AID> availableFirefighters = new ArrayList<>();

    public AssignerTachesPompiersBehaviour(Agent a) {
        super(a);
        System.out.println(a.getLocalName() + ": Starting task assignment behavior");
    }
    @Override
    public void action() {
        // TODO: distribute tasks to AgentPompier instances
        MessageTemplate mt = MessageTemplate.or(
                MessageTemplate.MatchContent("TEAM_ORDER:.*"),
                MessageTemplate.MatchContent("FIREFIGHTER_STATUS:.*")
        );

        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            String content = msg.getContent();

            if (content.startsWith("TEAM_ORDER:")) {
                String order = content.substring("TEAM_ORDER:".length());
                distributeTeamTasks(order);
            } else if (content.startsWith("FIREFIGHTER_STATUS:")) {
                updateFirefighterStatus(msg.getSender(), content);
            }
        } else {
            block();
        }
    }

    private void distributeTeamTasks(String order) {
        // Parse zone information from order
        Map<String, Integer> zoneRequirements = parseZoneRequirements(order);

        // Redistribute firefighters based on new requirements
        redistributeFirefighters(zoneRequirements);

        // Send individual assignments
        sendAssignments();
    }

    private Map<String, Integer> parseZoneRequirements(String order) {
        Map<String, Integer> requirements = new HashMap<>();
        // Example order format: "ZONE:West=3,East=2"
        String[] zones = order.split(",");
        for (String zone : zones) {
            String[] parts = zone.split("=");
            requirements.put(parts[0], Integer.parseInt(parts[1]));
        }
        return requirements;
    }

    private void redistributeFirefighters(Map<String, Integer> requirements) {
        // Clear current assignments
        zoneAssignments.clear();

        // Create new assignments based on requirements
        for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
            String zone = entry.getKey();
            int required = entry.getValue();

            List<AID> assigned = new ArrayList<>();
            for (int i = 0; i < required && i < availableFirefighters.size(); i++) {
                assigned.add(availableFirefighters.get(i));
            }

            zoneAssignments.put(zone, assigned);
            availableFirefighters.removeAll(assigned);
        }
    }

    private void sendAssignments() {
        for (Map.Entry<String, List<AID>> entry : zoneAssignments.entrySet()) {
            String zone = entry.getKey();
            List<AID> firefighters = entry.getValue();

            for (AID firefighter : firefighters) {
                ACLMessage assignment = new ACLMessage(ACLMessage.REQUEST);
                assignment.addReceiver(firefighter);
                assignment.setContent("ASSIGNMENT:ZONE=" + zone);
                myAgent.send(assignment);
            }
        }
    }

    private void updateFirefighterStatus(AID firefighter, String status) {
        if (status.contains("AVAILABLE")) {
            if (!availableFirefighters.contains(firefighter)) {
                availableFirefighters.add(firefighter);
            }
        } else {
            availableFirefighters.remove(firefighter);
        }
    }

}