package com.jade.RoboCupRescueProject.scenarios;

import com.jade.RoboCupRescueProject.utils.ContainerManager;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Scénario d'évacuation d'urgence pour la simulation RoboCupRescue.
 * Ce scénario simule la détection d'une zone dangereuse nécessitant une évacuation,
 * l'alerte au centre de commande, et la coordination pour l'évacuation des civils.
 */
public class ScenarioEvacuationUrgence {

    /**
     * Comportement de détection de zone dangereuse pour l'agent police.
     * Ce comportement envoie une alerte au centre de commande
     * lorsqu'une zone dangereuse est détectée.
     */
    public static class DetectionZoneDangerBehaviour extends OneShotBehaviour {
        private final String idZone;
        private final String typeRisque;
        private final int nombrePersonnes;
        private final boolean infrastructureCritique;
        private final String accessibilite;
        private final int niveauUrgence;

        public DetectionZoneDangerBehaviour(String idZone, String typeRisque, int nombrePersonnes, 
                                    boolean infrastructureCritique, String accessibilite, int niveauUrgence) {
            this.idZone = idZone;
            this.typeRisque = typeRisque;
            this.nombrePersonnes = nombrePersonnes;
            this.infrastructureCritique = infrastructureCritique;
            this.accessibilite = accessibilite;
            this.niveauUrgence = niveauUrgence;
        }

        @Override
        public void action() {
            // Envoyer l'alerte au Centre de Commande
            ACLMessage alerte = new ACLMessage(ACLMessage.INFORM);
            alerte.addReceiver(new AID("CommandCenter", AID.ISLOCALNAME));

            // Construire un message d'alerte complet avec toutes les informations
            StringBuilder messageAlerte = new StringBuilder();
            messageAlerte.append("Zone dangereuse détectée: ").append(idZone);
            messageAlerte.append(", type de risque : ").append(typeRisque);
            messageAlerte.append(", personnes à évacuer : ").append(nombrePersonnes);
            messageAlerte.append(", niveau d'urgence : ").append(niveauUrgence);
            messageAlerte.append(", infrastructure critique : ").append(infrastructureCritique ? "OUI" : "NON");
            messageAlerte.append(", accessibilité : ").append(accessibilite);

            alerte.setContent(messageAlerte.toString());
            myAgent.send(alerte);
        }
    }

    /**
     * Classe pour tester le scénario d'évacuation d'urgence.
     * Cette classe lance le scénario en créant un agent temporaire
     * qui envoie un message à l'agent police pour démarrer le scénario.
     */
    public static class TestScenarioEvacuation {
        // Délai d'initialisation du scénario (en millisecondes)
        private static final int DELAI_INITIALISATION = 2000;  // 2 secondes

        public static void lancerScenario() {
            // Paramètres du scénario
            String idZone = "Z-15";
            String typeRisque = "inondation imminente";
            int nombrePersonnes = 250;
            boolean infrastructureCritique = true;
            String accessibilite = "routes principales coupées";
            int niveauUrgence = 4; // Sur une échelle de 1 à 5

            System.out.println("\n========================================================");
            System.out.println("=== DÉMARRAGE DU SCÉNARIO: ÉVACUATION D'URGENCE ===");
            System.out.println("========================================================");
            System.out.println("\n=== CONTEXTE DU SCÉNARIO ===");
            System.out.println("Une zone dangereuse a été identifiée: " + idZone + " avec un risque d'" + typeRisque);
            System.out.println("Le niveau d'urgence est évalué à " + niveauUrgence + "/5 et l'accès est " + accessibilite);
            System.out.println("Il y a environ " + nombrePersonnes + " personnes à évacuer et des infrastructures critiques sont présentes: " + (infrastructureCritique ? "OUI" : "NON"));
            System.out.println("Un agent de police détecte la zone dangereuse et alerte le centre de commande");
            System.out.println("Le centre de commande coordonne l'évacuation et la sécurisation de la zone");

            System.out.println("\n=== ACTEURS DU SCÉNARIO ===");
            System.out.println("- Agent Police (Police-1)");
            System.out.println("- Centre de Commande (CommandCenter)");
            System.out.println("- Agent Logistique (Logistics-1)");

            System.out.println("\n=== SÉQUENCE D'INTERACTION ATTENDUE ===");
            System.out.println("1. Police → Centre de Commande : INFORM");
            System.out.println("   Message: \"Zone dangereuse détectée: " + idZone + ", type de risque : " + typeRisque + 
                             ", personnes à évacuer : " + nombrePersonnes + ", niveau d'urgence : " + niveauUrgence + 
                             ", infrastructure critique : " + (infrastructureCritique ? "OUI" : "NON") + 
                             ", accessibilité : " + accessibilite + "\"");
            System.out.println("2. Centre de Commande → Police : REQUEST");
            System.out.println("   Message: \"Reçu. Situation critique. Établir un périmètre de sécurité autour de la zone " + idZone + 
                             ". Priorité 1: évacuation des " + nombrePersonnes + " personnes. " +
                             "Priorité 2: sécurisation des infrastructures critiques. " +
                             "Tiens-moi au courant.\"");
            System.out.println("3. Police → Centre de Commande : CONFIRM");
            System.out.println("   Message: \"Bien reçu, périmètre de sécurité en cours d'établissement. Demande de renforts pour l'évacuation.\"");
            System.out.println("4. Centre de Commande → Logistique : REQUEST");
            System.out.println("   Message: \"Préparer ressources pour évacuation de " + nombrePersonnes + " personnes. Zone " + idZone + ". Urgence niveau " + niveauUrgence + "/5.\"");
            System.out.println("5. Logistique → Centre de Commande : CONFIRM");
            System.out.println("   Message: \"Ressources en préparation: 8 bus (capacité 320 personnes), 5 ambulances, 3 tonnes de provisions.\"");
            System.out.println("6. Police → Centre de Commande : INFORM (Progression)");
            System.out.println("   Message: \"PROGRESSION: Évacuation en cours. Personnes restantes: 120/" + nombrePersonnes + ". Périmètre sécurisé à 80%.\"");
            System.out.println("7. Police → Centre de Commande : INFORM (Succès)");
            System.out.println("   Message: \"SUCCÈS: Toutes les " + nombrePersonnes + " personnes ont été évacuées. Zone " + idZone + " entièrement sécurisée.\"");

            // Vérifier si nous sommes dans un contexte JADE
            boolean isJadeContext = isJadeRunning();

            if (!isJadeContext) {
                // Si nous ne sommes pas dans un contexte JADE (exécution depuis ScenarioTest),
                // afficher un message clair et ne pas essayer de créer des agents ou d'envoyer des messages
                System.out.println("\n=== MODE DÉMONSTRATION ===");
                System.out.println("Ce scénario est exécuté en mode démonstration (sans agents JADE).");
                System.out.println("Vous voyez cette information car vous exécutez le scénario depuis ScenarioTest.");
                System.out.println("Dans ce mode, seule la description du scénario est affichée, sans exécution réelle.");
                System.out.println("\n=== COMMENT EXÉCUTER LE SCÉNARIO COMPLET ===");
                System.out.println("Pour exécuter le scénario complet avec les agents JADE:");
                System.out.println("1. Exécutez la classe MainContainer (com.jade.RoboCupRescueProject.MainContainer)");
                System.out.println("2. Sélectionnez le scénario 'Évacuation d'Urgence' dans le menu");
                System.out.println("\n=== FIN DU MODE DÉMONSTRATION ===");
                return;
            }

            System.out.println("\n=== INITIALISATION DU SCÉNARIO ===");
            System.out.println("Préparation du scénario en cours...");

            // Créer un agent temporaire pour démarrer le scénario
            new Thread(() -> {
                try {
                    // Attendre que les agents soient prêts
                    System.out.println("Attente de l'initialisation des agents...");
                    Thread.sleep(DELAI_INITIALISATION);

                    // Vérifier à nouveau si JADE est correctement initialisé
                    if (!isJadeRunning()) {
                        System.out.println("\n=== MODE DÉMONSTRATION (THREAD) ===");
                        System.out.println("JADE n'est pas correctement initialisé dans ce thread.");
                        System.out.println("Vous voyez cette information car vous exécutez le scénario depuis ScenarioTest.");
                        System.out.println("\n=== COMMENT EXÉCUTER LE SCÉNARIO COMPLET ===");
                        System.out.println("Pour exécuter le scénario complet avec les agents JADE:");
                        System.out.println("1. Exécutez la classe MainContainer (com.jade.RoboCupRescueProject.MainContainer)");
                        System.out.println("2. Sélectionnez le scénario 'Évacuation d'Urgence' dans le menu");
                        System.out.println("\n=== FIN DU MODE DÉMONSTRATION ===");
                        return;
                    }

                    // Créer un message pour démarrer le scénario
                    System.out.println("\n=== LANCEMENT DU SCÉNARIO ===");
                    System.out.println("Envoi d'une demande de démarrage du scénario à l'agent Police-1");

                    // Créer le message à envoyer
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    AID receiverAID = new AID("Police-1", AID.ISLOCALNAME);
                    msg.addReceiver(receiverAID);

                    String messageScenario = "START_SCENARIO:EVACUATION:" + idZone + ":" + typeRisque + 
                                                ":" + nombrePersonnes + ":" + infrastructureCritique + 
                                                ":" + accessibilite + ":" + niveauUrgence;
                    msg.setContent(messageScenario);

                    System.out.println("Message à envoyer: \"" + messageScenario + "\"");

                    // Utiliser le ContainerManager pour envoyer le message
                    ContainerManager containerManager = ContainerManager.getInstance();

                    if (containerManager.getMainContainer() == null) {
                        System.out.println("\n=== ERREUR: CONTENEUR PRINCIPAL NON INITIALISÉ ===");
                        System.out.println("Le conteneur principal n'a pas été initialisé correctement.");
                        System.out.println("Assurez-vous d'exécuter le scénario depuis MainContainer.");
                        return;
                    }

                    boolean success = containerManager.sendMessage("Police-1", msg);

                    if (success) {
                        System.out.println("Message envoyé avec succès à Police-1");
                        System.out.println("\n=== EXÉCUTION DU SCÉNARIO EN COURS ===");
                        System.out.println("Veuillez observer les messages dans la console pour suivre le déroulement du scénario.");
                        System.out.println("--------------------------------------------------------");
                    } else {
                        System.out.println("\n=== ERREUR: IMPOSSIBLE D'ENVOYER LE MESSAGE ===");
                        System.out.println("Impossible d'envoyer le message à l'agent Police-1.");
                        System.out.println("Vérifiez que l'agent est bien démarré et enregistré dans le ContainerManager.");
                    }

                } catch (InterruptedException e) {
                    // Interruption du thread pendant l'attente
                    System.out.println("\n=== INTERRUPTION DU SCÉNARIO ===");
                    System.out.println("Le scénario a été interrompu pendant l'attente: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("\n=== ERREUR LORS DU DÉMARRAGE DU SCÉNARIO ===");
                    System.out.println("Type d'erreur: " + e.getClass().getSimpleName());
                    System.out.println("Message: " + e.getMessage());

                    // Vérifier si c'est une erreur liée à JADE
                    if (e.getMessage() != null && e.getMessage().contains("Platform")) {
                        System.out.println("\n=== MODE DÉMONSTRATION (ERREUR JADE) ===");
                        System.out.println("JADE n'est pas correctement initialisé.");
                        System.out.println("Vous voyez cette information car vous exécutez le scénario depuis ScenarioTest.");
                        System.out.println("\n=== COMMENT EXÉCUTER LE SCÉNARIO COMPLET ===");
                        System.out.println("Pour exécuter le scénario complet avec les agents JADE:");
                        System.out.println("1. Exécutez la classe MainContainer (com.jade.RoboCupRescueProject.MainContainer)");
                        System.out.println("2. Sélectionnez le scénario 'Évacuation d'Urgence' dans le menu");
                        System.out.println("\n=== FIN DU MODE DÉMONSTRATION ===");
                    } else {
                        // Pour les autres types d'erreurs, afficher des instructions pour démarrer manuellement
                        System.out.println("\n=== INSTRUCTIONS POUR DÉMARRER LE SCÉNARIO MANUELLEMENT ===");
                        System.out.println("Le démarrage automatique a échoué. Veuillez suivre ces instructions:");
                        System.out.println("1. Dans l'interface JADE, double-cliquez sur l'agent 'Police-1'");
                        System.out.println("2. Dans la fenêtre de l'agent, envoyez un message avec:");
                        System.out.println("   - Performative: REQUEST");
                        System.out.println("   - Receiver: Police-1");
                        System.out.println("   - Content: START_SCENARIO:EVACUATION:" + idZone + ":" + typeRisque);
                        System.out.println("=== FIN DES INSTRUCTIONS ===");
                    }
                }
            }).start();
        }

        /**
         * Vérifie si la plateforme JADE est en cours d'exécution et correctement initialisée.
         * @return true si JADE est en cours d'exécution et initialisé, false sinon
         */
        private static boolean isJadeRunning() {
            try {
                // Vérifier si nous pouvons créer un AID sans erreur
                new AID("test", AID.ISLOCALNAME);
                // Si nous arrivons ici sans exception, JADE est correctement initialisé
                return true;
            } catch (Exception e) {
                // Une exception indique que JADE n'est pas correctement initialisé
                return false;
            }
        }
    }
}