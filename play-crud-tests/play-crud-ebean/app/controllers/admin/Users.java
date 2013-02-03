package controllers.admin;

import fr.njin.play.crud.annotation.Print;
import fr.njin.play.crud.annotation.PrintFormatter;
import fr.njin.play.crud.controllers.Crud;
import models.User;
import play.data.format.Formatters;

import java.util.Locale;

public class Users extends Crud<String, User> {

    public Users() {
        super(String.class, User.class);
        Formatters.register(User.class, new PrintFormatter<User>(User.class) {
            @Override
            public String print(Print print, User user, Locale locale) {
                return user.name +" : "+user.email;
            }
        });
    }
}
