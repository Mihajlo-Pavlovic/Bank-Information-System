/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoints;

import resources.entities.Kamion;
import resources.entities.Mehanicar;
import resources.entities.Popravlja;
import resources.entities.PopravljaPK;
import resources.entities.Vozac;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

/**
 *
 * @author Stefan
 */
@Path("popravke")
@Stateless
public class Popravke {
    
    @PersistenceContext(unitName = "Zad3PU")
    EntityManager em;
    
    @POST
    @Path("{idK}/{dana}")
    public Response kreirajPopravku(@PathParam("idK") int idK, @PathParam("dana") int dana){
        Kamion kamion = em.find(Kamion.class, idK);
        if(kamion == null){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ne postoji kamion").build();
        }
        
        long count = em.createQuery("select COUNT(m) from Mehanicar m where m.specijalnost = :specijalnost", Long.class).setParameter("specijalnost", kamion.getMarka()).getSingleResult();
        if(count == 0){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Nema raspolozivih mehanicara za tu marku kamiona").build();
        }
        
        // dohvata mehanicara sa najmanjim brojem popravki
        Mehanicar mehanicar = em.createQuery("select m from Mehanicar m LEFT OUTER JOIN m.popravljaList p where m.specijalnost = :specijalnost group by m order by count(p), m.iDZap", Mehanicar.class).setParameter("specijalnost", kamion.getMarka()).setMaxResults(1).getSingleResult();
        
        Popravlja popravlja = new Popravlja();
        popravlja.setPopravljaPK(new PopravljaPK(mehanicar.getIDZap(), idK));
        popravlja.setDana(dana);
        
        em.persist(popravlja);
        kamion.setBrPopravljanja(kamion.getBrPopravljanja() + 1);
        em.persist(kamion);
        
        return Response.status(Response.Status.CREATED).entity("Popravka je kreirana").build();
    }
    
    @GET
    @Path("{idKam}")
    public Response getPopravke(@PathParam("idKam") int idKam){
        List<Popravlja> popravke = em.createNamedQuery("Popravlja.findByIDKam", Popravlja.class).setParameter("iDKam", idKam).getResultList();
        return Response.status(Response.Status.OK).entity(new GenericEntity<List<Popravlja>>(popravke){}).build();
    }
    
    @DELETE
    @Path("{idKam}/{idMeh}")
    public Response deletePopravka(@PathParam("idKam") int idKam, @PathParam("idMeh") int idMeh){
        
        Popravlja popravka = em.find(Popravlja.class, new PopravljaPK(idMeh, idKam));
        
        if(popravka != null){
            em.remove(popravka);
            Kamion kamion = em.find(Kamion.class, idKam);
            kamion.setBrPopravljanja(kamion.getBrPopravljanja() - 1);
        }
        
        return Response.status(Response.Status.NO_CONTENT).build();
    }
    
    
}
