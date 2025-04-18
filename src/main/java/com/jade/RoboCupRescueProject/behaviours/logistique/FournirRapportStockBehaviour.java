package com.jade.RoboCupRescueProject.behaviours.logistique;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class FournirRapportStockBehaviour extends CyclicBehaviour {
    public FournirRapportStockBehaviour(Agent a) { super(a); }
    @Override
    public void action() {
        // TODO: send periodic stock level INFORM
    }
}