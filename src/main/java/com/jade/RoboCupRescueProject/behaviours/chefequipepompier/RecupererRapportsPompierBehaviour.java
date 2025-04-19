package com.jade.RoboCupRescueProject.behaviours.chefequipepompier;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class RecupererRapportsPompierBehaviour extends CyclicBehaviour {
    private List<String> pendingReports = new ArrayList<>();

    public RecupererRapportsPompierBehaviour(Agent a) {
        super(a);
        System.out.println(a.getLocalName() + ": Starting to collect firefighter reports");
    }
    @Override
    public void action() {
        // TODO: collect INFORM messages from pompiers
        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchContent("REPORT:.*")
        );

        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            String content = msg.getContent();
            String report = content.substring("REPORT:".length());

            // Store the report
            processReport(report, msg.getSender().getLocalName());

            // Acknowledge receipt
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("REPORT_RECEIVED");
            myAgent.send(reply);

            // Trigger synthesis if we have enough reports
            if (shouldTriggerSynthesis()) {
                triggerSynthesis();
            }
        } else {
            block();
        }
    }

    private void processReport(String report, String firefighterId) {
        String formattedReport = String.format("[%s] %s", firefighterId, report);
        pendingReports.add(formattedReport);
        System.out.println(myAgent.getLocalName() + ": Received report from " + firefighterId);
    }

    private boolean shouldTriggerSynthesis() {
        // Trigger synthesis when we have 3 or more reports
        return pendingReports.size() >= 3;
    }

    private void triggerSynthesis() {
        // Send message to FaireSyntheseAuCentreBehaviour
        ACLMessage synthesisMsg = new ACLMessage(ACLMessage.REQUEST);
        synthesisMsg.setContent("SYNTHESIZE_REPORTS");
        myAgent.send(synthesisMsg);

        // Clear pending reports after triggering synthesis
        pendingReports.clear();
    }

    public List<String> getPendingReports() {
        return new ArrayList<>(pendingReports);
    }

}