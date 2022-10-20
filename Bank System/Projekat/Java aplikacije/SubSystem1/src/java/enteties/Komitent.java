/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enteties;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author novim
 */
@Entity
@Table(name = "komitent")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Komitent.findAll", query = "SELECT k FROM Komitent k"),
    @NamedQuery(name = "Komitent.findByIdKomitent", query = "SELECT k FROM Komitent k WHERE k.idKomitent = :idKomitent"),
    @NamedQuery(name = "Komitent.findByNaziv", query = "SELECT k FROM Komitent k WHERE k.naziv = :naziv"),
    @NamedQuery(name = "Komitent.findByAdresa", query = "SELECT k FROM Komitent k WHERE k.adresa = :adresa")})
public class Komitent implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idKomitent")
    private Integer idKomitent;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "naziv")
    private String naziv;
    @Size(max = 45)
    @Column(name = "adresa")
    private String adresa;
    @JoinColumn(name = "mesto", referencedColumnName = "PostanskiBroj")
    @ManyToOne
    private Mesto mesto;

    public Komitent() {
    }

    public Komitent(Integer idKomitent) {
        this.idKomitent = idKomitent;
    }

    public Komitent(Integer idKomitent, String naziv) {
        this.idKomitent = idKomitent;
        this.naziv = naziv;
    }

    public Integer getIdKomitent() {
        return idKomitent;
    }

    public void setIdKomitent(Integer idKomitent) {
        this.idKomitent = idKomitent;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public Mesto getMesto() {
        return mesto;
    }

    public void setMesto(Mesto mesto) {
        this.mesto = mesto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idKomitent != null ? idKomitent.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Komitent)) {
            return false;
        }
        Komitent other = (Komitent) object;
        if ((this.idKomitent == null && other.idKomitent != null) || (this.idKomitent != null && !this.idKomitent.equals(other.idKomitent))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Komitent[ idKomitent=" + idKomitent + " ]";
    }
    
}
