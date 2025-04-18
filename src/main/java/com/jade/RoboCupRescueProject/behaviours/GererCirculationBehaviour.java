package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for managing traffic.
 */
public class GererCirculationBehaviour extends Behaviour {
    
    public GererCirculationBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for managing traffic
        System.out.println(myAgent.getLocalName() + ": Managing traffic...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}