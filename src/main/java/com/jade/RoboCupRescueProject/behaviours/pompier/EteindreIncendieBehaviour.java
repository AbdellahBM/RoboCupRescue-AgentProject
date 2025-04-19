package com.jade.RoboCupRescueProject.behaviours.pompier;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class EteindreIncendieBehaviour extends CyclicBehaviour {
    private static final double WATER_USE_RATE = 10.0; // liters per second
    private static final double INITIAL_WATER_CAPACITY = 1000.0; // liters

    private double waterLevel;
    private boolean isExtinguishing;
    private String currentFireLocation;

    public EteindreIncendieBehaviour(Agent a) {
        super(a);
        waterLevel = INITIAL_WATER_CAPACITY;
        isExtinguishing = false;
        System.out.println(a.getLocalName() + ": Starting fire extinguishing behavior");
    }

    @Override
    public void action() {
        // Check for fire fighting commands
        MessageTemplate mt = MessageTemplate.or(
                MessageTemplate.MatchContent("START_EXTINGUISHING:.*"),
                MessageTemplate.MatchContent("STOP_EXTINGUISHING")
        );

        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            if (msg.getContent().startsWith("START_EXTINGUISHING:")) {
                handleStartExtinguishing(msg.getContent());
            } else {
                stopExtinguishing();
            }
        }

        // Continue extinguishing if active
        if (isExtinguishing) {
            continueExtinguishing();
        }

        block(100); // Small delay between actions
    }

    private void handleStartExtinguishing(String content) {
        String location = content.substring("START_EXTINGUISHING:".length());

        if (waterLevel > 0) {
            isExtinguishing = true;
            currentFireLocation = location;
            System.out.println(myAgent.getLocalName() + ": Starting to extinguish fire at " + location);
        } else {
            requestWaterResupply();
        }
    }

    private void continueExtinguishing() {
        // Use water
        double waterUsed = WATER_USE_RATE * 0.1; // For 100ms interval
        waterLevel -= waterUsed;

        if (waterLevel <= 0) {
            waterLevel = 0;
            stopExtinguishing();
            requestWaterResupply();
            return;
        }

        // Report progress
        ACLMessage progressMsg = new ACLMessage(ACLMessage.INFORM);
        progressMsg.setContent("EXTINGUISHING_PROGRESS:" + currentFireLocation +
                ",water_level=" + String.format("%.2f", waterLevel));
        myAgent.send(progressMsg);
    }

    private void stopExtinguishing() {
        if (isExtinguishing) {
            isExtinguishing = false;
            currentFireLocation = null;
            System.out.println(myAgent.getLocalName() + ": Stopping extinguishing operation");
        }
    }

    private void requestWaterResupply() {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.setContent("WATER_RESUPPLY_REQUEST:" + myAgent.getLocalName());
        myAgent.send(request);
    }

    public double getWaterLevel() {
        return waterLevel;
    }

    public void resupplyWater(double amount) {
        waterLevel = Math.min(INITIAL_WATER_CAPACITY, waterLevel + amount);
    }
}