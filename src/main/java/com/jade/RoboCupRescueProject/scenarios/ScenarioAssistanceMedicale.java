package com.jade.RoboCupRescueProject.scenarios;

import com.jade.RoboCupRescueProject.utils.ContainerManager;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Scénario d'assistance médicale pour la simulation RoboCupRescue.
 * Ce scénario simule la détection de victimes nécessitant une assistance médicale,
 * l'alerte au centre de commande, et la coordination pour les soins et l'évacuation des blessés.
 */
public class ScenarioAssistanceMedicale {

    /**
     * Comportement de détection de victimes pour l'agent ambulancier.
     * Ce comportement envoie une alerte au centre de commande
     * lorsque des victimes sont détectées.
     */
    public static class DetectionVictimesBehaviour extends OneShotBehaviour {
        private final String idZone;
        private final int nombreVictimes;
        private final String typeUrgence;
        private final int niveauGravite;
        private final boolean accesVehicule;
        private final String conditionsMeteo;

        public DetectionVictimesBehaviour(String idZone, int nombreVictimes, String typeUrgence, 
                                    int niveauGravite, boolean accesVehicule, String conditionsMeteo) {
            this.idZone = idZone;
            this.nombreVictimes = nombreVictimes;
            this.typeUrgence = typeUrgence;
            this.niveauGravite = niveauGravite;
            this.accesVehicule = accesVehicule;
            this.conditionsMeteo = conditionsMeteo;
        }

        @Override
        public void action() {
            // Envoyer l'alerte au Centre de Commande
            ACLMessage alerte = new ACLMessage(ACLMessage.INFORM);
            alerte.addReceiver(new AID("CommandCenter", AID.ISLOCALNAME));

            // Construire un message d'alerte complet avec toutes les informations
            StringBuilder messageAlerte = new StringBuilder();
            messageAlerte.append("Victimes détectées dans la zone ").append(idZone);
            messageAlerte.append(", nombre de victimes : ").append(nombreVictimes);
            messageAlerte.append(", type d'urgence : ").append(typeUrgence);
            messageAlerte.append(", niveau de gravité : ").append(niveauGravite);
            messageAlerte.append(", accès véhicule : ").append(accesVehicule ? "OUI" : "NON");
            messageAlerte.append(", conditions météo : ").append(conditionsMeteo);

            alerte.setContent(messageAlerte.toString());
            myAgent.send(alerte);
        }
    }

    /**
     * Classe pour tester le scénario d'assistance médicale.
     * Cette classe lance le scénario en créant un agent temporaire
     * qui envoie un message à l'agent ambulancier pour démarrer le scénario.
     */
    public static class TestScenarioMedical {
        // Délai d'initialisation du scénario (en millisecondes)
        private static final int DELAI_INITIALISATION = 2000;  // 2 secondes

        public static void lancerScenario() {
            // Paramètres du scénario
            String idZone = "M-23";
            int nombreVictimes = 12;
            String typeUrgence = "accident de transport en commun";
            int niveauGravite = 3; // Sur une échelle de 1 à 5
            boolean accesVehicule = true;
            String conditionsMeteo = "pluie modérée";

            System.out.println("\n========================================================");
            System.out.println("=== DÉMARRAGE DU SCÉNARIO: ASSISTANCE MÉDICALE ===");
            System.out.println("========================================================");
            System.out.println("\n=== CONTEXTE DU SCÉNARIO ===");
            System.out.println("Un " + typeUrgence + " s'est produit dans la zone " + idZone);
            System.out.println("Le niveau de gravité est évalué à " + niveauGravite + "/5 et l'accès véhicule est " + (accesVehicule ? "possible" : "impossible"));
            System.out.println("Il y a environ " + nombreVictimes + " victimes à traiter et les conditions météo sont: " + conditionsMeteo);
            System.out.println("Un agent ambulancier détecte les victimes et alerte le centre de commande");
            System.out.println("Le centre de commande coordonne l'intervention médicale et l'évacuation des blessés");

            System.out.println("\n=== ACTEURS DU SCÉNARIO ===");
            System.out.println("- Agent Ambulancier (Ambulance-1)");
            System.out.println("- Centre de Commande (CommandCenter)");
            System.out.println("- Agent Logistique (Logistics-1)");
            System.out.println("- Agent Police (Police-1)");

            System.out.println("\n=== SÉQUENCE D'INTERACTION ATTENDUE ===");
            System.out.println("1. Ambulancier → Centre de Commande : INFORM");
            System.out.println("   Message: \"Victimes détectées dans la zone " + idZone + ", nombre de victimes : " + nombreVictimes + 
                             ", type d'urgence : " + typeUrgence + ", niveau de gravité : " + niveauGravite + 
                             ", accès véhicule : " + (accesVehicule ? "OUI" : "NON") + 
                             ", conditions météo : " + conditionsMeteo + "\"");
            System.out.println("2. Centre de Commande → Ambulancier : REQUEST");
            System.out.println("   Message: \"Reçu. Situation médicale critique. Commencer triage et premiers soins dans la zone " + idZone + 
                             ". Priorité 1: stabilisation des cas critiques. " +
                             "Priorité 2: préparation pour évacuation. " +
                             "Tiens-moi au courant.\"");
            System.out.println("3. Centre de Commande → Police : REQUEST");
            System.out.println("   Message: \"Urgence médicale zone " + idZone + ". Sécuriser périmètre et faciliter accès ambulances.\"");
            System.out.println("4. Ambulancier → Centre de Commande : CONFIRM");
            System.out.println("   Message: \"Bien reçu, triage en cours. 3 cas critiques identifiés, besoin de matériel supplémentaire.\"");
            System.out.println("5. Centre de Commande → Logistique : REQUEST");
            System.out.println("   Message: \"Préparer ressources médicales pour " + nombreVictimes + " victimes. Zone " + idZone + ". Urgence niveau " + niveauGravite + "/5.\"");
            System.out.println("6. Logistique → Centre de Commande : CONFIRM");
            System.out.println("   Message: \"Ressources en préparation: 4 kits de trauma, 10 litres de sang, 15 kits de perfusion, 8 brancards.\"");
            System.out.println("7. Police → Centre de Commande : INFORM");
            System.out.println("   Message: \"Périmètre sécurisé. Voie d'accès dégagée pour les ambulances. Circulation déviée.\"");
            System.out.println("8. Ambulancier → Centre de Commande : INFORM (Progression)");
            System.out.println("   Message: \"PROGRESSION: Triage terminé. 3 cas critiques, 5 cas sérieux, 4 cas légers. 7/" + nombreVictimes + " victimes stabilisées.\"");
            System.out.println("9. Ambulancier → Centre de Commande : INFORM (Succès)");
            System.out.println("   Message: \"SUCCÈS: Toutes les " + nombreVictimes + " victimes ont été stabilisées. 3 évacuées en urgence, 9 prêtes pour transport.\"");

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
                System.out.println("2. Sélectionnez le scénario 'Assistance Médicale' dans le menu");
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
                        System.out.println("2. Sélectionnez le scénario 'Assistance Médicale' dans le menu");
                        System.out.println("\n=== FIN DU MODE DÉMONSTRATION ===");
                        return;
                    }

                    // Créer un message pour démarrer le scénario
                    System.out.println("\n=== LANCEMENT DU SCÉNARIO ===");
                    System.out.println("Envoi d'une demande de démarrage du scénario à l'agent Ambulance-1");

                    // Créer le message à envoyer
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    AID receiverAID = new AID("Ambulance-1", AID.ISLOCALNAME);
                    msg.addReceiver(receiverAID);

                    String messageScenario = "START_SCENARIO:MEDICAL:" + idZone + ":" + nombreVictimes + 
                                                ":" + typeUrgence + ":" + niveauGravite + 
                                                ":" + accesVehicule + ":" + conditionsMeteo;
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

                    boolean success = containerManager.sendMessage("Ambulance-1", msg);

                    if (success) {
                        System.out.println("Message envoyé avec succès à Ambulance-1");
                        System.out.println("\n=== EXÉCUTION DU SCÉNARIO EN COURS ===");
                        System.out.println("Veuillez observer les messages dans la console pour suivre le déroulement du scénario.");
                        System.out.println("--------------------------------------------------------");
                    } else {
                        System.out.println("\n=== ERREUR: IMPOSSIBLE D'ENVOYER LE MESSAGE ===");
                        System.out.println("Impossible d'envoyer le message à l'agent Ambulance-1.");
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
                        System.out.println("2. Sélectionnez le scénario 'Assistance Médicale' dans le menu");
                        System.out.println("\n=== FIN DU MODE DÉMONSTRATION ===");
                    } else {
                        // Pour les autres types d'erreurs, afficher des instructions pour démarrer manuellement
                        System.out.println("\n=== INSTRUCTIONS POUR DÉMARRER LE SCÉNARIO MANUELLEMENT ===");
                        System.out.println("Le démarrage automatique a échoué. Veuillez suivre ces instructions:");
                        System.out.println("1. Dans l'interface JADE, double-cliquez sur l'agent 'Ambulance-1'");
                        System.out.println("2. Dans la fenêtre de l'agent, envoyez un message avec:");
                        System.out.println("   - Performative: REQUEST");
                        System.out.println("   - Receiver: Ambulance-1");
                        System.out.println("   - Content: START_SCENARIO:MEDICAL:" + idZone + ":" + nombreVictimes);
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