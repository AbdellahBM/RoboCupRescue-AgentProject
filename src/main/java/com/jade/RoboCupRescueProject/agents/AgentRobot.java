package com.jade.RoboCupRescueProject.agents;

import com.jade.RoboCupRescueProject.behaviours.robot.*;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class AgentRobot extends Agent {
    // Constants for behavior timing
    private static final long EXPLORATION_PERIOD = 2000; // 2 seconds

    // Behaviors
    private ExplorerZoneBehaviour explorerBehaviour;
    private DetecterFoyersIncendieBehaviour detecterFoyersBehaviour;
    private LocaliserVictimesRobotBehaviour localiserVictimesBehaviour;

    // Status tracking
    private boolean isOperational;
    private double batteryLevel;

    @Override
    protected void setup() {
        // Initialize agent status
        isOperational = true;
        batteryLevel = 100.0;

        // Register the robot agent in the yellow pages
        registerRobotService();

        System.out.println("Robot Agent " + getAID().getLocalName() + " is starting up.");

        try {
            // Initialize and add behaviors
            initializeBehaviors();

            // Start system status monitoring
            startStatusMonitoring();

        } catch (Exception e) {
            System.err.println("Error during Robot Agent initialization: " + e.getMessage());
            e.printStackTrace();
            doDelete(); // Terminate the agent if initialization fails
        }
    }

    private void initializeBehaviors() {
        // Create and add explorer behavior
        explorerBehaviour = new ExplorerZoneBehaviour(this, EXPLORATION_PERIOD);
        addBehaviour(explorerBehaviour);

        // Create and add fire detection behavior
        detecterFoyersBehaviour = new DetecterFoyersIncendieBehaviour(this);
        addBehaviour(detecterFoyersBehaviour);

        // Create and add victim localization behavior
        localiserVictimesBehaviour = new LocaliserVictimesRobotBehaviour(this);
        addBehaviour(localiserVictimesBehaviour);

        System.out.println(getLocalName() + ": All behaviors initialized successfully");
    }

    private void registerRobotService() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("rescue-robot");
        sd.setName(getLocalName() + "-rescue-robot");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println(getLocalName() + ": Registered with Directory Facilitator");
        } catch (FIPAException fe) {
            System.err.println(getLocalName() + ": Failed to register with Directory Facilitator");
            fe.printStackTrace();
        }
    }

    private void startStatusMonitoring() {
        // Add a behavior to monitor robot status (battery, sensors, etc.)
        addBehaviour(new jade.core.behaviours.TickerBehaviour(this, 5000) {
            protected void onTick() {
                updateStatus();
            }
        });
    }

    private void updateStatus() {
        // Simulate battery drain
        batteryLevel = Math.max(0, batteryLevel - 0.1);

        // Check if battery is critically low
        if (batteryLevel < 10.0) {
            notifyLowBattery();
        }

        // Update operational status
        isOperational = batteryLevel > 5.0;
    }

    private void notifyLowBattery() {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(jade.lang.acl.ACLMessage.INFORM);
        msg.setContent("LOW_BATTERY:" + String.format("%.2f", batteryLevel) + "%");
        send(msg);
    }

    @Override
    protected void takeDown() {
        // Deregister from the Directory Facilitator
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Perform cleanup
        cleanup();

        System.out.println("Robot Agent " + getAID().getLocalName() + " terminating.");
    }

    private void cleanup() {
        // Stop all ongoing operations
        isOperational = false;

        // Send final status report
        sendFinalReport();
    }

    private void sendFinalReport() {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(jade.lang.acl.ACLMessage.INFORM);
        msg.setContent("SHUTDOWN_REPORT:battery=" + String.format("%.2f", batteryLevel));
        send(msg);
    }

    // Public methods that can be used by behaviors
    public boolean isOperational() {
        return isOperational;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void reportError(String errorMessage) {
        System.err.println(getLocalName() + " Error: " + errorMessage);
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(jade.lang.acl.ACLMessage.FAILURE);
        msg.setContent("ERROR:" + errorMessage);
        send(msg);
    }

    public void updateSensorStatus(String sensorType, boolean isWorking) {
        // Method to update sensor status
        if (!isWorking) {
            reportError("Sensor malfunction: " + sensorType);
        }
    }
}