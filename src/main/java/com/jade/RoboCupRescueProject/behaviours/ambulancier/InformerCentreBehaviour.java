package com.jade.RoboCupRescueProject.behaviours.ambulancier;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class InformerCentreBehaviour extends OneShotBehaviour {
    public InformerCentreBehaviour(Agent a) { super(a); }
    @Override
    public void action() {
        // TODO: send victim status to Centre de Commande
    }
}