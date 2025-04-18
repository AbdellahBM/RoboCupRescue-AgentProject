package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for providing route information.
 */
public class FournirItineraireBehaviour extends Behaviour {
    
    public FournirItineraireBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for providing route information
        System.out.println(myAgent.getLocalName() + ": Providing route information...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}