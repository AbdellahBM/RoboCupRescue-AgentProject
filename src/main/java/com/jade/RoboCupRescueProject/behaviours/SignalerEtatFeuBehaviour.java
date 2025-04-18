package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for reporting fire status.
 */
public class SignalerEtatFeuBehaviour extends Behaviour {
    
    public SignalerEtatFeuBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for reporting fire status
        System.out.println(myAgent.getLocalName() + ": Reporting fire status...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}