package com.jade.RoboCupRescueProject.agents;

import com.jade.RoboCupRescueProject.behaviours.centrecommande.ComportementSuiviExtinction;
import com.jade.RoboCupRescueProject.utils.AgentConsoleLogger;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class AgentCentreControle extends Agent {
    // Track all fire team leaders
    private List<AID> fireTeamLeaders = new ArrayList<>();

    // Track emergency situations
    private Map<String, EmergencySituation> activeSituations = new HashMap<>();

    // Track team assignments
    private Map<AID, String> teamAssignments = new HashMap<>();

    @Override
    protected void setup() {
        AgentConsoleLogger.logAgentStarting(this);

        // Register the command center service
        registerService();

        // Add behaviors
        addBehaviour(new EmergencyMonitorBehaviour(this, 5000));  // Check every 5 seconds
        addBehaviour(new HandleTeamReportsBehaviour());
        addBehaviour(new AssignMissionsBehaviour());
        addBehaviour(new ComportementSuiviExtinction());

        AgentConsoleLogger.logAgentStatus(this, "READY", "Centre de contrôle prêt à coordonner les opérations");
    }

    private void registerService() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("command-center");
        sd.setName("emergency-coordination");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    // Monitor emergency situations
    private class EmergencyMonitorBehaviour extends TickerBehaviour {
        public EmergencyMonitorBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            // Update situation status
            for (EmergencySituation situation : activeSituations.values()) {
                if (situation.isActive()) {
                    // Send updates to assigned teams
                    notifyTeams(situation);
                }
            }
        }

        private void notifyTeams(EmergencySituation situation) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent("UPDATE:" + situation.getLocation() + ":" + situation.getSeverity());

            // Send to assigned team leader
            for (AID teamLeader : fireTeamLeaders) {
                if (teamAssignments.get(teamLeader).equals(situation.getLocation())) {
                    msg.addReceiver(teamLeader);
                }
            }

            myAgent.send(msg);
        }
    }

    // Handle incoming reports from teams
    private class HandleTeamReportsBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // Process report
                String content = msg.getContent();
                if (content.startsWith("REPORT:")) {
                    processTeamReport(msg.getSender(), content.substring(7));
                }
            } else {
                block();
            }
        }

        private void processTeamReport(AID sender, String report) {
            // Update situation based on report
            String[] parts = report.split(":");
            String location = parts[0];
            String status = parts[1];

            if (activeSituations.containsKey(location)) {
                EmergencySituation situation = activeSituations.get(location);
                situation.updateStatus(status);

                // If situation resolved, reassign team
                if (situation.isResolved()) {
                    activeSituations.remove(location);
                    teamAssignments.remove(sender);
                }
            }
        }
    }

    // Assign missions to teams
    private class AssignMissionsBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                if (msg.getContent().startsWith("NEED_ASSIGNMENT")) {
                    assignTeam(msg.getSender());
                }
            } else {
                block();
            }
        }

        private void assignTeam(AID teamLeader) {
            // Find highest priority unassigned situation
            EmergencySituation highestPriority = findHighestPrioritySituation();

            if (highestPriority != null) {
                // Assign team to situation
                teamAssignments.put(teamLeader, highestPriority.getLocation());

                // Send assignment
                ACLMessage assignment = new ACLMessage(ACLMessage.REQUEST);
                assignment.addReceiver(teamLeader);
                assignment.setContent("ASSIGNMENT:" + highestPriority.getLocation() +
                        ":" + highestPriority.getSeverity());
                myAgent.send(assignment);
            }
        }

        private EmergencySituation findHighestPrioritySituation() {
            EmergencySituation highest = null;
            int highestPriority = -1;

            for (EmergencySituation situation : activeSituations.values()) {
                if (!teamAssignments.containsValue(situation.getLocation()) &&
                        situation.getSeverity() > highestPriority) {
                    highest = situation;
                    highestPriority = situation.getSeverity();
                }
            }

            return highest;
        }
    }

    // Helper class to track emergency situations
    private class EmergencySituation {
        private String location;
        private int severity;
        private boolean active;
        private String status;

        public EmergencySituation(String location, int severity) {
            this.location = location;
            this.severity = severity;
            this.active = true;
            this.status = "NEW";
        }

        public String getLocation() { return location; }
        public int getSeverity() { return severity; }
        public boolean isActive() { return active; }
        public boolean isResolved() { return "RESOLVED".equals(status); }

        public void updateStatus(String newStatus) {
            this.status = newStatus;
            if ("RESOLVED".equals(newStatus)) {
                this.active = false;
            }
        }
    }

    @Override
    protected void takeDown() {
        // Deregister from the DF
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        AgentConsoleLogger.logAgentStopping(this);
    }
}
