package com.jade.RoboCupRescueProject.behaviours.logistique;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

public class AcheminerApprovisionnementBehaviour extends CyclicBehaviour {
    private Map<String, DeliveryStatus> activeDeliveries;
    private static final int DELIVERY_TIME = 2000; // milliseconds

    public AcheminerApprovisionnementBehaviour(Agent a) {
        super(a);
        this.activeDeliveries = new HashMap<>();
        System.out.println(a.getLocalName() + ": Starting supply delivery behavior");
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchContent("INITIATE_DELIVERY:.*");
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            processDeliveryRequest(msg);
        }

        // Check and update ongoing deliveries
        updateActiveDeliveries();

        block(100); // Small delay between checks
    }

    private void processDeliveryRequest(ACLMessage msg) {
        String content = msg.getContent().substring("INITIATE_DELIVERY:".length());
        String[] parts = content.split(",");
        String resource = parts[0];
        int quantity = Integer.parseInt(parts[1]);
        String location = parts[2];

        // Create new delivery
        String deliveryId = generateDeliveryId();
        DeliveryStatus status = new DeliveryStatus(resource, quantity, location,
                System.currentTimeMillis());
        activeDeliveries.put(deliveryId, status);

        System.out.println(myAgent.getLocalName() + ": Starting delivery " + deliveryId +
                " of " + quantity + " " + resource + " to " + location);
    }

    private void updateActiveDeliveries() {
        Iterator<Map.Entry<String, DeliveryStatus>> it = activeDeliveries.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, DeliveryStatus> entry = it.next();
            DeliveryStatus status = entry.getValue();

            if (System.currentTimeMillis() - status.startTime >= DELIVERY_TIME) {
                // Delivery completed
                completeDelivery(entry.getKey(), status);
                it.remove();
            }
        }
    }

    private void completeDelivery(String deliveryId, DeliveryStatus status) {
        // Notify completion
        ACLMessage completionMsg = new ACLMessage(ACLMessage.INFORM);
        completionMsg.setContent("DELIVERY_COMPLETE:" + deliveryId + "," +
                status.resource + "," + status.quantity + "," +
                status.location);
        myAgent.send(completionMsg);

        System.out.println(myAgent.getLocalName() + ": Completed delivery " + deliveryId);
    }

    private String generateDeliveryId() {
        return "DEL-" + System.currentTimeMillis() + "-" +
                String.format("%04d", (int)(Math.random() * 10000));
    }

    private static class DeliveryStatus {
        final String resource;
        final int quantity;
        final String location;
        final long startTime;

        DeliveryStatus(String resource, int quantity, String location, long startTime) {
            this.resource = resource;
            this.quantity = quantity;
            this.location = location;
            this.startTime = startTime;
        }
    }
}