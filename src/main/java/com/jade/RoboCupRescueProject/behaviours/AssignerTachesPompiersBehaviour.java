package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for assigning tasks to firefighters.
 */
public class AssignerTachesPompiersBehaviour extends Behaviour {
    
    public AssignerTachesPompiersBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for assigning tasks to firefighters
        System.out.println(myAgent.getLocalName() + ": Assigning tasks to firefighters...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}