package fr.njin.play.crud.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Crud {

    String name() default "";
    boolean password() default false;
    boolean hidden() default false;
    boolean ignored() default false;
    boolean editable() default true;
    boolean searchable() default true;
    boolean selectable() default true;
    boolean sortable() default true;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    public static @interface Exclude {

    }
}
