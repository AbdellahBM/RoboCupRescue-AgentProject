package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for reporting changes in the environment.
 */
public class SignalerChangementsBehaviour extends Behaviour {
    
    public SignalerChangementsBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for reporting changes in the environment
        System.out.println(myAgent.getLocalName() + ": Reporting changes in the environment...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}