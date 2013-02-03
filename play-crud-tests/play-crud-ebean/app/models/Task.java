package models;

import fr.njin.play.crud.annotation.Print;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Task extends Model {

    public static enum Status {
        CONFIRMED,
        PENDING,
        COMPLETED
    }

    @Id
    public Long id;

    @Constraints.Required
    public String title;

    public boolean done = false;

    @Formats.DateTime(pattern="MM/dd/yy")
    public Date dueDate;

    @Print
    @ManyToOne
    public User assignedTo;

    public String folder;

    @Print
    @ManyToOne
    public Project project;

    public Status status;

}
