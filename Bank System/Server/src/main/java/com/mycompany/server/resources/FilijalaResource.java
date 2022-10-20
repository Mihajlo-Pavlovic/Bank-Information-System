/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.server.resources;

import enteties.Filijala;
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
@Path("filijala")
public class FilijalaResource {
    
    @Resource(lookup="jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup="mestoQueue")
    private Queue mestoQueue;
    
    @Resource(lookup="mestoResponseQueue")  
    private Queue mestoResponseQueue;
    
    
    @PUT
    @Path("{naziv}/{adresa}/{mesto}")
    public Response createFilijala(@PathParam("naziv") String naziv, @PathParam("adresa") String adresa, @PathParam("mesto") int mesto) {
        JMSContext context = connectionFactory.createContext();
    
        JMSProducer producer = context.createProducer();
        
        TextMessage msg = context.createTextMessage();
        try {
            msg.setStringProperty("command", "createFilijala");
            msg.setStringProperty("naziv", naziv);
            msg.setStringProperty("adresa", adresa);
            msg.setIntProperty("mesto", mesto);
            producer.send(mestoQueue, msg);
            return Response.status(Response.Status.CREATED).entity("Poslat zahtev za kreiranje filijale: "+naziv+", "+adresa+", "+mesto).build();
        } catch (JMSException ex) {
            Logger.getLogger(FilijalaResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
    @GET
    public Response getAllFilijala() {
        JMSContext context = connectionFactory.createContext();
    
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(mestoResponseQueue);
        
        TextMessage msg = context.createTextMessage();
        try {
            msg.setStringProperty("command", "getAllFilijala");
            producer.send(mestoQueue, msg);
            ObjectMessage msgO = (ObjectMessage) consumer.receive();
            ArrayList<Filijala> list = (ArrayList<Filijala>) msgO.getObject();
            return Response.status(Response.Status.OK).entity(new GenericEntity<ArrayList<Filijala>>(list){}).build();
        } catch (JMSException ex) {
            Logger.getLogger(FilijalaResource.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    }
}
