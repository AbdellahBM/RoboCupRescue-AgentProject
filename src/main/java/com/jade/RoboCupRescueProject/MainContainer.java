package com.jade.RoboCupRescueProject;

import com.jade.RoboCupRescueProject.scenarios.GestionnaireScenarios;
import com.jade.RoboCupRescueProject.utils.ConsoleColors;
import com.jade.RoboCupRescueProject.utils.ContainerManager;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainContainer {
    public static void main(String[] args) {
        try {
            System.out.println(ConsoleColors.formatTitle("INITIALISATION DU SYSTÈME ROBOCUP RESCUE", ConsoleColors.BLUE_BOLD));

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
            System.out.println(ConsoleColors.formatInfo("Création et démarrage des agents..."));
            createAndStartAgents(mainContainer);

            System.out.println("\n" + ConsoleColors.formatTitle("SYSTÈME ROBOCUP RESCUE INITIALISÉ", ConsoleColors.GREEN_BOLD));
            System.out.println(ConsoleColors.formatInfo("Vérification de la communication entre les agents..."));

            // Verify that all agents are properly initialized and can communicate
            boolean allAgentsReady = verifyAgentsReady(mainContainer);

            if (allAgentsReady) {
                System.out.println("\n" + ConsoleColors.formatTitle("SYSTÈME PRÊT", ConsoleColors.GREEN_BOLD));
                System.out.println(ConsoleColors.formatSuccess("Tous les agents sont initialisés et prêts à communiquer."));

                // Afficher le sélecteur de scénarios en mode console
                afficherMenuScenarios();
            } else {
                System.out.println("\n" + ConsoleColors.formatTitle("ERREUR D'INITIALISATION", ConsoleColors.RED_BOLD));
                System.out.println(ConsoleColors.formatError("Certains agents ne sont pas prêts. Veuillez vérifier les logs pour plus de détails."));
            }

        } catch (Exception e) {
            System.out.println("\n" + ConsoleColors.formatTitle("ERREUR CRITIQUE", ConsoleColors.RED_BOLD));
            System.out.println(ConsoleColors.formatError("Une erreur s'est produite lors de l'initialisation du système:"));
            System.out.println(ConsoleColors.formatError(e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * Affiche un menu en ligne de commande permettant de sélectionner un scénario.
     */
    private static void afficherMenuScenarios() {
        // Initialiser les scénarios
        GestionnaireScenarios.initialiserScenarios();

        // Flag pour indiquer si un scénario est en cours d'exécution
        AtomicBoolean scenarioEnCours = new AtomicBoolean(false);

        // ID du scénario en cours d'exécution
        final int[] scenarioIdEnCours = {0};

        // Créer un scanner dans un thread séparé pour éviter les blocages
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            List<String> listeScenarios = GestionnaireScenarios.getListeScenarios();

            // Afficher le titre et la description du simulateur avec un style amélioré
            System.out.println("\n" + ConsoleColors.formatTitle("ROBOCUP RESCUE - SIMULATEUR DE SECOURS", ConsoleColors.CYAN_BOLD));

            // Créer une boîte de bienvenue
            String welcomeContent = 
                "Bienvenue dans le simulateur RoboCup Rescue!\n" +
                "Ce système permet de simuler différents scénarios de secours\n" +
                "avec des agents autonomes qui collaborent pour résoudre des situations d'urgence.\n" +
                "\n" +
                "Utilisez les commandes ci-dessous pour interagir avec le simulateur.";

            System.out.println("\n" + ConsoleColors.createBox("BIENVENUE", welcomeContent, ConsoleColors.YELLOW_BOLD, ConsoleColors.BLUE));

            while (true) {
                // Afficher une ligne de séparation
                System.out.println("\n" + ConsoleColors.BLUE + ConsoleColors.DOUBLE_LINE.repeat(80) + ConsoleColors.RESET);

                // Afficher le statut actuel du système
                if (scenarioEnCours.get()) {
                    String status = "SCÉNARIO EN COURS";
                    String details = "ID: " + scenarioIdEnCours[0] + " - " + GestionnaireScenarios.getScenarioNom(scenarioIdEnCours[0]);
                    if (GestionnaireScenarios.isScenarioEnPause()) {
                        status = "SCÉNARIO EN PAUSE";
                    }
                    System.out.println(ConsoleColors.createStatusBar(status, details, ConsoleColors.YELLOW));
                } else {
                    System.out.println(ConsoleColors.createStatusBar("PRÊT", "Sélectionnez un scénario pour commencer", ConsoleColors.GREEN));
                }

                // Afficher le menu principal
                System.out.println("\n" + ConsoleColors.formatTitle("MENU PRINCIPAL", ConsoleColors.BLUE_BOLD));

                // Afficher les options disponibles en fonction de l'état du système
                if (scenarioEnCours.get()) {
                    // Si un scénario est en cours d'exécution
                    // Créer un tableau pour les commandes disponibles
                    String[][] commandData = {
                        {"p", "Mettre en pause / Reprendre le scénario"},
                        {"s", "Arrêter le scénario en cours"},
                        {"h", "Afficher l'aide et les instructions détaillées"},
                        {"q", "Quitter le simulateur"}
                    };

                    String[] headers = {"Commande", "Description"};
                    System.out.println("\n" + ConsoleColors.createTable(headers, commandData, ConsoleColors.PURPLE_BOLD, ConsoleColors.BLUE));

                    // Afficher des informations supplémentaires sur le scénario en cours
                    if (GestionnaireScenarios.isScenarioEnPause()) {
                        System.out.println("\n" + ConsoleColors.formatWarning(ConsoleColors.WARNING + " Le scénario est actuellement en pause. Appuyez sur 'p' pour reprendre."));
                    } else {
                        System.out.println("\n" + ConsoleColors.formatInfo(ConsoleColors.INFO + " Le scénario est en cours d'exécution. Vous pouvez le mettre en pause avec 'p'."));
                    }
                } else {
                    // Si aucun scénario n'est en cours d'exécution
                    // Créer un tableau pour les scénarios disponibles
                    String[][] scenarioData = new String[listeScenarios.size()][2];
                    for (int i = 0; i < listeScenarios.size(); i++) {
                        String[] parts = listeScenarios.get(i).split("\\.");
                        scenarioData[i][0] = parts[0];
                        scenarioData[i][1] = parts[1].trim();
                    }

                    String[] scenarioHeaders = {"ID", "Scénario"};
                    System.out.println("\n" + ConsoleColors.createTable(scenarioHeaders, scenarioData, ConsoleColors.GREEN_BOLD, ConsoleColors.BLUE));

                    // Créer un tableau pour les commandes disponibles
                    String[][] commandData = {
                        {"1-" + listeScenarios.size(), "Sélectionner et lancer le scénario correspondant"},
                        {"i <num>", "Afficher les informations détaillées sur un scénario (ex: i 1)"},
                        {"h", "Afficher l'aide et les instructions détaillées"},
                        {"q", "Quitter le simulateur"}
                    };

                    String[] commandHeaders = {"Commande", "Description"};
                    System.out.println("\n" + ConsoleColors.createTable(commandHeaders, commandData, ConsoleColors.PURPLE_BOLD, ConsoleColors.BLUE));
                }

                // Afficher l'invite de commande avec un style amélioré
                System.out.println("\n" + ConsoleColors.BLUE_BOLD + ConsoleColors.ARROW_RIGHT + " Entrez votre choix: " + ConsoleColors.RESET);

                try {
                    // Attendre l'entrée utilisateur sans bloquer le thread principal
                    String input = scanner.nextLine().trim();

                    // Traiter l'entrée utilisateur
                    if (input.equalsIgnoreCase("q") || input.equals("0")) {
                        // Quitter le simulateur
                        System.out.println("\n" + ConsoleColors.formatTitle("FIN DE LA SIMULATION", ConsoleColors.CYAN_BOLD));

                        String goodbyeContent = 
                            "Merci d'avoir utilisé le simulateur RoboCup Rescue!\n" +
                            "À bientôt pour de nouvelles simulations de secours.\n\n" +
                            "Développé dans le cadre du projet RoboCup Rescue.";

                        System.out.println(ConsoleColors.createBox("AU REVOIR", goodbyeContent, ConsoleColors.CYAN_BOLD, ConsoleColors.CYAN));
                        System.exit(0);
                    } else if (input.equalsIgnoreCase("h") || input.equals("?")) {
                        // Afficher l'aide
                        afficherAide(scenarioEnCours.get());
                    } else if ((input.equalsIgnoreCase("p") || input.equals(" ")) && scenarioEnCours.get()) {
                        // Mettre en pause / Reprendre le scénario (p ou espace)
                        boolean isPaused = GestionnaireScenarios.isScenarioEnPause();
                        GestionnaireScenarios.pauseResumeScenario();

                        // Afficher une barre de progression pour indiquer l'état du scénario
                        if (isPaused) {
                            // Si le scénario était en pause, il reprend maintenant
                            System.out.println(ConsoleColors.formatSuccess(ConsoleColors.ARROW_RIGHT + " Reprise du scénario " + 
                                scenarioIdEnCours[0] + ": " + GestionnaireScenarios.getScenarioNom(scenarioIdEnCours[0])));
                            System.out.println(ConsoleColors.createProgressBar(75, 50, ConsoleColors.GREEN_BOLD));
                        } else {
                            // Si le scénario était en cours, il est maintenant en pause
                            System.out.println(ConsoleColors.formatWarning(ConsoleColors.ARROW_RIGHT + " Pause du scénario " + 
                                scenarioIdEnCours[0] + ": " + GestionnaireScenarios.getScenarioNom(scenarioIdEnCours[0])));
                            System.out.println(ConsoleColors.createProgressBar(75, 50, ConsoleColors.YELLOW_BOLD));
                        }
                    } else if ((input.equalsIgnoreCase("s") || input.equals("x")) && scenarioEnCours.get()) {
                        // Arrêter le scénario (s ou x)
                        String scenarioName = GestionnaireScenarios.getScenarioNom(scenarioIdEnCours[0]);
                        GestionnaireScenarios.arreterScenario();
                        scenarioEnCours.set(false);
                        scenarioIdEnCours[0] = 0;

                        System.out.println("\n" + ConsoleColors.formatTitle("SCÉNARIO ARRÊTÉ", ConsoleColors.RED_BOLD));
                        System.out.println(ConsoleColors.formatSuccess(ConsoleColors.CHECK_MARK + " Le scénario \"" + scenarioName + "\" a été arrêté."));
                        System.out.println(ConsoleColors.formatInfo(ConsoleColors.INFO + " Vous pouvez maintenant sélectionner un autre scénario."));
                    } else if (input.startsWith("i ") || input.startsWith("info ") || input.startsWith("?")) {
                        // Afficher les informations détaillées sur un scénario (i, info ou ?)
                        if (scenarioEnCours.get()) {
                            System.out.println(ConsoleColors.formatWarning("Cette commande n'est pas disponible pendant l'exécution d'un scénario."));
                            System.out.println(ConsoleColors.formatInfo("Arrêtez d'abord le scénario en cours avec 's' ou 'x'."));
                            continue;
                        }

                        try {
                            String numPart = input.split(" ")[1].trim();
                            int idScenario = Integer.parseInt(numPart);
                            if (idScenario > 0 && idScenario <= listeScenarios.size()) {
                                GestionnaireScenarios.afficherDetailsScenario(idScenario);
                            } else {
                                System.out.println(ConsoleColors.formatWarning("Numéro de scénario invalide. Veuillez entrer un numéro entre 1 et " + listeScenarios.size() + "."));
                            }
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println(ConsoleColors.formatWarning("Format invalide. Utilisez 'i <num>' pour afficher les informations sur un scénario."));
                        } catch (NumberFormatException e) {
                            System.out.println(ConsoleColors.formatWarning("Format invalide. Utilisez 'i <num>' pour afficher les informations sur un scénario."));
                        }
                    } else if (!scenarioEnCours.get()) {
                        // Lancer un scénario
                        try {
                            int choix = Integer.parseInt(input);
                            if (choix > 0 && choix <= listeScenarios.size()) {
                                System.out.println("\n" + ConsoleColors.formatTitle("LANCEMENT DU SCÉNARIO " + choix, ConsoleColors.GREEN_BOLD));

                                // Afficher une barre de progression pour l'initialisation
                                System.out.println(ConsoleColors.formatInfo("Préparation des agents et de l'environnement en cours..."));

                                for (int i = 0; i <= 100; i += 20) {
                                    System.out.print("\r" + ConsoleColors.createProgressBar(i, 50, ConsoleColors.GREEN_BOLD));
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        // Ignorer l'interruption
                                    }
                                }
                                System.out.println("\n");

                                // Exécuter le scénario dans un thread séparé
                                final int scenarioId = choix;
                                scenarioIdEnCours[0] = scenarioId;
                                scenarioEnCours.set(true);

                                Thread scenarioThread = new Thread(() -> {
                                    boolean success = GestionnaireScenarios.executerScenario(scenarioId);

                                    if (success) {
                                        System.out.println("\n" + ConsoleColors.formatTitle("SCÉNARIO TERMINÉ", ConsoleColors.GREEN_BOLD));
                                        System.out.println(ConsoleColors.formatSuccess(ConsoleColors.CHECK_MARK + " Le scénario " + scenarioId + " est terminé avec succès."));
                                        System.out.println(ConsoleColors.createProgressBar(100, 50, ConsoleColors.GREEN_BOLD));
                                    } else {
                                        System.out.println("\n" + ConsoleColors.formatTitle("SCÉNARIO INTERROMPU", ConsoleColors.YELLOW_BOLD));
                                        System.out.println(ConsoleColors.formatWarning(ConsoleColors.WARNING + " Le scénario " + scenarioId + " a été interrompu ou a rencontré une erreur."));
                                        System.out.println(ConsoleColors.createProgressBar(50, 50, ConsoleColors.YELLOW_BOLD));
                                    }

                                    System.out.println("\n" + ConsoleColors.formatInfo(ConsoleColors.INFO + " Vous pouvez maintenant sélectionner un autre scénario ou quitter (q)."));
                                    scenarioEnCours.set(false);
                                    scenarioIdEnCours[0] = 0;
                                });

                                scenarioThread.setDaemon(true); // Le thread se terminera quand le thread principal se termine
                                scenarioThread.start();

                                // Afficher les commandes disponibles pendant l'exécution du scénario
                                System.out.println(ConsoleColors.formatInfo("Scénario en cours d'exécution. Commandes disponibles:"));
                                System.out.println(ConsoleColors.formatMenuOption("p ou ESPACE", "Mettre en pause / Reprendre le scénario"));
                                System.out.println(ConsoleColors.formatMenuOption("s ou x", "Arrêter le scénario"));
                                System.out.println(ConsoleColors.formatMenuOption("h ou ?", "Afficher l'aide"));

                                // Attendre un peu pour que le scénario démarre
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    // Ignorer l'interruption
                                }
                            } else {
                                System.out.println(ConsoleColors.formatWarning("Choix invalide! Veuillez entrer un numéro entre 1 et " + listeScenarios.size() + "."));
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(ConsoleColors.formatWarning("Entrée invalide. Veuillez entrer un nombre ou une commande valide."));
                        }
                    } else {
                        System.out.println(ConsoleColors.formatWarning("Commande non reconnue ou non disponible dans l'état actuel."));
                        System.out.println(ConsoleColors.formatInfo("Tapez 'h' ou '?' pour afficher l'aide."));
                    }
                } catch (Exception e) {
                    System.out.println("\n" + ConsoleColors.formatTitle("ERREUR DÉTECTÉE", ConsoleColors.RED_BOLD));
                    System.out.println(ConsoleColors.formatError("Une erreur s'est produite lors du traitement de votre demande:"));
                    System.out.println(ConsoleColors.formatError("→ " + e.getMessage()));
                    System.out.println(ConsoleColors.formatInfo("Veuillez réessayer ou sélectionner un autre scénario."));

                    // Afficher la trace d'erreur complète uniquement en mode développement
                    // e.printStackTrace();
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
            System.out.println(ConsoleColors.formatError("Thread principal interrompu: " + e.getMessage()));
        }
    }

    /**
     * Affiche l'aide du simulateur avec un format amélioré.
     * @param scenarioEnCours true si un scénario est en cours d'exécution, false sinon
     */
    private static void afficherAide(boolean scenarioEnCours) {
        System.out.println("\n" + ConsoleColors.formatTitle("AIDE DU SIMULATEUR", ConsoleColors.PURPLE_BOLD));

        // Section À propos dans une boîte
        String aboutContent = 
            "RoboCup Rescue est un simulateur de situations d'urgence où des agents autonomes\n" +
            "collaborent pour résoudre des situations de crise comme des incendies, des évacuations\n" +
            "ou des opérations de secours médical.\n\n" +
            "Le simulateur utilise la plateforme JADE pour créer un système multi-agent où chaque\n" +
            "agent a un rôle spécifique (pompier, ambulancier, police, etc.) et communique avec\n" +
            "les autres pour coordonner les opérations de secours.";

        System.out.println("\n" + ConsoleColors.createBox("À PROPOS", aboutContent, ConsoleColors.BLUE_BOLD, ConsoleColors.BLUE));

        // Section Commandes dans un tableau
        System.out.println("\n" + ConsoleColors.formatSubtitle("GUIDE D'UTILISATION", ConsoleColors.GREEN_BOLD));

        String[][] commandData;
        String[] commandHeaders = {"Commande", "Description", "Exemple"};

        if (scenarioEnCours) {
            commandData = new String[][] {
                {"p", "Mettre en pause ou reprendre le scénario en cours", "p"},
                {"s", "Arrêter le scénario en cours et revenir au menu principal", "s"},
                {"h", "Afficher cette aide détaillée", "h"},
                {"q", "Quitter le simulateur", "q"}
            };
        } else {
            commandData = new String[][] {
                {"<num>", "Lancer le scénario correspondant au numéro", "1"},
                {"i <num>", "Afficher les informations détaillées sur un scénario", "i 2"},
                {"h", "Afficher cette aide détaillée", "h"},
                {"q", "Quitter le simulateur", "q"}
            };
        }

        System.out.println(ConsoleColors.createTable(commandHeaders, commandData, ConsoleColors.GREEN_BOLD, ConsoleColors.BLUE));

        // Section Scénarios dans un tableau
        System.out.println("\n" + ConsoleColors.formatSubtitle("SCÉNARIOS DISPONIBLES", ConsoleColors.YELLOW_BOLD));

        String[][] scenarioData = new String[][] {
            {"1", "Alerte Incendie", "Simulation d'un incendie avec sauvetage de victimes"},
            {"2", "Évacuation d'Urgence", "Simulation d'une évacuation de zone dangereuse"},
            {"3", "Assistance Médicale", "Simulation d'une opération de secours médical"}
        };

        String[] scenarioHeaders = {"ID", "Nom", "Description"};
        System.out.println(ConsoleColors.createTable(scenarioHeaders, scenarioData, ConsoleColors.YELLOW_BOLD, ConsoleColors.BLUE));

        // Section Agents dans un tableau
        System.out.println("\n" + ConsoleColors.formatSubtitle("AGENTS DU SYSTÈME", ConsoleColors.CYAN_BOLD));

        String[][] agentData = new String[][] {
            {"Centre de Contrôle", "Supervise l'ensemble des opérations et coordonne les équipes"},
            {"Centre de Commande", "Gère les communications et dispatche les missions"},
            {"Chef d'Équipe Pompier", "Coordonne les pompiers sur le terrain"},
            {"Pompier", "Intervient pour éteindre les incendies et sauver les victimes"},
            {"Ambulancier", "Fournit les premiers soins et transporte les victimes"},
            {"Police", "Sécurise les zones et gère la circulation"},
            {"Logistique", "Gère les ressources et l'approvisionnement"},
            {"Robot", "Explore les zones dangereuses et détecte les victimes"}
        };

        String[] agentHeaders = {"Agent", "Rôle"};
        System.out.println(ConsoleColors.createTable(agentHeaders, agentData, ConsoleColors.CYAN_BOLD, ConsoleColors.BLUE));

        // Conseils d'utilisation
        String tipsContent = 
            ConsoleColors.STAR + " Pour une meilleure expérience, exécutez le simulateur dans un terminal supportant les couleurs ANSI.\n" +
            ConsoleColors.STAR + " Chaque scénario peut être mis en pause à tout moment avec la commande 'p'.\n" +
            ConsoleColors.STAR + " Utilisez 'i <num>' pour obtenir des informations détaillées sur un scénario avant de le lancer.\n" +
            ConsoleColors.STAR + " Pendant l'exécution d'un scénario, vous pouvez suivre les communications entre les agents.";

        System.out.println("\n" + ConsoleColors.createBox("CONSEILS", tipsContent, ConsoleColors.PURPLE_BOLD, ConsoleColors.PURPLE));
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
            System.out.print(ConsoleColors.GREEN_BOLD + "." + ConsoleColors.RESET);
        } catch (Exception e) {
            System.out.println("\n" + ConsoleColors.formatError("Échec du démarrage de l'agent: " + name));
            System.out.println(ConsoleColors.formatError("Cause: " + e.getMessage()));
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
            System.out.print(ConsoleColors.BLUE + "Attente de l'initialisation des agents" + ConsoleColors.RESET);
            for (int i = 0; i < 3; i++) {
                Thread.sleep(1000);
                System.out.print(ConsoleColors.BLUE + "." + ConsoleColors.RESET);
            }
            System.out.println();
        } catch (InterruptedException e) {
            System.out.println(ConsoleColors.formatError("Interruption pendant l'attente de l'initialisation des agents: " + e.getMessage()));
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

        if (agentsRunning == expectedAgents.length) {
            System.out.println(ConsoleColors.formatSuccess("Agents actifs: " + agentsRunning + "/" + expectedAgents.length));
        } else {
            System.out.println(ConsoleColors.formatWarning("Agents actifs: " + agentsRunning + "/" + expectedAgents.length));
        }

        // If all agents are running, wait a bit more to ensure they've registered with the DF
        if (allAgentsReady) {
            try {
                System.out.print(ConsoleColors.BLUE + "Finalisation de l'initialisation" + ConsoleColors.RESET);
                for (int i = 0; i < 3; i++) {
                    Thread.sleep(1000);
                    System.out.print(ConsoleColors.BLUE + "." + ConsoleColors.RESET);
                }
                System.out.println();
            } catch (InterruptedException e) {
                // Ignore interruption
            }
        }

        return allAgentsReady;
    }
}
