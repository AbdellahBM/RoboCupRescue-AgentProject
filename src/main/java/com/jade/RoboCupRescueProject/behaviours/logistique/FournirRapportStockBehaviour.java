package com.jade.RoboCupRescueProject.behaviours.logistique;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

public class FournirRapportStockBehaviour extends TickerBehaviour {
    private Map<String, ResourceStatus> resourceStatus;
    private static final long REPORT_INTERVAL = 5000; // 5 seconds

    public FournirRapportStockBehaviour(Agent a) {
        super(a, REPORT_INTERVAL);
        initializeResourceStatus();
        System.out.println(a.getLocalName() + ": Starting stock reporting behavior");
    }

    private void initializeResourceStatus() {
        resourceStatus = new HashMap<>();
        resourceStatus.put("WATER", new ResourceStatus(1000, "Storage-A"));
        resourceStatus.put("FUEL", new ResourceStatus(500, "Storage-B"));
        resourceStatus.put("MEDICAL_KITS", new ResourceStatus(50, "Storage-C"));
        resourceStatus.put("OXYGEN_TANKS", new ResourceStatus(30, "Storage-C"));
    }

    @Override
    protected void onTick() {
        // Update resource status from recent changes
        processStockUpdates();

        // Generate and send report
        generateStockReport();
    }

    private void processStockUpdates() {
        MessageTemplate mt = MessageTemplate.MatchContent("STOCK_CHANGE:.*");
        ACLMessage msg;

        while ((msg = myAgent.receive(mt)) != null) {
            String content = msg.getContent().substring("STOCK_CHANGE:".length());
            String[] parts = content.split(",");
            String resource = parts[0];
            int quantity = Integer.parseInt(parts[1]);

            ResourceStatus status = resourceStatus.get(resource);
            if (status != null) {
                status.quantity = quantity;
                status.lastUpdate = System.currentTimeMillis();
            }
        }
    }

    private void generateStockReport() {
        StringBuilder report = new StringBuilder("STOCK_REPORT:\n");

        for (Map.Entry<String, ResourceStatus> entry : resourceStatus.entrySet()) {
            ResourceStatus status = entry.getValue();
            report.append(String.format("- %s: %d units at %s (Updated: %s)\n",
                    entry.getKey(),
                    status.quantity,
                    status.location,
                    new Date(status.lastUpdate)
            ));
        }

        // Send report to command center
        ACLMessage reportMsg = new ACLMessage(ACLMessage.INFORM);
        reportMsg.setContent(report.toString());
        myAgent.send(reportMsg);

        System.out.println(myAgent.getLocalName() + ": Stock report generated and sent");
    }

    private static class ResourceStatus {
        int quantity;
        final String location;
        long lastUpdate;

        ResourceStatus(int quantity, String location) {
            this.quantity = quantity;
            this.location = location;
            this.lastUpdate = System.currentTimeMillis();
        }
    }
}