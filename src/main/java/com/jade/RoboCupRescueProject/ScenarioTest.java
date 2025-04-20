package com.jade.RoboCupRescueProject;

import com.jade.RoboCupRescueProject.scenarios.GestionnaireScenarios;

import java.util.List;
import java.util.Scanner;

/**
 * Classe de test pour exécuter les scénarios de simulation.
 * Cette classe permet de sélectionner et d'exécuter un scénario
 * à partir d'une interface en ligne de commande.
 */
public class ScenarioTest {
    public static void main(String[] args) {
        System.out.println("=== RoboCup Rescue - Test des Scénarios ===");
        // Initialiser les scénarios
        GestionnaireScenarios.initialiserScenarios();

        // Afficher le menu de sélection
        afficherMenuScenarios();
    }

    /**
     * Affiche un menu en ligne de commande permettant de sélectionner un scénario.
     */
    private static void afficherMenuScenarios() {
        Scanner scanner = new Scanner(System.in);
        List<String> listeScenarios = GestionnaireScenarios.getListeScenarios();

        while (true) {
            System.out.println("\nScénarios disponibles:");
            for (String scenario : listeScenarios) {
                System.out.println(scenario);
            }

            System.out.println("\nEntrez le numéro du scénario à exécuter (ou 0 pour quitter):");

            try {
                int choix = scanner.nextInt();

                if (choix == 0) {
                    System.out.println("Au revoir!");
                    break;
                } else if (choix > 0 && choix <= listeScenarios.size()) {
                    System.out.println("Exécution du scénario " + choix + "...\n");
                    GestionnaireScenarios.executerScenario(choix);

                    System.out.println("\nScénario terminé. Appuyez sur Entrée pour continuer...");
                    scanner.nextLine(); // Consommer la nouvelle ligne après nextInt()
                    scanner.nextLine(); // Attendre que l'utilisateur appuie sur Entrée
                } else {
                    System.out.println("Choix invalide. Veuillez entrer un numéro entre 1 et " + listeScenarios.size() + ".");
                }
            } catch (Exception e) {
                System.out.println("Entrée invalide. Veuillez entrer un nombre.");
                scanner.nextLine(); // Nettoyer le buffer d'entrée
            }
        }

        scanner.close();
    }
}
