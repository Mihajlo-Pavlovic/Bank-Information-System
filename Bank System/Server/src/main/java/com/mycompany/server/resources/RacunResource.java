/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.server.resources;

import enteties.Racun;
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
import javax.ws.rs.DELETE;
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
@Path("racun")
public class RacunResource {
        
    @Resource(lookup="jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup="subsystem2Queue")
    private Queue queue;
    
    @Resource(lookup="subsystem2ResponseQueue")  
    private Queue responseQueue;
    
    
    @PUT
    @Path("{brRacuna}/{mesto}/{komitent}")
    public Response createRacun(@PathParam("brRacuna") String br, @PathParam("mesto") int mesto, @PathParam("komitent") String idK){
        JMSContext context = connectionFactory.createContext();
    
        JMSProducer producer = context.createProducer();
        TextMessage msg = context.createTextMessage();
        
        try {
            msg.setStringProperty("command", "createRacun");
            msg.setStringProperty("brRacuna", br);
            msg.setIntProperty("mesto", mesto);
            msg.setStringProperty("komitent", idK);
            producer.send(queue, msg);
            System.out.println("Poslao zahtev za kreiranje racuna");
            return Response.ok("Kreirao racuna").build();
        } catch (JMSException ex) {
            Logger.getLogger(RacunResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
    @DELETE
    @Path("{brRacuna}")
    public Response closeRacun(@PathParam("brRacuna") String br) {
        JMSContext context = connectionFactory.createContext();
    
        JMSProducer producer = context.createProducer();
        TextMessage msg = context.createTextMessage();
        
        try {
            msg.setStringProperty("command", "closeRacun");
            msg.setStringProperty("brRacuna", br);

            producer.send(queue, msg);
//            System.out.println("Poslao zahtev za kreiranje racuna");
            return Response.ok("Zatvorio racuna").build();
        } catch (JMSException ex) {
            Logger.getLogger(RacunResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    
    }
    
    @GET
    @Path("{komitent}")
    public Response getAll(@PathParam("komitent") String kom) {
                JMSContext context = connectionFactory.createContext();
    
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(responseQueue);
        TextMessage msg = context.createTextMessage();
        
        try {
            msg.setStringProperty("command", "getAllRacun");
            msg.setStringProperty("komitent", kom);
            
            producer.send(queue, msg);
            
            ObjectMessage msgO = (ObjectMessage) consumer.receive(2500);
            ArrayList<Racun> list = (ArrayList<Racun>) msgO.getObject();
            
            return Response.status(Response.Status.OK).entity(new GenericEntity<ArrayList<Racun>>(list){}).build();
        } catch (JMSException ex) {
            Logger.getLogger(RacunResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    
        
    }
}
