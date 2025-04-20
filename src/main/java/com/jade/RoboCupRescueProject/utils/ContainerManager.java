package com.jade.RoboCupRescueProject.utils;

import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class to manage the JADE container and agent references.
 * This class provides a centralized way to access the main container and agent controllers
 * from anywhere in the application, avoiding the need to create new containers.
 */
public class ContainerManager {
    private static ContainerManager instance;
    private AgentContainer mainContainer;
    private Map<String, AgentController> agentControllers = new HashMap<>();

    /**
     * Private constructor to enforce singleton pattern
     */
    private ContainerManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the singleton instance of the ContainerManager
     * @return The ContainerManager instance
     */
    public static synchronized ContainerManager getInstance() {
        if (instance == null) {
            instance = new ContainerManager();
        }
        return instance;
    }

    /**
     * Set the main container reference
     * @param container The main JADE container
     */
    public void setMainContainer(AgentContainer container) {
        this.mainContainer = container;
    }

    /**
     * Get the main container reference
     * @return The main JADE container
     */
    public AgentContainer getMainContainer() {
        return mainContainer;
    }

    /**
     * Register an agent controller with a name
     * @param name The name of the agent
     * @param controller The agent controller
     */
    public void registerAgent(String name, AgentController controller) {
        agentControllers.put(name, controller);
    }

    /**
     * Get an agent controller by name
     * @param name The name of the agent
     * @return The agent controller, or null if not found
     */
    public AgentController getAgent(String name) {
        return agentControllers.get(name);
    }

    /**
     * Check if an agent exists
     * @param name The name of the agent
     * @return true if the agent exists, false otherwise
     */
    public boolean hasAgent(String name) {
        return agentControllers.containsKey(name);
    }

    /**
     * Send a message to an agent using its controller
     * @param receiverName The name of the receiving agent
     * @param message The message to send
     * @return true if the message was sent successfully, false otherwise
     */
    public boolean sendMessage(String receiverName, ACLMessage message) {
        try {
            if (!hasAgent(receiverName)) {
                System.err.println("Agent not found: " + receiverName);
                return false;
            }

            // Create a temporary agent to send the message
            jade.core.Agent tempAgent = new jade.core.Agent() {
                @Override
                protected void setup() {
                    send(message);
                    doDelete();
                }
            };

            // Start the temporary agent in the main container
            mainContainer.acceptNewAgent("TempMessageSender_" + System.currentTimeMillis(), tempAgent).start();
            return true;
        } catch (Exception e) {
            System.err.println("Error sending message to " + receiverName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}