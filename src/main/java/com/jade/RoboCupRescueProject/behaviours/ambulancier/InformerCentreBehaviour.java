package com.jade.RoboCupRescueProject.behaviours.ambulancier;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jade.RoboCupRescueProject.agents.AgentAmbulancier;

/**
 * Behavior responsible for reporting information about victims to the command center.
 * This behavior collects data about victims, their status, and location, and transmits
 * this information to the command center for coordination purposes.
 */
public class InformerCentreBehaviour extends CyclicBehaviour {
    private Map<String, String> victimReports = new HashMap<>(); // Map of victim location to triage status
    private List<String> transportedVictims = new ArrayList<>(); // List of transported victims
    private int totalVictimsFound = 0;
    private int totalVictimsTransported = 0;

    // Counters for triage categories
    private int countLeger = 0;
    private int countUrgent = 0;
    private int countCritique = 0;

    public InformerCentreBehaviour(Agent a) { 
        super(a); 
        System.out.println(a.getLocalName() + ": Starting inform center behavior");
    }

    @Override
    public void action() {
        // Listen for requests to inform the command center
        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
            MessageTemplate.MatchContent("INFORM_CENTRE:.*")
        );
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            // Process request to inform command center
            String content = msg.getContent();
            String[] parts = content.substring("INFORM_CENTRE:".length()).split(",");

            if (parts.length >= 2) {
                String locationStr = parts[0];
                String triageCategory = parts[1];

                // Update victim reports
                victimReports.put(locationStr, triageCategory);
                totalVictimsFound++;

                // Update triage category counters
                updateTriageCounters(triageCategory);

                // Send report to command center
                sendVictimReport(locationStr, triageCategory);
            }
        } else {
            // Check for other types of messages
            MessageTemplate transportMt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchContent("VICTIM_TRANSPORTED:.*")
            );
            ACLMessage transportMsg = myAgent.receive(transportMt);

            if (transportMsg != null) {
                // Process transport completion message
                String content = transportMsg.getContent();
                String[] parts = content.substring("VICTIM_TRANSPORTED:".length()).split(",");

                if (parts.length >= 3) {
                    String locationStr = parts[0];
                    String facility = parts[1];
                    String triageCategory = parts[2];

                    // Update transported victims list
                    transportedVictims.add(locationStr);
                    totalVictimsTransported++;

                    // Send transport completion report to command center
                    sendTransportReport(locationStr, facility, triageCategory);
                }
            } else {
                // Periodically send summary reports to command center
                if (Math.random() < 0.01) { // 1% chance each cycle (to avoid flooding)
                    sendSummaryReport();
                }

                block(); // Block until a message is received
            }
        }
    }

    /**
     * Update the counters for each triage category
     * @param triageCategory The triage category to increment
     */
    private void updateTriageCounters(String triageCategory) {
        if (triageCategory.equals(SoinsPremiersBehaviour.TRIAGE_LEGER)) {
            countLeger++;
        } else if (triageCategory.equals(SoinsPremiersBehaviour.TRIAGE_URGENT)) {
            countUrgent++;
        } else if (triageCategory.equals(SoinsPremiersBehaviour.TRIAGE_CRITIQUE)) {
            countCritique++;
        }
    }

    /**
     * Send a report about a victim to the command center
     * @param locationStr The location of the victim
     * @param triageCategory The triage category of the victim
     */
    private void sendVictimReport(String locationStr, String triageCategory) {
        System.out.println(myAgent.getLocalName() + ": Sending victim report to command center");

        // Update agent status
        ((AgentAmbulancier)myAgent).updateStatus("REPORTING_VICTIM:" + triageCategory);

        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
        inform.addReceiver(new AID("CentreCommande", AID.ISLOCALNAME));
        inform.setContent("VICTIM_REPORT:" + locationStr + "," + triageCategory);
        myAgent.send(inform);

        System.out.println(myAgent.getLocalName() + ": Victim report sent to command center");
    }

    /**
     * Send a report about a completed transport to the command center
     * @param locationStr The original location of the victim
     * @param facility The destination facility
     * @param triageCategory The triage category of the victim
     */
    private void sendTransportReport(String locationStr, String facility, String triageCategory) {
        System.out.println(myAgent.getLocalName() + ": Sending transport completion report to command center");

        // Update agent status
        ((AgentAmbulancier)myAgent).updateStatus("REPORTING_TRANSPORT:" + triageCategory);

        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
        inform.addReceiver(new AID("CentreCommande", AID.ISLOCALNAME));
        inform.setContent("TRANSPORT_REPORT:" + locationStr + "," + facility + "," + triageCategory);
        myAgent.send(inform);

        System.out.println(myAgent.getLocalName() + ": Transport report sent to command center");
    }

    /**
     * Send a summary report to the command center
     */
    private void sendSummaryReport() {
        System.out.println(myAgent.getLocalName() + ": Sending summary report to command center");

        // Update agent status
        ((AgentAmbulancier)myAgent).updateStatus("SENDING_SUMMARY_REPORT");

        // Create summary message
        StringBuilder summary = new StringBuilder("SUMMARY_REPORT:");
        summary.append("total_found=").append(totalVictimsFound).append(",");
        summary.append("total_transported=").append(totalVictimsTransported).append(",");
        summary.append("leger=").append(countLeger).append(",");
        summary.append("urgent=").append(countUrgent).append(",");
        summary.append("critique=").append(countCritique);

        // Send summary to command center
        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
        inform.addReceiver(new AID("CentreCommande", AID.ISLOCALNAME));
        inform.setContent(summary.toString());
        myAgent.send(inform);

        System.out.println(myAgent.getLocalName() + ": Summary report sent to command center");
    }
}
