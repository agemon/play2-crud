package fr.njin.play.crud.core;

import java.lang.Override;
import java.lang.String;
import java.lang.reflect.Field;

public class FieldInfo {

    public static enum FieldType {
        RELATION,
        TEXT,
        NUMBER,
        BOOLEAN,
        DATE,
        ENUM,
        FILE,
        PASSWORD
    }

    public static enum FormWidgetType {
        INPUT,
        TEXTAREA,
        CHECKBOX,
        RADIO_BUTTONS,
        SELECT,
        CUSTOM
    }

    private Field field;
    private Class<?> fieldClass;
    private boolean isId;
    private String screenName;
    private boolean isMultiple;
    private boolean isEditable;
    private boolean isSelectable;
    private boolean isSearchable;
    private boolean isSortable;
    private FieldType fieldType;
    private FormWidgetType widgetType;
    private boolean isHidden;
    private boolean isRequired;
    private boolean isIgnored;
    private TextAreaInfo textAreaInfo;

    public FieldInfo() {
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Class<?> getFieldClass() {
        return fieldClass;
    }

    public void setFieldClass(Class<?> fieldClass) {
        this.fieldClass = fieldClass;
    }

    public boolean isId() {
        return isId;
    }

    public void setId(boolean id) {
        isId = id;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
    }

    public boolean isSearchable() {
        return isSearchable;
    }

    public void setSearchable(boolean searchable) {
        isSearchable = searchable;
    }

    public boolean isSortable() {
        return isSortable;
    }

    public void setSortable(boolean sortable) {
        isSortable = sortable;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public FormWidgetType getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(FormWidgetType widgetType) {
        this.widgetType = widgetType;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public boolean isIgnored() {
        return isIgnored;
    }

    public void setIgnored(boolean ignored) {
        isIgnored = ignored;
    }

    public TextAreaInfo getTextAreaInfo() {
        return textAreaInfo;
    }

    public void setTextAreaInfo(TextAreaInfo textAreaInfo) {
        this.textAreaInfo = textAreaInfo;
    }

    public String getDisplayName() {
        return screenName != null ? screenName : field.getName();
    }

    public boolean isRelation() {
        return FieldType.RELATION.equals(fieldType);
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
                "field=" + field +
                ", fieldClass=" + fieldClass +
                ", isId=" + isId +
                ", screenName=" + screenName +
                ", isMultiple=" + isMultiple +
                ", isEditable=" + isEditable +
                ", isSelectable=" + isSelectable +
                ", isSearchable=" + isSearchable +
                ", isSortable=" + isSortable +
                ", fieldType=" + fieldType +
                ", widgetType=" + widgetType +
                ", isHidden=" + isHidden +
                ", isRequired=" + isRequired +
                ", isIgnored=" + isIgnored +
                ", textAreaInfo=" + textAreaInfo +
                '}';
    }

    public class TextAreaInfo {
        public int row;
        public int col;

        public TextAreaInfo(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}
