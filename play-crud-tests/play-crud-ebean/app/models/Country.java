package models;

import fr.njin.play.crud.core.annotation.Crud;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Crud.Exclude
@Entity
public class Country extends Model {

    @Id
    public Long id;
}
