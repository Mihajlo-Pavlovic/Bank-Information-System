/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subsystem2;

import enteties.Komitent2;
import enteties.Racun;
import enteties.Transakcija;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Query;

/**
 *
 * @author novim
 */
public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "subsystem2Queue")
    private static Queue queue;

    @Resource(lookup = "subsystem2ResponseQueue")
    private static Queue responseQueue;

    @Resource(lookup = "subsystem3ResponseQueue2")
    private static Queue responseQueueSubSystem3;

    @Resource(lookup = "subsystem3QueueDiff")
    private static Queue queueDiff;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Servis2 is running");

        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(queue);

        while (true) {
            try {

                TextMessage msg = (TextMessage) consumer.receive();
                String command = msg.getStringProperty("command");
                System.out.println("Recived msg " + command);
                switch (command) {
                    case "createKomitent":
                        createKomitent(msg);
                        break;
                    case "createRacun":
                        createRacun(msg);
                        break;
                    case "closeRacun":
                        closeRacun(msg);
                    case "getAllRacun":
                        getAllRacun(msg, context, producer);
                        break;
                    case "createPrenos":
                        transaction(msg);
                        break;
                    case "createUplata":
                        uplata(msg);
                        break;
                    case "createIsplata":
                        createIsplata(msg);
                        break;
                    case "getAllTransakcija":
                        getAllTransakcija(msg, context, producer);
                        break;
                    case "backUp":
                        backup(context, producer);
                        break;
                    case "compare":
                        String data = "";
                        String cmd = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump --skip-comments --skip-extended-insert -u root -ppass bankabaza2 -r C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem2\\diff.sql";
                         {
                            try {
                                Process runtimeProcess;
                                runtimeProcess = Runtime.getRuntime().exec(cmd);
                                runtimeProcess.waitFor();
                                File f = new File("C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem2\\diff.sql");
                                Scanner myReader = new Scanner(f);
                                while (myReader.hasNextLine()) {
                                    data = data + myReader.nextLine() + "\n";
                                }
                                ObjectMessage objM = context.createObjectMessage();
                                objM.setObject(data);
                                myReader.close();
                                objM.setStringProperty("id", "2");
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
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void backup(JMSContext context, JMSProducer producer) throws JMSException, FileNotFoundException {
        String data = "";
        backupDB("bankabaza2", "root", "pass", "C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem2\\bankabaza2.sql");

        File f = new File("C:\\Users\\novim\\Documents\\NetBeansProjects\\Subsystem2\\bankabaza2.sql");
        Scanner myReader = new Scanner(f);
        while (myReader.hasNextLine()) {
            data = data + myReader.nextLine() + "\n";
        }
        ObjectMessage objM = context.createObjectMessage();
        objM.setObject(data);
        myReader.close();
        producer.send(responseQueueSubSystem3, objM);
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

    private static void getAllTransakcija(TextMessage msg, JMSContext context, JMSProducer producer) throws JMSException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Subsystem2PU");
        EntityManager em = emf.createEntityManager();
        try {
            String r = msg.getStringProperty("idRac");
            Query q = em.createQuery("select t from Transakcija t where t.racun1.brojRacuna =:br or t.racun2.brojRacuna =:br");
            q.setParameter("br", r);
            ArrayList<Transakcija> list = new ArrayList<>(q.getResultList());
            ObjectMessage msgO = context.createObjectMessage();
            for (Transakcija t : list) {
                System.out.println(t);
            }
            msgO.setObject(list);
            producer.send(responseQueue, msgO);
            System.out.println("Poslao listu transakcija");
        } finally {
            emf.close();
        }
    }

    private static void createIsplata(TextMessage msg) throws JMSException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Subsystem2PU");
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction transaction = em.getTransaction();
            String idR = msg.getStringProperty("brRacuna");
            float amount = msg.getFloatProperty("amount");
            String svrha = msg.getStringProperty("svrha");
            int num = em.createQuery("select t from Transakcija t where t.racun1.brojRacuna = :br").setParameter("br", idR).getResultList().size() + 1;
            Racun r = em.createNamedQuery("Racun.findByBrojRacuna", Racun.class).setParameter("brojRacuna", idR).getSingleResult();
            //da li ima sredstva
            //proveriti i aktivan neaktivan

            if (!"neaktivan".equals(r.getStatus()) && !r.getStatus().equals("blokiran")) {
                Transakcija t = new Transakcija();
                t.setRacun1(r);
                t.setRacun2(null);
                t.setSvrha(svrha);
                t.setRedniBroj1(num);
                t.setKolicina(-1 * amount);
                t.setDatumVreme(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
                r.setStanje(r.getStanje() - amount);
                r.setBrojTransakcija(r.getBrojTransakcija() + 1);
                if (r.getStanje() < -1 * r.getMinus()) {
                    r.setStatus("blokiran");
                }
                transaction.begin();
                em.persist(r);
                em.persist(t);
                transaction.commit();
            } else {
                System.out.println(r.getBrojRacuna() + " je neaktivan");
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            emf.close();
        }
    }

    private static void uplata(TextMessage msg) throws JMSException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Subsystem2PU");
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction transaction = em.getTransaction();
            String idR = msg.getStringProperty("brRacuna1");
            float amount = msg.getFloatProperty("amount");
            String svrha = msg.getStringProperty("svrha");
            int num = em.createQuery("select t from Transakcija t where t.racun1.brojRacuna = :br").setParameter("br", idR).getResultList().size() + 1;
            Racun r = em.createNamedQuery("Racun.findByBrojRacuna", Racun.class).setParameter("brojRacuna", idR).getSingleResult();
            //da li ima sredstva
            //proveriti i aktivan neaktivan

            if (!"neaktivan".equals(r.getStatus())) {
                Transakcija t = new Transakcija();
                t.setRacun1(r);
                t.setRacun2(null);
                t.setSvrha(svrha);
                t.setRedniBroj1(num);
                t.setKolicina(amount);
                t.setDatumVreme(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
                r.setStanje(r.getStanje() + amount);
                r.setBrojTransakcija(r.getBrojTransakcija() + 1);
                if (r.getStatus().equals("blokiran")) {
                    //da li je izasao iz blokade
                    if (r.getStanje() >= -1 * r.getMinus()) {
                        r.setStatus("aktivan");
                    }
                }
                transaction.begin();
                em.persist(r);
                em.persist(t);
                transaction.commit();
            } else {
                System.out.println(r.getBrojRacuna() + " je neaktivan");
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            emf.close();
        }
    }

    private static void transaction(TextMessage msg) throws JMSException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Subsystem2PU");
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction transaction = em.getTransaction();
            String idR1 = msg.getStringProperty("brRacuna1");
            String idR2 = msg.getStringProperty("brRacuna2");
            float amount = msg.getFloatProperty("amount");
            String svrha = msg.getStringProperty("svrha");
            int num1 = em.createQuery("select t from Transakcija t where t.racun1.brojRacuna = :br").setParameter("br", idR1).getResultList().size() + 1;
            int num2 = em.createQuery("select t from Transakcija t where t.racun1.brojRacuna = :br").setParameter("br", idR2).getResultList().size() + 1;

            Racun r1 = em.createNamedQuery("Racun.findByBrojRacuna", Racun.class).setParameter("brojRacuna", idR1).getSingleResult();
            Racun r2 = em.createNamedQuery("Racun.findByBrojRacuna", Racun.class).setParameter("brojRacuna", idR2).getSingleResult();
            //da li ima sredstva
            //proveriti i aktivan neaktivan

            if (!"blokiran".equals(r1.getStatus()) && !"neaktivan".equals(r1.getStatus()) && !"neaktivan".equals(r2.getStatus())) {
                Transakcija t = new Transakcija();
                t.setRacun1(r1);
                t.setRacun2(r2);
                t.setSvrha(svrha);
                t.setRedniBroj1(num1);
                t.setKolicina(amount);
                t.setDatumVreme(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
                r1.setStanje(r1.getStanje() - amount);
                r2.setStanje(r2.getStanje() + amount);
                r1.setBrojTransakcija(r1.getBrojTransakcija() + 1);
                r2.setBrojTransakcija(r2.getBrojTransakcija() + 1);
                //usao u nedozvoljeni minus
                //proveriti da li se uplatom menja status
                if (r1.getStanje() < -1 * r1.getMinus()) {
                    r1.setStatus("blokiran");
                    System.out.println(r1.getBrojRacuna() + " blokiran");
                }
                if ("blokiran".equals(r2.getStatus())) {
                    //da li je izasao iz blokade
                    if (r2.getStanje() >= -1 * r2.getMinus()) {
                        r2.setStatus("aktivan");
                    }
                }
                transaction.begin();
                em.persist(r1);
                em.persist(r2);
                em.persist(t);
                transaction.commit();
            } else {
                System.out.println(r1.getBrojRacuna() + " je vec blokiran");
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            emf.close();
        }
    }

    private static void getAllRacun(TextMessage msg, JMSContext context, JMSProducer producer) throws JMSException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Subsystem2PU");
        EntityManager em = emf.createEntityManager();
        try {
            String kom = msg.getStringProperty("komitent");
            int komId = em.createNamedQuery("Komitent2.findByNaziv", Komitent2.class).setParameter("naziv", kom).getSingleResult().getIdkomitent();
            Query q = em.createQuery("select r from Racun r where r.komitent.idkomitent =:id ");
            q.setParameter("id", komId);
            ArrayList<Racun> list = new ArrayList<>(q.getResultList());
            ObjectMessage msgO = context.createObjectMessage();
            for (Racun r : list) {
                System.out.println(r);
            }
            msgO.setObject(list);
            producer.send(responseQueue, msgO);

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            emf.close();
        }
    }

    private static void closeRacun(TextMessage msg) throws JMSException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Subsystem2PU");
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction transaction = em.getTransaction();

            String brRacuna = msg.getStringProperty("brRacuna");
            Racun r = em.createNamedQuery("Racun.findByBrojRacuna", Racun.class).setParameter("brojRacuna", brRacuna).getSingleResult();
            r.setStatus("Neaktivan");
            transaction.begin();
            em.persist(r);
            transaction.commit();

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            emf.close();
        }
    }

    private static void createKomitent(TextMessage msg) throws JMSException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Subsystem2PU");
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction transaction = em.getTransaction();

            String naziv = msg.getStringProperty("naziv");
            String adresa = msg.getStringProperty("adresa");
            int mesto = msg.getIntProperty("mesto");
            int id = msg.getIntProperty("id");
            Komitent2 k = new Komitent2();
            k.setNaziv(naziv);
            k.setAdresa(adresa);
            k.setMesto(mesto);
            k.setIdkomitent(id);

            transaction.begin();
            em.persist(k);
            transaction.commit();

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            emf.close();
        }
    }

    private static void createRacun(TextMessage msg) throws JMSException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Subsystem2PU");
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction transaction = em.getTransaction();

            String brRacuna = msg.getStringProperty("brRacuna");
            int mesto = msg.getIntProperty("mesto");
            String komitent = msg.getStringProperty("komitent");
            Racun r = new Racun();
            r.setBrojRacuna(brRacuna);
            r.setMesto(mesto);
            r.setBrojTransakcija(0);
            r.setDatumVreme(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
            r.setMinus(0);
            r.setStanje(0);
            r.setStatus("aktivan");

            Komitent2 k = em.createNamedQuery("Komitent2.findByNaziv", Komitent2.class).setParameter("naziv", komitent).getSingleResult();
            if (k != null) {
                r.setKomitent(k);
                transaction.begin();
                em.persist(r);
                transaction.commit();
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            emf.close();
        }
    }
}
