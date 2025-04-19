package com.jade.RoboCupRescueProject.test;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class TestScenarios {
    public static void simulateFireScenario(Agent sender, AID receiver) {
        // Create a fire detection message
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(receiver);
        msg.setContent("FIRE_DETECTED:Building-A,intensity=80");
        sender.send(msg);
    }

    public static void simulateVictimScenario(Agent sender, AID receiver) {
        // Create a victim detection message
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(receiver);
        msg.setContent("VICTIM_DETECTED:Floor-2,condition=critical");
        sender.send(msg);
    }
}