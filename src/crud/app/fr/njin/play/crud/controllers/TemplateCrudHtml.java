package fr.njin.play.crud.controllers;

import fr.njin.play.crud.controllers.form.CrudForm;
import fr.njin.play.crud.views.html.part.form;
import play.api.templates.Html;

public class TemplateCrudHtml<I, T> implements CrudHtml<I, T> {

    @Override
    public Html listHtmlFor(Crud<I, T> crudController, CrudForm<I, T> form, String fieldName) {
        return fr.njin.play.crud.views.html.part.list.render(crudController, form, fieldName, true);
    }

    @Override
    public Html showHtmlFor(Crud<I, T> crudController, CrudForm<I, T> form, String fieldName) {
        return fr.njin.play.crud.views.html.part.list.render(crudController, form, fieldName, false);
    }

    @Override
    public Html inputFor(Crud<I, T> crudController, CrudForm<I, T> form, String fieldName) {
        return fr.njin.play.crud.views.html.part.form.render(crudController, form, fieldName);
    }
}
