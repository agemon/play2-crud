package fr.njin.play.crud.annotation;

import fr.njin.play.crud.controllers.TemplateCrudHtml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CrudHtml {
    Class<? extends fr.njin.play.crud.controllers.CrudHtml> value() default TemplateCrudHtml.class;
}
