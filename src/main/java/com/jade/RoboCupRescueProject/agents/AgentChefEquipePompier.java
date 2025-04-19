package com.jade.RoboCupRescueProject.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import com.jade.RoboCupRescueProject.behaviours.chefequipepompier.*;

public class AgentChefEquipePompier extends Agent {
    // Behaviors
    private RecevoirOrdresCentreBehaviour recevoirOrdresBehaviour;
    private AssignerTachesPompiersBehaviour assignerTachesBehaviour;
    private RecupererRapportsPompierBehaviour recupererRapportsBehaviour;
    private FaireSyntheseAuCentreBehaviour faireSyntheseBehaviour;

    @Override
    protected void setup() {
        System.out.println("Agent Chef Equipe Pompier " + getLocalName() + " starting...");

        try {
            // Register the agent in the yellow pages (DF)
            registerService();

            // Initialize behaviors in the correct order
            initializeBehaviors();

            System.out.println("Agent Chef Equipe Pompier " + getLocalName() + " ready.");
        } catch (Exception e) {
            System.err.println("Error during initialization: " + e.getMessage());
            e.printStackTrace();
            doDelete();
        }
    }

    private void registerService() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("chef-equipe-pompier");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println(getLocalName() + ": Registered with Directory Facilitator");
        } catch (FIPAException fe) {
            throw new RuntimeException("Failed to register with DF: " + fe.getMessage());
        }
    }

    private void initializeBehaviors() {
        // Create behaviors in the correct order due to dependencies
        recupererRapportsBehaviour = new RecupererRapportsPompierBehaviour(this);
        assignerTachesBehaviour = new AssignerTachesPompiersBehaviour(this);
        recevoirOrdresBehaviour = new RecevoirOrdresCentreBehaviour(this);
        faireSyntheseBehaviour = new FaireSyntheseAuCentreBehaviour(this, recupererRapportsBehaviour);

        // Add behaviors to the agent
        addBehaviour(recevoirOrdresBehaviour);
        addBehaviour(assignerTachesBehaviour);
        addBehaviour(recupererRapportsBehaviour);
        addBehaviour(faireSyntheseBehaviour);
    }

    @Override
    protected void takeDown() {
        // Deregister from the DF
        try {
            DFService.deregister(this);
            System.out.println(getLocalName() + ": Deregistered from Directory Facilitator");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.println("Agent Chef Equipe Pompier " + getLocalName() + " terminating.");
    }
}