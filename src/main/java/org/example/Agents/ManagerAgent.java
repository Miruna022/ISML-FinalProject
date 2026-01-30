package org.example.Agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManagerAgent extends Agent {
    private int totalTransactions = 0;
    private int totalEnergyDistributed = 0;
    //buffer for incoming transactions (cuz some get lost rn)
    private List<Integer> buffer = Collections.synchronizedList(new ArrayList<Integer>());

    @Override
    protected void setup() {
        System.out.println("\u001B[36m" + "GRID MANAGER STARTED. Monitoring system..." + "\u001B[0m");

        //receive input, dont print yet
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    try{
                        int energy = Integer.parseInt(msg.getContent());
                        buffer.add(energy);
                    } catch (NumberFormatException e) {
                        System.out.println("[Manager] Received non-integer message: " + msg.getContent());
                    }
                } else {
                    block();
                }
            }
        });

        //now print!
        addBehaviour(new TickerBehaviour(this, 3000) {
            @Override
            protected void onTick() {
                if (!buffer.isEmpty()) {
                    printStats();
                }
            }
        });
    }

    private void printStats() {
        synchronized (buffer) {
            int cycleSum = 0;
            int cycleCount = buffer.size();

            for (int energy : buffer) {
                cycleSum += energy;
            }


            totalEnergyDistributed += cycleSum;
            totalTransactions += cycleCount;

            String color = "\u001B[34m";
            String reset = "\u001B[0m";

            System.out.println(color + "-".repeat(20));
            System.out.println("[GRID STATISTICS UPDATED (Last 3 seconds)]");

            for (int energy : buffer) {
                System.out.printf(color + "  > Processed Transaction: %-4d kW             | %n", energy);
            }

            System.out.printf(" Batch Total:    %-4d kW    | Count: %-2d        |%n", cycleSum, cycleCount);
            System.out.printf(" GLOBAL TOTAL:   %-4d kW    | Count: %-2d        |%n", totalEnergyDistributed, totalTransactions);
            System.out.println("-".repeat(20) + reset);

            buffer.clear();
        }
    }
}
