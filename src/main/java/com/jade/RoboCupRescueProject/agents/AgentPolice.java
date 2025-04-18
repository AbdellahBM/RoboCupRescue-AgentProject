package com.jade.RoboCupRescueProject.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import com.jade.RoboCupRescueProject.behaviours.police.GererCirculationBehaviour;
import com.jade.RoboCupRescueProject.behaviours.police.FournirItineraireBehaviour;
import com.jade.RoboCupRescueProject.behaviours.police.BloquerZoneDangerBehaviour;
import com.jade.RoboCupRescueProject.behaviours.police.SignalerChangementsBehaviour;

public class AgentPolice extends Agent {
    @Override
    protected void setup() {
        registerService("police");
        addBehaviour(new GererCirculationBehaviour(this));
        addBehaviour(new FournirItineraireBehaviour(this));
        addBehaviour(new BloquerZoneDangerBehaviour(this));
        addBehaviour(new SignalerChangementsBehaviour(this));
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
