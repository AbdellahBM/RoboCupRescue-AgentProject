package com.jade.RoboCupRescueProject.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Behaviour for locating victims using robot scanning capabilities.
 * This behavior is specifically for the robot agent to scan ruins for victims.
 */
public class LocaliserVictimesRobotBehaviour extends Behaviour {
    
    public LocaliserVictimesRobotBehaviour(Agent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        // Implementation for locating victims using robot scanning
        System.out.println(myAgent.getLocalName() + ": Scanning ruins for victims...");
    }
    
    @Override
    public boolean done() {
        // This behavior never ends
        return false;
    }
}