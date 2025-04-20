package com.jade.RoboCupRescueProject;

import com.jade.RoboCupRescueProject.scenarios.GestionnaireScenarios;
import com.jade.RoboCupRescueProject.utils.ContainerManager;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


import java.util.List;
import java.util.Scanner;

public class MainContainer {
    public static void main(String[] args) {
        try {
            System.out.println("\n=== INITIALISATION DU SYSTÈME ROBOCUP RESCUE ===");

            // Create the JADE runtime
            Runtime rt = Runtime.instance();

            // Create the main container profile
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.GUI, "false"); // Disable JADE GUI for console-only visualization
            // Disable JADE logging
            profile.setParameter("jade_core_messaging_MessageManager_deliverytimethreshold", "-1");

            // Create the main container
            AgentContainer mainContainer = rt.createMainContainer(profile);

            // Store the main container in the ContainerManager
            ContainerManager.getInstance().setMainContainer(mainContainer);

            // Create and start all agents
            System.out.println("Création et démarrage des agents...");
            createAndStartAgents(mainContainer);

            System.out.println("\n=== SYSTÈME ROBOCUP RESCUE INITIALISÉ ===");
            System.out.println("Vérification de la communication entre les agents...");

            // Verify that all agents are properly initialized and can communicate
            boolean allAgentsReady = verifyAgentsReady(mainContainer);

            if (allAgentsReady) {
                System.out.println("\n=== SYSTÈME PRÊT ===");
                System.out.println("Tous les agents sont initialisés et prêts à communiquer.");

                // Afficher le sélecteur de scénarios en mode console
                afficherMenuScenarios();
            } else {
                System.err.println("\n=== ERREUR D'INITIALISATION ===");
                System.err.println("Certains agents ne sont pas prêts. Veuillez vérifier les logs pour plus de détails.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche un menu en ligne de commande permettant de sélectionner un scénario.
     */
    private static void afficherMenuScenarios() {
        // Initialiser les scénarios
        GestionnaireScenarios.initialiserScenarios();

        // Créer un scanner dans un thread séparé pour éviter les blocages
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            List<String> listeScenarios = GestionnaireScenarios.getListeScenarios();

            System.out.println("\n=== RoboCup Rescue - Sélecteur de Scénarios ===");

            while (true) {
                System.out.println("\nScénarios disponibles:");
                for (String scenario : listeScenarios) {
                    System.out.println(scenario);
                }

                System.out.println("\nEntrez le numéro du scénario à exécuter (ou 0 pour quitter):");

                try {
                    // Attendre l'entrée utilisateur sans bloquer le thread principal
                    String input = scanner.nextLine();
                    int choix;

                    try {
                        choix = Integer.parseInt(input.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Entrée invalide. Veuillez entrer un nombre.");
                        continue;
                    }

                    if (choix == 0) {
                        System.out.println("Fin de la simulation. Au revoir!");
                        System.exit(0);
                    } else if (choix > 0 && choix <= listeScenarios.size()) {
                        System.out.println("Exécution du scénario " + choix + "...\n");

                        // Exécuter le scénario dans un thread séparé
                        final int scenarioId = choix;
                        Thread scenarioThread = new Thread(() -> {
                            GestionnaireScenarios.executerScenario(scenarioId);
                            System.out.println("\nScénario terminé. Vous pouvez maintenant sélectionner un autre scénario.");
                        });
                        scenarioThread.setDaemon(true); // Le thread se terminera quand le thread principal se termine
                        scenarioThread.start();

                        // Attendre un peu pour que le scénario démarre
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // Ignorer l'interruption
                        }
                    } else {
                        System.out.println("Choix invalide. Veuillez entrer un numéro entre 1 et " + listeScenarios.size() + ".");
                    }
                } catch (Exception e) {
                    System.out.println("Erreur lors du traitement de l'entrée: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        // Démarrer le thread d'entrée utilisateur
        inputThread.setDaemon(false); // Le thread ne se terminera pas quand le thread principal se termine
        inputThread.start();

        // Attendre que le thread d'entrée utilisateur se termine (ce qui n'arrivera jamais sauf si l'utilisateur quitte)
        try {
            inputThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread principal interrompu: " + e.getMessage());
        }
    }

    private static void createAndStartAgents(AgentContainer container) {
        try {
            // Create Control Center Agent
            startAgent(container, "ControlCenter",
                    "com.jade.RoboCupRescueProject.agents.AgentCentreControle");

            // Create Command Center Agent
            startAgent(container, "CommandCenter",
                    "com.jade.RoboCupRescueProject.agents.AgentCentreCommande");

            // Create Fire Team Leader
            startAgent(container, "FireTeamLeader",
                    "com.jade.RoboCupRescueProject.agents.AgentChefEquipePompier");

            // Create Ambulance Agent
            startAgent(container, "Ambulance-1",
                    "com.jade.RoboCupRescueProject.agents.AgentAmbulancier");

            // Create Logistics Agent
            startAgent(container, "Logistics-1",
                    "com.jade.RoboCupRescueProject.agents.AgentLogistique");

            // Create Police Agent
            startAgent(container, "Police-1",
                    "com.jade.RoboCupRescueProject.agents.AgentPolice");

            // Create Firefighter Agent
            startAgent(container, "Firefighter-1",
                    "com.jade.RoboCupRescueProject.agents.AgentPompier");

            // Create Robot Agent
            startAgent(container, "Robot-1",
                    "com.jade.RoboCupRescueProject.agents.AgentRobot");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startAgent(AgentContainer container, String name, String className) {
        try {
            AgentController agent = container.createNewAgent(name, className, null);
            agent.start();

            // Register the agent controller in the ContainerManager
            ContainerManager.getInstance().registerAgent(name, agent);

            // Simplified logging - just a dot to indicate progress without cluttering the console
            System.out.print(".");
        } catch (Exception e) {
            System.err.println("\nFailed to start agent: " + name);
            e.printStackTrace();
        }
    }

    /**
     * Verify that all agents are properly initialized and can communicate.
     * This method checks if all expected agents are running by verifying their controllers.
     * 
     * @param container The agent container
     * @return true if all agents are ready, false otherwise
     */
    private static boolean verifyAgentsReady(AgentContainer container) {
        // Define the expected agent names
        String[] expectedAgents = {
            "Firefighter-1",    // Firefighter
            "Ambulance-1",      // Ambulance
            "Police-1",         // Police
            "CommandCenter",    // Command Center
            "ControlCenter",    // Control Center
            "FireTeamLeader",   // Fire Team Leader
            "Logistics-1",      // Logistics
            "Robot-1"           // Robot
        };

        // Wait for agents to initialize
        try {
            System.out.print("Attente de l'initialisation des agents");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(1000);
                System.out.print(".");
            }
            System.out.println();
        } catch (InterruptedException e) {
            System.err.println("Interruption pendant l'attente de l'initialisation des agents: " + e.getMessage());
            return false;
        }

        // Check if all expected agents are running
        boolean allAgentsReady = true;
        int agentsRunning = 0;

        for (String agentName : expectedAgents) {
            try {
                AgentController agent = container.getAgent(agentName);
                if (agent != null) {
                    agentsRunning++;
                } else {
                    allAgentsReady = false;
                }
            } catch (Exception e) {
                allAgentsReady = false;
            }
        }

        System.out.println("Agents actifs: " + agentsRunning + "/" + expectedAgents.length);

        // If all agents are running, wait a bit more to ensure they've registered with the DF
        if (allAgentsReady) {
            try {
                System.out.print("Finalisation de l'initialisation");
                for (int i = 0; i < 3; i++) {
                    Thread.sleep(1000);
                    System.out.print(".");
                }
                System.out.println();
            } catch (InterruptedException e) {
                // Ignore interruption
            }
        }

        return allAgentsReady;
    }
}
