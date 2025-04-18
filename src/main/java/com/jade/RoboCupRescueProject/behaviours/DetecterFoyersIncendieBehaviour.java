package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for detecting fire sources.
 */
public class DetecterFoyersIncendieBehaviour extends Behaviour {
    
    public DetecterFoyersIncendieBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for detecting fire sources
        System.out.println(myAgent.getLocalName() + ": Detecting fire sources...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}