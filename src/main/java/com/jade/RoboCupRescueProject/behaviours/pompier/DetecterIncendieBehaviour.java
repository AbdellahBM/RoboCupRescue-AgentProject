package com.jade.RoboCupRescueProject.behaviours.pompier;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class DetecterIncendieBehaviour extends CyclicBehaviour {
    public DetecterIncendieBehaviour(Agent a) { super(a); }
    @Override
    public void action() {
        // TODO: detect fire events and send INFORM
    }
}