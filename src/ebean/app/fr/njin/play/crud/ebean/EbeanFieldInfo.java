package fr.njin.play.crud.ebean;

import com.avaje.ebean.annotation.Formula;
import fr.njin.play.crud.core.FieldInfo;
import fr.njin.play.crud.core.annotation.Crud;
import fr.njin.play.crud.core.annotation.CrudWidget;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;

public class EbeanFieldInfo extends FieldInfo {

    public EbeanFieldInfo(Field field) {
        super();
        setField(field);
        setFieldClass(field.getType());
        setEditable(true);
        setSelectable(true);
        setSearchable(true);
        setSortable(true);
        inspect();
    }

    protected void inspect() {

        if(getField().isAnnotationPresent(Id.class))
            setId(true);

        if(getField().isAnnotationPresent(Formula.class))
            setEditable(false);

        if (getField().isAnnotationPresent(OneToOne.class) || getField().isAnnotationPresent(ManyToOne.class)) {
            setSelectable(false);
            setSearchable(false);
            setFieldType(FieldType.RELATION);
            setFieldClass(getField().getType());
            setWidgetType(FormWidgetType.SELECT);

            if (getField().isAnnotationPresent(OneToOne.class)) {
                OneToOne annotation = getField().getAnnotation(OneToOne.class);
                setRequired(!annotation.optional());
            }

            if (getField().isAnnotationPresent(ManyToOne.class)) {
                ManyToOne annotation = getField().getAnnotation(ManyToOne.class);
                setRequired(!annotation.optional());
            }

        }

        if (getField().isAnnotationPresent(OneToMany.class) || getField().isAnnotationPresent(ManyToMany.class)) {
            setSelectable(false);
            setSearchable(false);
            setSortable(false);
            setFieldType(FieldType.RELATION);
            setFieldClass((Class) ((ParameterizedType) getField().getGenericType()).getActualTypeArguments()[0]);
            setWidgetType(FormWidgetType.SELECT);
            setMultiple(true);

            if (getField().isAnnotationPresent(OneToMany.class)) {
                OneToMany annotation = getField().getAnnotation(OneToMany.class);
                if(!annotation.mappedBy().trim().isEmpty()) {
                    setEditable(false);
                }
            }

            if (getField().isAnnotationPresent(ManyToMany.class)) {
                ManyToMany annotation = getField().getAnnotation(ManyToMany.class);
                if(!annotation.mappedBy().trim().isEmpty()) {
                    setEditable(false);
                }
            }

        }

        if (CharSequence.class.isAssignableFrom(getField().getType())) {
            setFieldType(FieldType.TEXT);
            if(getField().isAnnotationPresent(CrudWidget.TextArea.class)) {
                CrudWidget.TextArea annotation = getField().getAnnotation(CrudWidget.TextArea.class);
                setWidgetType(FormWidgetType.TEXTAREA);
                setTextAreaInfo(new TextAreaInfo(annotation.row(), annotation.col()));
            }else
                setWidgetType(FormWidgetType.INPUT);
        }

        if (Number.class.isAssignableFrom(getField().getType()) ||
                getField().getType().equals(double.class) ||
                getField().getType().equals(int.class) ||
                getField().getType().equals(long.class)) {

            setFieldType(FieldType.NUMBER);
            setWidgetType(FormWidgetType.INPUT);
        }

        if (Boolean.class.isAssignableFrom(getField().getType()) ||
                getField().getType().equals(boolean.class)) {
            setFieldType(FieldType.BOOLEAN);
            setWidgetType(FormWidgetType.CHECKBOX);
        }

        if (Date.class.isAssignableFrom(getField().getType())) {
            setFieldType(FieldType.DATE);
            setWidgetType(FormWidgetType.INPUT);
        }
        if(File.class.isAssignableFrom(getField().getType())) {
            setFieldType(FieldType.FILE);
            setWidgetType(FormWidgetType.INPUT);
        }

        if (getField().getType().isEnum()) {
            setFieldType(FieldType.ENUM);
            setWidgetType(FormWidgetType.RADIO_BUTTONS);
        }

        if(getField().isAnnotationPresent(Crud.class)) {
            Crud annotation = getField().getAnnotation(Crud.class);
            if(!annotation.name().trim().isEmpty())
                setScreenName(annotation.name());
            if(annotation.password()) {
                setFieldType(FieldType.PASSWORD);
                setSearchable(false);
                setSortable(false);
            }
            setHidden(annotation.hidden());
            setIgnored(annotation.ignored());
            setEditable(annotation.editable());
            setSelectable(annotation.selectable());
            setSearchable(annotation.searchable());
            setSortable(annotation.sortable());
        }

        if (getField().isAnnotationPresent(Constraints.Required.class)) {
            setRequired(true);
        }
    }
}
