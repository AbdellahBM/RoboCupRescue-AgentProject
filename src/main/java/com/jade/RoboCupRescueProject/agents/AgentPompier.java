package com.jade.RoboCupRescueProject.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import com.jade.RoboCupRescueProject.behaviours.DetecterIncendieBehaviour;
import com.jade.RoboCupRescueProject.behaviours.EteindreIncendieBehaviour;
import com.jade.RoboCupRescueProject.behaviours.SignalerEtatFeuBehaviour;
import com.jade.RoboCupRescueProject.behaviours.DemanderAssistanceLogistiqueBehaviour;

public class AgentPompier extends Agent {
    @Override
    protected void setup() {
        registerService("pompier");
        addBehaviour(new DetecterIncendieBehaviour(this));
        addBehaviour(new EteindreIncendieBehaviour(this));
        addBehaviour(new SignalerEtatFeuBehaviour(this));
        addBehaviour(new DemanderAssistanceLogistiqueBehaviour(this));
    }

    private void registerService(String type) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try { DFService.register(this, dfd); } catch (Exception e) { e.printStackTrace(); }
    }
}