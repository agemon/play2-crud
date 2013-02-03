package controllers.admin;

import fr.njin.play.crud.annotation.Print;
import fr.njin.play.crud.annotation.PrintFormatter;
import fr.njin.play.crud.controllers.Crud;
import models.Project;
import play.data.format.Formatters;

import java.util.Locale;

public class Projects extends Crud<Long, Project> {

    public Projects() {
        super(Long.class, Project.class);
        Formatters.register(Project.class, new PrintFormatter<Project>(Project.class) {
            @Override
            public String print(Print print, Project project, Locale locale) {
                return project.name;
            }
        });
    }

}
