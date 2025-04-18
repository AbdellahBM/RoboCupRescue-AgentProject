package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for requesting logistical assistance.
 */
public class DemanderAssistanceLogistiqueBehaviour extends Behaviour {
    
    public DemanderAssistanceLogistiqueBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for requesting logistical assistance
        System.out.println(myAgent.getLocalName() + ": Requesting logistical assistance...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}