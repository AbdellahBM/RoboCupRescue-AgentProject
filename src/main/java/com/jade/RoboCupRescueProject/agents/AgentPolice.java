package com.jade.RoboCupRescueProject.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.behaviours.TickerBehaviour;
import com.jade.RoboCupRescueProject.behaviours.police.GererCirculationBehaviour;
import com.jade.RoboCupRescueProject.behaviours.police.FournirItineraireBehaviour;
import com.jade.RoboCupRescueProject.behaviours.police.BloquerZoneDangerBehaviour;
import com.jade.RoboCupRescueProject.behaviours.police.SignalerChangementsBehaviour;
import java.util.HashMap;
import java.util.Map;

/**
 * Agent Police (Police Agent)
 * 
 * Responsibilities:
 * 1. Manage traffic and secure transport routes (highways, roads, bridges...)
 * 2. Establish security perimeters around dangerous areas (fire, collapse, explosion)
 * 3. Maintain public order: manage the influx of people, prevent intrusions into disaster areas
 * 
 * This agent uses four behaviors to fulfill its responsibilities:
 * - GererCirculationBehaviour: Manages traffic and secures transport routes
 * - BloquerZoneDangerBehaviour: Establishes security perimeters around dangerous areas
 * - FournirItineraireBehaviour: Provides route assistance to emergency vehicles
 * - SignalerChangementsBehaviour: Alerts other agents about obstacles or imminent risks
 */
public class AgentPolice extends Agent {
    // Agent state
    private String currentStatus = "READY";
    private int securityPerimetersEstablished = 0;
    private int routeAssistanceProvided = 0;
    private int alertsSent = 0;

    // Road status map (road ID -> status)
    private Map<String, String> roadStatus = new HashMap<>();

    // Security zones map (zone ID -> status)
    private Map<String, String> securityZones = new HashMap<>();

    @Override
    protected void setup() {
        System.out.println("Agent Police " + getLocalName() + " starting...");

        // Register the agent in the yellow pages (DF)
        registerService("police");

        // Add the agent's behaviors
        addBehaviour(new GererCirculationBehaviour(this));
        addBehaviour(new FournirItineraireBehaviour(this));
        addBehaviour(new BloquerZoneDangerBehaviour(this));
        addBehaviour(new SignalerChangementsBehaviour(this));

        // Add a status update behavior
        addBehaviour(new TickerBehaviour(this, 10000) { // Every 10 seconds
            @Override
            protected void onTick() {
                System.out.println("Agent Police " + myAgent.getLocalName() + 
                                   " status: " + currentStatus + 
                                   ", Security perimeters: " + securityPerimetersEstablished + 
                                   ", Route assistance: " + routeAssistanceProvided +
                                   ", Alerts sent: " + alertsSent);
            }
        });

        System.out.println("Agent Police " + getLocalName() + " ready.");
    }

    /**
     * Register the agent's services in the Directory Facilitator (DF)
     * @param type The type of service to register
     */
    private void registerService(String type) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try { 
            DFService.register(this, dfd); 
            System.out.println("Agent Police " + getLocalName() + " registered with DF");
        } catch (FIPAException e) { 
            System.err.println("Error registering Agent Police " + getLocalName() + " with DF: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    /**
     * Update the agent's status
     * @param status The new status
     */
    public void updateStatus(String status) {
        this.currentStatus = status;
    }

    /**
     * Update the status of a road
     * @param roadId The ID of the road
     * @param status The new status (OPEN, CLOSED, RESTRICTED, etc.)
     */
    public void updateRoadStatus(String roadId, String status) {
        roadStatus.put(roadId, status);
    }

    /**
     * Get the status of a road
     * @param roadId The ID of the road
     * @return The status of the road, or null if not found
     */
    public String getRoadStatus(String roadId) {
        return roadStatus.getOrDefault(roadId, null);
    }

    /**
     * Add a security zone
     * @param zoneId The ID of the zone
     * @param status The status of the zone (DANGER, RESTRICTED, EVACUATED, etc.)
     */
    public void addSecurityZone(String zoneId, String status) {
        securityZones.put(zoneId, status);
        securityPerimetersEstablished++;
    }

    /**
     * Get the status of a security zone
     * @param zoneId The ID of the zone
     * @return The status of the zone, or null if not found
     */
    public String getSecurityZoneStatus(String zoneId) {
        return securityZones.getOrDefault(zoneId, null);
    }

    /**
     * Increment the count of route assistance provided
     */
    public void incrementRouteAssistance() {
        this.routeAssistanceProvided++;
    }

    /**
     * Increment the count of alerts sent
     */
    public void incrementAlertsSent() {
        this.alertsSent++;
    }

    /**
     * Get the number of security perimeters established
     * @return The number of security perimeters established
     */
    public int getSecurityPerimetersCount() {
        return this.securityPerimetersEstablished;
    }

    /**
     * Get the number of route assistance provided
     * @return The number of route assistance provided
     */
    public int getRouteAssistanceCount() {
        return this.routeAssistanceProvided;
    }

    /**
     * Get the number of alerts sent
     * @return The number of alerts sent
     */
    public int getAlertsSentCount() {
        return this.alertsSent;
    }

    @Override
    protected void takeDown() {
        // Deregister from the DF
        try {
            DFService.deregister(this);
            System.out.println("Agent Police " + getLocalName() + " deregistered from DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        System.out.println("Agent Police " + getLocalName() + " terminating.");
    }
}
