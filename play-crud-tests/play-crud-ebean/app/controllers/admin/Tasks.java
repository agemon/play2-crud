package controllers.admin;

import fr.njin.play.crud.annotation.Print;
import fr.njin.play.crud.annotation.PrintFormatter;
import fr.njin.play.crud.controllers.Crud;
import models.Task;
import play.data.format.Formatters;

import java.util.Locale;

public class Tasks extends Crud<Long, Task> {
    public Tasks() {
        super(Long.class, Task.class);
        Formatters.register(Task.class, new PrintFormatter<Task>(Task.class) {
            @Override
            public String print(Print print, Task task, Locale locale) {
                return task.title;
            }
        });
    }
}
