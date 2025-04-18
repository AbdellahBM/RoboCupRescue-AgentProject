package com.jade.RoboCupRescueProject.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import com.jade.RoboCupRescueProject.behaviours.LocaliserVictimesBehaviour;
import com.jade.RoboCupRescueProject.behaviours.SoinsPremiersBehaviour;
import com.jade.RoboCupRescueProject.behaviours.TransporterVictimesBehaviour;
import com.jade.RoboCupRescueProject.behaviours.InformerCentreBehaviour;

public class AgentAmbulancier extends Agent {
    @Override
    protected void setup() {
        registerService("ambulancier");
        addBehaviour(new LocaliserVictimesBehaviour(this));
        addBehaviour(new SoinsPremiersBehaviour(this));
        addBehaviour(new TransporterVictimesBehaviour(this));
        addBehaviour(new InformerCentreBehaviour(this));
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