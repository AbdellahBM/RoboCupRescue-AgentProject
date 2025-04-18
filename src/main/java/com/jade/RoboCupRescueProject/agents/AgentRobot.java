package com.jade.RoboCupRescueProject.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import com.jade.RoboCupRescueProject.behaviours.robot.ExplorerZoneBehaviour;
import com.jade.RoboCupRescueProject.behaviours.robot.DetecterFoyersIncendieBehaviour;
import com.jade.RoboCupRescueProject.behaviours.robot.LocaliserVictimesRobotBehaviour;

public class AgentRobot extends Agent {
    @Override
    protected void setup() {
        registerService("robot");
        addBehaviour(new ExplorerZoneBehaviour(this));
        addBehaviour(new DetecterFoyersIncendieBehaviour(this));
        addBehaviour(new LocaliserVictimesRobotBehaviour(this));
    }

    private void registerService(String type) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try { DFService.register(this, dfd); } catch (Exception e) { e.printStackTrace(); }
    }
}
