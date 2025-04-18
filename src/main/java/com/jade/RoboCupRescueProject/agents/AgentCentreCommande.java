package com.jade.RoboCupRescueProject.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import com.jade.RoboCupRescueProject.behaviours.centrecommande.CollecterInfosBehaviour;
import com.jade.RoboCupRescueProject.behaviours.centrecommande.PlanifierInterventionBehaviour;
import com.jade.RoboCupRescueProject.behaviours.centrecommande.DispatcherMissionsBehaviour;
import com.jade.RoboCupRescueProject.behaviours.centrecommande.GererRessourcesGlobalesBehaviour;

public class AgentCentreCommande extends Agent {
    @Override
    protected void setup() {
        registerService("centre-commande");
        addBehaviour(new CollecterInfosBehaviour(this));
        addBehaviour(new PlanifierInterventionBehaviour(this));
        addBehaviour(new DispatcherMissionsBehaviour(this));
        addBehaviour(new GererRessourcesGlobalesBehaviour(this));
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

    @Override
    protected void takeDown() {
        // Cleanup if needed
    }
}
