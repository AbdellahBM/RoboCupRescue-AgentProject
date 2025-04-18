package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for locating victims.
 */
public class LocaliserVictimesBehaviour extends Behaviour {
    
    public LocaliserVictimesBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for locating victims
        System.out.println(myAgent.getLocalName() + ": Locating victims...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}