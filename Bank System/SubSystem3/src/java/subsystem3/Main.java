/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subsystem3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;

/**
 *
 * @author novim
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "subsystem3Queue")
    private static Queue queue3;

    @Resource(lookup = "subsystem3ResponseQueue")
    private static Queue responseQueue3;

    @Resource(lookup = "subsystem3ResponseQueue2")
    private static Queue responseQueue3_2;

    @Resource(lookup = "subsystem2Queue")
    private static Queue queueSystem2;

    @Resource(lookup = "mestoQueue")
    private static Queue queueSystem1;

    @Resource(lookup = "subsystem3QueueDiff")
    private static Queue queueDiff;
    
    @Resource(lookup = "subsystem3QueueDiffR")
    private static Queue queueDiffR;

    public static void main(String[] args) {
        System.out.println("Servis3 is running");

        JMSContext context1 = connectionFactory.createContext();
        JMSContext context2 = connectionFactory.createContext();
        JMSProducer producer1 = context1.createProducer();
        JMSProducer producer2 = context2.createProducer();
        JMSConsumer consumer = context1.createConsumer(queue3);
//        JMSConsumer consumer2 = context2.createConsumer(queue3);
        JMSConsumer consumerR1 = context1.createConsumer(responseQueue3_2);
        JMSConsumer consumerR2 = context2.createConsumer(responseQueue3);
        Thread t2 = new Thread() {
            @Override
            public void run() {

                //posalji zahtev za dumpom baze
                TextMessage msg = context1.createTextMessage();
                try {
                    System.out.println("Radim update 2 " + LocalTime.now().toString());
                    msg.setStringProperty("command", "backUp");
                    producer1.send(queueSystem2, msg);
                    //zapamti backup
                    ObjectMessage msgO = context1.createObjectMessage();
                    msgO = (ObjectMessage) consumerR1.receive();
                    String s = (String) msgO.getObject();
                    File f = new File("C:\\Users\\novim\\Documents\\NetBeansProjects\\SubSystem3\\backup2.sql");

                    FileWriter fw = new FileWriter(f);
                    fw.write(s);
                    fw.close();
                    restoreDB("backup2", "root", "pass", "");

                    //restore db
                } catch (JMSException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        };
        Thread t1 = new Thread() {
            @Override
            public void run() {

                //posalji zahtev za dumpom baze
                TextMessage msg = context2.createTextMessage();
                try {
                    System.out.println("Radim update 1 " + LocalTime.now().toString());
                    msg.setStringProperty("command", "backUp");
                    producer2.send(queueSystem1, msg);
                    //zapamti backup
                    ObjectMessage msgO = context2.createObjectMessage();
                    msgO = (ObjectMessage) consumerR2.receive();
                    String s = (String) msgO.getObject();
                    File f = new File("C:\\Users\\novim\\Documents\\NetBeansProjects\\SubSystem3\\backup1.sql");

                    FileWriter fw = new FileWriter(f);
                    fw.write(s);
                    fw.close();
                    restoreDB("backup1", "root", "pass", "");

                    //restore db
                } catch (JMSException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        };
        final ScheduledExecutorService executorService1 = Executors.newSingleThreadScheduledExecutor();
        executorService1.scheduleAtFixedRate(t1, 0, 120, TimeUnit.SECONDS);
        final ScheduledExecutorService executorService2 = Executors.newSingleThreadScheduledExecutor();
        executorService2.scheduleAtFixedRate(t2, 10, 125, TimeUnit.SECONDS);

        while (true) {
            TextMessage msg = (TextMessage) consumer.receive();
            String command;
            try {
                command = msg.getStringProperty("command");
                System.out.println("Recived msg " + command);
                switch (command) {
                    case "diff":
                        compare();
                        break;
                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static boolean restoreDB(String dbName, String dbUserName, String dbPassword, String path) {
        String executeCmd1 = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql -u " + dbUserName + " -p" + dbPassword + " " + "-e \"drop database if exists " + dbName + "\"";
        String executeCmd2 = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql -u " + dbUserName + " -p" + dbPassword + " " + "-e \"create database if not exists " + dbName + "\"";
        Process runtimeProcess;
        try {

            runtimeProcess = Runtime.getRuntime().exec(executeCmd1);
            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                System.out.println("Database droped successfully");

                runtimeProcess = Runtime.getRuntime().exec(executeCmd2);
                processComplete = runtimeProcess.waitFor();

                if (processComplete == 0) {
                    System.out.println("Database created successfully");

                    Runtime.getRuntime().exec("C:\\Users\\novim\\Desktop\\Bacup\\" + dbName + ".cmd");
                    processComplete = runtimeProcess.waitFor();

                    if (processComplete == 0) {
                        System.out.println("Database updated successfully");
                        return true;
                    } else {
                        System.out.println("Could not updated the database");
                    }
                } else {
                    System.out.println("Could not crate the database");
                }
            } else {
                System.out.println("Could not drop the base");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static void compare() {
        try {
            //moram dobijem baze
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queueDiff);
            TextMessage msg = context.createTextMessage();
            try {
                msg.setStringProperty("command", "compare");
                producer.send(queueSystem1, msg);
                producer.send(queueSystem2, msg);

                for (int i = 0; i < 2; i++) {
                    ObjectMessage msgO = (ObjectMessage) consumer.receive();
                    if ("1".equals(msgO.getStringProperty("id"))) {
                        String s1 = (String) msgO.getObject();
                        File f1 = new File("C:\\Users\\novim\\Documents\\NetBeansProjects\\SubSystem3\\diffDB1.sql");
                        FileWriter fw = new FileWriter(f1);
                        fw.write(s1);
                        fw.close();

                    } else if ("2".equals(msgO.getStringProperty("id"))) {
                        String s2 = (String) msgO.getObject();
                        File f2= new File("C:\\Users\\novim\\Documents\\NetBeansProjects\\SubSystem3\\diffDB2.sql");
                        FileWriter fw = new FileWriter(f2);
                        fw.write(s2);
                        fw.close();
                        

                    }
                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            Runtime rt = Runtime.getRuntime();
            String cmd1 = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump --skip-comments --skip-extended-insert -u root -ppass backup1 -r C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem3\\diffBK1.sql";
            rt.exec(cmd1).waitFor();
            String cmd2 = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump --skip-comments --skip-extended-insert -u root -ppass backup2 -r C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem3\\diffBK2.sql";
            rt.exec(cmd2).waitFor();
            String resp ="";
            for (int i = 1; i < 3; i++) {
                Process proc;
                String cmdFC = "fc /a /l /t C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem3\\diffDB" + i + ".sql C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem3\\diffBK" + i + ".sql";
                proc = rt.exec(cmdFC);
                proc.waitFor();
                InputStream stdIn = proc.getInputStream();
                InputStreamReader isr = new InputStreamReader(stdIn);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    resp = resp + line + "\n";
                }
                stdIn.close();
                isr.close();
                br.close();
                int exitVal = proc.waitFor();
                System.out.println("Process exitValue: " + exitVal);
                proc.destroy();
            }
            TextMessage msgR = context.createTextMessage();
            msgR.setStringProperty("resp", resp);
            producer.send(queueDiffR, msgR);
            File f1 = new File("C:\\Users\\novim\\Documents\\NetBeansProjects\\SubSystem3\\diffDB2.sql");
            File f2 = new File("C:\\Users\\novim\\Documents\\NetBeansProjects\\SubSystem3\\diffDB1.sql");
            File f3 = new File("C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem3\\diffBK1.sql");
            File f4 = new File("C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem3\\diffBK2.sql");
            f1.delete();
            f2.delete();
            f3.delete();
            f4.delete();
            consumer.close();
            context.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
