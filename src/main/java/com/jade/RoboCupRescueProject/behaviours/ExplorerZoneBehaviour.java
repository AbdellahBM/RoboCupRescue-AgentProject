package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for exploring an area.
 */
public class ExplorerZoneBehaviour extends Behaviour {
    
    public ExplorerZoneBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for exploring an area
        System.out.println(myAgent.getLocalName() + ": Exploring area...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}