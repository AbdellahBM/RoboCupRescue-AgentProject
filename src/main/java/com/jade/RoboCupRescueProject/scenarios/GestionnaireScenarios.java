package com.jade.RoboCupRescueProject.scenarios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire des scénarios de test pour la simulation RoboCupRescue.
 * Cette classe permet d'initialiser et d'exécuter différents scénarios
 * de test pour valider le fonctionnement du système multi-agent.
 */
public class GestionnaireScenarios {
    // Map des scénarios disponibles (ID -> Nom)
    private static final Map<Integer, String> scenariosDisponibles = new HashMap<>();

    // Initialisation des scénarios disponibles
    static {
        scenariosDisponibles.put(1, "Alerte Incendie");
        scenariosDisponibles.put(2, "Évacuation d'Urgence");
        scenariosDisponibles.put(3, "Assistance Médicale");
        // Ajouter d'autres scénarios ici
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
            System.out.println("Erreur: Scénario " + idScenario + " non trouvé.");
            return false;
        }

        System.out.println("\n=== Exécution du Scénario " + idScenario + ": " + 
                          scenariosDisponibles.get(idScenario) + " ===");

        switch (idScenario) {
            case 1:
                executerScenario1();
                break;
            case 2:
                executerScenario2();
                break;
            case 3:
                executerScenario3();
                break;
            default:
                System.out.println("Scénario " + idScenario + " non implémenté.");
                return false;
        }

        return true;
    }

    /**
     * Initialise tous les scénarios disponibles.
     */
    public static void initialiserScenarios() {
        System.out.println("=== Initialisation des Scénarios de Test ===");
        System.out.println("Scénarios disponibles:");

        for (String scenario : getListeScenarios()) {
            System.out.println("- " + scenario);
        }
    }

    /**
     * Exécute le scénario 1: Alerte Incendie.
     * Ce scénario simule la détection d'un incendie par un pompier,
     * l'alerte au centre de commande, et la coordination pour l'extinction et le sauvetage des victimes.
     */
    public static void executerScenario1() {
        System.out.println("Description: Ce scénario simule la détection d'un incendie par un pompier,");
        System.out.println("l'alerte au centre de commande, et la coordination pour l'extinction et le sauvetage des victimes.");
        System.out.println("Le scénario inclut des informations sur les victimes, les matières dangereuses et l'accessibilité du bâtiment.");
        System.out.println("Le scénario suit le protocole 'Alerte Incendie' avec les étapes suivantes:");
        System.out.println("1. Pompier → Centre de Commande : INFORM (Détection de l'incendie avec détails sur les victimes et conditions)");
        System.out.println("2. Centre de Commande → Pompier : REQUEST (Ordre d'intervention avec priorités)");
        System.out.println("3. Pompier → Centre de Commande : CONFIRM (Confirmation de l'ordre avec préparation spécifique)");
        System.out.println("4. Pompier → Centre de Commande : INFORM (Progression du sauvetage et de l'extinction)");
        System.out.println("5. Pompier → Centre de Commande : INFORM (Succès du sauvetage et de l'extinction)");
        System.out.println("=== Début de l'exécution du scénario ===\n");

        // Lancer le scénario
        ScenarioAlerteIncendie.TestScenarioIncendie.lancerScenario();
    }

    /**
     * Exécute le scénario 2: Évacuation d'Urgence.
     * Ce scénario simule l'évacuation d'une zone dangereuse.
     */
    public static void executerScenario2() {
        System.out.println("Description: Ce scénario simule l'évacuation d'une zone dangereuse.");
        System.out.println("=== Début de l'exécution du scénario ===\n");

        // Placeholder pour l'implémentation future
        System.out.println("Scénario 'Évacuation d'Urgence' non encore implémenté.");
        System.out.println("Ce scénario sera disponible dans une version future.");
    }

    /**
     * Exécute le scénario 3: Assistance Médicale.
     * Ce scénario simule une opération d'assistance médicale.
     */
    public static void executerScenario3() {
        System.out.println("Description: Ce scénario simule une opération d'assistance médicale.");
        System.out.println("=== Début de l'exécution du scénario ===\n");

        // Placeholder pour l'implémentation future
        System.out.println("Scénario 'Assistance Médicale' non encore implémenté.");
        System.out.println("Ce scénario sera disponible dans une version future.");
    }
}
