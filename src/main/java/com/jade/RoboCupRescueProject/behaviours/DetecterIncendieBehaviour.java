package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for detecting fires.
 */
public class DetecterIncendieBehaviour extends Behaviour {
    
    public DetecterIncendieBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for detecting fires
        System.out.println(myAgent.getLocalName() + ": Detecting fires...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}