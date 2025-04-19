package com.jade.RoboCupRescueProject.gui;

import javax.swing.*;
import java.awt.*;

public class SimulationGUI extends JFrame {
    private JTextArea logArea;
    private JPanel mapPanel;

    public SimulationGUI() {
        setTitle("RoboCup Rescue Simulation");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeComponents();
    }

    private void initializeComponents() {
        // Create main panels
        mapPanel = new JPanel();
        logArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(logArea);

        // Layout
        setLayout(new BorderLayout());
        add(mapPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Add controls
        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Start Simulation");
        JButton pauseButton = new JButton("Pause");
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        add(controlPanel, BorderLayout.NORTH);
    }

    public void updateLog(String message) {
        logArea.append(message + "\n");
    }

    public void updateMap(/* parameters for map update */) {
        // Update visualization
        mapPanel.repaint();
    }
}