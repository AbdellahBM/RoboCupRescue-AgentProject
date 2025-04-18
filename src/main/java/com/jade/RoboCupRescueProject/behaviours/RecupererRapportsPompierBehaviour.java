package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for retrieving reports from firefighters.
 */
public class RecupererRapportsPompierBehaviour extends Behaviour {
    
    public RecupererRapportsPompierBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for retrieving reports from firefighters
        System.out.println(myAgent.getLocalName() + ": Retrieving reports from firefighters...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}