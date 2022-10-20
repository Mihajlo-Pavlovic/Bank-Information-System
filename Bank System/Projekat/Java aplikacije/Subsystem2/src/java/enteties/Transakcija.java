/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enteties;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author novim
 */
@Entity
@Table(name = "transakcija")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Transakcija.findAll", query = "SELECT t FROM Transakcija t"),
    @NamedQuery(name = "Transakcija.findByIdtransakcija", query = "SELECT t FROM Transakcija t WHERE t.idtransakcija = :idtransakcija"),
    @NamedQuery(name = "Transakcija.findByKolicina", query = "SELECT t FROM Transakcija t WHERE t.kolicina = :kolicina"),
    @NamedQuery(name = "Transakcija.findByDatumVreme", query = "SELECT t FROM Transakcija t WHERE t.datumVreme = :datumVreme"),
    @NamedQuery(name = "Transakcija.findByRedniBroj1", query = "SELECT t FROM Transakcija t WHERE t.redniBroj1 = :redniBroj1"),
    @NamedQuery(name = "Transakcija.findByRedniBroj2", query = "SELECT t FROM Transakcija t WHERE t.redniBroj2 = :redniBroj2"),
    @NamedQuery(name = "Transakcija.findBySvrha", query = "SELECT t FROM Transakcija t WHERE t.svrha = :svrha")})
public class Transakcija implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idtransakcija")
    private Integer idtransakcija;
    @Basic(optional = false)
    @NotNull
    @Column(name = "kolicina")
    private float kolicina;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datumVreme")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumVreme;
    @Basic(optional = false)
    @NotNull
    @Column(name = "redniBroj1")
    private int redniBroj1;
    @Basic(optional = false)
    @NotNull
    @Column(name = "redniBroj2")
    private int redniBroj2;
    @Size(max = 45)
    @Column(name = "svrha")
    private String svrha;
    @JoinColumn(name = "racun1", referencedColumnName = "brojRacuna")
    @ManyToOne(optional = false)
    private Racun racun1;
    @JoinColumn(name = "racun2", referencedColumnName = "brojRacuna")
    @ManyToOne
    private Racun racun2;

    public Transakcija() {
    }

    public Transakcija(Integer idtransakcija) {
        this.idtransakcija = idtransakcija;
    }

    public Transakcija(Integer idtransakcija, float kolicina, Date datumVreme, int redniBroj1, int redniBroj2) {
        this.idtransakcija = idtransakcija;
        this.kolicina = kolicina;
        this.datumVreme = datumVreme;
        this.redniBroj1 = redniBroj1;
        this.redniBroj2 = redniBroj2;
    }

    public Integer getIdtransakcija() {
        return idtransakcija;
    }

    public void setIdtransakcija(Integer idtransakcija) {
        this.idtransakcija = idtransakcija;
    }

    public float getKolicina() {
        return kolicina;
    }

    public void setKolicina(float kolicina) {
        this.kolicina = kolicina;
    }

    public Date getDatumVreme() {
        return datumVreme;
    }

    public void setDatumVreme(Date datumVreme) {
        this.datumVreme = datumVreme;
    }

    public int getRedniBroj1() {
        return redniBroj1;
    }

    public void setRedniBroj1(int redniBroj1) {
        this.redniBroj1 = redniBroj1;
    }

    public int getRedniBroj2() {
        return redniBroj2;
    }

    public void setRedniBroj2(int redniBroj2) {
        this.redniBroj2 = redniBroj2;
    }

    public String getSvrha() {
        return svrha;
    }

    public void setSvrha(String svrha) {
        this.svrha = svrha;
    }

    public Racun getRacun1() {
        return racun1;
    }

    public void setRacun1(Racun racun1) {
        this.racun1 = racun1;
    }

    public Racun getRacun2() {
        return racun2;
    }

    public void setRacun2(Racun racun2) {
        this.racun2 = racun2;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idtransakcija != null ? idtransakcija.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Transakcija)) {
            return false;
        }
        Transakcija other = (Transakcija) object;
        if ((this.idtransakcija == null && other.idtransakcija != null) || (this.idtransakcija != null && !this.idtransakcija.equals(other.idtransakcija))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "enteties.Transakcija[ idtransakcija=" + idtransakcija + " ]";
    }
    
}
