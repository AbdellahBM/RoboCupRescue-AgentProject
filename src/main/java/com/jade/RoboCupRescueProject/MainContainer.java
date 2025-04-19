package com.jade.RoboCupRescueProject;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainContainer {
    public static void main(String[] args) {
        try {
            // Create the JADE runtime
            Runtime rt = Runtime.instance();

            // Create the main container profile
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.GUI, "true"); // Enable JADE GUI

            // Create the main container
            AgentContainer mainContainer = rt.createMainContainer(profile);

            // Create and start all agents
            createAndStartAgents(mainContainer);

            System.out.println("RoboCup Rescue Simulation Started...");

        } catch (Exception e) {
            e.printStackTrace();
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
            System.out.println("Successfully started agent: " + name);
        } catch (Exception e) {
            System.err.println("Failed to start agent: " + name);
            e.printStackTrace();
        }
    }
}