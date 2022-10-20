package enteties;

import enteties.Racun;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.4.v20190115-rNA", date="2022-07-06T16:53:50")
@StaticMetamodel(Transakcija.class)
public class Transakcija_ { 

    public static volatile SingularAttribute<Transakcija, Integer> redniBroj2;
    public static volatile SingularAttribute<Transakcija, Integer> redniBroj1;
    public static volatile SingularAttribute<Transakcija, Date> datumVreme;
    public static volatile SingularAttribute<Transakcija, String> svrha;
    public static volatile SingularAttribute<Transakcija, Integer> idtransakcija;
    public static volatile SingularAttribute<Transakcija, Float> kolicina;
    public static volatile SingularAttribute<Transakcija, Racun> racun1;
    public static volatile SingularAttribute<Transakcija, Racun> racun2;

}