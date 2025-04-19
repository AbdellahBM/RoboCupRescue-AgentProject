package com.jade.RoboCupRescueProject.behaviours.chefequipepompier;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class FaireSyntheseAuCentreBehaviour extends OneShotBehaviour {
    private final RecupererRapportsPompierBehaviour reportsBehaviour;

    public FaireSyntheseAuCentreBehaviour(Agent a, RecupererRapportsPompierBehaviour reportsBehaviour) {
        super(a);
        this.reportsBehaviour = reportsBehaviour;
        System.out.println(a.getLocalName() + ": Starting synthesis behavior");
    }
    @Override
    public void action() {
        // TODO: send summary report to Centre de Commande
        MessageTemplate mt = MessageTemplate.MatchContent("SYNTHESIZE_REPORTS");
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            // Create synthesis from pending reports
            String synthesis = createSynthesis();

            // Send synthesis to command center
            sendSynthesisToCenter(synthesis);
        } else {
            block();
        }
    }

    private String createSynthesis() {
        StringBuilder synthesis = new StringBuilder();
        synthesis.append("SYNTHESIS_REPORT:\n");

        for (String report : reportsBehaviour.getPendingReports()) {
            synthesis.append("- ").append(report).append("\n");
        }

        System.out.println(myAgent.getLocalName() + ": Created synthesis report");
        return synthesis.toString();
    }

    private void sendSynthesisToCenter(String synthesis) {
        ACLMessage synthMsg = new ACLMessage(ACLMessage.INFORM);
        // Add command center as receiver
        synthMsg.setContent(synthesis);
        myAgent.send(synthMsg);

        System.out.println(myAgent.getLocalName() + ": Sent synthesis to command center");
    }
}