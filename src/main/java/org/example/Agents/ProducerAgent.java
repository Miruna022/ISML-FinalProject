package org.example.Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.util.Random;

public class ProducerAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getLocalName() + "started and registering service...");

        //Describe the agent
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        //Describe the offered service
        ServiceDescription sd = new ServiceDescription();
        sd.setName("Energy Producer Service");
        sd.setType("energy-producer");
        dfd.addServices(sd);

        try{
            DFService.register(this, dfd);  // here we register in YP
            System.out.println(getLocalName() + "service registered successfully.");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        //Adding a ContractNetResponder
        MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        addBehaviour(new ContractNetResponder(this, template) {

            protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException{
                //Random energy stock!!!
                int energyGeneration = new Random().nextInt(101) + 50; //produced energy ranges between 50-150

                //here is the consumer's request
                int energyRequest = 0;
                try{
                    energyRequest = Integer.parseInt(cfp.getContent());
                } catch (NumberFormatException e) {
                    energyRequest = 0;
                }
                System.out.println(getLocalName() + ": Generated " + energyGeneration + "kW. Request is " + energyRequest + "kW.");

                //this is the producer's decision
                if (energyGeneration >= energyRequest) {
                    int price = new Random().nextInt(5000) + 1000;
                    ACLMessage propose = cfp.createReply();
                    propose.setPerformative(ACLMessage.PROPOSE);
                    propose.setContent(String.valueOf(price));
                    return propose;
                } else {
                    System.out.println("\u001B[31m" + getLocalName() + ": Not enough energy. Declining." + "\u001B[0m");
                    throw new RefuseException("insufficient-energy");
                }
            }


            @Override
            protected ACLMessage handleAcceptProposal (ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
                ACLMessage inform = accept.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                inform.setContent("Energy sold");

                try{
                    String amount = cfp.getContent();
                    ACLMessage report = new ACLMessage(ACLMessage.INFORM);
                    report.addReceiver(new AID("manager", AID.ISLOCALNAME));
                    report.setContent(amount);
                    myAgent.send(report);
                } catch (Exception e) {
                    System.out.println("ERROR sending report to manager: " + e.getMessage());
                    e.printStackTrace();
                }

                return inform;
            }

            @Override
            protected void handleRejectProposal (ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
                // i'll comment this out since it's too crowded...
                // System.out.println(getLocalName() + ": Proposal rejected by " + reject.getSender().getLocalName());
            }
        });
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println(getLocalName() + "deregistered.");
    }
}
