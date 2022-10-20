/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.server.resources;

import enteties.Transakcija;
import java.util.ArrayList;
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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

/**
 *
 * @author novim
 */
@Path("transakcija")
public class TransakcijaResource {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "subsystem2Queue")
    private Queue queue;

    @Resource(lookup = "subsystem2ResponseQueue")
    private Queue responseQueue;

    @PUT
    @Path("prenos/{idRac1}/{idRac2}/{amount}/{svrha}")
    public Response transfer(@PathParam("idRac1") String idR1, @PathParam("idRac2") String idR2, @PathParam("amount") float amount, @PathParam("svrha") String svrha) {
        JMSContext context = connectionFactory.createContext();

        JMSProducer producer = context.createProducer();
        TextMessage msg = context.createTextMessage();

        try {
            msg.setStringProperty("command", "createPrenos");
            msg.setStringProperty("brRacuna1", idR1);
            msg.setStringProperty("brRacuna2", idR2);
            msg.setFloatProperty("amount", amount);
            msg.setStringProperty("svrha", svrha);

            producer.send(queue, msg);
            System.out.println("Poslao zahtev za kreiranje prenosa");
            return Response.ok("Kreirao prenos novca").build();
        } catch (JMSException ex) {
            Logger.getLogger(RacunResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @PUT
    @Path("uplata/{idRac}/{amount}/{svrha}")
    public Response uplata(@PathParam("idRac") String idR1, @PathParam("amount") float amount, @PathParam("svrha") String svrha) {
        JMSContext context = connectionFactory.createContext();

        JMSProducer producer = context.createProducer();
        TextMessage msg = context.createTextMessage();

        try {
            msg.setStringProperty("command", "createUplata");
            msg.setStringProperty("brRacuna1", idR1);
            msg.setFloatProperty("amount", amount);
            msg.setStringProperty("svrha", svrha);

            producer.send(queue, msg);
            System.out.println("Poslao zahtev za kreiranje uplate");
            return Response.ok("Kreirao uplatu novca").build();
        } catch (JMSException ex) {
            Logger.getLogger(RacunResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @PUT
    @Path("isplata/{idRac}/{amount}/{svrha}")
    public Response isplata(@PathParam("idRac") String idR1, @PathParam("amount") float amount, @PathParam("svrha") String svrha) {
        JMSContext context = connectionFactory.createContext();

        JMSProducer producer = context.createProducer();
        TextMessage msg = context.createTextMessage();

        try {
            msg.setStringProperty("command", "createIsplata");
            msg.setStringProperty("brRacuna", idR1);
            msg.setFloatProperty("amount", amount);
            msg.setStringProperty("svrha", svrha);

            producer.send(queue, msg);
            System.out.println("Poslao zahtev za kreiranje isplata");
            return Response.ok("Kreirao isplatu novca").build();
        } catch (JMSException ex) {
            Logger.getLogger(RacunResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
    @GET
    @Path("getAll/{idRac}")
    public Response getAll(@PathParam("idRac") String idR){
        JMSContext context = connectionFactory.createContext();
    
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(responseQueue);

        TextMessage msg = context.createTextMessage();
        try {
            msg.setStringProperty("command", "getAllTransakcija");
            msg.setStringProperty("idRac", idR);
            producer.send(queue, msg);
            System.out.println("Posalo zahteva da dovhatim sve transakcije racuna "+idR);
            
            ObjectMessage msgO = (ObjectMessage) consumer.receive();
            ArrayList<Transakcija> list = (ArrayList<Transakcija>) msgO.getObject();
            return Response.status(Response.Status.OK).entity(new GenericEntity<ArrayList<Transakcija>>(list){}).build();
        } catch (JMSException ex) {
            Logger.getLogger(FilijalaResource.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
}
    