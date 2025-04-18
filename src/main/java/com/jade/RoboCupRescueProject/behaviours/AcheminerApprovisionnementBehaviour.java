package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for routing supplies.
 */
public class AcheminerApprovisionnementBehaviour extends Behaviour {
    
    public AcheminerApprovisionnementBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for routing supplies
        System.out.println(myAgent.getLocalName() + ": Routing supplies...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}