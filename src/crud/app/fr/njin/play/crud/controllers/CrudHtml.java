package fr.njin.play.crud.controllers;

import fr.njin.play.crud.controllers.form.CrudForm;
import play.api.templates.Html;

public interface CrudHtml<I, T> {

    Html listHtmlFor(Crud<I, T> crudController, CrudForm<I, T> form, String fieldName);
    Html showHtmlFor(Crud<I, T> crudController, CrudForm<I, T> form, String fieldName);
    Html inputFor(Crud<I, T> crudController, CrudForm<I, T> crudForm, String fieldName);
}
