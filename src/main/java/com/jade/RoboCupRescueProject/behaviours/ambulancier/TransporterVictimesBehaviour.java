package com.jade.RoboCupRescueProject.behaviours.ambulancier;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.jade.RoboCupRescueProject.agents.AgentAmbulancier;

/**
 * Behavior responsible for transporting victims to medical facilities.
 * This behavior is triggered after first aid has been provided and coordinates
 * the evacuation of victims to hospitals or medical triage points.
 */
public class TransporterVictimesBehaviour extends CyclicBehaviour {
    private Random random = new Random();
    private Map<String, String> transportQueue = new HashMap<>(); // Map of victim location to triage status

    // Medical facility locations (simulated)
    private final String[] MEDICAL_FACILITIES = {
        "Hospital_Central:100,200",
        "Hospital_Nord:300,400",
        "TriagePoint_Est:500,100",
        "TriagePoint_Ouest:150,500"
    };

    public TransporterVictimesBehaviour(Agent a) { 
        super(a); 
        System.out.println(a.getLocalName() + ": Starting victim transport behavior");
    }

    @Override
    public void action() {
        // Listen for transport requests
        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
            MessageTemplate.MatchContent("TRANSPORT_VICTIM:.*")
        );
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            // Process transport request
            String content = msg.getContent();
            String[] parts = content.substring("TRANSPORT_VICTIM:".length()).split(",");

            if (parts.length >= 2) {
                String locationStr = parts[0];
                String triageCategory = parts[1];

                // Add to transport queue
                transportQueue.put(locationStr, triageCategory);
                System.out.println(myAgent.getLocalName() + ": Added victim at " + locationStr + 
                                   " with triage category " + triageCategory + " to transport queue");

                // Process the transport queue
                processTransportQueue();
            }
        } else {
            block(); // Block until a message is received
        }
    }

    /**
     * Process the transport queue and evacuate victims
     */
    private void processTransportQueue() {
        if (transportQueue.isEmpty()) {
            return;
        }

        // In a real implementation, we would optimize the transport order based on triage category
        // and distance to medical facilities. For simplicity, we'll just process one victim at a time.
        String locationStr = transportQueue.keySet().iterator().next();
        String triageCategory = transportQueue.remove(locationStr);

        // Select appropriate medical facility based on triage category
        String facility = selectMedicalFacility(triageCategory);

        // Request route from police if needed
        if (random.nextDouble() < 0.3) { // 30% chance of needing route assistance
            requestRouteAssistance(locationStr, facility);
        }

        // Transport the victim
        transportVictim(locationStr, facility, triageCategory);
    }

    /**
     * Select an appropriate medical facility based on the victim's triage category
     * @param triageCategory The triage category of the victim
     * @return The selected medical facility
     */
    private String selectMedicalFacility(String triageCategory) {
        // Select facility based on triage category
        // Critical victims go to hospitals, others may go to triage points
        if (triageCategory.equals(SoinsPremiersBehaviour.TRIAGE_CRITIQUE)) {
            // Critical victims go to hospitals
            return random.nextBoolean() ? MEDICAL_FACILITIES[0] : MEDICAL_FACILITIES[1];
        } else if (triageCategory.equals(SoinsPremiersBehaviour.TRIAGE_URGENT)) {
            // Urgent victims preferably go to hospitals, but can go to triage points if necessary
            return MEDICAL_FACILITIES[random.nextInt(3)];
        } else {
            // Minor injuries can go to any facility, preferably triage points
            return MEDICAL_FACILITIES[random.nextInt(4)];
        }
    }

    /**
     * Request route assistance from police agents
     * @param fromLocation The starting location
     * @param toFacility The destination facility
     */
    private void requestRouteAssistance(String fromLocation, String toFacility) {
        System.out.println(myAgent.getLocalName() + ": Requesting route assistance from police");

        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(new AID("Police", AID.ISLOCALNAME));
        request.setContent("ROUTE_REQUEST:" + fromLocation + "," + toFacility);
        myAgent.send(request);

        // In a real implementation, we would wait for a response
        // For simplicity, we'll just simulate a delay
        try {
            Thread.sleep(500); // 0.5 second
            System.out.println(myAgent.getLocalName() + ": Received route from police");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Transport a victim to a medical facility
     * @param locationStr The location of the victim
     * @param facility The destination medical facility
     * @param triageCategory The triage category of the victim
     */
    private void transportVictim(String locationStr, String facility, String triageCategory) {
        System.out.println(myAgent.getLocalName() + ": Transporting victim from " + locationStr + 
                           " to " + facility + " (Category: " + triageCategory + ")");

        // Simulate transport time based on triage category
        try {
            int transportTime = 0;
            if (triageCategory.equals(SoinsPremiersBehaviour.TRIAGE_CRITIQUE)) {
                transportTime = 2000; // 2 seconds for critical cases (emergency transport)
            } else if (triageCategory.equals(SoinsPremiersBehaviour.TRIAGE_URGENT)) {
                transportTime = 3000; // 3 seconds for urgent cases
            } else {
                transportTime = 4000; // 4 seconds for minor injuries
            }

            Thread.sleep(transportTime);
            System.out.println(myAgent.getLocalName() + ": Victim successfully transported to " + facility);

            // Update agent status and increment transported victims counter
            ((AgentAmbulancier)myAgent).updateStatus("TRANSPORT_COMPLETED");
            ((AgentAmbulancier)myAgent).incrementVictimsTransported();

            // Notify the command center about the completed transport
            ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
            inform.addReceiver(new AID("CentreCommande", AID.ISLOCALNAME));
            inform.setContent("VICTIM_TRANSPORTED:" + locationStr + "," + facility + "," + triageCategory);
            myAgent.send(inform);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
