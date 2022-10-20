/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enteties;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author novim
 */
@Entity
@Table(name = "racun")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Racun.findAll", query = "SELECT r FROM Racun r"),
    @NamedQuery(name = "Racun.findByBrojRacuna", query = "SELECT r FROM Racun r WHERE r.brojRacuna = :brojRacuna"),
    @NamedQuery(name = "Racun.findByMesto", query = "SELECT r FROM Racun r WHERE r.mesto = :mesto"),
    @NamedQuery(name = "Racun.findByStanje", query = "SELECT r FROM Racun r WHERE r.stanje = :stanje"),
    @NamedQuery(name = "Racun.findByMinus", query = "SELECT r FROM Racun r WHERE r.minus = :minus"),
    @NamedQuery(name = "Racun.findByDatumVreme", query = "SELECT r FROM Racun r WHERE r.datumVreme = :datumVreme"),
    @NamedQuery(name = "Racun.findByBrojTransakcija", query = "SELECT r FROM Racun r WHERE r.brojTransakcija = :brojTransakcija"),
    @NamedQuery(name = "Racun.findByStatus", query = "SELECT r FROM Racun r WHERE r.status = :status")})
public class Racun implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "mesto")
    private int mesto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "stanje")
    private float stanje;
    @Basic(optional = false)
    @NotNull
    @Column(name = "minus")
    private float minus;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datumVreme")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumVreme;
    @Basic(optional = false)
    @NotNull
    @Column(name = "brojTransakcija")
    private int brojTransakcija;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "status")
    private String status;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "brojRacuna")
    private String brojRacuna;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "racun1")
    private List<Transakcija> transakcijaList;
    @OneToMany(mappedBy = "racun2")
    private List<Transakcija> transakcijaList1;
    @JoinColumn(name = "komitent", referencedColumnName = "idkomitent")
    @ManyToOne(optional = false)
    private Komitent2 komitent;

    public Racun() {
    }

    public Racun(String brojRacuna) {
        this.brojRacuna = brojRacuna;
    }

    public Racun(String brojRacuna, int mesto, float stanje, float minus, Date datumVreme, int brojTransakcija, String status) {
        this.brojRacuna = brojRacuna;
        this.mesto = mesto;
        this.stanje = stanje;
        this.minus = minus;
        this.datumVreme = datumVreme;
        this.brojTransakcija = brojTransakcija;
        this.status = status;
    }

    public String getBrojRacuna() {
        return brojRacuna;
    }

    public void setBrojRacuna(String brojRacuna) {
        this.brojRacuna = brojRacuna;
    }


    @XmlTransient
    public List<Transakcija> getTransakcijaList() {
        return transakcijaList;
    }

    public void setTransakcijaList(List<Transakcija> transakcijaList) {
        this.transakcijaList = transakcijaList;
    }

    @XmlTransient
    public List<Transakcija> getTransakcijaList1() {
        return transakcijaList1;
    }

    public void setTransakcijaList1(List<Transakcija> transakcijaList1) {
        this.transakcijaList1 = transakcijaList1;
    }

    public Komitent2 getKomitent() {
        return komitent;
    }

    public void setKomitent(Komitent2 komitent) {
        this.komitent = komitent;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (brojRacuna != null ? brojRacuna.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Racun)) {
            return false;
        }
        Racun other = (Racun) object;
        if ((this.brojRacuna == null && other.brojRacuna != null) || (this.brojRacuna != null && !this.brojRacuna.equals(other.brojRacuna))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "enteties.Racun[ brojRacuna=" + brojRacuna + " ]";
    }

    public int getMesto() {
        return mesto;
    }

    public void setMesto(int mesto) {
        this.mesto = mesto;
    }

    public float getStanje() {
        return stanje;
    }

    public void setStanje(float stanje) {
        this.stanje = stanje;
    }

    public float getMinus() {
        return minus;
    }

    public void setMinus(float minus) {
        this.minus = minus;
    }

    public Date getDatumVreme() {
        return datumVreme;
    }

    public void setDatumVreme(Date datumVreme) {
        this.datumVreme = datumVreme;
    }

    public int getBrojTransakcija() {
        return brojTransakcija;
    }

    public void setBrojTransakcija(int brojTransakcija) {
        this.brojTransakcija = brojTransakcija;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
