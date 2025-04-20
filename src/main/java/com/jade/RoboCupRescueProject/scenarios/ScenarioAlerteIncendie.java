package com.jade.RoboCupRescueProject.scenarios;

import com.jade.RoboCupRescueProject.utils.ContainerManager;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Scénario d'alerte incendie pour la simulation RoboCupRescue.
 * Ce scénario simule la détection d'un incendie par un pompier,
 * l'alerte au centre de commande, et la coordination pour l'extinction.
 */
public class ScenarioAlerteIncendie {

    /**
     * Comportement de détection de feu pour l'agent pompier.
     * Ce comportement envoie une alerte au centre de commande
     * lorsqu'un incendie est détecté.
     */
    public static class DetectionFeuBehaviour extends OneShotBehaviour {
        private final String idBatiment;
        private final String intensiteFeu;
        private final int nombreVictimes;
        private final boolean matieresDangereuses;
        private final String accessibilite;
        private final int etage;

        public DetectionFeuBehaviour(String idBatiment, String intensiteFeu, int nombreVictimes, 
                                    boolean matieresDangereuses, String accessibilite, int etage) {
            this.idBatiment = idBatiment;
            this.intensiteFeu = intensiteFeu;
            this.nombreVictimes = nombreVictimes;
            this.matieresDangereuses = matieresDangereuses;
            this.accessibilite = accessibilite;
            this.etage = etage;
        }

        @Override
        public void action() {
            // Envoyer l'alerte au Centre de Commande
            ACLMessage alerte = new ACLMessage(ACLMessage.INFORM);
            alerte.addReceiver(new AID("CommandCenter", AID.ISLOCALNAME));

            // Construire un message d'alerte plus complet avec toutes les informations
            StringBuilder messageAlerte = new StringBuilder();
            messageAlerte.append("Incendie détecté au bâtiment ").append(idBatiment);
            messageAlerte.append(", intensité estimée : ").append(intensiteFeu);
            messageAlerte.append(", victimes estimées : ").append(nombreVictimes);
            messageAlerte.append(", étage : ").append(etage);
            messageAlerte.append(", matières dangereuses : ").append(matieresDangereuses ? "OUI" : "NON");
            messageAlerte.append(", accessibilité : ").append(accessibilite);

            alerte.setContent(messageAlerte.toString());

            // Logs supprimés pour réduire le bruit dans la console

            myAgent.send(alerte);
        }
    }

    /**
     * Classe pour tester le scénario d'alerte incendie.
     * Cette classe lance le scénario en créant un agent temporaire
     * qui envoie un message à l'agent pompier pour démarrer le scénario.
     */
    public static class TestScenarioIncendie {
        // Délai d'initialisation du scénario (en millisecondes)
        private static final int DELAI_INITIALISATION = 2000;  // 2 secondes

        public static void lancerScenario() {
            // Paramètres du scénario
            String idBatiment = "X";
            String intensiteFeu = "forte";
            int nombreVictimes = 3;
            boolean matieresDangereuses = true;
            String accessibilite = "difficile";
            int etage = 2;

            System.out.println("\n========================================================");
            System.out.println("=== DÉMARRAGE DU SCÉNARIO: ALERTE INCENDIE ===");
            System.out.println("========================================================");
            System.out.println("\n=== CONTEXTE DU SCÉNARIO ===");
            System.out.println("Un feu se déclare dans le bâtiment " + idBatiment + " avec une intensité " + intensiteFeu);
            System.out.println("L'incendie est situé au " + etage + "ème étage et l'accès est " + accessibilite);
            System.out.println("Il y a environ " + nombreVictimes + " victimes piégées et des matières dangereuses sont présentes: " + (matieresDangereuses ? "OUI" : "NON"));
            System.out.println("Un agent pompier détecte l'incendie et alerte le centre de commande");
            System.out.println("Le centre de commande coordonne l'intervention d'extinction et de sauvetage");

            System.out.println("\n=== ACTEURS DU SCÉNARIO ===");
            System.out.println("- Agent Pompier (Firefighter-1)");
            System.out.println("- Centre de Commande (CommandCenter)");

            System.out.println("\n=== SÉQUENCE D'INTERACTION ATTENDUE ===");
            System.out.println("1. Pompier → Centre de Commande : INFORM");
            System.out.println("   Message: \"Incendie détecté au bâtiment " + idBatiment + ", intensité estimée : " + intensiteFeu + 
                             ", victimes estimées : " + nombreVictimes + ", étage : " + etage + 
                             ", matières dangereuses : " + (matieresDangereuses ? "OUI" : "NON") + 
                             ", accessibilité : " + accessibilite + "\"");
            System.out.println("2. Centre de Commande → Pompier : REQUEST");
            System.out.println("   Message: \"Reçu. Situation critique. Dirige-toi sur zone " + idBatiment + 
                             ". Priorité 1: sauvetage des " + nombreVictimes + " victimes au " + etage + "ème étage. " +
                             "Priorité 2: extinction du feu. Attention aux matières dangereuses. " +
                             "Tiens-moi au courant.\"");
            System.out.println("3. Pompier → Centre de Commande : CONFIRM");
            System.out.println("   Message: \"Bien reçu, je pars immédiatement. Équipement spécial pour matières dangereuses activé.\"");
            System.out.println("4. Pompier → Centre de Commande : INFORM (Progression)");
            System.out.println("   Message: \"PROGRESSION: Sauvetage en cours. Victimes restantes: X/" + nombreVictimes + ". Extinction en cours. Eau utilisée: Y/Z\"");
            System.out.println("5. Pompier → Centre de Commande : INFORM (Succès)");
            System.out.println("   Message: \"SUCCES: Toutes les victimes ont été évacuées. Le feu a été éteint. Eau totale utilisée: Z\"");

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
                System.out.println("2. Sélectionnez le scénario 'Alerte Incendie' dans le menu");
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
                        System.out.println("2. Sélectionnez le scénario 'Alerte Incendie' dans le menu");
                        System.out.println("\n=== FIN DU MODE DÉMONSTRATION ===");
                        return;
                    }

                    // Créer un message pour démarrer le scénario
                    System.out.println("\n=== LANCEMENT DU SCÉNARIO ===");
                    System.out.println("Envoi d'une demande de démarrage du scénario à l'agent Firefighter-1");

                    // Créer le message à envoyer
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    AID receiverAID = new AID("Firefighter-1", AID.ISLOCALNAME);
                    msg.addReceiver(receiverAID);

                    String messageScenario = "START_SCENARIO:FIRE_ALERT:" + idBatiment + ":" + intensiteFeu + 
                                                ":" + nombreVictimes + ":" + matieresDangereuses + 
                                                ":" + accessibilite + ":" + etage;
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

                    boolean success = containerManager.sendMessage("Firefighter-1", msg);

                    if (success) {
                        System.out.println("Message envoyé avec succès à Firefighter-1");
                        System.out.println("\n=== EXÉCUTION DU SCÉNARIO EN COURS ===");
                        System.out.println("Veuillez observer les messages dans la console pour suivre le déroulement du scénario.");
                        System.out.println("--------------------------------------------------------");
                    } else {
                        System.out.println("\n=== ERREUR: IMPOSSIBLE D'ENVOYER LE MESSAGE ===");
                        System.out.println("Impossible d'envoyer le message à l'agent Firefighter-1.");
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
                        System.out.println("2. Sélectionnez le scénario 'Alerte Incendie' dans le menu");
                        System.out.println("\n=== FIN DU MODE DÉMONSTRATION ===");
                    } else {
                        // Pour les autres types d'erreurs, afficher des instructions pour démarrer manuellement
                        System.out.println("\n=== INSTRUCTIONS POUR DÉMARRER LE SCÉNARIO MANUELLEMENT ===");
                        System.out.println("Le démarrage automatique a échoué. Veuillez suivre ces instructions:");
                        System.out.println("1. Dans l'interface JADE, double-cliquez sur l'agent 'Firefighter-1'");
                        System.out.println("2. Dans la fenêtre de l'agent, envoyez un message avec:");
                        System.out.println("   - Performative: REQUEST");
                        System.out.println("   - Receiver: Firefighter-1");
                        System.out.println("   - Content: START_SCENARIO:FIRE_ALERT:" + idBatiment + ":" + intensiteFeu);
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
                // C'est un test plus fiable que simplement vérifier si Runtime.instance() existe
                new AID("test", AID.ISLOCALNAME);

                // Si nous arrivons ici sans exception, JADE est correctement initialisé
                return true;
            } catch (Exception e) {
                // Une exception (notamment "Unknown Platform Name") indique que JADE 
                // n'est pas correctement initialisé ou que nous ne sommes pas dans un conteneur JADE
                return false;
            }
        }
    }
}
