package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for informing the command center.
 */
public class InformerCentreBehaviour extends Behaviour {
    
    public InformerCentreBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for informing the command center
        System.out.println(myAgent.getLocalName() + ": Informing the command center...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}