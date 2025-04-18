package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for synthesizing information for the command center.
 */
public class FaireSyntheseAuCentreBehaviour extends Behaviour {
    
    public FaireSyntheseAuCentreBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for synthesizing information for the command center
        System.out.println(myAgent.getLocalName() + ": Synthesizing information for the command center...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}