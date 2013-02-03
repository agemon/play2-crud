package fr.njin.play.crud.controllers.form;

import fr.njin.play.crud.core.FieldInfo;
import fr.njin.play.crud.core.ModelInfo;
import org.springframework.beans.SimpleTypeConverter;
import play.api.data.Field;
import play.data.Form;
import play.data.format.Formatters;

public class CrudForm<I, T> {

    private final ModelInfo modelInfo;
    private Form<T> form;

    public CrudForm(ModelInfo modelInfo, Form<T> form) {
        this.modelInfo = modelInfo;
        this.form = form;
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    public Form<T> getForm() {
        return form;
    }

    private Object convertId(String idString, Class<?> clazz) {
        Object id;
        try{
            id = Formatters.conversion.convert(idString, modelInfo.getId().getFieldClass());
        }catch (Exception e) {
            id = new SimpleTypeConverter().convertIfNecessary(idString, modelInfo.getId().getFieldClass());
        }
        return id;
    }

    public Object getId() {
        String idString = form.field(modelInfo.getId().getField().getName()).value();
        return convertId(idString, modelInfo.getId().getFieldClass());
    }

    public String getUrlId() {
        return Formatters.print(getId());
    }

    public String getRowId() {
        return nameToId(modelInfo.getName()+"_"+getUrlId());
    }

    public FieldInfo getFieldInfo(String fieldName) {
        return modelInfo.getFields().get(fieldName);
    }

    public Form.Field getField(String fieldName) {
        return getForm().field(fieldName);
    }

    public Object getRelationId(ModelInfo modelInfo, Form.Field field) {
        String id = field.sub(modelInfo.getId().getField().getName()).value();
        try {
            return Formatters.parse(id, modelInfo.getId().getFieldClass());
        }catch (Exception e) {
            return id;
        }
    }

    public Object getRelationId(ModelInfo modelInfo, Field field) {
        String id = field.apply(modelInfo.getId().getField().getName()).value().get();
        try {
            return Formatters.parse(id, modelInfo.getId().getFieldClass());
        }catch (Exception e) {
            return id;
        }
    }

    public String getRelationUrlId(ModelInfo modelInfo, Form.Field field) {
        return Formatters.print(getRelationId(modelInfo, field));
    }

    public String getRelationUrlId(ModelInfo modelInfo, Field field) {
        return Formatters.print(getRelationId(modelInfo, field));
    }

    public String getRelationRowId(ModelInfo modelInfo, Form.Field field) {
        return nameToId(modelInfo.getName() + "_" + getRelationUrlId(modelInfo, field));
    }

    public String getRelationRowId(ModelInfo modelInfo, Field field) {
        return nameToId(modelInfo.getName() + "_" + getRelationUrlId(modelInfo, field));
    }

    /**
     * Binds request data to this form - that is, handles form submission.
     *
     * @return this form filled with the new data
     */
    public CrudForm<I, T> bindFromRequest(String... allowedFields) {
        form = form.bindFromRequest(allowedFields);
        return this;
    }

    /**
     * Populates this form with an existing value, used for edit forms.
     *
     * @param value existing value of type <code>T</code> used to fill this form
     * @return this form filled with the new data
     */
    public CrudForm<I, T> fill(T value) {
        form = form.fill(value);
        return this;
    }

    /*
        TODO play.data.Form.Field doesn't have a getId method unlike play.api.data.Field
     */
    public static String nameToId(String name) {
        return name.replace('.', '_').replace('[', '_').replace(']', '_').replace("@","_");
    }
}
