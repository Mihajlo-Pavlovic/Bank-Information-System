/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.server.resources;

import enteties.Komitent;
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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

/**
 *
 * @author novim
 */
@Path("kom")
public class KomitentResource {
    
    @Resource(lookup="jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup="mestoQueue")
    private Queue mestoQueue;
    
    @Resource(lookup="mestoResponseQueue")  
    private Queue mestoResponseQueue;
    
    @PUT
    @Path("{naziv}/{adresa}/{mesto}")
    public Response createKomitent(@PathParam("naziv") String naziv, @PathParam("adresa") String adresa, @PathParam("mesto") int mesto) {
        JMSContext context = connectionFactory.createContext();
    
        JMSProducer producer = context.createProducer();
        TextMessage msg = context.createTextMessage();
        try {
            msg.setStringProperty("command", "createKomitent");
            msg.setStringProperty("naziv", naziv);
            msg.setStringProperty("adresa", adresa);
            msg.setIntProperty("mesto", mesto);
            producer.send(mestoQueue, msg);
            return Response.status(Response.Status.OK).entity("Poslat zahtev za kreiranje filijale").build();
        } catch (JMSException ex) {
            Logger.getLogger(FilijalaResource.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
    @POST
    @Path("{naziv}/{mesto}")
    public Response changeMesto(@PathParam("naziv") String naziv, @PathParam("mesto") int mesto) {
        JMSContext context = connectionFactory.createContext();
    
        JMSProducer producer = context.createProducer();
        TextMessage msg = context.createTextMessage();
        try {
            msg.setStringProperty("command", "upadteKomitent");
            msg.setStringProperty("naziv", naziv);
            msg.setIntProperty("mesto", mesto);
            producer.send(mestoQueue, msg);
            return Response.status(Response.Status.OK).entity("Poslat zahtev za kreiranje komitenta").build();
        } catch (JMSException ex) {
            Logger.getLogger(FilijalaResource.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
    @GET
    public Response getAll() {
        JMSContext context = connectionFactory.createContext();
    
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(mestoResponseQueue);

        TextMessage msg = context.createTextMessage();
        try {
            msg.setStringProperty("command", "getAllKomitent");
            producer.send(mestoQueue, msg);
            
            ObjectMessage msgO = (ObjectMessage) consumer.receive();
            ArrayList<Komitent> list = (ArrayList<Komitent>) msgO.getObject();
            return Response.status(Response.Status.OK).entity(new GenericEntity<ArrayList<Komitent>>(list){}).build();
        } catch (JMSException ex) {
            Logger.getLogger(FilijalaResource.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
