package com.jade.RoboCupRescueProject.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import com.jade.RoboCupRescueProject.behaviours.RecevoirOrdresCentreBehaviour;
import com.jade.RoboCupRescueProject.behaviours.AssignerTachesPompiersBehaviour;
import com.jade.RoboCupRescueProject.behaviours.RecupererRapportsPompierBehaviour;
import com.jade.RoboCupRescueProject.behaviours.FaireSyntheseAuCentreBehaviour;

public class AgentChefEquipePompier extends Agent {
    @Override
    protected void setup() {
        registerService("chef-equipe-pompier");
        addBehaviour(new RecevoirOrdresCentreBehaviour(this));
        addBehaviour(new AssignerTachesPompiersBehaviour(this));
        addBehaviour(new RecupererRapportsPompierBehaviour(this));
        addBehaviour(new FaireSyntheseAuCentreBehaviour(this));
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