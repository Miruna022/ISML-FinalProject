package org.example;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.*;

public class Main {
    public static void main(String[] args) {
        try{
            Runtime rt = Runtime.instance();
            Profile p = new ProfileImpl();
            p.setParameter(Profile.MAIN_HOST, "localhost");
            p.setParameter(Profile.GUI, "true");
            ContainerController cc = rt.createMainContainer(p);

            //start manager
            AgentController manager = cc.createNewAgent("manager", "org.example.Agents.ManagerAgent", null);
            manager.start();


            //start 5 producers
            AgentController producer1 = cc.createNewAgent("producer1", "org.example.Agents.ProducerAgent", null);
            producer1.start();

            AgentController producer2 = cc.createNewAgent("producer2", "org.example.Agents.ProducerAgent", null);
            producer2.start();

            AgentController producer3 = cc.createNewAgent("producer3", "org.example.Agents.ProducerAgent", null);
            producer3.start();

            AgentController producer4 = cc.createNewAgent("producer4", "org.example.Agents.ProducerAgent", null);
            producer4.start();

            AgentController producer5 = cc.createNewAgent("producer5", "org.example.Agents.ProducerAgent", null);
            producer5.start();



            //start 3 consumers
            Thread.sleep(2000);
            AgentController consumer1 = cc.createNewAgent("consumer1", "org.example.Agents.ConsumerAgent", null);
            consumer1.start();

            AgentController consumer2 = cc.createNewAgent("consumer2", "org.example.Agents.ConsumerAgent", null);
            consumer2.start();

            AgentController consumer3 = cc.createNewAgent("consumer3", "org.example.Agents.ConsumerAgent", null);
            consumer3.start();


        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
