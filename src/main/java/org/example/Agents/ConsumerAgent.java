package org.example.Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.Date;
import java.util.Random;
import java.util.Vector;

public class ConsumerAgent extends Agent {
    @Override
    protected void setup() {
        int initialDelay = new Random().nextInt(5000);

        System.out.println(getLocalName() + "active. Waiting " + initialDelay + "ms to start cycle.");

        addBehaviour(new WakerBehaviour(this, initialDelay) {
            @Override
            protected void onWake() {
                startNegotiationCycle();
            }
        });
    }

    private void startNegotiationCycle() {
        int interval = new Random().nextInt(6000) + 6000;

        addBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                System.out.println("\u001B[33m" + "--- [" + getLocalName() + "] Starting a new negotiation cycle---" + "\u001B[0m");

                //Inserting a DFagent template
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("energy-producer");
                template.addServices(sd);

                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    if (result.length > 0) {
                        Vector<AID> sellers = new Vector<>();
                        for (DFAgentDescription dfd : result) {
                            AID seller = dfd.getName();
                            sellers.add(seller);
                        }

                        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                        for (AID seller : sellers) {
                            cfp.addReceiver(seller);
                        }
                        int energy = new Random().nextInt(145) + 5; //request between 5 and 150 energy
                        cfp.setContent(String.valueOf(energy));
                        cfp.setConversationId("energy-trade-" + System.currentTimeMillis());
                        cfp.setReplyByDate(new Date(System.currentTimeMillis() + 3000));

                        addBehaviour(new ContractNetInitiator(myAgent, cfp) {

//                                protected void handlePropose(ACLMessage propose, Vector acceptances) {
//                                    System.out.println(propose.getSender().getLocalName() + " proposed price: " + propose.getContent());
//                                }

                            protected void handleAllResponses(Vector responses, Vector acceptances) {
                                int bestPrice = Integer.MAX_VALUE;
                                ACLMessage bestProposal = null;

                                for (Object o : responses) {
                                    ACLMessage msg = (ACLMessage) o;
                                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                                        int price = Integer.parseInt(msg.getContent());
                                        if (price < bestPrice) {
                                            bestPrice = price;
                                            bestProposal = msg;
                                        }
                                    }
                                }

                                for (Object o : responses) {
                                    ACLMessage msg = (ACLMessage) o;
                                    ACLMessage reply = msg.createReply();
                                    if (msg.equals(bestProposal)) {
                                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                        System.out.println("\u001b[32m" + getLocalName() + ": Accepting proposal from " + msg.getSender().getLocalName() + " (Price: $" + bestPrice + ")" + "\u001B[0m");
                                    } else {
                                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                    }
                                    acceptances.add(reply);
                                }
                            }
                        });
                    } else {
                        System.out.println("No energy producers found");
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}