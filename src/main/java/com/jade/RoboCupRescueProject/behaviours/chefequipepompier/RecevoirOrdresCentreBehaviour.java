package com.jade.RoboCupRescueProject.behaviours.chefequipepompier;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RecevoirOrdresCentreBehaviour extends CyclicBehaviour {
    private boolean communicationActive = true;

    public RecevoirOrdresCentreBehaviour(Agent a) {
        super(a);
        System.out.println(a.getLocalName() + ": Starting to receive orders from command centre");
    }

    @Override
    public void action() {
        // TODO: listen for REQUEST from Centre de Commande
        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.MatchContent("ORDER:.*")
        );

        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            String content = msg.getContent();
            String order = content.substring("ORDER:".length());

            // precess the order
            processOrder(order);

            // acknowledge receipt
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("ORDER_RECEIVED:" + order);
            myAgent.send(reply);
        } else {
            block();
        }

        // chek communication status every few cycles
        if (Math.random() < 0.1) { // 10% chance to check communication
            checkCommunicationStatus();

        }
    }

    private void processOrder(String order) {
        System.out.println(myAgent.getLocalName() + ": Processing order: " + order);
        // create message to distribute order to team
        ACLMessage teamMsg = new ACLMessage(ACLMessage.REQUEST);
        teamMsg.setContent("TEAM_ORDER:" + order);

        // send to assignerTeachesPompiersBehaviour for execution
        myAgent.send(teamMsg);
    }

    private void checkCommunicationStatus(){
        // simulate communication check
        communicationActive = Math.random() > 0.1; // 10% chance of communication failure

        if (!communicationActive) {
            System.out.println(myAgent.getLocalName() + ": Communication with command center lost! Activating backup plan.");
            activateBackupPlan();
        }
    }

    private void activateBackupPlan(){
        // notify team members about communication loss and backup plan activation
        ACLMessage backupMsg = new ACLMessage(ACLMessage.INFORM);
        backupMsg.setContent("ACTIVATE_BACKUP_PLAN");
        myAgent.send(backupMsg);
    }
}