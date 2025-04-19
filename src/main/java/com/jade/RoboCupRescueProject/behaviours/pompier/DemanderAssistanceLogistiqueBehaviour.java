package com.jade.RoboCupRescueProject.behaviours.pompier;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class DemanderAssistanceLogistiqueBehaviour extends CyclicBehaviour {
    private final EteindreIncendieBehaviour extinguishBehaviour;
    private static final double WATER_THRESHOLD = 200.0; // Request when below 200L
    private long lastRequestTime = 0;
    private static final long REQUEST_COOLDOWN = 5000; // 5 seconds between requests

    public DemanderAssistanceLogistiqueBehaviour(Agent a, EteindreIncendieBehaviour extinguishBehaviour) {
        super(a);
        this.extinguishBehaviour = extinguishBehaviour;
        System.out.println(a.getLocalName() + ": Starting logistics assistance behavior");
    }

    @Override
    public void action() {
        checkResources();
        block(1000); // Check every second
    }

    private void checkResources() {
        double currentWaterLevel = extinguishBehaviour.getWaterLevel();

        if (currentWaterLevel < WATER_THRESHOLD &&
                System.currentTimeMillis() - lastRequestTime > REQUEST_COOLDOWN) {

            requestLogisticalSupport(currentWaterLevel);
            lastRequestTime = System.currentTimeMillis();
        }
    }

    private void requestLogisticalSupport(double currentWaterLevel) {
        // Request water resupply
        ACLMessage waterRequest = new ACLMessage(ACLMessage.REQUEST);
        waterRequest.setContent("LOGISTICS_REQUEST:WATER," +
                (1000 - currentWaterLevel) + "," +
                myAgent.getLocalName());
        myAgent.send(waterRequest);

        // Request any other needed supplies
        checkAndRequestOtherSupplies();
    }

    private void checkAndRequestOtherSupplies() {
        // Check fuel level (if implemented)
        // Check equipment status (if implemented)
        // Request other supplies as needed
    }
}