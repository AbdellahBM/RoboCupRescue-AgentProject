package com.jade.RoboCupRescueProject.agents;

import com.jade.RoboCupRescueProject.behaviours.pompier.ComportementExtinctionFeu;
import com.jade.RoboCupRescueProject.behaviours.pompier.FireAlertResponseBehaviour;
import com.jade.RoboCupRescueProject.scenarios.ScenarioAlerteIncendie;
import com.jade.RoboCupRescueProject.utils.AgentConsoleLogger;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.*;

public class AgentPompier extends Agent {
    private double waterLevel = 1000.0;
    private String currentLocation = "BASE";
    private boolean isAvailable = true;

    @Override
    protected void setup() {
        // Log agent starting
        AgentConsoleLogger.logAgentStarting(this);

        // Register with Directory Facilitator
        registerWithDF();

        // Initialize behaviors
        addBehaviours();

        // Ajouter le comportement de réponse aux alertes incendie
        addBehaviour(new FireAlertResponseBehaviour());

        // Log agent status
        AgentConsoleLogger.logFirefighterStatus(this, currentLocation, waterLevel, isAvailable);
    }

    // Méthode pour démarrer le scénario d'incendie
    public void demarrerScenarioIncendie(String idBatiment, String intensiteFeu, int nombreVictimes, 
                                        boolean matieresDangereuses, String accessibilite, int etage) {
        String details = String.format("Bâtiment: %s, Intensité: %s, Victimes: %d, Matières dangereuses: %s, Accessibilité: %s, Étage: %d",
                                      idBatiment, intensiteFeu, nombreVictimes, 
                                      matieresDangereuses ? "Oui" : "Non", accessibilite, etage);

        AgentConsoleLogger.logAgentAction(this, "Démarrage scénario incendie", details);

        addBehaviour(new ScenarioAlerteIncendie.DetectionFeuBehaviour(idBatiment, intensiteFeu, 
                                                                    nombreVictimes, matieresDangereuses, 
                                                                    accessibilite, etage));
    }


    private void registerWithDF() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("pompier");  // This matches what CommandCenter searches for
        sd.setName("firefighting-service");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void addBehaviours() {
        // Add behavior to handle mission requests
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        // Vérifier si c'est une demande de démarrage de scénario
                        if (msg.getContent().startsWith("START_SCENARIO:FIRE_ALERT:")) {
                            handleScenarioRequest(msg);
                        } else {
                            handleMissionRequest(msg);
                        }
                    }
                } else {
                    block();
                }
            }
        });

        // Add behavior to report status periodically
        addBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            protected void onTick() {
                reportStatus();
            }
        });
    }

    /**
     * Gère une demande de démarrage de scénario
     * @param msg Le message contenant la demande
     */
    private void handleScenarioRequest(ACLMessage msg) {
        try {
            // Extraire les paramètres du scénario
            String[] parts = msg.getContent().split(":");
            if (parts.length >= 8) {
                String idBatiment = parts[2];
                String intensiteFeu = parts[3];
                int nombreVictimes = Integer.parseInt(parts[4]);
                boolean matieresDangereuses = Boolean.parseBoolean(parts[5]);
                String accessibilite = parts[6];
                int etage = Integer.parseInt(parts[7]);

                AgentConsoleLogger.logAgentAction(this, "Réception demande", "Demande de démarrage du scénario d'incendie reçue");
                demarrerScenarioIncendie(idBatiment, intensiteFeu, nombreVictimes, 
                                        matieresDangereuses, accessibilite, etage);
            } else {
                String errorDetails = "Format attendu: START_SCENARIO:FIRE_ALERT:idBatiment:intensiteFeu:nombreVictimes:" +
                                     "matieresDangereuses:accessibilite:etage";
                AgentConsoleLogger.logAgentStatus(this, "ERREUR", "Format de message invalide pour le démarrage du scénario. " + errorDetails);
            }
        } catch (Exception e) {
            AgentConsoleLogger.logAgentStatus(this, "ERREUR", "Erreur lors du traitement de la demande de scénario: " + e.getMessage());
        }
    }

    private void handleMissionRequest(ACLMessage msg) {
        if (msg.getContent().startsWith("MISSION:FIRE_FIGHTING")) {
            if (isAvailable && waterLevel > 100) {
                // Accept mission
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.AGREE);
                reply.setContent("MISSION_ACCEPTED");
                send(reply);
                isAvailable = false;

                // Start mission execution
                executeMission(msg.getContent());
            } else {
                // Reject mission
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("UNAVAILABLE:" + (isAvailable ? "LOW_WATER" : "BUSY"));
                send(reply);
            }
        }
    }

    private void executeMission(String missionDetails) {
        String[] parts = missionDetails.split(":");
        if (parts.length >= 4) {
            String location = parts[2];
            addBehaviour(new OneShotBehaviour(this) {
                @Override
                public void action() {
                    // Log mission execution
                    AgentConsoleLogger.logAgentAction(myAgent, "Exécution mission", "Intervention sur incendie à " + location);

                    // Simulate fire fighting
                    waterLevel -= 200;
                    currentLocation = location;

                    // Update status
                    AgentConsoleLogger.logFirefighterStatus(myAgent, location, waterLevel, false);

                    // Report mission completion
                    reportMissionComplete(location);
                    isAvailable = true;

                    // Log updated status after mission
                    AgentConsoleLogger.logFirefighterStatus(myAgent, currentLocation, waterLevel, isAvailable);
                }
            });
        }
    }

    private void reportStatus() {
        // Only send status updates during scenario execution to reduce console noise
        if (!currentLocation.equals("BASE") || !isAvailable) {
            // Log status to console for user
            AgentConsoleLogger.logFirefighterStatus(this, currentLocation, waterLevel, isAvailable);

            // Send status message to other agents
            ACLMessage status = new ACLMessage(ACLMessage.INFORM);
            status.setContent(String.format("STATUS:FIREFIGHTER:%s:water=%.1f:location=%s:available=%b",
                    getLocalName(), waterLevel, currentLocation, isAvailable));
            // Add command center as receiver
            send(status);
        }
    }

    private void reportMissionComplete(String location) {
        ACLMessage report = new ACLMessage(ACLMessage.INFORM);
        report.setContent("MISSION_COMPLETE:FIRE_FIGHTING:" + location);
        // Add command center as receiver
        send(report);
    }

    @Override
    protected void takeDown() {
        // Deregister from the DF
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Log agent stopping
        AgentConsoleLogger.logAgentStopping(this);
    }

    // Utility methods
    public double getWaterLevel() {
        return waterLevel;
    }

    public void resupplyWater(double amount) {
        this.waterLevel = Math.min(1000, waterLevel + amount);
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }
}
