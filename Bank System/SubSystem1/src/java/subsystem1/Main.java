/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subsystem1;

import enteties.Filijala;
import enteties.Komitent;
import enteties.Mesto;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author novim
 */
public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "mestoQueue")
    private static Queue mestoQueue;

    @Resource(lookup = "mestoResponseQueue")
    private static Queue mestoResponseQueue;

    @Resource(lookup = "subsystem2Queue")
    private static Queue queue2;

    @Resource(lookup = "subsystem3ResponseQueue")
    private static Queue responseQueueSubSystem3;

    @Resource(lookup = "subsystem3QueueDiff")
    private static Queue queueDiff;

    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Servis1 is running");

        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(mestoQueue);

        while (true) {
            try {
                TextMessage msg = (TextMessage) consumer.receive();
                String command = msg.getStringProperty("command");
                System.out.println("Recived msg " + command);
                switch (command) {
                    case "createMesto":
                        createMesto(msg);
                        break;
                    case "getAllMesto":
                        getAll(context, producer);
                        break;
                    case "createFilijala":
                        createFilijala(msg);
                        break;
                    case "getAllFilijala":
                        getAllFilijale(context, producer);
                        break;
                    case "createKomitent":
                        int id = createKomitent(msg);
                        TextMessage msg1 = context.createTextMessage();
                        msg1.setIntProperty("id", id);
                        msg1.setStringProperty("naziv", msg.getStringProperty("naziv"));
                        msg1.setStringProperty("adresa", msg.getStringProperty("adresa"));
                        msg1.setIntProperty("mesto", msg.getIntProperty("mesto"));
                        msg1.setStringProperty("command", "createKomitent");
                        producer.send(queue2, msg1);
                        break;
                    case "upadteKomitent":
                        udpateKomitent(msg);
                        break;
                    case "getAllKomitent":
                        getAllKomitent(context, producer);
                        break;
                    case "backUp":
                        backup(context, producer);
                        break;
                    case "compare":
                        String data = "";
                        String cmd = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump --skip-comments --skip-extended-insert -u root -ppass bankabaza1 -r C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem1\\diff.sql";
                         {
                            try {
                                Runtime.getRuntime().exec(cmd).waitFor();
                                File f = new File("C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem1\\diff.sql");
                                Scanner myReader = new Scanner(f);
                                while (myReader.hasNextLine()) {
                                    data = data + myReader.nextLine() + "\n";
                                }
                                ObjectMessage objM = context.createObjectMessage();
                                objM.setObject(data);
                                myReader.close();
                                objM.setStringProperty("id", "1");
                                producer.send(queueDiff, objM);
                                f.delete();
                            } catch (IOException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    private static void backup(JMSContext context, JMSProducer producer) throws JMSException {
        String data = "";
        backupDB("bankabaza1", "root", "pass", "C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem1\\bankabaza1.sql");
        File f = new File("C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem1\\bankabaza1.sql");
        Scanner myReader;
        try {
            myReader = new Scanner(f);
            while (myReader.hasNextLine()) {
                data = data + myReader.nextLine() + "\n";
            }
            ObjectMessage objM = context.createObjectMessage();
            objM.setObject(data);
            myReader.close();
            producer.send(responseQueueSubSystem3, objM);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean backupDB(String dbName, String dbUserName, String dbPassword, String path) {
        String executeCmd1 = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump -u " + dbUserName + " -p" + dbPassword + " " + dbName + " -r " + path;
        Process runtimeProcess;
        try {

            runtimeProcess = Runtime.getRuntime().exec(executeCmd1);
            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                System.out.println("Backup created successfully");
                return true;
            } else {
                System.out.println("Could not create the backup");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    private static void getAllKomitent(JMSContext context, JMSProducer producer) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SubSystem1PU");
        EntityManager em = emf.createEntityManager();

        ArrayList<Komitent> list = new ArrayList<>(em.createNamedQuery("Komitent.findAll", Komitent.class).getResultList());
        ObjectMessage msgR = context.createObjectMessage();
        try {

            msgR.setObject(list);
            for (Komitent f : (ArrayList<Komitent>) msgR.getObject()) {
                System.out.println(f);
            }
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        producer.send(mestoResponseQueue, msgR);
        System.out.println("Poslao sve komitente");
    }

    private static void udpateKomitent(TextMessage msg) throws JMSException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SubSystem1PU");
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction transaction = em.getTransaction();

            String naziv = msg.getStringProperty("naziv");
            int mesto = msg.getIntProperty("mesto");
            Komitent k = em.createNamedQuery("Komitent.findByNaziv", Komitent.class).setParameter("naziv", naziv).getSingleResult();
            Mesto m = em.createNamedQuery("Mesto.findByPostanskiBroj", Mesto.class).setParameter("postanskiBroj", mesto).getSingleResult();
            if (m == null) {
                System.out.println("Ne postoji mesto sa postanski brojem: " + mesto);
            } else {
                k.setMesto(m);
                transaction.begin();
                em.persist(k);
                transaction.commit();
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            emf.close();
        }
    }

    private static int createKomitent(TextMessage msg) throws JMSException {

        int id = 0;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SubSystem1PU");
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction transaction = em.getTransaction();

            String naziv = msg.getStringProperty("naziv");
            String adresa = msg.getStringProperty("adresa");
            int mesto = msg.getIntProperty("mesto");
            Komitent k = new Komitent();
            k.setNaziv(naziv);
            k.setAdresa(adresa);
            Mesto m = em.createNamedQuery("Mesto.findByPostanskiBroj", Mesto.class).setParameter("postanskiBroj", mesto).getSingleResult();
            if (m == null) {
                System.out.println("Ne postoji mesto sa postanski brojem: " + mesto);
            } else {
                k.setMesto(m);
                transaction.begin();
                em.persist(k);
                transaction.commit();
            }
            id = em.createNamedQuery("Komitent.findByNaziv", Komitent.class).setParameter("naziv", naziv).getSingleResult().getIdKomitent();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            emf.close();
        }

        return id;
    }

    private static void getAllFilijale(JMSContext context, JMSProducer producer) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SubSystem1PU");
        EntityManager em = emf.createEntityManager();

        ArrayList<Filijala> list = new ArrayList<>(em.createNamedQuery("Filijala.findAll", Filijala.class).getResultList());
        ObjectMessage msgR = context.createObjectMessage();
        try {

            msgR.setObject(list);
            for (Filijala f : (ArrayList<Filijala>) msgR.getObject()) {
                System.out.println(f);
            }
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        producer.send(mestoResponseQueue, msgR);
        System.out.println("Poslao sve filijale");
    }

    private static void createFilijala(TextMessage msg) throws JMSException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SubSystem1PU");
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction transaction = em.getTransaction();

            String naziv = msg.getStringProperty("naziv");
            String adresa = msg.getStringProperty("adresa");
            int mesto = msg.getIntProperty("mesto");
            Filijala f = new Filijala();
            f.setNaziv(naziv);
            f.setAdresa(adresa);
            Mesto m = em.createNamedQuery("Mesto.findByPostanskiBroj", Mesto.class).setParameter("postanskiBroj", mesto).getSingleResult();
            if (m == null) {
                System.out.println("Ne postoji mesto sa postanski brojem: " + mesto);
            } else {
                f.setMesto(m);
                transaction.begin();
                em.persist(f);
                transaction.commit();
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            emf.close();
        }
    }

    private static void createMesto(TextMessage msg) throws JMSException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SubSystem1PU");
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction transaction = em.getTransaction();

            int posBr;
            posBr = msg.getIntProperty("posBr");
            String naziv = msg.getStringProperty("naziv");
            System.out.println("PosBr: " + posBr + " Naziv: " + naziv);
            Mesto m = new Mesto(posBr, naziv);

            transaction.begin();
            em.persist(m);
            transaction.commit();
        } finally {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            emf.close();
        }

    }

    private static void getAll(JMSContext context, JMSProducer producer) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SubSystem1PU");
        EntityManager em = emf.createEntityManager();

        ArrayList<Mesto> list = new ArrayList<>(em.createNamedQuery("Mesto.findAll", Mesto.class).getResultList());
        ObjectMessage msgR = context.createObjectMessage();
        try {

            msgR.setObject(list);
            for (Mesto m : (ArrayList<Mesto>) msgR.getObject()) {
                System.out.println(m);
            }
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        producer.send(mestoResponseQueue, msgR);
        System.out.println("Poslao sva mesta");
    }
}
