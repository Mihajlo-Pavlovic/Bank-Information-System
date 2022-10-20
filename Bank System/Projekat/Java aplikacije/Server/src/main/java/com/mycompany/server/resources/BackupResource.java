/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.server.resources;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author novim
 */
@Path("backup")
public class BackupResource {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "subsystem3Queue")
    private Queue queue;
    
    @Resource(lookup = "subsystem3QueueDiffR")
    private Queue queueDiffR;

    @GET
    @Path("diff")
    public Response diff() {
        try {
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queueDiffR);
            TextMessage msg = context.createTextMessage();
            msg.setStringProperty("command", "diff");
            producer.send(queue, msg);
            TextMessage msgR = (TextMessage) consumer.receive();
            String res = msgR.getStringProperty("resp");
            return Response.ok(res).build();
        } catch (JMSException ex) {
            Logger.getLogger(BackupResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
