package com.jade.RoboCupRescueProject.behaviours.centrecommande;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Comportement pour gérer les alertes incendie reçues des pompiers.
 * Ce comportement écoute les messages INFORM qui signalent un incendie,
 * extrait les informations pertinentes et envoie une demande d'intervention.
 */
public class FireAlertHandlerBehaviour extends CyclicBehaviour {

    // Délai pour visualiser le processus (en millisecondes)
    private final int DELAI_TRAITEMENT_ALERTE = 1500;  // 1.5 secondes

    // État du traitement de l'alerte
    private enum Etape { ATTENTE_ALERTE, TRAITEMENT_ALERTE, ENVOI_REPONSE }
    private Etape etapeActuelle = Etape.ATTENTE_ALERTE;
    private ACLMessage alerteEnTraitement = null;
    private String building = null;
    private String intensity = null;
    private String expediteur = null;
    private int nombreVictimes = 0;
    private boolean matieresDangereuses = false;
    private String accessibilite = "";
    private int etage = 0;

    @Override
    public void action() {
        switch (etapeActuelle) {
            case ATTENTE_ALERTE:
                // Écouter les messages d'alerte incendie
                MessageTemplate tm = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage msg = myAgent.receive(tm);

                if (msg != null) {
                    if (msg.getContent().startsWith("Incendie détecté")) {
                        // Sauvegarder le message pour traitement ultérieur
                        alerteEnTraitement = msg;

                        // Extraire les informations de l'alerte
                        try {
                            String[] parts = msg.getContent().split(",");
                            building = parts[0].split("bâtiment ")[1];
                            intensity = parts[1].split(": ")[1];
                            nombreVictimes = Integer.parseInt(parts[2].split(": ")[1]);
                            etage = Integer.parseInt(parts[3].split(": ")[1]);
                            matieresDangereuses = parts[4].split(": ")[1].equals("OUI");
                            accessibilite = parts[5].split(": ")[1];
                            expediteur = msg.getSender().getLocalName();

                            // Logs supprimés pour réduire le bruit dans la console

                            // Passer à l'étape de traitement
                            etapeActuelle = Etape.TRAITEMENT_ALERTE;

                            // Utiliser un WakerBehaviour pour envoyer la réponse après un délai
                            myAgent.addBehaviour(new jade.core.behaviours.WakerBehaviour(myAgent, DELAI_TRAITEMENT_ALERTE) {
                                @Override
                                protected void onWake() {
                                    // Créer le message de réponse
                                    ACLMessage response = new ACLMessage(ACLMessage.REQUEST);
                                    response.addReceiver(alerteEnTraitement.getSender());

                                    // Construire un message d'intervention plus détaillé
                                    StringBuilder messageIntervention = new StringBuilder();
                                    messageIntervention.append("Reçu. Situation critique. Dirige-toi sur zone ").append(building);

                                    // Ajouter des instructions spécifiques en fonction de la situation
                                    if (nombreVictimes > 0) {
                                        messageIntervention.append(". Priorité 1: sauvetage des ").append(nombreVictimes)
                                                          .append(" victimes au ").append(etage).append("ème étage");
                                    }

                                    messageIntervention.append(". Priorité ").append(nombreVictimes > 0 ? "2" : "1")
                                                      .append(": extinction du feu");

                                    if (matieresDangereuses) {
                                        messageIntervention.append(". Attention aux matières dangereuses");
                                    }

                                    if ("difficile".equals(accessibilite)) {
                                        messageIntervention.append(". Accès difficile, prévois équipement spécial");
                                    }

                                    messageIntervention.append(". Tiens-moi au courant.");

                                    response.setContent(messageIntervention.toString());

                                    // Envoyer la demande d'intervention
                                    myAgent.send(response);

                                    // Logs supprimés pour réduire le bruit dans la console

                                    // Revenir à l'état d'attente pour traiter d'autres alertes
                                    etapeActuelle = Etape.ATTENTE_ALERTE;
                                    alerteEnTraitement = null;
                                    building = null;
                                    intensity = null;
                                    expediteur = null;
                                }
                            });
                        } catch (Exception e) {
                            // Erreur silencieuse pour réduire le bruit dans la console
                            etapeActuelle = Etape.ATTENTE_ALERTE;
                            alerteEnTraitement = null;
                        }
                    }
                } else {
                    block();
                }
                break;

            case TRAITEMENT_ALERTE:
                // Attendre que le WakerBehaviour termine
                block();
                break;

            case ENVOI_REPONSE:
                // Cette étape est gérée par le WakerBehaviour
                block();
                break;
        }
    }
}
