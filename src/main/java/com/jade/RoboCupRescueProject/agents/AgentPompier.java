package com.jade.RoboCupRescueProject.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.*;

public class AgentPompier extends Agent {
    private double waterLevel = 1000.0;
    private String currentLocation = "BASE";
    private boolean isAvailable = true;

    @Override
    protected void setup() {
        // Register with Directory Facilitator
        registerWithDF();

        // Initialize behaviors
        addBehaviours();

        System.out.println("Firefighter Agent " + getLocalName() + " is ready.");
        System.out.println(getLocalName() + ": Starting fire extinguishing behavior");
        System.out.println(getLocalName() + ": Starting fire detection behavior");
        System.out.println(getLocalName() + ": Starting logistics assistance behavior");
        System.out.println(getLocalName() + ": Starting fire status reporting behavior");
    }

    private void registerWithDF() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("pompier");  // This matches what CommandCenter searches for
        sd.setName("firefighting-service");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println(getLocalName() + ": Registered with Directory Facilitator");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void addBehaviours() {
        // Add behavior to handle mission requests
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                    handleMissionRequest(msg);
                } else {
                    block();
                }
            }
        });

        // Add behavior to report status periodically
        addBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            protected void onTick() {
                reportStatus();
            }
        });
    }

    private void handleMissionRequest(ACLMessage msg) {
        if (msg.getContent().startsWith("MISSION:FIRE_FIGHTING")) {
            if (isAvailable && waterLevel > 100) {
                // Accept mission
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.AGREE);
                reply.setContent("MISSION_ACCEPTED");
                send(reply);
                isAvailable = false;

                // Start mission execution
                executeMission(msg.getContent());
            } else {
                // Reject mission
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("UNAVAILABLE:" + (isAvailable ? "LOW_WATER" : "BUSY"));
                send(reply);
            }
        }
    }

    private void executeMission(String missionDetails) {
        String[] parts = missionDetails.split(":");
        if (parts.length >= 4) {
            String location = parts[2];
            addBehaviour(new OneShotBehaviour(this) {
                @Override
                public void action() {
                    System.out.println(getLocalName() + ": Executing fire fighting mission at " + location);
                    // Simulate fire fighting
                    waterLevel -= 200;
                    currentLocation = location;

                    // Report mission completion
                    reportMissionComplete(location);
                    isAvailable = true;
                }
            });
        }
    }

    private void reportStatus() {
        ACLMessage status = new ACLMessage(ACLMessage.INFORM);
        status.setContent(String.format("STATUS:FIREFIGHTER:%s:water=%.1f:location=%s:available=%b",
                getLocalName(), waterLevel, currentLocation, isAvailable));
        // Add command center as receiver
        send(status);
    }

    private void reportMissionComplete(String location) {
        ACLMessage report = new ACLMessage(ACLMessage.INFORM);
        report.setContent("MISSION_COMPLETE:FIRE_FIGHTING:" + location);
        // Add command center as receiver
        send(report);
    }

    @Override
    protected void takeDown() {
        // Deregister from the DF
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Firefighter Agent " + getLocalName() + " terminating.");
    }

    // Utility methods
    public double getWaterLevel() {
        return waterLevel;
    }

    public void resupplyWater(double amount) {
        this.waterLevel = Math.min(1000, waterLevel + amount);
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }
}