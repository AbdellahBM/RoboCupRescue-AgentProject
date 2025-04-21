package com.jade.RoboCupRescueProject.utils;

/**
 * Classe utilitaire pour ajouter des couleurs et du formatage au texte de la console.
 * Utilise les codes d'échappement ANSI pour modifier l'apparence du texte.
 */
public class ConsoleColors {
    // Couleurs de texte
    public static final String RESET = "\033[0m";  // Réinitialise toutes les modifications
    public static final String BLACK = "\033[0;30m";   // Noir
    public static final String RED = "\033[0;31m";     // Rouge
    public static final String GREEN = "\033[0;32m";   // Vert
    public static final String YELLOW = "\033[0;33m";  // Jaune
    public static final String BLUE = "\033[0;34m";    // Bleu
    public static final String PURPLE = "\033[0;35m";  // Violet
    public static final String CYAN = "\033[0;36m";    // Cyan
    public static final String WHITE = "\033[0;37m";   // Blanc

    // Couleurs de texte en gras
    public static final String BLACK_BOLD = "\033[1;30m";  // Noir gras
    public static final String RED_BOLD = "\033[1;31m";    // Rouge gras
    public static final String GREEN_BOLD = "\033[1;32m";  // Vert gras
    public static final String YELLOW_BOLD = "\033[1;33m"; // Jaune gras
    public static final String BLUE_BOLD = "\033[1;34m";   // Bleu gras
    public static final String PURPLE_BOLD = "\033[1;35m"; // Violet gras
    public static final String CYAN_BOLD = "\033[1;36m";   // Cyan gras
    public static final String WHITE_BOLD = "\033[1;37m";  // Blanc gras

    // Couleurs de fond
    public static final String BLACK_BACKGROUND = "\033[40m";  // Fond noir
    public static final String RED_BACKGROUND = "\033[41m";    // Fond rouge
    public static final String GREEN_BACKGROUND = "\033[42m";  // Fond vert
    public static final String YELLOW_BACKGROUND = "\033[43m"; // Fond jaune
    public static final String BLUE_BACKGROUND = "\033[44m";   // Fond bleu
    public static final String PURPLE_BACKGROUND = "\033[45m"; // Fond violet
    public static final String CYAN_BACKGROUND = "\033[46m";   // Fond cyan
    public static final String WHITE_BACKGROUND = "\033[47m";  // Fond blanc

    // Styles de texte
    public static final String UNDERLINE = "\033[4m";    // Souligné
    public static final String BLINK = "\033[5m";        // Clignotant
    public static final String REVERSED = "\033[7m";     // Inversé (fond/texte)
    public static final String INVISIBLE = "\033[8m";    // Invisible

    // Symboles spéciaux pour l'interface
    public static final String ARROW_RIGHT = "→";
    public static final String ARROW_LEFT = "←";
    public static final String ARROW_UP = "↑";
    public static final String ARROW_DOWN = "↓";
    public static final String CHECK_MARK = "✓";
    public static final String CROSS_MARK = "✖";
    public static final String WARNING = "⚠";
    public static final String INFO = "ℹ";
    public static final String STAR = "★";
    public static final String BULLET = "•";
    public static final String DOUBLE_LINE = "═";
    public static final String SINGLE_LINE = "─";
    public static final String VERTICAL_LINE = "│";
    public static final String CORNER_TOP_LEFT = "┌";
    public static final String CORNER_TOP_RIGHT = "┐";
    public static final String CORNER_BOTTOM_LEFT = "└";
    public static final String CORNER_BOTTOM_RIGHT = "┘";
    public static final String T_RIGHT = "├";
    public static final String T_LEFT = "┤";
    public static final String T_UP = "┴";
    public static final String T_DOWN = "┬";
    public static final String CROSS = "┼";

    /**
     * Formate un titre avec une couleur spécifique et des bordures.
     * @param title Le titre à formater
     * @param color La couleur à utiliser
     * @return Le titre formaté
     */
    public static String formatTitle(String title, String color) {
        int length = title.length() + 8; // 4 espaces de chaque côté
        StringBuilder sb = new StringBuilder();

        // Ligne supérieure
        sb.append(color).append("=".repeat(length)).append(RESET).append("\n");

        // Ligne du titre
        sb.append(color).append("=== ").append(WHITE_BOLD).append(title).append(color).append(" ===").append(RESET).append("\n");

        // Ligne inférieure
        sb.append(color).append("=".repeat(length)).append(RESET);

        return sb.toString();
    }

    /**
     * Formate un sous-titre avec une couleur spécifique et des bordures.
     * @param subtitle Le sous-titre à formater
     * @param color La couleur à utiliser
     * @return Le sous-titre formaté
     */
    public static String formatSubtitle(String subtitle, String color) {
        int length = subtitle.length() + 4; // 2 espaces de chaque côté
        StringBuilder sb = new StringBuilder();

        // Ligne du sous-titre
        sb.append(color).append("-- ").append(subtitle).append(" --").append(RESET);

        return sb.toString();
    }

    /**
     * Formate un message d'information.
     * @param message Le message à formater
     * @return Le message formaté
     */
    public static String formatInfo(String message) {
        return BLUE + "ℹ " + message + RESET;
    }

    /**
     * Formate un message d'avertissement.
     * @param message Le message à formater
     * @return Le message formaté
     */
    public static String formatWarning(String message) {
        return YELLOW_BOLD + "⚠ " + message + RESET;
    }

    /**
     * Formate un message d'erreur.
     * @param message Le message à formater
     * @return Le message formaté
     */
    public static String formatError(String message) {
        return RED_BOLD + "✖ " + message + RESET;
    }

    /**
     * Formate un message de succès.
     * @param message Le message à formater
     * @return Le message formaté
     */
    public static String formatSuccess(String message) {
        return GREEN_BOLD + "✓ " + message + RESET;
    }

    /**
     * Formate une option de menu.
     * @param key La touche ou le numéro de l'option
     * @param description La description de l'option
     * @return L'option formatée
     */
    public static String formatMenuOption(String key, String description) {
        return CYAN_BOLD + "  " + key + RESET + " - " + description;
    }

    /**
     * Crée une boîte avec un titre et un contenu.
     * @param title Le titre de la boîte
     * @param content Le contenu de la boîte
     * @param titleColor La couleur du titre
     * @param borderColor La couleur de la bordure
     * @return La boîte formatée
     */
    public static String createBox(String title, String content, String titleColor, String borderColor) {
        String[] lines = content.split("\n");
        int maxLength = title.length() + 4; // Longueur minimale basée sur le titre

        // Trouver la longueur maximale des lignes de contenu
        for (String line : lines) {
            maxLength = Math.max(maxLength, line.length() + 4); // +4 pour les marges
        }

        StringBuilder box = new StringBuilder();

        // Ligne supérieure
        box.append(borderColor).append(CORNER_TOP_LEFT);
        box.append(DOUBLE_LINE.repeat(maxLength));
        box.append(CORNER_TOP_RIGHT).append(RESET).append("\n");

        // Ligne du titre
        box.append(borderColor).append(VERTICAL_LINE).append(RESET);
        box.append(" ").append(titleColor).append(title).append(RESET);
        box.append(" ".repeat(maxLength - title.length() - 1));
        box.append(borderColor).append(VERTICAL_LINE).append(RESET).append("\n");

        // Ligne de séparation
        box.append(borderColor).append(T_RIGHT);
        box.append(SINGLE_LINE.repeat(maxLength));
        box.append(T_LEFT).append(RESET).append("\n");

        // Lignes de contenu
        for (String line : lines) {
            box.append(borderColor).append(VERTICAL_LINE).append(RESET);
            box.append(" ").append(line);
            box.append(" ".repeat(maxLength - line.length() - 1));
            box.append(borderColor).append(VERTICAL_LINE).append(RESET).append("\n");
        }

        // Ligne inférieure
        box.append(borderColor).append(CORNER_BOTTOM_LEFT);
        box.append(DOUBLE_LINE.repeat(maxLength));
        box.append(CORNER_BOTTOM_RIGHT).append(RESET);

        return box.toString();
    }

    /**
     * Crée une barre de statut avec des informations.
     * @param status Le statut principal
     * @param details Les détails supplémentaires
     * @param statusColor La couleur du statut
     * @return La barre de statut formatée
     */
    public static String createStatusBar(String status, String details, String statusColor) {
        StringBuilder statusBar = new StringBuilder();

        int totalWidth = 80; // Largeur totale de la barre de statut
        int statusWidth = status.length() + 4; // +4 pour les marges
        int detailsWidth = totalWidth - statusWidth - 3; // -3 pour les séparateurs

        statusBar.append(BLUE_BACKGROUND).append(WHITE_BOLD);
        statusBar.append(" ").append(status).append(" ");
        statusBar.append(RESET).append(statusColor);
        statusBar.append(" ").append(details);

        // Remplir le reste de la barre avec des espaces
        statusBar.append(" ".repeat(Math.max(0, detailsWidth - details.length())));
        statusBar.append(RESET);

        return statusBar.toString();
    }

    /**
     * Crée un tableau avec des en-têtes et des données.
     * @param headers Les en-têtes du tableau
     * @param data Les données du tableau (lignes x colonnes)
     * @param headerColor La couleur des en-têtes
     * @param borderColor La couleur des bordures
     * @return Le tableau formaté
     */
    public static String createTable(String[] headers, String[][] data, String headerColor, String borderColor) {
        if (headers.length == 0 || data.length == 0) {
            return "";
        }

        int[] columnWidths = new int[headers.length];

        // Déterminer la largeur de chaque colonne
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();

            for (String[] row : data) {
                if (i < row.length) {
                    columnWidths[i] = Math.max(columnWidths[i], row[i].length());
                }
            }

            // Ajouter une marge
            columnWidths[i] += 2;
        }

        StringBuilder table = new StringBuilder();

        // Ligne supérieure
        table.append(borderColor).append(CORNER_TOP_LEFT);
        for (int i = 0; i < headers.length; i++) {
            table.append(DOUBLE_LINE.repeat(columnWidths[i]));
            if (i < headers.length - 1) {
                table.append(T_DOWN);
            }
        }
        table.append(CORNER_TOP_RIGHT).append(RESET).append("\n");

        // Ligne d'en-tête
        table.append(borderColor).append(VERTICAL_LINE).append(RESET);
        for (int i = 0; i < headers.length; i++) {
            table.append(headerColor).append(" ").append(headers[i]);
            table.append(" ".repeat(columnWidths[i] - headers[i].length() - 1));
            table.append(RESET).append(borderColor).append(VERTICAL_LINE).append(RESET);
        }
        table.append("\n");

        // Ligne de séparation
        table.append(borderColor).append(T_RIGHT);
        for (int i = 0; i < headers.length; i++) {
            table.append(SINGLE_LINE.repeat(columnWidths[i]));
            if (i < headers.length - 1) {
                table.append(CROSS);
            }
        }
        table.append(T_LEFT).append(RESET).append("\n");

        // Lignes de données
        for (String[] row : data) {
            table.append(borderColor).append(VERTICAL_LINE).append(RESET);
            for (int i = 0; i < headers.length; i++) {
                String cell = (i < row.length) ? row[i] : "";
                table.append(" ").append(cell);
                table.append(" ".repeat(columnWidths[i] - cell.length() - 1));
                table.append(borderColor).append(VERTICAL_LINE).append(RESET);
            }
            table.append("\n");
        }

        // Ligne inférieure
        table.append(borderColor).append(CORNER_BOTTOM_LEFT);
        for (int i = 0; i < headers.length; i++) {
            table.append(DOUBLE_LINE.repeat(columnWidths[i]));
            if (i < headers.length - 1) {
                table.append(T_UP);
            }
        }
        table.append(CORNER_BOTTOM_RIGHT).append(RESET);

        return table.toString();
    }

    /**
     * Crée une barre de progression.
     * @param progress La progression (0-100)
     * @param width La largeur de la barre
     * @param progressColor La couleur de la progression
     * @return La barre de progression formatée
     */
    public static String createProgressBar(int progress, int width, String progressColor) {
        progress = Math.max(0, Math.min(100, progress)); // Limiter entre 0 et 100
        int progressWidth = (int) Math.round(width * progress / 100.0);

        StringBuilder progressBar = new StringBuilder();
        progressBar.append("[");
        progressBar.append(progressColor);
        progressBar.append("=".repeat(progressWidth));
        progressBar.append(RESET);
        progressBar.append(" ".repeat(width - progressWidth));
        progressBar.append("] ").append(progress).append("%");

        return progressBar.toString();
    }
}
