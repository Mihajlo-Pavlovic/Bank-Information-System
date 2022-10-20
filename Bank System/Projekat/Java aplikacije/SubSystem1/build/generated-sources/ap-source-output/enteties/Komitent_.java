package enteties;

import enteties.Mesto;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.4.v20190115-rNA", date="2022-07-06T16:53:56")
@StaticMetamodel(Komitent.class)
public class Komitent_ { 

    public static volatile SingularAttribute<Komitent, Integer> idKomitent;
    public static volatile SingularAttribute<Komitent, String> naziv;
    public static volatile SingularAttribute<Komitent, String> adresa;
    public static volatile SingularAttribute<Komitent, Mesto> mesto;

}