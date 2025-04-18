package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for providing first aid.
 */
public class SoinsPremiersBehaviour extends Behaviour {
    
    public SoinsPremiersBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for providing first aid
        System.out.println(myAgent.getLocalName() + ": Providing first aid...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}