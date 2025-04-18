package com.jade.RoboCupRescueProject.behaviours.robot;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class DetecterFoyersIncendieBehaviour extends CyclicBehaviour {
    public DetecterFoyersIncendieBehaviour(Agent a) { super(a); }
    @Override
    public void action() {
        // TODO: detect heat signatures and send INFORM
    }
}