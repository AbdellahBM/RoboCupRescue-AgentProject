package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for transporting victims.
 */
public class TransporterVictimesBehaviour extends Behaviour {
    
    public TransporterVictimesBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for transporting victims
        System.out.println(myAgent.getLocalName() + ": Transporting victims...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}