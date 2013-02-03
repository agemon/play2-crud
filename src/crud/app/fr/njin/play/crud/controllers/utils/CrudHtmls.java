package fr.njin.play.crud.controllers.utils;

import fr.njin.play.crud.controllers.Crud;
import fr.njin.play.crud.controllers.TemplateCrudHtml;
import fr.njin.play.crud.controllers.form.CrudForm;
import play.api.templates.Html;
import scala.Option;
import scala.Some;

public final class CrudHtmls {

    public static class Link extends TemplateCrudHtml {

        @Override
        public Html listHtmlFor(Crud crudController, CrudForm form, String fieldName) {
            String value = form.getForm().field(fieldName).value();
            return fr.njin.play.crud.views.html.utils.link.render(value, value, Some.apply("_blank"));
        }

        @Override
        public Html showHtmlFor(Crud crudController, CrudForm form, String fieldName) {
            return listHtmlFor(crudController, form, fieldName);
        }
    }

    public static class Email extends TemplateCrudHtml {

        @Override
        public Html listHtmlFor(Crud crudController, CrudForm form, String fieldName) {
            String value = form.getForm().field(fieldName).value();
            return fr.njin.play.crud.views.html.utils.link.render("mailto:" + value, value, Option.<String>empty());
        }

        @Override
        public Html showHtmlFor(Crud crudController, CrudForm form, String fieldName) {
            return listHtmlFor(crudController, form, fieldName);
        }
    }
}
