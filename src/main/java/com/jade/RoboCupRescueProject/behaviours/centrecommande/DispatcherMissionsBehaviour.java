package com.jade.RoboCupRescueProject.behaviours.centrecommande;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DispatcherMissionsBehaviour extends CyclicBehaviour {
    // Agent types
    private static final String AGENT_TYPE_POMPIER = "pompier";
    private static final String AGENT_TYPE_AMBULANCIER = "ambulancier";
    private static final String AGENT_TYPE_POLICE = "police";

    // Mission types and priorities
    public static final String MISSION_FIRE_FIGHTING = "FIRE_FIGHTING";
    public static final String MISSION_VICTIM_RESCUE = "VICTIM_RESCUE";
    public static final String MISSION_ROAD_CLEARING = "ROAD_CLEARING";
    public static final String MISSION_SECURITY_ZONE = "SECURITY_ZONE";

    // Mission status
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_ASSIGNED = "ASSIGNED";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_FAILED = "FAILED";

    // Cache and timing management
    private final ConcurrentHashMap<String, List<AID>> availableAgents;
    private final ConcurrentHashMap<String, String> missionStatus;
    private final ConcurrentHashMap<String, Long> agentLastResponse;
    private long lastDispatchTime = 0;
    private long lastAgentLookupTime = 0;

    // Configuration constants
    private static final long DISPATCH_INTERVAL = 5000; // 5 seconds
    private static final long AGENT_LOOKUP_INTERVAL = 30000; // 30 seconds
    private static final long AGENT_RESPONSE_TIMEOUT = 10000; // 10 seconds
    private static final int MAX_RETRY_ATTEMPTS = 3;

    public DispatcherMissionsBehaviour(Agent a) {
        super(a);
        this.availableAgents = new ConcurrentHashMap<>();
        this.missionStatus = new ConcurrentHashMap<>();
        this.agentLastResponse = new ConcurrentHashMap<>();

        // Initialize agent cache
        initializeAgentCache();
        System.out.println(myAgent.getLocalName() + ": Starting mission dispatching behavior");
    }

    private void initializeAgentCache() {
        availableAgents.put(AGENT_TYPE_POMPIER, new ArrayList<>());
        availableAgents.put(AGENT_TYPE_AMBULANCIER, new ArrayList<>());
        availableAgents.put(AGENT_TYPE_POLICE, new ArrayList<>());
        updateAvailableAgents();
    }

    @Override
    public void action() {
        try {
            long currentTime = System.currentTimeMillis();

            // Update agent cache if needed
            if (currentTime - lastAgentLookupTime > AGENT_LOOKUP_INTERVAL) {
                updateAvailableAgents();
                lastAgentLookupTime = currentTime;
            }

            // Process responses from agents
            processAgentResponses();

            // Check if it's time to dispatch new missions
            if (currentTime - lastDispatchTime > DISPATCH_INTERVAL) {
                dispatchPendingMissions();
                lastDispatchTime = currentTime;
            }

            // Check for timed-out missions
            checkMissionTimeouts(currentTime);

            block(100); // Small delay to prevent CPU overuse
        } catch (Exception e) {
            System.err.println(myAgent.getLocalName() + ": Error in dispatch behavior: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateAvailableAgents() {
        System.out.println(myAgent.getLocalName() + ": Updating available agents");

        for (String agentType : new String[]{AGENT_TYPE_POMPIER, AGENT_TYPE_AMBULANCIER, AGENT_TYPE_POLICE}) {
            try {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType(agentType);
                template.addServices(sd);

                DFAgentDescription[] result = DFService.search(myAgent, template);
                List<AID> agents = new ArrayList<>();
                for (DFAgentDescription agent : result) {
                    agents.add(agent.getName());
                }
                availableAgents.put(agentType, agents);

                System.out.println(myAgent.getLocalName() + ": Found " + agents.size() +
                        " agents of type " + agentType);
            } catch (FIPAException e) {
                System.err.println(myAgent.getLocalName() + ": Error searching for " +
                        agentType + " agents: " + e.getMessage());
            }
        }
    }

    private void dispatchPendingMissions() {
        // Get pending missions from the command center
        List<Mission> pendingMissions = getPendingMissions();

        for (Mission mission : pendingMissions) {
            String agentType = determineAgentType(mission.getType());
            List<AID> agents = availableAgents.get(agentType);

            if (agents != null && !agents.isEmpty()) {
                // Try to find the most suitable agent
                AID selectedAgent = selectSuitableAgent(agents, mission);
                if (selectedAgent != null) {
                    dispatchMissionToAgent(selectedAgent, mission);
                } else {
                    handleNoAvailableAgent(mission);
                }
            } else {
                System.out.println(myAgent.getLocalName() + ": No " + agentType +
                        " agents available for mission: " + mission.getDescription());
                handleNoAvailableAgent(mission);
            }
        }
    }

    private void dispatchMissionToAgent(AID agent, Mission mission) {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(agent);
        request.setConversationId(mission.getId());
        request.setContent(formatMissionMessage(mission));

        myAgent.send(request);
        missionStatus.put(mission.getId(), STATUS_ASSIGNED);
        agentLastResponse.put(agent.getLocalName(), System.currentTimeMillis());

        System.out.println(myAgent.getLocalName() + ": Dispatched mission " + mission.getId() +
                " to " + agent.getLocalName());
    }

    private void processAgentResponses() {
        MessageTemplate mt = MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
        );

        ACLMessage msg = myAgent.receive(mt);
        while (msg != null) {
            String missionId = msg.getConversationId();
            if (msg.getPerformative() == ACLMessage.AGREE) {
                handleMissionAccepted(missionId, msg.getSender());
            } else {
                handleMissionRefused(missionId, msg.getSender(), msg.getContent());
            }
            msg = myAgent.receive(mt);
        }
    }

    private void handleMissionAccepted(String missionId, AID agent) {
        missionStatus.put(missionId, STATUS_ASSIGNED);
        agentLastResponse.put(agent.getLocalName(), System.currentTimeMillis());
        System.out.println(myAgent.getLocalName() + ": Mission " + missionId +
                " accepted by " + agent.getLocalName());
    }

    private void handleMissionRefused(String missionId, AID agent, String reason) {
        System.out.println(myAgent.getLocalName() + ": Mission " + missionId +
                " refused by " + agent.getLocalName() + ": " + reason);
        missionStatus.put(missionId, STATUS_PENDING);
        retryMission(missionId);
    }

    private void checkMissionTimeouts(long currentTime) {
        for (Map.Entry<String, Long> entry : new HashMap<>(agentLastResponse).entrySet()) {
            if (currentTime - entry.getValue() > AGENT_RESPONSE_TIMEOUT) {
                handleAgentTimeout(entry.getKey());
            }
        }
    }

    private void handleAgentTimeout(String agentName) {
        System.out.println(myAgent.getLocalName() + ": Agent " + agentName + " timed out");
        agentLastResponse.remove(agentName);
        // Update agent availability and retry affected missions
        updateAgentAvailability(agentName);
    }

    private String determineAgentType(String missionType) {
        switch (missionType) {
            case MISSION_FIRE_FIGHTING:
                return AGENT_TYPE_POMPIER;
            case MISSION_VICTIM_RESCUE:
                return AGENT_TYPE_AMBULANCIER;
            case MISSION_ROAD_CLEARING:
            case MISSION_SECURITY_ZONE:
                return AGENT_TYPE_POLICE;
            default:
                throw new IllegalArgumentException("Unknown mission type: " + missionType);
        }
    }

    // Helper methods
    private List<Mission> getPendingMissions() {
        // Implementation depends on how missions are stored in the command center
        // This should return a list of missions that need to be assigned
        return new ArrayList<>(); // Placeholder
    }

    private AID selectSuitableAgent(List<AID> agents, Mission mission) {
        // Implement agent selection logic based on various criteria
        // For now, return the first available agent
        return agents.isEmpty() ? null : agents.get(0);
    }

    private void handleNoAvailableAgent(Mission mission) {
        // Implement fallback strategy
        System.out.println(myAgent.getLocalName() + ": No suitable agent found for mission: " +
                mission.getDescription());
    }

    private String formatMissionMessage(Mission mission) {
        return String.format("MISSION:%s:%s:%d:%s",
                mission.getType(),
                mission.getLocation(),
                mission.getPriority(),
                mission.getDescription()
        );
    }

    private void retryMission(String missionId) {
        // Implement mission retry logic
    }

    private void updateAgentAvailability(String agentName) {
        // Update agent availability status
    }

    // Inner class to represent a Mission
    private static class Mission {
        private final String id;
        private final String type;
        private final String location;
        private final int priority;
        private final String description;

        public Mission(String id, String type, String location, int priority, String description) {
            this.id = id;
            this.type = type;
            this.location = location;
            this.priority = priority;
            this.description = description;
        }

        // Getters
        public String getId() { return id; }
        public String getType() { return type; }
        public String getLocation() { return location; }
        public int getPriority() { return priority; }
        public String getDescription() { return description; }
    }
}