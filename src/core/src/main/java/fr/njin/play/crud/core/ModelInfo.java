package fr.njin.play.crud.core;

import java.util.Map;

public class ModelInfo {

    private String name;
    private Class<?> modelClass;
    private FieldInfo id;
    private Map<String, FieldInfo> fields;

    public ModelInfo() {
    }

    public ModelInfo(String name, Class<?> modelClass, FieldInfo id, Map<String, FieldInfo> fields) {
        this.name = name;
        this.modelClass = modelClass;
        this.id = id;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getModelClass() {
        return modelClass;
    }

    public void setModelClass(Class<?> modelClass) {
        this.modelClass = modelClass;
    }

    public FieldInfo getId() {
        return id;
    }

    public void setId(FieldInfo id) {
        this.id = id;
    }

    public Map<String, FieldInfo> getFields() {
        return fields;
    }

    public void setFields(Map<String, FieldInfo> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "ModelInfo{" +
                "name='" + name + '\'' +
                ", modelClass=" + modelClass +
                ", id=" + id +
                ", fields=" + fields +
                '}';
    }
}
