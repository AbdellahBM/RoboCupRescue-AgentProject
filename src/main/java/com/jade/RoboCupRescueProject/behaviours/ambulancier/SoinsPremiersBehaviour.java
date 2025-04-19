package com.jade.RoboCupRescueProject.behaviours.ambulancier;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.jade.RoboCupRescueProject.agents.AgentAmbulancier;

/**
 * Behavior responsible for providing first aid to victims and performing triage.
 * This behavior is triggered when a victim is found and evaluates their condition
 * to determine the appropriate level of care needed.
 */
public class SoinsPremiersBehaviour extends CyclicBehaviour {
    // Triage categories
    public static final String TRIAGE_LEGER = "LEGER";      // Minor injuries
    public static final String TRIAGE_URGENT = "URGENT";    // Urgent care needed
    public static final String TRIAGE_CRITIQUE = "CRITIQUE"; // Critical condition

    private Random random = new Random();
    private Map<String, String> victimStatus = new HashMap<>(); // Map of victim location to triage status

    public SoinsPremiersBehaviour(Agent a) { 
        super(a); 
        System.out.println(a.getLocalName() + ": Starting first aid behavior");
    }

    @Override
    public void action() {
        // Listen for messages about found victims
        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.MatchContent("VICTIM_FOUND:.*")
        );
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            // Process message about found victim
            String content = msg.getContent();
            String locationStr = content.substring("VICTIM_FOUND:".length());

            // Perform triage and provide first aid
            String triageResult = performTriage(locationStr);
            provideFirstAid(locationStr, triageResult);

            // Store the victim status for later reference
            victimStatus.put(locationStr, triageResult);

            // Notify the transport behavior about the victim
            ACLMessage transportMsg = new ACLMessage(ACLMessage.REQUEST);
            transportMsg.addReceiver(myAgent.getAID());
            transportMsg.setContent("TRANSPORT_VICTIM:" + locationStr + "," + triageResult);
            myAgent.send(transportMsg);

            // Notify the inform center behavior about the victim
            ACLMessage informMsg = new ACLMessage(ACLMessage.REQUEST);
            informMsg.addReceiver(myAgent.getAID());
            informMsg.setContent("INFORM_CENTRE:" + locationStr + "," + triageResult);
            myAgent.send(informMsg);
        } else {
            block(); // Block until a message is received
        }
    }

    /**
     * Perform triage on a victim to determine the severity of their condition
     * @param locationStr The location of the victim
     * @return The triage category (LEGER, URGENT, CRITIQUE)
     */
    private String performTriage(String locationStr) {
        System.out.println(myAgent.getLocalName() + ": Performing triage on victim at " + locationStr);

        // Simulate triage process
        // In a real implementation, this would involve assessing vital signs, injuries, etc.
        double severity = random.nextDouble();
        String triageCategory;

        if (severity < 0.5) {
            triageCategory = TRIAGE_LEGER;
        } else if (severity < 0.8) {
            triageCategory = TRIAGE_URGENT;
        } else {
            triageCategory = TRIAGE_CRITIQUE;
        }

        System.out.println(myAgent.getLocalName() + ": Triage result for victim at " + locationStr + ": " + triageCategory);
        return triageCategory;
    }

    /**
     * Provide first aid to a victim based on their triage category
     * @param locationStr The location of the victim
     * @param triageCategory The triage category of the victim
     */
    private void provideFirstAid(String locationStr, String triageCategory) {
        System.out.println(myAgent.getLocalName() + ": Providing first aid to victim at " + locationStr);

        // Update agent status
        ((AgentAmbulancier)myAgent).updateStatus("PROVIDING_FIRST_AID:" + triageCategory);

        // Simulate providing first aid based on triage category
        switch (triageCategory) {
            case TRIAGE_LEGER:
                System.out.println(myAgent.getLocalName() + ": Basic first aid provided to victim with minor injuries");
                break;
            case TRIAGE_URGENT:
                System.out.println(myAgent.getLocalName() + ": Advanced first aid provided to victim with urgent needs");
                break;
            case TRIAGE_CRITIQUE:
                System.out.println(myAgent.getLocalName() + ": Critical care provided to stabilize victim in critical condition");
                break;
            default:
                System.out.println(myAgent.getLocalName() + ": Unknown triage category: " + triageCategory);
        }

        // Simulate time taken to provide first aid
        try {
            int treatmentTime = 0;
            if (triageCategory.equals(TRIAGE_LEGER)) {
                treatmentTime = 1000; // 1 second for minor injuries
            } else if (triageCategory.equals(TRIAGE_URGENT)) {
                treatmentTime = 2000; // 2 seconds for urgent cases
            } else if (triageCategory.equals(TRIAGE_CRITIQUE)) {
                treatmentTime = 3000; // 3 seconds for critical cases
            }

            Thread.sleep(treatmentTime);
            System.out.println(myAgent.getLocalName() + ": First aid completed for victim at " + locationStr);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
