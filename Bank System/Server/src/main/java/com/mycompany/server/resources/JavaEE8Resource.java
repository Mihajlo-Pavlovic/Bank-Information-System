package com.mycompany.server.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author 
 */
@Path("test")
public class JavaEE8Resource {
    
    @GET
    public Response ping(){
        return Response
                .ok("ping pong")
                .build();
    }
}
