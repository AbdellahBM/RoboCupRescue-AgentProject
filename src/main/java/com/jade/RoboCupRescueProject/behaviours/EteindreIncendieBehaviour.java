package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for extinguishing fires.
 */
public class EteindreIncendieBehaviour extends Behaviour {
    
    public EteindreIncendieBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for extinguishing fires
        System.out.println(myAgent.getLocalName() + ": Extinguishing fires...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}