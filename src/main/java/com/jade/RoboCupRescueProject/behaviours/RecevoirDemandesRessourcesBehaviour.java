package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for receiving resource requests.
 */
public class RecevoirDemandesRessourcesBehaviour extends Behaviour {
    
    public RecevoirDemandesRessourcesBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for receiving resource requests
        System.out.println(myAgent.getLocalName() + ": Receiving resource requests...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}