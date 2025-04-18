package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for managing global resources in the rescue system.
 */
public class GererRessourcesGlobalesBehaviour extends Behaviour {
    
    public GererRessourcesGlobalesBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for managing global resources
        System.out.println(myAgent.getLocalName() + ": Managing global resources...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}