package models;

import fr.njin.play.crud.core.annotation.Crud;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.File;

@Entity(name = "users")
public class User extends Model {

    @Id
    @Constraints.Email
    @Formats.NonEmpty
    public String email;

    @Constraints.Required
    public String name;

    public String password;

    @Crud(hidden = true)
    public String picture;

    @Transient
    @Crud(selectable = false, searchable = false, sortable = false)
    private File pictureFile;

    public File getPictureFile() {
        if(picture != null)
            pictureFile = new File(picture);
        return pictureFile != null && pictureFile.exists() ? pictureFile : null;
    }

    public void setPictureFile(File pictureFile) {
        this.pictureFile = pictureFile;
        if(pictureFile != null && pictureFile.exists()) {
            deletePrevious();
            picture = pictureFile.getAbsolutePath();
        }
        else if(pictureFile == null) {
            deletePrevious();
        }
    }

    private void deletePrevious() {
        File previous = picture != null ? new File(picture) : null;
        if (previous != null && previous.exists())
            previous.delete();
        picture = "";
    }
}
