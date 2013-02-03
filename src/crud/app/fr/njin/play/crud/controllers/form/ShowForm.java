package fr.njin.play.crud.controllers.form;

import fr.njin.play.crud.controllers.Crud;
import fr.njin.play.crud.core.ModelInfo;
import play.api.templates.Html;
import play.data.Form;

public class ShowForm<I, T> extends CrudForm <I, T> {

    private Crud<I, T> crud;

    public ShowForm(Crud<I, T> crud, ModelInfo modelInfo) {
        super(modelInfo, new Form<T>(crud.getModelClass()));
        this.crud = crud;
    }

    public Html html(String field) {
        return crud.showHtmlFor(this, field);
    }

}
