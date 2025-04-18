package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for receiving orders from the command center.
 */
public class RecevoirOrdresCentreBehaviour extends Behaviour {
    
    public RecevoirOrdresCentreBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for receiving orders from the command center
        System.out.println(myAgent.getLocalName() + ": Receiving orders from command center...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}