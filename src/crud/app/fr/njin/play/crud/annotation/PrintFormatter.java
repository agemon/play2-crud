package fr.njin.play.crud.annotation;

import play.data.format.Formatters;

import java.text.ParseException;
import java.util.Locale;

public abstract class PrintFormatter<T> extends Formatters.AnnotationFormatter<Print, T> {

    private final Class<T> clazz;

    public PrintFormatter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T parse(Print print, String s, Locale locale) throws ParseException {
        return Formatters.parse(s, clazz);
    }

}
