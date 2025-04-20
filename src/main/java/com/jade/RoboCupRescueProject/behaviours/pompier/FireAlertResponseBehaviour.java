package com.jade.RoboCupRescueProject.behaviours.pompier;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Behavior for handling fire alert responses from the Command Center.
 * This behavior listens for REQUEST messages from the Command Center
 * and sends a CONFIRM message in response before starting the fire
 * extinguishing process.
 */
public class FireAlertResponseBehaviour extends CyclicBehaviour {

    // Délai avant de démarrer l'extinction (en millisecondes)
    private final int DELAI_AVANT_EXTINCTION = 1500;  // 1.5 secondes

    // État du traitement de la réponse
    private enum Etape { ATTENTE_MESSAGE, CONFIRMATION, DEMARRAGE_EXTINCTION, TERMINE }
    private Etape etapeActuelle = Etape.ATTENTE_MESSAGE;
    private ACLMessage messageEnTraitement = null;
    private String buildingId = null;
    private boolean sauvetageNecessaire = false;
    private boolean matieresDangereuses = false;
    private boolean accesDifficile = false;
    private int nombreVictimes = 0;
    private int etage = 0;

    @Override
    public void action() {
        switch (etapeActuelle) {
            case ATTENTE_MESSAGE:
                // Create a template to receive REQUEST messages
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(mt);

                if (msg != null) {
                    String content = msg.getContent();

                    // Check if this is a fire alert response
                    if (content.startsWith("Reçu. Situation critique. Dirige-toi sur zone")) {
                        // Extract the building ID
                        String[] parts = content.split("zone ");
                        buildingId = parts[1].split("\\.")[0];

                        // Extract additional information
                        sauvetageNecessaire = content.contains("Priorité 1: sauvetage des");
                        matieresDangereuses = content.contains("Attention aux matières dangereuses");
                        accesDifficile = content.contains("Accès difficile");

                        // Extract number of victims if present
                        if (sauvetageNecessaire) {
                            try {
                                String victimesText = content.split("sauvetage des ")[1];
                                nombreVictimes = Integer.parseInt(victimesText.split(" ")[0]);

                                // Extract floor if present
                                if (content.contains("victimes au")) {
                                    String etageText = content.split("victimes au ")[1];
                                    etage = Integer.parseInt(etageText.split("ème")[0]);
                                }
                            } catch (Exception e) {
                                System.err.println(myAgent.getLocalName() + ": Erreur lors de l'extraction des informations sur les victimes: " + e.getMessage());
                            }
                        }

                        // Logs supprimés pour réduire le bruit dans la console

                        // Sauvegarder le message pour traitement ultérieur
                        messageEnTraitement = msg;

                        // Passer à l'étape de confirmation après un court délai
                        etapeActuelle = Etape.CONFIRMATION;

                        // Utiliser un WakerBehaviour pour envoyer la confirmation après un délai
                        myAgent.addBehaviour(new jade.core.behaviours.WakerBehaviour(myAgent, 1000) {
                            @Override
                            protected void onWake() {
                                // Send CONFIRM message with enhanced acknowledgement
                                ACLMessage confirm = new ACLMessage(ACLMessage.CONFIRM);
                                confirm.addReceiver(messageEnTraitement.getSender());

                                // Construire un message de confirmation plus détaillé
                                StringBuilder messageConfirmation = new StringBuilder();
                                messageConfirmation.append("Bien reçu, je pars immédiatement");

                                // Ajouter des détails spécifiques en fonction de la situation
                                if (sauvetageNecessaire) {
                                    messageConfirmation.append(". Priorité au sauvetage des ")
                                                      .append(nombreVictimes).append(" victimes");
                                    if (etage > 0) {
                                        messageConfirmation.append(" au ").append(etage).append("ème étage");
                                    }
                                }

                                if (matieresDangereuses) {
                                    messageConfirmation.append(". Équipement spécial pour matières dangereuses activé");
                                }

                                if (accesDifficile) {
                                    messageConfirmation.append(". Équipement d'accès difficile préparé");
                                }

                                messageConfirmation.append(".");

                                confirm.setContent(messageConfirmation.toString());
                                myAgent.send(confirm);

                                // Logs supprimés pour réduire le bruit dans la console

                                // Passer à l'étape de démarrage de l'extinction
                                etapeActuelle = Etape.DEMARRAGE_EXTINCTION;
                            }
                        });
                    }
                } else {
                    block();
                }
                break;

            case CONFIRMATION:
                // Attendre que le WakerBehaviour termine
                block();
                break;

            case DEMARRAGE_EXTINCTION:
                // Utiliser un WakerBehaviour pour démarrer l'extinction après un délai
                myAgent.addBehaviour(new jade.core.behaviours.WakerBehaviour(myAgent, DELAI_AVANT_EXTINCTION) {
                    @Override
                    protected void onWake() {
                        // Start fire extinguishing behavior with victim rescue if needed
                        // Logs supprimés pour réduire le bruit dans la console
                        myAgent.addBehaviour(new ComportementExtinctionFeu(nombreVictimes, matieresDangereuses, accesDifficile));

                        // Revenir à l'état d'attente pour traiter d'autres messages
                        etapeActuelle = Etape.ATTENTE_MESSAGE;
                        messageEnTraitement = null;
                        buildingId = null;
                    }
                });

                // Passer à l'état TERMINE pour attendre que le WakerBehaviour termine
                etapeActuelle = Etape.TERMINE;
                break;

            case TERMINE:
                // Attendre que le WakerBehaviour termine
                block();
                break;
        }
    }
}
