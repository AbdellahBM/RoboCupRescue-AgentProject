package com.jade.RoboCupRescueProject.behaviours.logistique;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecevoirDemandesRessourcesBehaviour extends CyclicBehaviour {
    private Map<String, Integer> resourceStock;
    private Map<String, List<ResourceRequest>> pendingRequests;
    private static final int CRITICAL_THRESHOLD = 20; // percentage

    public RecevoirDemandesRessourcesBehaviour(Agent a) {
        super(a);
        initializeStock();
        pendingRequests = new HashMap<>();
        System.out.println(a.getLocalName() + ": Starting resource request handling");;
    }

    private void initializeStock() {
        resourceStock = new HashMap<>();
        // Initialize with default stock levels
        resourceStock.put("WATER", 1000);    // liters
        resourceStock.put("FUEL", 500);      // liters
        resourceStock.put("MEDICAL_KITS", 50); // units
        resourceStock.put("OXYGEN_TANKS", 30); // units
    }


    @Override
    public void action() {
        // TODO: listen for resource REQUESTs
        MessageTemplate mt = MessageTemplate.or(
                MessageTemplate.MatchContent("RESOURCE_REQUEST:.*"),
                MessageTemplate.MatchContent("STOCK_UPDATE:.*")
        );

        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            String content = msg.getContent();

            if (content.startsWith("RESOURCE_REQUEST:")) {
                handleResourceRequest(msg);
            } else if (content.startsWith("STOCK_UPDATE:")) {
                updateStockLevels(content);
            }
        } else {
            block();
        }
    }

    private void handleResourceRequest(ACLMessage msg) {
        String content = msg.getContent().substring("RESOURCE_REQUEST:".length());
        String[] parts = content.split(",");
        String resource = parts[0];
        int quantity = Integer.parseInt(parts[1]);
        String location = parts[2];

        ResourceRequest request = new ResourceRequest(
                msg.getSender(),
                resource,
                quantity,
                location,
                System.currentTimeMillis()
        );

        // Add to pending requests
        pendingRequests.computeIfAbsent(resource, k -> new ArrayList<>()).add(request);

        // Check if we can fulfill the request
        if (canFulfillRequest(resource, quantity)) {
            // Trigger resource delivery
            ACLMessage deliveryMsg = new ACLMessage(ACLMessage.REQUEST);
            deliveryMsg.setContent("INITIATE_DELIVERY:" + resource + "," + quantity + "," + location);
            myAgent.send(deliveryMsg);

            // Update stock
            updateStock(resource, -quantity);
        } else {
            // Request emergency resupply if stock is low
            checkAndRequestEmergencyResupply(resource);
        }
    }

    private boolean canFulfillRequest(String resource, int quantity) {
        return resourceStock.getOrDefault(resource, 0) >= quantity;
    }

    private void updateStock(String resource, int change) {
        int currentStock = resourceStock.getOrDefault(resource, 0);
        resourceStock.put(resource, currentStock + change);

        // Notify stock report behavior
        ACLMessage reportMsg = new ACLMessage(ACLMessage.INFORM);
        reportMsg.setContent("STOCK_CHANGE:" + resource + "," + resourceStock.get(resource));
        myAgent.send(reportMsg);
    }

    private void checkAndRequestEmergencyResupply(String resource) {
        int currentStock = resourceStock.getOrDefault(resource, 0);
        int maxStock = getMaxStock(resource);

        if (currentStock < (maxStock * CRITICAL_THRESHOLD / 100)) {
            ACLMessage emergencyMsg = new ACLMessage(ACLMessage.REQUEST);
            emergencyMsg.setContent("EMERGENCY_RESUPPLY:" + resource);
            myAgent.send(emergencyMsg);
        }
    }

    private void updateStockLevels(String content) {
        String[] parts = content.substring("STOCK_UPDATE:".length()).split(",");
        String resource = parts[0];
        int newQuantity = Integer.parseInt(parts[1]);
        resourceStock.put(resource, newQuantity);
    }

    private int getMaxStock(String resource) {
        switch (resource) {
            case "WATER": return 1000;
            case "FUEL": return 500;
            case "MEDICAL_KITS": return 50;
            case "OXYGEN_TANKS": return 30;
            default: return 0;
        }
    }

    private static class ResourceRequest {
        final jade.core.AID requester;
        final String resource;
        final int quantity;
        final String location;
        final long timestamp;

        ResourceRequest(jade.core.AID requester, String resource, int quantity,
                        String location, long timestamp) {
            this.requester = requester;
            this.resource = resource;
            this.quantity = quantity;
            this.location = location;
            this.timestamp = timestamp;
        }
    }

}