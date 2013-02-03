package models;

import fr.njin.play.crud.annotation.Print;
import fr.njin.play.crud.core.annotation.CrudWidget;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String name;

    @CrudWidget.TextArea
    public String description;

    public String folder;

    @Print
    @ManyToMany
    public List<User> members = new ArrayList<User>();

    public Project(String name, String folder, User owner) {
        this.name = name;
        this.folder = folder;
        this.members.add(owner);
    }

}
