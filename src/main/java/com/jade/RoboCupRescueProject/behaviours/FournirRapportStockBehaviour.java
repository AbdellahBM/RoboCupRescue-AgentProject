package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for providing inventory reports.
 */
public class FournirRapportStockBehaviour extends Behaviour {
    
    public FournirRapportStockBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for providing inventory reports
        System.out.println(myAgent.getLocalName() + ": Providing inventory reports...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}