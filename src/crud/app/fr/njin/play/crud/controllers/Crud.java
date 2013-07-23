package fr.njin.play.crud.controllers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import fr.njin.play.crud.controllers.form.CreateForm;
import fr.njin.play.crud.controllers.form.CrudForm;
import fr.njin.play.crud.controllers.form.ListForm;
import fr.njin.play.crud.controllers.form.ShowForm;
import fr.njin.play.crud.core.*;
import fr.njin.play.crud.views.html.create;
import fr.njin.play.crud.views.html.edit;
import fr.njin.play.crud.views.html.list;
import fr.njin.play.crud.views.html.show;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.SimpleTypeConverter;
import play.Logger;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.data.format.Formatters;
import play.i18n.Messages;
import play.libs.F;
import play.libs.Scala;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.Option;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Crud<I,T> extends Controller {

    public static final String FLASH_SUCCESS_KEY="crud.flash.success";
    public static final String FLASH_ERROR_KEY="crud.flash.error";

    private Logger.ALogger logger = Logger.of(Crud.class);
    private final Class<I> idClass;
    private final Class<T> modelClass;
    private final int fetchCount;

    public Crud(Class<I> idClass, Class<T> modelClass) {
        this(idClass, modelClass, 30);
    }

    public Crud(Class<I> idClass, Class<T> modelClass, int fetchCount) {
        this.idClass = idClass;
        this.modelClass = modelClass;
        this.fetchCount = fetchCount;
    }

    public Class<I> getIdClass() {
        return idClass;
    }

    public Class<T> getModelClass() {
        return modelClass;
    }

    public int getFetchCount() {
        return fetchCount;
    }

    public Iterable<ListForm<I, T>> dataToForm(final ModelInfo modelInfo,final Iterable<?> data) {
        return Iterables.transform(data, new Function<Object, ListForm<I,T>>() {
            @Override
            @SuppressWarnings("unchecked")
            public ListForm<I,T> apply(Object o) {
                ListForm<I,T> form = new ListForm<I, T>(Crud.this, modelInfo);
                form.fill((T)o);
                return form;
            }
        });
    }

    public Result index(ModelInfo modelInfo, DataProvider<?, ?> dataProvider) {
        return redirect(routes.ControllersManager.list(modelInfo.getName(), false, 0, null, null));
    }

    @SuppressWarnings("unchecked")
    public Result list(ModelInfo modelInfo, DataProvider<?, ?> dataProvider, int page, String query, String order, boolean forRelation) {
        Iterable<FieldInfo> fields = listFields(modelInfo);
        Predicate<FieldInfo> relationField = relationField();
        Function<FieldInfo, String> toName = fieldInfoToName();

        Iterable<String> selectables =  Iterables.transform(Iterables.filter(fields, Predicates.and(selectField(), Predicates.not(relationField))), toName);
        Iterable<String> fetchables = Iterables.transform(Iterables.filter(fields, relationField), toName);
        Iterable<String> searchables = Iterables.transform(Iterables.filter(fields, searchField()), toName);

        F.Option<Iterable<String>> extendedSearchFields = extendSearchFields();
        if(extendedSearchFields.isDefined())
            searchables = Iterables.concat(searchables, extendedSearchFields.get());

        String search = StringUtils.trimToNull(query);
        Option<String> searchOption = Scala.None();
        if(search != null)
            searchOption = Option.apply(search);

        Sort sort = Sort.parse(StringUtils.trimToNull(order));
        Option<Sort> sortOption = Scala.None();
        if(sort != null)
            sortOption = Option.apply(sort);


        Page<?> data = dataProvider.get(page, getFetchCount(), search, sort, selectables, searchables, fetchables);
        return ok(listView(modelInfo, fields, (Page<T>)data, searchOption, sortOption, forRelation));
    }

    @SuppressWarnings("unchecked")
    public Result show(ModelInfo modelInfo, DataProvider<?, ?> dataProvider, String idString, boolean modal) {
        I id = id(idString);
        T object = ((DataProvider<I,T>)dataProvider).get(id);
        if(object == null)
            return notFound(Messages.get("crud.nofound." + modelInfo.getName(), id));

        ShowForm<I,T> form = new ShowForm<I,T>(this, modelInfo);
        form.fill(object);

        Iterable<FieldInfo> fields = showFields(modelInfo);
        return ok(showView(modelInfo, form, fields, modal));
    }

    @SuppressWarnings("unchecked")
    public Result create(ModelInfo modelInfo, DataProvider<?, ?> dataProvider) {
        CreateForm<I,T> form = new CreateForm<I, T>(this, modelInfo);
        Iterable<FieldInfo> fields = createFields(modelInfo);
        return ok(createView(modelInfo, form, fields));
    }

    @SuppressWarnings("unchecked")
    public Result save(ModelInfo modelInfo, DataProvider<?, ?> dataProvider) {
        CrudForm <I, T> createForm = new CreateForm<I, T>(this, modelInfo);

        Iterable<FieldInfo> fields = createFields(modelInfo);
        String[] allowed = new String[]{};
        Lists.newArrayList(Iterables.transform(fields, fieldInfoToName())).toArray(allowed);

        Form<T> form = createForm.bindFromRequest(allowed).getForm();
        if (form.hasErrors()) {
            return badRequest(create.render(this, modelInfo, (CreateForm<?,?>)createForm, fields));
        } else {
            T object = form.get();
            proceedFiles(object, Iterables.filter(fields, isFile()));
            object = onSave(object);
            ((DataProvider<I,T>)dataProvider).save(object);

            try {
                I idObject = (I)PropertyUtils.getProperty(object, modelInfo.getId().getField().getName());
                flash(FLASH_SUCCESS_KEY, fr.njin.play.crud.views.html.ui.flashCreateSuccess(modelInfo.getName(), idObject.toString()));
                return redirect(routes.ControllersManager.show(modelInfo.getName(), Formatters.print(idObject),false));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Result edit(ModelInfo modelInfo, DataProvider<?, ?> dataProvider, String idString) {
        I id = id(idString);
        T object = ((DataProvider<I,T>)dataProvider).get(id);
        if(object == null)
            return notFound(Messages.get("crud.nofound." + modelInfo.getName(), id));

        CreateForm<I,T> form = new CreateForm<I, T>(this, modelInfo);
        form.fill(object);

        Iterable<FieldInfo> fields = createFields(modelInfo);
        return ok(editView(modelInfo, form, fields, idString));
    }


    @SuppressWarnings("unchecked")
    public Result update(ModelInfo modelInfo, DataProvider<?, ?> dataProvider, String idString) {
        I id = id(idString);
        CrudForm <I, T> createForm = new CreateForm<I, T>(this, modelInfo);

        Iterable<FieldInfo> fields = createFields(modelInfo);
        String[] allowed = new String[]{};
        Lists.newArrayList(Iterables.transform(fields, fieldInfoToName())).toArray(allowed);

        Form<T> form = createForm.bindFromRequest(allowed).getForm();
        if (form.hasErrors()) {
            Logger.debug(form.errorsAsJson()+"");
            return badRequest(edit.render(this, modelInfo, (CreateForm<?, ?>) createForm, fields, idString));
        } else {
            T object = form.get();
            proceedFiles(object, Iterables.filter(fields, isFile()));

            try {
                PropertyUtils.setProperty(object, modelInfo.getId().getField().getName(), id);
                object = onUpdate(object);
                ((DataProvider<I,T>)dataProvider).update(object);
                I idObject = (I)PropertyUtils.getProperty(object, modelInfo.getId().getField().getName());
                flash(FLASH_SUCCESS_KEY, fr.njin.play.crud.views.html.ui.flashUpdateSuccess(modelInfo.getName(), idObject.toString()));
                return redirect(routes.ControllersManager.show(modelInfo.getName(), Formatters.print(idObject),false));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Result delete(ModelInfo modelInfo, DataProvider<?, ?> dataProvider, String idString) {
        I id = id(idString);
        ((DataProvider<I,T>)dataProvider).delete(id);
        return redirect(routes.ControllersManager.list(modelInfo.getName(), false, 0, null, null));
    }

    @SuppressWarnings("unchecked")
    public Result attachment(ModelInfo modelInfo, DataProvider<?, ?> dataProvider, String idString, String field) {
        I id = id(idString);
        T object = ((DataProvider<I,T>)dataProvider).get(id);
        if(object == null)
            return notFound(Messages.get("crud.nofound." + modelInfo.getName(), id));

        try {
            File file = (File)PropertyUtils.getProperty(object, field);
            return ok(file);
        } catch (IllegalAccessException e) {
            logger.warn("Failed to get property " + field, e);
        } catch (InvocationTargetException e) {
            logger.warn("Failed to get property " + field, e);
        } catch (NoSuchMethodException e) {
            logger.warn("Failed to get property " + field, e);
        }
        return notFound();
    }

    private I id(String idString) {
        I id;
        try{
            id = Formatters.conversion.convert(idString, idClass);
        }catch (Exception e) {
            id = new SimpleTypeConverter().convertIfNecessary(idString, idClass);
        }
        return id;
    }

    private Function<FieldInfo, String> fieldInfoToName() {
        return new Function<FieldInfo, String>() {
            @Override
            public String apply(FieldInfo fieldInfo) {
                return fieldInfo.getField().getName();
            }
        };
    }

    private Predicate<FieldInfo> isIgnored() {
        return new Predicate<FieldInfo>() {
            @Override
            public boolean apply(FieldInfo fieldInfo) {
                return fieldInfo.isIgnored();
            }
        };
    }

    private Predicate<FieldInfo> editableField() {
        return new Predicate<FieldInfo>() {
            @Override
            public boolean apply(FieldInfo fieldInfo) {
                return fieldInfo.isEditable();
            }
        };
    }

    private Predicate<FieldInfo> relationField() {
        return new Predicate<FieldInfo>(){
            @Override
            public boolean apply(FieldInfo fieldInfo) {
                return fieldInfo.isRelation();
            }
        };
    }

    private Predicate<FieldInfo> selectField() {
        return new Predicate<FieldInfo>(){
            @Override
            public boolean apply(FieldInfo fieldInfo) {
                return fieldInfo.isSelectable();
            }
        };
    }

    private Predicate<FieldInfo> searchField() {
        return new Predicate<FieldInfo>(){
            @Override
            public boolean apply(FieldInfo fieldInfo) {
                return fieldInfo.isSearchable();
            }
        };
    }

    private Predicate<FieldInfo> isFile() {
        return new Predicate<FieldInfo>() {
            @Override
            public boolean apply(FieldInfo fieldInfo) {
                return FieldInfo.FieldType.FILE == fieldInfo.getFieldType();
            }
        };
    }

    private Iterable<FieldInfo> unsortedListFields(ModelInfo modelInfo) {
        Iterable<FieldInfo> fields = modelInfo.getFields().values();
        Predicate<FieldInfo> filter = Predicates.not(isIgnored());

        F.Option<Predicate<FieldInfo>> optionFilter = listFieldsFilter();
        if(optionFilter.isDefined())
            filter = Predicates.and(filter, optionFilter.get());

        fields =  Iterables.filter(fields, filter);
        return fields;
    }

    private void proceedFiles(T object, Iterable<FieldInfo> fileFields) {
        Http.MultipartFormData multipartFormData = request().body().asMultipartFormData();

        if(multipartFormData != null) {
            for(Http.MultipartFormData.FilePart part : multipartFormData.getFiles()) {
                try {
                    File f = part.getFile();
                    //TODO is it true? Check this in HTTP spec
                    if(f.length() > 0)
                        PropertyUtils.setProperty(object, part.getKey(), part.getFile());
                } catch (IllegalAccessException e) {
                    logger.warn("Failed to set property "+part.getKey(), e);
                } catch (InvocationTargetException e) {
                    logger.warn("Failed to set property " + part.getKey(), e);
                } catch (NoSuchMethodException e) {
                    logger.warn("Failed to set property " + part.getKey(), e);
                }
            }
            DynamicForm dynamicForm = DynamicForm.form().bindFromRequest(request());
            for (FieldInfo fieldInfo : fileFields) {
                String name = fieldInfo.getField().getName();
                logger.debug(name);
                try {
                    if(Boolean.parseBoolean(dynamicForm.get(name+"Delete"))) {
                        logger.debug("Delete");
                        try {
                            PropertyUtils.setProperty(object, name, null);
                        } catch (IllegalAccessException e) {
                            logger.warn("Failed to set property "+name, e);
                        } catch (NoSuchMethodException e) {
                            logger.warn("Failed to set property " + name, e);
                        } catch (InvocationTargetException e) {
                            logger.warn("Failed to set property " + name, e);
                        }
                    }
                }catch (NumberFormatException e){};
            }
        }
    }

    protected T onSave(T object) {
        return object;
    }

    protected T onUpdate(T object) {
        return object;
    }

    protected Html listView(ModelInfo modelInfo, Iterable<FieldInfo> fields, Page<T> data, Option<String> searchOption, Option<Sort> sortOption, boolean forRelation) {
        return list.render(this, modelInfo, fields, data, searchOption, sortOption, forRelation);
    }

    protected Html showView(ModelInfo modelInfo, ShowForm<I, T> form, Iterable<FieldInfo> fields, boolean modal) {
        return show.render(this, modelInfo, form, fields, modal);
    }

    protected Html createView(ModelInfo modelInfo, CreateForm<I, T> form, Iterable<FieldInfo> fields) {
        return create.render(this, modelInfo, form, fields);
    }

    protected Html editView(ModelInfo modelInfo, CreateForm<I, T> form, Iterable<FieldInfo> fields, String idString) {
        return edit.render(this, modelInfo, form, fields, idString);
    }

    protected List<FieldInfo> listFields(ModelInfo modelInfo) {
        Iterable<FieldInfo> fields = unsortedListFields(modelInfo);

        List<FieldInfo> fieldInfoList = Lists.newArrayList(fields);
        F.Option<Ordering<FieldInfo>> ordering = listFieldsOrdering();
        if(ordering.isDefined())
            Collections.sort(fieldInfoList, ordering.get());

        return fieldInfoList;
    }

    protected F.Option<Ordering<FieldInfo>> listFieldsOrdering() {
        return F.Option.None();
    }

    protected F.Option<Predicate<FieldInfo>> listFieldsFilter() {
        return F.Option.None();
    }

    protected F.Option<Iterable<String>> extendSearchFields() {
        return F.Option.None();
    }

    protected List<FieldInfo> showFields(ModelInfo modelInfo) {
        Iterable<FieldInfo> fields = modelInfo.getFields().values();

        F.Option<Predicate<FieldInfo>> filter = showFieldsFilter();
        if(filter.isDefined())
            fields = Iterables.filter(fields, filter.get());

        List<FieldInfo> fieldInfoList = Lists.newArrayList(fields);
        F.Option<Ordering<FieldInfo>> ordering = showFieldsOrdering();
        if(ordering.isDefined())
            Collections.sort(fieldInfoList, ordering.get());

        return fieldInfoList;
    }

    protected F.Option<Ordering<FieldInfo>> showFieldsOrdering() {
        return F.Option.None();
    }

    protected F.Option<Predicate<FieldInfo>> showFieldsFilter() {
        return F.Option.None();
    }

    @SuppressWarnings("unchecked")
    protected List<FieldInfo> createFields(ModelInfo modelInfo) {
        Iterable<FieldInfo> fields = modelInfo.getFields().values();

        Predicate<FieldInfo> filter = Predicates.and(editableField(), Predicates.not(isIgnored()), new Predicate<FieldInfo>() {
            @Override
            public boolean apply(FieldInfo fieldInfo) {
                return !fieldInfo.isId();
            }
        });

        F.Option<Predicate<FieldInfo>> optionFilter = createFieldsFilter();
        if(optionFilter.isDefined())
            filter = Predicates.and(filter, optionFilter.get());

        fields = Iterables.filter(fields, filter);

        List<FieldInfo> fieldInfoList = Lists.newArrayList(fields);
        F.Option<Ordering<FieldInfo>> ordering = createFieldsOrdering();
        if(ordering.isDefined())
            Collections.sort(fieldInfoList, ordering.get());

        return fieldInfoList;
    }

    protected F.Option<Ordering<FieldInfo>> createFieldsOrdering() {
        return F.Option.None();
    }

    protected F.Option<Predicate<FieldInfo>> createFieldsFilter() {
        return F.Option.None();
    }

    protected CrudHtml crudHtmlFor(ModelInfo modelInfo, String fieldName) {
        CrudHtml crudHtml;
        FieldInfo fieldInfo = modelInfo.getFields().get(fieldName);
        if(fieldInfo.getField().isAnnotationPresent(fr.njin.play.crud.annotation.CrudHtml.class)) {
            Class<? extends CrudHtml> crudHtmlClass = fieldInfo.getField().getAnnotation(fr.njin.play.crud.annotation.CrudHtml.class).value();
            try {
                crudHtml = crudHtmlClass.newInstance();
            } catch (Exception e) {
                logger.warn("Unable to instantiate " + crudHtmlClass);
                crudHtml = getCrudHtml();
            }
        }else
            crudHtml = getCrudHtml();
        return crudHtml;
    }

    public CrudHtml<I, T> getCrudHtml() {
        return new TemplateCrudHtml<I, T>();
    }

    @SuppressWarnings("unchecked")
    public Html listHtmlFor(ListForm<I, T> crudForm, String fieldName) {
        return crudHtmlFor(crudForm.getModelInfo(), fieldName).listHtmlFor(this, crudForm, fieldName);
    }

    @SuppressWarnings("unchecked")
    public Html showHtmlFor(ShowForm<I, T> crudForm, String fieldName) {
        return crudHtmlFor(crudForm.getModelInfo(), fieldName).showHtmlFor(this, crudForm, fieldName);
    }

    @SuppressWarnings("unchecked")
    public Html htmlInputFor(CrudForm<I, T> crudForm, String fieldName) {
        return crudHtmlFor(crudForm.getModelInfo(), fieldName).inputFor(this, crudForm, fieldName);
    }

    public String getSortKey(ModelInfo modelInfo, String key) {
        FieldInfo fieldInfo = modelInfo.getFields().get(key);
        if(fieldInfo.isRelation()) {
            F.Option<ModelInfo> relationModelInfo = ControllersManager.getInstance().getModel(fieldInfo.getFieldClass());
            if(!relationModelInfo.isDefined())
                throw new RuntimeException("No ModelInfo found for "+fieldInfo.getFieldClass());
            return key+"."+relationModelInfo.get().getId().getField().getName();
        }
        return key;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> options(FieldInfo fieldInfo) {
        switch (fieldInfo.getFieldType()) {
            case ENUM:
                return Maps.uniqueIndex(
                        Iterables.transform(Lists.newArrayList(fieldInfo.getField().getType().getEnumConstants()), new Function<Object, String>() {
                            @Override
                            public String apply(Object o) {
                                return o.toString();
                            }
                        }),
                        new Function<Object, String>() {
                            @Override
                            public String apply(Object o) {
                                return o.toString();
                            }
                        }
                );
            case RELATION:
                ControllersManager manager = ControllersManager.getInstance();
                final ModelInfo modelInfo = manager.getModel(fieldInfo.getFieldClass()).get();
                final DataProvider dataProvider = manager.dataProvider(modelInfo);

                return Maps.transformValues(
                        Maps.uniqueIndex(dataProvider.all(), new Function<Object, String>() {
                            @Override
                            public String apply(Object o) {
                                try {
                                    FieldInfo idFieldInfo = modelInfo.getId();
                                    return Formatters.conversion.convert(PropertyUtils.getProperty(o, idFieldInfo.getField().getName()), String.class);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }),
                        new Function<Object, String>() {
                            @Override
                            public String apply(Object o) {
                                return Formatters.print(o);
                            }
                        });
            default:
                //TODO
                return null;
        }
    }
}
