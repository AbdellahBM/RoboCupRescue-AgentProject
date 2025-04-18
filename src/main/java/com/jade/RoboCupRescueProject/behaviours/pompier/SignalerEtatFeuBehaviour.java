package com.jade.RoboCupRescueProject.behaviours.pompier;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class SignalerEtatFeuBehaviour extends OneShotBehaviour {
    public SignalerEtatFeuBehaviour(Agent a) { super(a); }
    @Override
    public void action() {
        // TODO: send fire status update to Chef d'Équipe or Centre
    }
}