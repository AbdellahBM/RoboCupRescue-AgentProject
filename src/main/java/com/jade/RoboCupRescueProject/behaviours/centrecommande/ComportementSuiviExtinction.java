package com.jade.RoboCupRescueProject.behaviours.centrecommande;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Comportement pour suivre l'évolution de l'extinction d'un incendie.
 * Ce comportement reçoit et traite les messages de progression, de confirmation
 * et de succès envoyés par l'agent pompier.
 */
public class ComportementSuiviExtinction extends CyclicBehaviour {

    // Compteur pour suivre les mises à jour de progression
    private int compteurMisesAJour = 0;

    @Override
    public void action() {
        // Créer un template pour les messages INFORM et CONFIRM
        MessageTemplate mtInform = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        MessageTemplate mtConfirm = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
        MessageTemplate mt = MessageTemplate.or(mtInform, mtConfirm);

        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            String contenu = msg.getContent();
            int performative = msg.getPerformative();
            String expediteur = msg.getSender().getLocalName();

            if (performative == ACLMessage.CONFIRM) {
                // Traiter le message de confirmation silencieusement
                // Logs supprimés pour réduire le bruit dans la console
            } else if (contenu.startsWith("PROGRESSION:")) {
                // Traiter le message de progression silencieusement
                compteurMisesAJour++;

                // Logs supprimés pour réduire le bruit dans la console
            } else if (contenu.startsWith("SUCCES:")) {
                // Traiter le message de succès silencieusement
                // Logs supprimés pour réduire le bruit dans la console
                mettreAJourStatutMission();
            }
        } else {
            block();
        }
    }

    private void mettreAJourStatutMission() {
        // Logs supprimés pour réduire le bruit dans la console
        // Mise à jour silencieuse du statut de la mission
    }
}
