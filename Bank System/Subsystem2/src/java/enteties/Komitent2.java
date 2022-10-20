/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enteties;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author novim
 */
@Entity
@Table(name = "komitent")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Komitent2.findAll", query = "SELECT k FROM Komitent2 k"),
    @NamedQuery(name = "Komitent2.findByIdkomitent", query = "SELECT k FROM Komitent2 k WHERE k.idkomitent = :idkomitent"),
    @NamedQuery(name = "Komitent2.findByNaziv", query = "SELECT k FROM Komitent2 k WHERE k.naziv = :naziv"),
    @NamedQuery(name = "Komitent2.findByAdresa", query = "SELECT k FROM Komitent2 k WHERE k.adresa = :adresa"),
    @NamedQuery(name = "Komitent2.findByMesto", query = "SELECT k FROM Komitent2 k WHERE k.mesto = :mesto")})
public class Komitent2 implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "naziv")
    private String naziv;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "adresa")
    private String adresa;
    @Basic(optional = false)
    @NotNull
    @Column(name = "mesto")
    private int mesto;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idkomitent")
    private Integer idkomitent;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "komitent")
    private List<Racun> racunList;

    public Komitent2() {
    }

    public Komitent2(Integer idkomitent) {
        this.idkomitent = idkomitent;
    }

    public Komitent2(Integer idkomitent, String naziv, String adresa, int mesto) {
        this.idkomitent = idkomitent;
        this.naziv = naziv;
        this.adresa = adresa;
        this.mesto = mesto;
    }

    public Integer getIdkomitent() {
        return idkomitent;
    }

    public void setIdkomitent(Integer idkomitent) {
        this.idkomitent = idkomitent;
    }


    @XmlTransient
    public List<Racun> getRacunList() {
        return racunList;
    }

    public void setRacunList(List<Racun> racunList) {
        this.racunList = racunList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idkomitent != null ? idkomitent.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Komitent2)) {
            return false;
        }
        Komitent2 other = (Komitent2) object;
        if ((this.idkomitent == null && other.idkomitent != null) || (this.idkomitent != null && !this.idkomitent.equals(other.idkomitent))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "enteties.Komitent2[ idkomitent=" + idkomitent + " ]";
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

    public int getMesto() {
        return mesto;
    }

    public void setMesto(int mesto) {
        this.mesto = mesto;
    }
    
}
