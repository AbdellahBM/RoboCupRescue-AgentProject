package com.jade.RoboCupRescueProject.behaviours.pompier;

import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class ComportementExtinctionFeu extends SimpleBehaviour {
    private boolean estEteint = false;
    private boolean victimesSecourues = false;
    private int eauUtilisee = 0;
    private final int EAU_NECESSAIRE = 100;
    private int eauParEtape = 20;

    // Paramètres pour le sauvetage des victimes
    private int nombreVictimesTotal = 0;
    private int nombreVictimesRestantes = 0;
    private int nombreVictimesSecouruesParEtape = 1;
    private boolean sauvetageNecessaire = false;

    // Paramètres pour les conditions spéciales
    private boolean matieresDangereuses = false;
    private boolean accesDifficile = false;

    // Délais pour visualiser le processus (en millisecondes)
    private final int DELAI_DEPLACEMENT = 2000;  // 2 secondes
    private final int DELAI_EXTINCTION = 2000;   // 2 secondes
    private int delaiSauvetage = 2500;    // 2.5 secondes

    // État du comportement
    private enum Etape { DEPLACEMENT, SAUVETAGE, EXTINCTION, FINALISATION, TERMINE }
    private Etape etapeActuelle = Etape.DEPLACEMENT;
    private long tempsProchainEvenement = 0;

    // Constructeur avec paramètres pour le sauvetage et les conditions spéciales
    public ComportementExtinctionFeu() {
        // Constructeur par défaut sans paramètres spécifiques
    }

    public ComportementExtinctionFeu(int nombreVictimes, boolean matieresDangereuses, boolean accesDifficile) {
        this.nombreVictimesTotal = nombreVictimes;
        this.nombreVictimesRestantes = nombreVictimes;
        this.sauvetageNecessaire = (nombreVictimes > 0);
        this.matieresDangereuses = matieresDangereuses;
        this.accesDifficile = accesDifficile;

        // Ajuster les paramètres en fonction des conditions
        if (matieresDangereuses) {
            // Les matières dangereuses ralentissent l'extinction
            this.eauParEtape = 15;
        }

        if (accesDifficile) {
            // L'accès difficile ralentit le sauvetage
            this.nombreVictimesSecouruesParEtape = 1;
            this.delaiSauvetage = 3000; // 3 secondes
        } else {
            this.nombreVictimesSecouruesParEtape = 2;
        }
    }

    @Override
    public void action() {
        // Vérifier si c'est le moment d'exécuter la prochaine étape
        long tempsActuel = System.currentTimeMillis();
        if (tempsActuel < tempsProchainEvenement) {
            block(tempsProchainEvenement - tempsActuel); // Bloquer jusqu'au prochain événement
            return;
        }

        switch (etapeActuelle) {
            case DEPLACEMENT:
                // Simuler le déplacement vers le lieu de l'incendie
                // Logs supprimés pour réduire le bruit dans la console

                // Déterminer la prochaine étape en fonction de la présence de victimes
                if (sauvetageNecessaire) {
                    etapeActuelle = Etape.SAUVETAGE;
                } else {
                    etapeActuelle = Etape.EXTINCTION;
                }

                tempsProchainEvenement = System.currentTimeMillis() + DELAI_DEPLACEMENT;

                // Comportement silencieux pour le début de l'opération
                myAgent.addBehaviour(new jade.core.behaviours.WakerBehaviour(myAgent, DELAI_DEPLACEMENT) {
                    @Override
                    protected void onWake() {
                        // Logs supprimés pour réduire le bruit dans la console
                    }
                });
                break;

            case SAUVETAGE:
                // Sauvetage des victimes
                if (nombreVictimesRestantes > 0) {
                    // Calculer le nombre de victimes secourues dans cette étape
                    int victimesSecouruesEtape = Math.min(nombreVictimesRestantes, nombreVictimesSecouruesParEtape);
                    nombreVictimesRestantes -= victimesSecouruesEtape;

                    // Rapporter la progression du sauvetage
                    rapporterProgressionSauvetage();

                    // Vérifier si toutes les victimes ont été secourues
                    if (nombreVictimesRestantes <= 0) {
                        // Toutes les victimes ont été secourues, passer à l'extinction
                        victimesSecourues = true;
                        etapeActuelle = Etape.EXTINCTION;
                    }

                    // Définir le temps pour la prochaine étape
                    tempsProchainEvenement = System.currentTimeMillis() + delaiSauvetage;
                } else {
                    // Pas de victimes à secourir, passer à l'extinction
                    victimesSecourues = true;
                    etapeActuelle = Etape.EXTINCTION;
                    tempsProchainEvenement = System.currentTimeMillis();
                }
                break;

            case EXTINCTION:
                // Utiliser l'eau
                eauUtilisee += eauParEtape;

                // Calculer le pourcentage d'avancement
                int pourcentage = (eauUtilisee * 100) / EAU_NECESSAIRE;

                // Rapporter la progression
                rapporterProgression(pourcentage);

                // Vérifier si le feu est éteint
                if (eauUtilisee >= EAU_NECESSAIRE) {
                    etapeActuelle = Etape.FINALISATION;
                    // Log supprimé pour réduire le bruit dans la console
                    rapporterSucces();
                    tempsProchainEvenement = System.currentTimeMillis() + 1000; // Attendre 1 seconde avant de terminer
                } else {
                    // Continuer l'extinction
                    tempsProchainEvenement = System.currentTimeMillis() + DELAI_EXTINCTION;
                }
                break;

            case FINALISATION:
                // Terminer le comportement
                etapeActuelle = Etape.TERMINE;
                estEteint = true;
                break;

            case TERMINE:
                // Ne rien faire, le comportement est terminé
                break;
        }
    }

    private void rapporterProgressionSauvetage() {
        ACLMessage msgProgression = new ACLMessage(ACLMessage.INFORM);
        msgProgression.addReceiver(new AID("CommandCenter", AID.ISLOCALNAME));

        // Construire un message de progression pour le sauvetage
        StringBuilder message = new StringBuilder();
        message.append("PROGRESSION: Sauvetage en cours. Victimes restantes: ")
               .append(nombreVictimesRestantes).append("/").append(nombreVictimesTotal);

        if (eauUtilisee > 0) {
            message.append(". Extinction en cours. Eau utilisée: ")
                   .append(eauUtilisee).append("/").append(EAU_NECESSAIRE);
        }

        msgProgression.setContent(message.toString());
        myAgent.send(msgProgression);
    }

    private void rapporterProgression(int pourcentage) {
        ACLMessage msgProgression = new ACLMessage(ACLMessage.INFORM);
        msgProgression.addReceiver(new AID("CommandCenter", AID.ISLOCALNAME));

        // Construire un message de progression pour l'extinction
        StringBuilder message = new StringBuilder();
        message.append("PROGRESSION: Extinction en cours. Eau utilisée: ")
               .append(eauUtilisee).append("/").append(EAU_NECESSAIRE);

        // Ajouter des informations sur le sauvetage si nécessaire
        if (sauvetageNecessaire) {
            message.append(". Victimes secourues: ")
                   .append(nombreVictimesTotal - nombreVictimesRestantes)
                   .append("/").append(nombreVictimesTotal);
        }

        msgProgression.setContent(message.toString());
        myAgent.send(msgProgression);
    }

    private void rapporterSucces() {
        ACLMessage msgSucces = new ACLMessage(ACLMessage.INFORM);
        msgSucces.addReceiver(new AID("CommandCenter", AID.ISLOCALNAME));

        // Construire un message de succès complet
        StringBuilder message = new StringBuilder();
        message.append("SUCCES: ");

        // Ajouter des informations sur le sauvetage si nécessaire
        if (sauvetageNecessaire) {
            if (victimesSecourues) {
                message.append("Toutes les victimes ont été évacuées. ");
            } else {
                message.append(nombreVictimesTotal - nombreVictimesRestantes)
                       .append("/").append(nombreVictimesTotal)
                       .append(" victimes évacuées. ");
            }
        }

        message.append("Le feu a été éteint. Eau totale utilisée: ").append(eauUtilisee);

        msgSucces.setContent(message.toString());
        myAgent.send(msgSucces);
    }

    @Override
    public boolean done() {
        return estEteint;
    }

}
