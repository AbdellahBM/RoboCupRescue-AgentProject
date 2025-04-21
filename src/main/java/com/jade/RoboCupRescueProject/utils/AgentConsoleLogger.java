package com.jade.RoboCupRescueProject.utils;

import jade.core.Agent;

/**
 * Classe utilitaire pour afficher des informations sur les agents dans la console
 * de manière claire et compréhensible pour l'utilisateur.
 */
public class AgentConsoleLogger {

    /**
     * Affiche un message de démarrage d'agent
     * @param agent L'agent qui démarre
     */
    public static void logAgentStarting(Agent agent) {
        String agentType = getAgentType(agent.getLocalName());
        String message = String.format("L'agent %s %s démarre...", 
                                      agentType, 
                                      agent.getLocalName());
        
        System.out.println(ConsoleColors.formatTitle("DÉMARRAGE AGENT", ConsoleColors.GREEN_BOLD));
        System.out.println(ConsoleColors.formatInfo(message));
    }

    /**
     * Affiche un message de fin d'agent
     * @param agent L'agent qui s'arrête
     */
    public static void logAgentStopping(Agent agent) {
        String agentType = getAgentType(agent.getLocalName());
        String message = String.format("L'agent %s %s s'arrête...", 
                                      agentType, 
                                      agent.getLocalName());
        
        System.out.println(ConsoleColors.formatTitle("ARRÊT AGENT", ConsoleColors.RED_BOLD));
        System.out.println(ConsoleColors.formatInfo(message));
    }

    /**
     * Affiche un message d'action d'agent
     * @param agent L'agent qui effectue l'action
     * @param action L'action effectuée
     * @param details Les détails de l'action
     */
    public static void logAgentAction(Agent agent, String action, String details) {
        String agentType = getAgentType(agent.getLocalName());
        
        String[][] data = {
            {agent.getLocalName(), agentType, action, details}
        };
        
        String[] headers = {"Nom", "Type", "Action", "Détails"};
        
        System.out.println(ConsoleColors.createTable(headers, data, ConsoleColors.BLUE_BOLD, ConsoleColors.BLUE));
    }

    /**
     * Affiche un statut d'agent
     * @param agent L'agent dont on affiche le statut
     * @param status Le statut de l'agent
     * @param details Les détails du statut
     */
    public static void logAgentStatus(Agent agent, String status, String details) {
        String agentType = getAgentType(agent.getLocalName());
        String statusColor = getStatusColor(status);
        
        System.out.println(ConsoleColors.createStatusBar(
            String.format("Agent %s: %s", agentType, agent.getLocalName()),
            String.format("Statut: %s | %s", status, details),
            statusColor
        ));
    }

    /**
     * Affiche un rapport de centre de commande
     * @param agent L'agent centre de commande
     * @param fires Nombre d'incendies
     * @param victims Nombre de victimes
     * @param roadIssues Nombre de problèmes routiers
     * @param missions Nombre de missions
     */
    public static void logCommandCenterReport(Agent agent, int fires, int victims, int roadIssues, int missions) {
        String[][] data = {
            {"Incendies", String.valueOf(fires), getFireSeverity(fires)},
            {"Victimes", String.valueOf(victims), getVictimSeverity(victims)},
            {"Problèmes routiers", String.valueOf(roadIssues), getRoadSeverity(roadIssues)},
            {"Missions", String.valueOf(missions), ""}
        };
        
        String[] headers = {"Type", "Nombre", "Gravité"};
        
        System.out.println(ConsoleColors.formatTitle("RAPPORT DU CENTRE DE COMMANDE", ConsoleColors.CYAN_BOLD));
        System.out.println(ConsoleColors.createTable(headers, data, ConsoleColors.BLUE_BOLD, ConsoleColors.BLUE));
    }

    /**
     * Affiche un message de mission pour un agent pompier
     * @param agent L'agent pompier
     * @param location Lieu de la mission
     * @param waterLevel Niveau d'eau
     * @param isAvailable Disponibilité
     */
    public static void logFirefighterStatus(Agent agent, String location, double waterLevel, boolean isAvailable) {
        String status = isAvailable ? "Disponible" : "En mission";
        String waterStatus = String.format("Eau: %.1f litres", waterLevel);
        String locationInfo = String.format("Position: %s", location);
        
        String[][] data = {
            {agent.getLocalName(), status, waterStatus, locationInfo}
        };
        
        String[] headers = {"Pompier", "Statut", "Ressources", "Position"};
        
        System.out.println(ConsoleColors.createTable(headers, data, ConsoleColors.RED_BOLD, ConsoleColors.RED));
    }

    /**
     * Détermine le type d'agent à partir de son nom local
     * @param localName Nom local de l'agent
     * @return Type d'agent
     */
    private static String getAgentType(String localName) {
        if (localName.contains("Pompier")) return "Pompier";
        if (localName.contains("Ambulancier")) return "Ambulancier";
        if (localName.contains("Police")) return "Police";
        if (localName.contains("Robot")) return "Robot";
        if (localName.contains("Logistique")) return "Logistique";
        if (localName.contains("CentreControle")) return "Centre de Contrôle";
        if (localName.contains("CentreCommande")) return "Centre de Commande";
        if (localName.contains("ChefEquipe")) return "Chef d'Équipe";
        return "Inconnu";
    }

    /**
     * Détermine la couleur à utiliser en fonction du statut
     * @param status Statut de l'agent
     * @return Couleur correspondante
     */
    private static String getStatusColor(String status) {
        if (status.contains("READY") || status.contains("Disponible")) return ConsoleColors.GREEN;
        if (status.contains("BUSY") || status.contains("En mission")) return ConsoleColors.YELLOW;
        if (status.contains("CRITICAL") || status.contains("Urgence")) return ConsoleColors.RED;
        if (status.contains("WARNING") || status.contains("Avertissement")) return ConsoleColors.YELLOW;
        return ConsoleColors.BLUE;
    }

    /**
     * Détermine la gravité des incendies
     * @param fires Nombre d'incendies
     * @return Description de la gravité
     */
    private static String getFireSeverity(int fires) {
        if (fires == 0) return ConsoleColors.GREEN + "Aucun" + ConsoleColors.RESET;
        if (fires < 3) return ConsoleColors.YELLOW + "Modérée" + ConsoleColors.RESET;
        return ConsoleColors.RED + "Critique" + ConsoleColors.RESET;
    }

    /**
     * Détermine la gravité des victimes
     * @param victims Nombre de victimes
     * @return Description de la gravité
     */
    private static String getVictimSeverity(int victims) {
        if (victims == 0) return ConsoleColors.GREEN + "Aucune" + ConsoleColors.RESET;
        if (victims < 5) return ConsoleColors.YELLOW + "Modérée" + ConsoleColors.RESET;
        return ConsoleColors.RED + "Critique" + ConsoleColors.RESET;
    }

    /**
     * Détermine la gravité des problèmes routiers
     * @param issues Nombre de problèmes
     * @return Description de la gravité
     */
    private static String getRoadSeverity(int issues) {
        if (issues == 0) return ConsoleColors.GREEN + "Aucun" + ConsoleColors.RESET;
        if (issues < 3) return ConsoleColors.YELLOW + "Modérés" + ConsoleColors.RESET;
        return ConsoleColors.RED + "Critiques" + ConsoleColors.RESET;
    }
}