package com.jade.RoboCupRescueProject.behaviours.pompier;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class DemanderAssistanceLogistiqueBehaviour extends OneShotBehaviour {
    public DemanderAssistanceLogistiqueBehaviour(Agent a) { super(a); }
    @Override
    public void action() {
        // TODO: request additional resources from AgentLogistique
    }
}