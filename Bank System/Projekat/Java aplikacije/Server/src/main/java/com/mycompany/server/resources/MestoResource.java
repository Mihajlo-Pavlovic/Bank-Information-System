/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.server.resources;

import enteties.Mesto;
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




@Path("mesto")
public class MestoResource {
    
    @Resource(lookup="jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup="mestoQueue")
    private Queue mestoQueue;
    
    @Resource(lookup="mestoResponseQueue")  
    private Queue mestoResponseQueue;
    
    
    @PUT
    @Path("{posBr}/{naziv}")
    public Response createMesto(@PathParam("posBr") int posBr, @PathParam("naziv") String naziv) {
        JMSContext context = connectionFactory.createContext();
    
        JMSProducer producer = context.createProducer();
        try {
            TextMessage msg = context.createTextMessage();
            
            msg.setStringProperty("command", "createMesto");
            msg.setIntProperty("posBr", posBr);
            msg.setStringProperty("naziv", naziv);
            
            producer.send(mestoQueue, msg);
            
            return Response.ok("Kreiraj mesto: "+posBr + ", "+naziv).build();
        } catch (JMSException ex) {
            Logger.getLogger(MestoResource.class.getName()).log(Level.SEVERE, null, ex);
        }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
    }
    
    @GET
    @Path("mesta")
    public Response getAllPosiljke() {        
        JMSContext context = connectionFactory.createContext();
        JMSConsumer consumer = context.createConsumer(mestoResponseQueue);
        JMSProducer producer = context.createProducer();
        try {
            TextMessage msg = context.createTextMessage();
            
            msg.setStringProperty("command", "getAllMesto");
            System.out.println("Saljem zahteva za svim mestima");
            producer.send(mestoQueue, msg);
            System.out.println("Poslao sam zahtev za svim mestima");
            ObjectMessage msgRecive = (ObjectMessage) consumer.receive();
            ArrayList<Mesto> list = (ArrayList<Mesto>) msgRecive.getObject();
            System.out.println("Primio sam odgovor za svim mestima");
            for(Mesto m : list)
                System.out.println(m);
            return Response.status(Response.Status.OK).entity(new GenericEntity<ArrayList<Mesto>>(list){}).build();
        } catch (JMSException ex) {
            Logger.getLogger(MestoResource.class.getName()).log(Level.SEVERE, null, ex);
        }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
    }
    
    
}
