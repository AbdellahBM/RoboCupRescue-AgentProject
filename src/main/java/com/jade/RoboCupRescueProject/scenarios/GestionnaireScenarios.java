package com.jade.RoboCupRescueProject.scenarios;

import com.jade.RoboCupRescueProject.utils.ConsoleColors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Structure pour stocker les informations détaillées d'un scénario.
 */
class ScenarioInfo {
    String nom;
    String description;
    List<String> acteurs;
    List<String> etapes;
    Map<String, String> parametres;

    public ScenarioInfo(String nom, String description) {
        this.nom = nom;
        this.description = description;
        this.acteurs = new ArrayList<>();
        this.etapes = new ArrayList<>();
        this.parametres = new HashMap<>();
    }
}

/**
 * Gestionnaire des scénarios de test pour la simulation RoboCupRescue.
 * Cette classe permet d'initialiser et d'exécuter différents scénarios
 * de test pour valider le fonctionnement du système multi-agent.
 */
public class GestionnaireScenarios {
    // Map des scénarios disponibles (ID -> Nom)
    private static final Map<Integer, String> scenariosDisponibles = new HashMap<>();

    // Map des informations détaillées sur les scénarios (ID -> ScenarioInfo)
    private static final Map<Integer, ScenarioInfo> scenariosInfo = new HashMap<>();

    // Flag pour indiquer si un scénario est en cours d'exécution
    private static boolean scenarioEnCours = false;

    // Flag pour indiquer si un scénario est en pause
    private static boolean scenarioEnPause = false;

    // Initialisation des scénarios disponibles
    static {
        scenariosDisponibles.put(1, "Alerte Incendie");
        scenariosDisponibles.put(2, "Évacuation d'Urgence");
        scenariosDisponibles.put(3, "Assistance Médicale");
        // Ajouter d'autres scénarios ici
    }

    /**
     * Initialise les informations détaillées sur les scénarios.
     * Cette méthode est appelée lors de l'initialisation des scénarios.
     */
    private static void initScenarioInfo() {
        // Scénario 1: Alerte Incendie
        ScenarioInfo scenarioIncendie = new ScenarioInfo(
            "Alerte Incendie",
            "Ce scénario simule la détection d'un incendie par un pompier, " +
            "l'alerte au centre de commande, et la coordination pour l'extinction et le sauvetage des victimes. " +
            "Le scénario inclut des informations sur les victimes, les matières dangereuses et l'accessibilité du bâtiment."
        );

        // Acteurs du scénario
        scenarioIncendie.acteurs.add("Agent Pompier (Firefighter-1)");
        scenarioIncendie.acteurs.add("Centre de Commande (CommandCenter)");

        // Étapes du scénario
        scenarioIncendie.etapes.add("Pompier → Centre de Commande : INFORM (Détection de l'incendie avec détails sur les victimes et conditions)");
        scenarioIncendie.etapes.add("Centre de Commande → Pompier : REQUEST (Ordre d'intervention avec priorités)");
        scenarioIncendie.etapes.add("Pompier → Centre de Commande : CONFIRM (Confirmation de l'ordre avec préparation spécifique)");
        scenarioIncendie.etapes.add("Pompier → Centre de Commande : INFORM (Progression du sauvetage et de l'extinction)");
        scenarioIncendie.etapes.add("Pompier → Centre de Commande : INFORM (Succès du sauvetage et de l'extinction)");

        // Paramètres du scénario
        scenarioIncendie.parametres.put("Bâtiment", "B-42");
        scenarioIncendie.parametres.put("Intensité du feu", "très élevée");
        scenarioIncendie.parametres.put("Nombre de victimes", "7");
        scenarioIncendie.parametres.put("Matières dangereuses", "Oui");
        scenarioIncendie.parametres.put("Accessibilité", "partiellement bloquée");
        scenarioIncendie.parametres.put("Étage", "3");

        scenariosInfo.put(1, scenarioIncendie);

        // Scénario 2: Évacuation d'Urgence
        ScenarioInfo scenarioEvacuation = new ScenarioInfo(
            "Évacuation d'Urgence",
            "Ce scénario simule l'évacuation d'une zone dangereuse nécessitant une évacuation, " +
            "l'alerte au centre de commande, et la coordination pour l'évacuation des civils. " +
            "Le scénario inclut des informations sur le nombre de personnes à évacuer, la présence d'infrastructures critiques et l'accessibilité de la zone."
        );

        // Acteurs du scénario
        scenarioEvacuation.acteurs.add("Agent Police (Police-1)");
        scenarioEvacuation.acteurs.add("Centre de Commande (CommandCenter)");
        scenarioEvacuation.acteurs.add("Agent Logistique (Logistics-1)");

        // Étapes du scénario
        scenarioEvacuation.etapes.add("Police → Centre de Commande : INFORM (Détection de la zone dangereuse avec détails)");
        scenarioEvacuation.etapes.add("Centre de Commande → Police : REQUEST (Ordre d'établir un périmètre et d'évacuer)");
        scenarioEvacuation.etapes.add("Police → Centre de Commande : CONFIRM (Confirmation de l'ordre)");
        scenarioEvacuation.etapes.add("Centre de Commande → Logistique : REQUEST (Demande de ressources pour l'évacuation)");
        scenarioEvacuation.etapes.add("Logistique → Centre de Commande : CONFIRM (Confirmation des ressources)");
        scenarioEvacuation.etapes.add("Police → Centre de Commande : INFORM (Progression de l'évacuation)");
        scenarioEvacuation.etapes.add("Police → Centre de Commande : INFORM (Succès de l'évacuation)");

        // Paramètres du scénario
        scenarioEvacuation.parametres.put("Zone", "Z-15");
        scenarioEvacuation.parametres.put("Type de danger", "Fuite de gaz toxique");
        scenarioEvacuation.parametres.put("Nombre de personnes", "120");
        scenarioEvacuation.parametres.put("Infrastructures critiques", "Hôpital, École");
        scenarioEvacuation.parametres.put("Accessibilité", "bonne");

        scenariosInfo.put(2, scenarioEvacuation);

        // Scénario 3: Assistance Médicale
        ScenarioInfo scenarioMedical = new ScenarioInfo(
            "Assistance Médicale",
            "Ce scénario simule la détection de victimes nécessitant une assistance médicale, " +
            "l'alerte au centre de commande, et la coordination pour les soins et l'évacuation des blessés. " +
            "Le scénario inclut des informations sur le nombre de victimes, le type d'urgence, le niveau de gravité, l'accès véhicule et les conditions météo."
        );

        // Acteurs du scénario
        scenarioMedical.acteurs.add("Agent Ambulancier (Ambulance-1)");
        scenarioMedical.acteurs.add("Centre de Commande (CommandCenter)");
        scenarioMedical.acteurs.add("Agent Police (Police-1)");
        scenarioMedical.acteurs.add("Agent Logistique (Logistics-1)");

        // Étapes du scénario
        scenarioMedical.etapes.add("Ambulancier → Centre de Commande : INFORM (Détection des victimes avec détails)");
        scenarioMedical.etapes.add("Centre de Commande → Ambulancier : REQUEST (Ordre de triage et premiers soins)");
        scenarioMedical.etapes.add("Centre de Commande → Police : REQUEST (Demande de sécurisation du périmètre)");
        scenarioMedical.etapes.add("Ambulancier → Centre de Commande : CONFIRM (Confirmation du triage)");
        scenarioMedical.etapes.add("Centre de Commande → Logistique : REQUEST (Demande de ressources médicales)");
        scenarioMedical.etapes.add("Logistique → Centre de Commande : CONFIRM (Confirmation des ressources)");
        scenarioMedical.etapes.add("Police → Centre de Commande : INFORM (Périmètre sécurisé)");
        scenarioMedical.etapes.add("Ambulancier → Centre de Commande : INFORM (Progression des soins)");
        scenarioMedical.etapes.add("Ambulancier → Centre de Commande : INFORM (Succès des soins et évacuation)");

        // Paramètres du scénario
        scenarioMedical.parametres.put("Lieu", "Accident routier, Route N7");
        scenarioMedical.parametres.put("Nombre de victimes", "5");
        scenarioMedical.parametres.put("Type d'urgence", "Traumatismes multiples");
        scenarioMedical.parametres.put("Niveau de gravité", "Élevé (2 critiques, 3 stables)");
        scenarioMedical.parametres.put("Accès véhicule", "Difficile");
        scenarioMedical.parametres.put("Conditions météo", "Pluie");

        scenariosInfo.put(3, scenarioMedical);
    }

    /**
     * Retourne la liste des scénarios disponibles.
     * @return Une liste de chaînes contenant les noms des scénarios disponibles
     */
    public static List<String> getListeScenarios() {
        List<String> listeScenarios = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : scenariosDisponibles.entrySet()) {
            listeScenarios.add(entry.getKey() + ". " + entry.getValue());
        }
        return listeScenarios;
    }

    /**
     * Exécute un scénario spécifique par son ID.
     * @param idScenario L'ID du scénario à exécuter
     * @return true si le scénario a été exécuté avec succès, false sinon
     */
    public static boolean executerScenario(int idScenario) {
        if (!scenariosDisponibles.containsKey(idScenario)) {
            System.out.println(ConsoleColors.formatError("Erreur: Scénario " + idScenario + " non trouvé."));
            return false;
        }

        // Vérifier si un scénario est déjà en cours d'exécution
        if (scenarioEnCours && !scenarioEnPause) {
            System.out.println("\n" + ConsoleColors.formatWarning("Attention: Un scénario est déjà en cours d'exécution."));
            System.out.println(ConsoleColors.formatInfo("Vous pouvez le mettre en pause ou l'arrêter avant d'en lancer un nouveau."));
            return false;
        }

        // Réinitialiser les flags
        scenarioEnCours = true;
        scenarioEnPause = false;

        System.out.println("\n" + ConsoleColors.formatTitle("EXÉCUTION DU SCÉNARIO " + idScenario + ": " + 
                          scenariosDisponibles.get(idScenario), ConsoleColors.GREEN_BOLD));

        boolean success = false;

        try {
            switch (idScenario) {
                case 1:
                    executerScenario1();
                    success = true;
                    break;
                case 2:
                    executerScenario2();
                    success = true;
                    break;
                case 3:
                    executerScenario3();
                    success = true;
                    break;
                default:
                    System.out.println(ConsoleColors.formatWarning("Scénario " + idScenario + " non implémenté."));
                    success = false;
            }
        } catch (Exception e) {
            System.out.println("\n" + ConsoleColors.formatTitle("ERREUR LORS DE L'EXÉCUTION DU SCÉNARIO", ConsoleColors.RED_BOLD));
            System.out.println(ConsoleColors.formatError("Type d'erreur: " + e.getClass().getSimpleName()));
            System.out.println(ConsoleColors.formatError("Message: " + e.getMessage()));
            success = false;
        } finally {
            // Réinitialiser les flags à la fin de l'exécution
            scenarioEnCours = false;
            scenarioEnPause = false;
        }

        return success;
    }

    /**
     * Met en pause ou reprend l'exécution du scénario en cours.
     * @return true si l'opération a réussi, false sinon
     */
    public static boolean pauseResumeScenario() {
        if (!scenarioEnCours) {
            System.out.println(ConsoleColors.formatWarning("Aucun scénario n'est en cours d'exécution."));
            return false;
        }

        scenarioEnPause = !scenarioEnPause;

        if (scenarioEnPause) {
            System.out.println("\n" + ConsoleColors.formatTitle("SCÉNARIO MIS EN PAUSE", ConsoleColors.YELLOW_BOLD));
            System.out.println(ConsoleColors.formatInfo("L'exécution du scénario est temporairement suspendue."));
            System.out.println(ConsoleColors.formatInfo("Appuyez sur 'p' pour reprendre l'exécution."));
        } else {
            System.out.println("\n" + ConsoleColors.formatTitle("REPRISE DU SCÉNARIO", ConsoleColors.GREEN_BOLD));
            System.out.println(ConsoleColors.formatSuccess("L'exécution du scénario reprend."));
        }

        return true;
    }

    /**
     * Arrête l'exécution du scénario en cours.
     * @return true si l'opération a réussi, false sinon
     */
    public static boolean arreterScenario() {
        if (!scenarioEnCours) {
            System.out.println(ConsoleColors.formatWarning("Aucun scénario n'est en cours d'exécution."));
            return false;
        }

        scenarioEnCours = false;
        scenarioEnPause = false;

        System.out.println("\n" + ConsoleColors.formatTitle("ARRÊT DU SCÉNARIO", ConsoleColors.RED_BOLD));
        System.out.println(ConsoleColors.formatWarning("L'exécution du scénario a été arrêtée."));

        return true;
    }

    /**
     * Initialise tous les scénarios disponibles.
     */
    public static void initialiserScenarios() {
        System.out.println(ConsoleColors.formatTitle("INITIALISATION DES SCÉNARIOS", ConsoleColors.BLUE_BOLD));
        System.out.println(ConsoleColors.formatSubtitle("SCÉNARIOS DISPONIBLES", ConsoleColors.GREEN));

        for (String scenario : getListeScenarios()) {
            System.out.println(ConsoleColors.formatMenuOption(scenario.split("\\.")[0], scenario.split("\\.")[1].trim()));
        }

        // Initialiser les informations détaillées sur les scénarios
        initScenarioInfo();

        System.out.println(ConsoleColors.formatSuccess("Tous les scénarios ont été initialisés avec succès."));
    }

    /**
     * Vérifie si un scénario est en cours d'exécution.
     * @return true si un scénario est en cours d'exécution, false sinon
     */
    public static boolean isScenarioEnCours() {
        return scenarioEnCours;
    }

    /**
     * Vérifie si un scénario est en pause.
     * @return true si un scénario est en pause, false sinon
     */
    public static boolean isScenarioEnPause() {
        return scenarioEnPause;
    }

    /**
     * Définit l'état d'exécution d'un scénario.
     * @param enCours true si un scénario est en cours d'exécution, false sinon
     */
    public static void setScenarioEnCours(boolean enCours) {
        scenarioEnCours = enCours;
    }

    /**
     * Met en pause ou reprend l'exécution d'un scénario.
     * @param enPause true pour mettre en pause, false pour reprendre
     */
    public static void setScenarioEnPause(boolean enPause) {
        scenarioEnPause = enPause;
    }

    /**
     * Retourne les informations détaillées sur un scénario spécifique.
     * @param idScenario L'ID du scénario
     * @return Les informations détaillées sur le scénario, ou null si le scénario n'existe pas
     */
    public static ScenarioInfo getScenarioInfo(int idScenario) {
        return scenariosInfo.get(idScenario);
    }

    /**
     * Retourne le nom d'un scénario spécifique.
     * @param idScenario L'ID du scénario
     * @return Le nom du scénario, ou null si le scénario n'existe pas
     */
    public static String getScenarioNom(int idScenario) {
        ScenarioInfo info = scenariosInfo.get(idScenario);
        return (info != null) ? info.nom : scenariosDisponibles.get(idScenario);
    }

    /**
     * Affiche les informations détaillées sur un scénario spécifique avec un format amélioré.
     * @param idScenario L'ID du scénario à afficher
     * @return true si le scénario a été trouvé et affiché, false sinon
     */
    public static boolean afficherDetailsScenario(int idScenario) {
        if (!scenariosDisponibles.containsKey(idScenario)) {
            System.out.println(ConsoleColors.formatError("Erreur: Scénario " + idScenario + " non trouvé."));
            return false;
        }

        ScenarioInfo info = scenariosInfo.get(idScenario);
        if (info == null) {
            System.out.println(ConsoleColors.formatError("Erreur: Informations détaillées non disponibles pour le scénario " + idScenario + "."));
            return false;
        }

        // Afficher le titre du scénario
        System.out.println("\n" + ConsoleColors.formatTitle("DÉTAILS DU SCÉNARIO " + idScenario + ": " + info.nom, ConsoleColors.CYAN_BOLD));

        // Afficher une ligne de séparation
        System.out.println(ConsoleColors.CYAN + ConsoleColors.DOUBLE_LINE.repeat(80) + ConsoleColors.RESET);

        // Afficher la description dans une boîte
        System.out.println("\n" + ConsoleColors.createBox("DESCRIPTION", info.description, ConsoleColors.BLUE_BOLD, ConsoleColors.BLUE));

        // Afficher les acteurs dans un tableau
        System.out.println("\n" + ConsoleColors.formatSubtitle("ACTEURS IMPLIQUÉS", ConsoleColors.GREEN_BOLD));

        String[][] acteurData = new String[info.acteurs.size()][1];
        for (int i = 0; i < info.acteurs.size(); i++) {
            acteurData[i][0] = info.acteurs.get(i);
        }

        String[] acteurHeaders = {"Agent"};
        System.out.println(ConsoleColors.createTable(acteurHeaders, acteurData, ConsoleColors.GREEN_BOLD, ConsoleColors.GREEN));

        // Afficher les étapes dans un tableau
        System.out.println("\n" + ConsoleColors.formatSubtitle("ÉTAPES DU SCÉNARIO", ConsoleColors.PURPLE_BOLD));

        String[][] etapeData = new String[info.etapes.size()][2];
        for (int i = 0; i < info.etapes.size(); i++) {
            etapeData[i][0] = String.valueOf(i + 1);
            etapeData[i][1] = info.etapes.get(i);
        }

        String[] etapeHeaders = {"#", "Description"};
        System.out.println(ConsoleColors.createTable(etapeHeaders, etapeData, ConsoleColors.PURPLE_BOLD, ConsoleColors.PURPLE));

        // Afficher les paramètres dans un tableau
        System.out.println("\n" + ConsoleColors.formatSubtitle("PARAMÈTRES DU SCÉNARIO", ConsoleColors.RED_BOLD));

        String[][] paramData = new String[info.parametres.size()][2];
        int i = 0;
        for (Map.Entry<String, String> entry : info.parametres.entrySet()) {
            paramData[i][0] = entry.getKey();
            paramData[i][1] = entry.getValue();
            i++;
        }

        String[] paramHeaders = {"Paramètre", "Valeur"};
        System.out.println(ConsoleColors.createTable(paramHeaders, paramData, ConsoleColors.RED_BOLD, ConsoleColors.RED));

        // Afficher des instructions pour lancer le scénario
        String instructionsContent = 
            ConsoleColors.ARROW_RIGHT + " Pour lancer ce scénario, entrez simplement '" + idScenario + "' dans le menu principal.\n" +
            ConsoleColors.ARROW_RIGHT + " Pendant l'exécution, vous pourrez mettre en pause (p) ou arrêter (s) le scénario à tout moment.\n" +
            ConsoleColors.ARROW_RIGHT + " Suivez les communications entre les agents pour comprendre le déroulement du scénario.";

        System.out.println("\n" + ConsoleColors.createBox("INSTRUCTIONS", instructionsContent, ConsoleColors.YELLOW_BOLD, ConsoleColors.YELLOW));

        return true;
    }

    /**
     * Exécute le scénario 1: Alerte Incendie.
     * Ce scénario simule la détection d'un incendie par un pompier,
     * l'alerte au centre de commande, et la coordination pour l'extinction et le sauvetage des victimes.
     */
    public static void executerScenario1() {
        System.out.println("\n" + ConsoleColors.formatTitle("SCÉNARIO: ALERTE INCENDIE", ConsoleColors.RED_BOLD));

        System.out.println("\n" + ConsoleColors.formatSubtitle("DESCRIPTION", ConsoleColors.YELLOW));
        System.out.println(ConsoleColors.YELLOW + "Ce scénario simule la détection d'un incendie par un pompier," + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "l'alerte au centre de commande, et la coordination pour l'extinction et le sauvetage des victimes." + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "Le scénario inclut des informations sur les victimes, les matières dangereuses et l'accessibilité du bâtiment." + ConsoleColors.RESET);

        System.out.println("\n" + ConsoleColors.formatSubtitle("PROTOCOLE 'ALERTE INCENDIE'", ConsoleColors.CYAN));
        System.out.println(ConsoleColors.CYAN_BOLD + "1. " + ConsoleColors.CYAN + "Pompier → Centre de Commande : INFORM (Détection de l'incendie avec détails sur les victimes et conditions)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "2. " + ConsoleColors.CYAN + "Centre de Commande → Pompier : REQUEST (Ordre d'intervention avec priorités)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "3. " + ConsoleColors.CYAN + "Pompier → Centre de Commande : CONFIRM (Confirmation de l'ordre avec préparation spécifique)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "4. " + ConsoleColors.CYAN + "Pompier → Centre de Commande : INFORM (Progression du sauvetage et de l'extinction)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "5. " + ConsoleColors.CYAN + "Pompier → Centre de Commande : INFORM (Succès du sauvetage et de l'extinction)" + ConsoleColors.RESET);

        System.out.println("\n" + ConsoleColors.formatTitle("DÉBUT DE L'EXÉCUTION DU SCÉNARIO", ConsoleColors.GREEN_BOLD) + "\n");

        // Lancer le scénario
        ScenarioAlerteIncendie.TestScenarioIncendie.lancerScenario();
    }

    /**
     * Exécute le scénario 2: Évacuation d'Urgence.
     * Ce scénario simule l'évacuation d'une zone dangereuse.
     */
    public static void executerScenario2() {
        System.out.println("\n" + ConsoleColors.formatTitle("SCÉNARIO: ÉVACUATION D'URGENCE", ConsoleColors.PURPLE_BOLD));

        System.out.println("\n" + ConsoleColors.formatSubtitle("DESCRIPTION", ConsoleColors.YELLOW));
        System.out.println(ConsoleColors.YELLOW + "Ce scénario simule l'évacuation d'une zone dangereuse nécessitant une évacuation," + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "l'alerte au centre de commande, et la coordination pour l'évacuation des civils." + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "Le scénario inclut des informations sur le nombre de personnes à évacuer, la présence d'infrastructures critiques et l'accessibilité de la zone." + ConsoleColors.RESET);

        System.out.println("\n" + ConsoleColors.formatSubtitle("PROTOCOLE 'ÉVACUATION D'URGENCE'", ConsoleColors.CYAN));
        System.out.println(ConsoleColors.CYAN_BOLD + "1. " + ConsoleColors.CYAN + "Police → Centre de Commande : INFORM (Détection de la zone dangereuse avec détails)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "2. " + ConsoleColors.CYAN + "Centre de Commande → Police : REQUEST (Ordre d'établir un périmètre et d'évacuer)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "3. " + ConsoleColors.CYAN + "Police → Centre de Commande : CONFIRM (Confirmation de l'ordre)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "4. " + ConsoleColors.CYAN + "Centre de Commande → Logistique : REQUEST (Demande de ressources pour l'évacuation)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "5. " + ConsoleColors.CYAN + "Logistique → Centre de Commande : CONFIRM (Confirmation des ressources)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "6. " + ConsoleColors.CYAN + "Police → Centre de Commande : INFORM (Progression de l'évacuation)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "7. " + ConsoleColors.CYAN + "Police → Centre de Commande : INFORM (Succès de l'évacuation)" + ConsoleColors.RESET);

        System.out.println("\n" + ConsoleColors.formatTitle("DÉBUT DE L'EXÉCUTION DU SCÉNARIO", ConsoleColors.GREEN_BOLD) + "\n");

        // Lancer le scénario
        ScenarioEvacuationUrgence.TestScenarioEvacuation.lancerScenario();
    }

    /**
     * Exécute le scénario 3: Assistance Médicale.
     * Ce scénario simule une opération d'assistance médicale.
     */
    public static void executerScenario3() {
        System.out.println("\n" + ConsoleColors.formatTitle("SCÉNARIO: ASSISTANCE MÉDICALE", ConsoleColors.BLUE_BOLD));

        System.out.println("\n" + ConsoleColors.formatSubtitle("DESCRIPTION", ConsoleColors.YELLOW));
        System.out.println(ConsoleColors.YELLOW + "Ce scénario simule la détection de victimes nécessitant une assistance médicale," + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "l'alerte au centre de commande, et la coordination pour les soins et l'évacuation des blessés." + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "Le scénario inclut des informations sur le nombre de victimes, le type d'urgence, le niveau de gravité, l'accès véhicule et les conditions météo." + ConsoleColors.RESET);

        System.out.println("\n" + ConsoleColors.formatSubtitle("PROTOCOLE 'ASSISTANCE MÉDICALE'", ConsoleColors.CYAN));
        System.out.println(ConsoleColors.CYAN_BOLD + "1. " + ConsoleColors.CYAN + "Ambulancier → Centre de Commande : INFORM (Détection des victimes avec détails)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "2. " + ConsoleColors.CYAN + "Centre de Commande → Ambulancier : REQUEST (Ordre de triage et premiers soins)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "3. " + ConsoleColors.CYAN + "Centre de Commande → Police : REQUEST (Demande de sécurisation du périmètre)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "4. " + ConsoleColors.CYAN + "Ambulancier → Centre de Commande : CONFIRM (Confirmation du triage)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "5. " + ConsoleColors.CYAN + "Centre de Commande → Logistique : REQUEST (Demande de ressources médicales)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "6. " + ConsoleColors.CYAN + "Logistique → Centre de Commande : CONFIRM (Confirmation des ressources)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "7. " + ConsoleColors.CYAN + "Police → Centre de Commande : INFORM (Périmètre sécurisé)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "8. " + ConsoleColors.CYAN + "Ambulancier → Centre de Commande : INFORM (Progression des soins)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN_BOLD + "9. " + ConsoleColors.CYAN + "Ambulancier → Centre de Commande : INFORM (Succès des soins et évacuation)" + ConsoleColors.RESET);

        System.out.println("\n" + ConsoleColors.formatTitle("DÉBUT DE L'EXÉCUTION DU SCÉNARIO", ConsoleColors.GREEN_BOLD) + "\n");

        // Lancer le scénario
        ScenarioAssistanceMedicale.TestScenarioMedical.lancerScenario();
    }
}
