package com.jade.RoboCupRescueProject.behaviours.centrecommande;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import com.jade.RoboCupRescueProject.agents.AgentCentreCommande;

/**
 * Behavior responsible for managing global resources.
 * This behavior monitors the available resources (number of vehicles, water quantity, medical equipment),
 * processes resource requests from other agents, and coordinates with the logistics agent
 * to ensure adequate supplies are available.
 */
public class GererRessourcesGlobalesBehaviour extends CyclicBehaviour {
    // Resource types
    private static final String RESOURCE_FIRE_TRUCKS = "fire_trucks";
    private static final String RESOURCE_AMBULANCES = "ambulances";
    private static final String RESOURCE_POLICE_CARS = "police_cars";
    private static final String RESOURCE_WATER_SUPPLY = "water_supply";
    private static final String RESOURCE_MEDICAL_KITS = "medical_kits";
    private static final String RESOURCE_BARRICADES = "barricades";

    // Resource thresholds
    private static final int THRESHOLD_FIRE_TRUCKS = 3;
    private static final int THRESHOLD_AMBULANCES = 2;
    private static final int THRESHOLD_POLICE_CARS = 4;
    private static final int THRESHOLD_WATER_SUPPLY = 1000; // liters
    private static final int THRESHOLD_MEDICAL_KITS = 15;
    private static final int THRESHOLD_BARRICADES = 10;

    // Resource consumption rates (per minute)
    private static final Map<String, Integer> CONSUMPTION_RATES = new HashMap<>();

    private Random random = new Random();
    private long lastResourceCheckTime = 0;
    private static final long RESOURCE_CHECK_INTERVAL = 10000; // 10 seconds

    // Pending resource requests
    private Map<String, Integer> pendingRequests = new HashMap<>();

    public GererRessourcesGlobalesBehaviour(Agent a) { 
        super(a); 
        // Removed startup logging to reduce console clutter

        // Initialize consumption rates
        CONSUMPTION_RATES.put(RESOURCE_FIRE_TRUCKS, 0);
        CONSUMPTION_RATES.put(RESOURCE_AMBULANCES, 0);
        CONSUMPTION_RATES.put(RESOURCE_POLICE_CARS, 0);
        CONSUMPTION_RATES.put(RESOURCE_WATER_SUPPLY, 100); // 100 liters per minute
        CONSUMPTION_RATES.put(RESOURCE_MEDICAL_KITS, 2); // 2 kits per minute
        CONSUMPTION_RATES.put(RESOURCE_BARRICADES, 1); // 1 barricade per minute
    }

    @Override
    public void action() {
        // Check for resource request messages
        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
            MessageTemplate.MatchContent("RESOURCE_REQUEST:.*")
        );
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            // Process resource request
            processResourceRequest(msg);
        } else {
            // Check for resource allocation messages
            MessageTemplate mtAlloc = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchContent("RESOURCE_ALLOCATION:.*")
            );
            ACLMessage allocMsg = myAgent.receive(mtAlloc);

            if (allocMsg != null) {
                // Process resource allocation
                processResourceAllocation(allocMsg);
            } else {
                // Periodically check and update resources
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastResourceCheckTime > RESOURCE_CHECK_INTERVAL) {
                    checkAndUpdateResources();
                    lastResourceCheckTime = currentTime;
                }

                block(1000); // Block for 1 second
            }
        }
    }

    /**
     * Process a resource request message
     * @param msg The message containing the resource request
     */
    private void processResourceRequest(ACLMessage msg) {
        String content = msg.getContent();
        String sender = msg.getSender().getLocalName();

        System.out.println(myAgent.getLocalName() + ": Processing resource request from " + sender + ": " + content);

        // Update agent status
        ((AgentCentreCommande)myAgent).updateStatus("PROCESSING_RESOURCE_REQUEST");

        // Extract resource information
        String[] parts = content.substring("RESOURCE_REQUEST:".length()).split(",");
        if (parts.length >= 2) {
            String resourceType = parts[0];
            int quantity = Integer.parseInt(parts[1]);

            // Check if we have enough resources
            AgentCentreCommande agent = (AgentCentreCommande)myAgent;
            int available = agent.getResourceQuantity(resourceType);

            if (available >= quantity) {
                // We have enough resources, allocate them
                agent.updateResource(resourceType, available - quantity);

                // Send confirmation
                sendResourceConfirmation(msg.getSender(), resourceType, quantity);

                System.out.println(myAgent.getLocalName() + ": Allocated " + quantity + " " + 
                                   resourceType + " to " + sender);
            } else {
                // Not enough resources, request more from logistics
                int shortfall = quantity - available;
                requestAdditionalResources(resourceType, shortfall);

                // Add to pending requests
                pendingRequests.put(sender + ":" + resourceType, quantity);

                // Send pending message
                sendResourcePending(msg.getSender(), resourceType, quantity);

                System.out.println(myAgent.getLocalName() + ": Requested additional " + shortfall + 
                                   " " + resourceType + " from logistics for " + sender);
            }
        }
    }

    /**
     * Process a resource allocation message
     * @param msg The message containing the resource allocation
     */
    private void processResourceAllocation(ACLMessage msg) {
        String content = msg.getContent();
        String sender = msg.getSender().getLocalName();

        System.out.println(myAgent.getLocalName() + ": Processing resource allocation from " + 
                           sender + ": " + content);

        // Extract resource information
        String[] parts = content.substring("RESOURCE_ALLOCATION:".length()).split(",");
        if (parts.length >= 2) {
            String resourceType = parts[0];
            int quantity = Integer.parseInt(parts[1]);

            // Update our resources
            AgentCentreCommande agent = (AgentCentreCommande)myAgent;
            int current = agent.getResourceQuantity(resourceType);
            agent.updateResource(resourceType, current + quantity);

            System.out.println(myAgent.getLocalName() + ": Received " + quantity + " " + 
                               resourceType + " from " + sender);

            // Check if we can fulfill any pending requests
            checkPendingRequests(resourceType);
        }
    }

    /**
     * Check if we can fulfill any pending requests
     * @param resourceType The type of resource that was received
     */
    private void checkPendingRequests(String resourceType) {
        AgentCentreCommande agent = (AgentCentreCommande)myAgent;
        int available = agent.getResourceQuantity(resourceType);

        // Check each pending request
        for (Map.Entry<String, Integer> entry : new HashMap<>(pendingRequests).entrySet()) {
            String key = entry.getKey();
            int quantity = entry.getValue();

            if (key.endsWith(":" + resourceType) && available >= quantity) {
                // We can fulfill this request
                String requesterName = key.substring(0, key.lastIndexOf(":"));

                // Allocate the resources
                agent.updateResource(resourceType, available - quantity);

                // Send confirmation
                AID requester = new AID(requesterName, AID.ISLOCALNAME);
                sendResourceConfirmation(requester, resourceType, quantity);

                // Remove from pending requests
                pendingRequests.remove(key);

                System.out.println(myAgent.getLocalName() + ": Fulfilled pending request for " + 
                                   quantity + " " + resourceType + " to " + requesterName);

                // Update available resources
                available -= quantity;
            }
        }
    }

    /**
     * Check and update resources
     */
    private void checkAndUpdateResources() {
        // Removed checking and updating resources logging to reduce console clutter

        // Update agent status
        ((AgentCentreCommande)myAgent).updateStatus("CHECKING_RESOURCES");

        // Simulate resource consumption
        simulateResourceConsumption();

        // Check if any resources are below threshold
        checkResourceThresholds();
    }

    /**
     * Simulate resource consumption
     */
    private void simulateResourceConsumption() {
        AgentCentreCommande agent = (AgentCentreCommande)myAgent;

        // Calculate consumption since last check
        double minutesSinceLastCheck = RESOURCE_CHECK_INTERVAL / 60000.0; // Convert to minutes

        // Update each resource
        for (Map.Entry<String, Integer> entry : CONSUMPTION_RATES.entrySet()) {
            String resourceType = entry.getKey();
            int ratePerMinute = entry.getValue();

            // Calculate consumption
            int consumption = (int)(ratePerMinute * minutesSinceLastCheck);

            // Only consume if there's active consumption
            if (consumption > 0) {
                int current = agent.getResourceQuantity(resourceType);
                int newAmount = Math.max(0, current - consumption);

                // Update the resource
                agent.updateResource(resourceType, newAmount);

                // Removed consumed resources logging to reduce console clutter
            }
        }
    }

    /**
     * Check if any resources are below threshold
     */
    private void checkResourceThresholds() {
        AgentCentreCommande agent = (AgentCentreCommande)myAgent;

        // Check each resource
        checkResourceThreshold(agent, RESOURCE_FIRE_TRUCKS, THRESHOLD_FIRE_TRUCKS);
        checkResourceThreshold(agent, RESOURCE_AMBULANCES, THRESHOLD_AMBULANCES);
        checkResourceThreshold(agent, RESOURCE_POLICE_CARS, THRESHOLD_POLICE_CARS);
        checkResourceThreshold(agent, RESOURCE_WATER_SUPPLY, THRESHOLD_WATER_SUPPLY);
        checkResourceThreshold(agent, RESOURCE_MEDICAL_KITS, THRESHOLD_MEDICAL_KITS);
        checkResourceThreshold(agent, RESOURCE_BARRICADES, THRESHOLD_BARRICADES);
    }

    /**
     * Check if a specific resource is below threshold
     * @param agent The command center agent
     * @param resourceType The type of resource to check
     * @param threshold The threshold for this resource
     */
    private void checkResourceThreshold(AgentCentreCommande agent, String resourceType, int threshold) {
        int current = agent.getResourceQuantity(resourceType);

        if (current < threshold) {
            // Resource is below threshold, request more
            int requestAmount = threshold * 2 - current; // Request enough to get to twice the threshold
            requestAdditionalResources(resourceType, requestAmount);

            // Removed resource below threshold logging to reduce console clutter
        }
    }

    /**
     * Request additional resources from logistics
     * @param resourceType The type of resource to request
     * @param quantity The quantity to request
     */
    private void requestAdditionalResources(String resourceType, int quantity) {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(new AID("Logistique", AID.ISLOCALNAME));

        request.setContent("RESOURCE_REQUEST:" + resourceType + "," + quantity);
        myAgent.send(request);
    }

    /**
     * Send a resource confirmation message
     * @param receiver The receiver of the message
     * @param resourceType The type of resource
     * @param quantity The quantity of resource
     */
    private void sendResourceConfirmation(AID receiver, String resourceType, int quantity) {
        ACLMessage confirm = new ACLMessage(ACLMessage.INFORM);
        confirm.addReceiver(receiver);

        confirm.setContent("RESOURCE_CONFIRMATION:" + resourceType + "," + quantity);
        myAgent.send(confirm);
    }

    /**
     * Send a resource pending message
     * @param receiver The receiver of the message
     * @param resourceType The type of resource
     * @param quantity The quantity of resource
     */
    private void sendResourcePending(AID receiver, String resourceType, int quantity) {
        ACLMessage pending = new ACLMessage(ACLMessage.INFORM);
        pending.addReceiver(receiver);

        pending.setContent("RESOURCE_PENDING:" + resourceType + "," + quantity);
        myAgent.send(pending);
    }
}
