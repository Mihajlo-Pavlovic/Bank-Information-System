package enteties;

import enteties.Komitent2;
import enteties.Transakcija;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.4.v20190115-rNA", date="2022-07-06T16:53:50")
@StaticMetamodel(Racun.class)
public class Racun_ { 

    public static volatile SingularAttribute<Racun, Integer> brojTransakcija;
    public static volatile SingularAttribute<Racun, Float> minus;
    public static volatile SingularAttribute<Racun, Date> datumVreme;
    public static volatile SingularAttribute<Racun, Float> stanje;
    public static volatile SingularAttribute<Racun, Komitent2> komitent;
    public static volatile SingularAttribute<Racun, Integer> mesto;
    public static volatile ListAttribute<Racun, Transakcija> transakcijaList;
    public static volatile ListAttribute<Racun, Transakcija> transakcijaList1;
    public static volatile SingularAttribute<Racun, String> status;
    public static volatile SingularAttribute<Racun, String> brojRacuna;

}