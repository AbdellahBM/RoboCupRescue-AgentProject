package com.jade.RoboCupRescueProject.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import com.jade.RoboCupRescueProject.behaviours.logistique.RecevoirDemandesRessourcesBehaviour;
import com.jade.RoboCupRescueProject.behaviours.logistique.AcheminerApprovisionnementBehaviour;
import com.jade.RoboCupRescueProject.behaviours.logistique.FournirRapportStockBehaviour;

public class AgentLogistique extends Agent {
    @Override
    protected void setup() {
        registerService("logistique");
        addBehaviour(new RecevoirDemandesRessourcesBehaviour(this));
        addBehaviour(new AcheminerApprovisionnementBehaviour(this));
        addBehaviour(new FournirRapportStockBehaviour(this));
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
