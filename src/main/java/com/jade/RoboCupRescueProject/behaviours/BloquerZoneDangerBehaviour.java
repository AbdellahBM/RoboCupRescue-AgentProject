package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for blocking dangerous areas.
 */
public class BloquerZoneDangerBehaviour extends Behaviour {
    
    public BloquerZoneDangerBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for blocking dangerous areas
        System.out.println(myAgent.getLocalName() + ": Blocking dangerous areas...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}