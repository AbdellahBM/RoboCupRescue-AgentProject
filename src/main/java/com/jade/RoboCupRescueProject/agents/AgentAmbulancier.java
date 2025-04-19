package com.jade.RoboCupRescueProject.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.behaviours.TickerBehaviour;
import com.jade.RoboCupRescueProject.behaviours.ambulancier.LocaliserVictimesBehaviour;
import com.jade.RoboCupRescueProject.behaviours.ambulancier.SoinsPremiersBehaviour;
import com.jade.RoboCupRescueProject.behaviours.ambulancier.TransporterVictimesBehaviour;
import com.jade.RoboCupRescueProject.behaviours.ambulancier.InformerCentreBehaviour;

/**
 * Agent Ambulancier (Ambulance Agent)
 * 
 * Responsibilities:
 * 1. Locate and rescue victims in the disaster area
 * 2. Provide first aid and stabilize the condition of the injured
 * 3. Coordinate the evacuation of victims to hospitals or medical triage points
 * 
 * This agent uses four behaviors to fulfill its responsibilities:
 * - LocaliserVictimesBehaviour: Searches for victims in the disaster area
 * - SoinsPremiersBehaviour: Provides first aid and performs triage
 * - TransporterVictimesBehaviour: Transports victims to medical facilities
 * - InformerCentreBehaviour: Reports information to the command center
 */
public class AgentAmbulancier extends Agent {
    // Agent state
    private int victimsFound = 0;
    private int victimsTransported = 0;
    private String currentStatus = "READY";

    @Override
    protected void setup() {
        System.out.println("Agent Ambulancier " + getLocalName() + " starting...");

        // Register the agent in the yellow pages (DF)
        registerService("ambulancier");

        // Add the agent's behaviors
        addBehaviour(new LocaliserVictimesBehaviour(this));
        addBehaviour(new SoinsPremiersBehaviour(this));
        addBehaviour(new TransporterVictimesBehaviour(this));
        addBehaviour(new InformerCentreBehaviour(this));

        // Add a status update behavior
        addBehaviour(new TickerBehaviour(this, 10000) { // Every 10 seconds
            @Override
            protected void onTick() {
                System.out.println("Agent Ambulancier " + myAgent.getLocalName() + 
                                   " status: " + currentStatus + 
                                   ", Victims found: " + victimsFound + 
                                   ", Victims transported: " + victimsTransported);
            }
        });

        System.out.println("Agent Ambulancier " + getLocalName() + " ready.");
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
            System.out.println("Agent Ambulancier " + getLocalName() + " registered with DF");
        } catch (FIPAException e) { 
            System.err.println("Error registering Agent Ambulancier " + getLocalName() + " with DF: " + e.getMessage());
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
     * Increment the count of victims found
     */
    public void incrementVictimsFound() {
        this.victimsFound++;
    }

    /**
     * Increment the count of victims transported
     */
    public void incrementVictimsTransported() {
        this.victimsTransported++;
    }

    @Override
    protected void takeDown() {
        // Deregister from the DF
        try {
            DFService.deregister(this);
            System.out.println("Agent Ambulancier " + getLocalName() + " deregistered from DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        System.out.println("Agent Ambulancier " + getLocalName() + " terminating.");
    }
}
