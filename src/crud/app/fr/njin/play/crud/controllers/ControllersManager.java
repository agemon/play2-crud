package fr.njin.play.crud.controllers;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import fr.njin.play.crud.core.DataProvider;
import fr.njin.play.crud.core.DataProviderFactory;
import fr.njin.play.crud.core.ModelInfo;
import fr.njin.play.crud.core.ModelRegistry;
import play.Logger;
import play.cache.Cache;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.Callable;

public class ControllersManager extends Controller {

    private static ControllersManager instance;

    private Logger.ALogger logger = Logger.of(ControllersManager.class);

	private ModelRegistry modelRegistry;
    private ControllerRegistry controllerRegistry;
    private DataProviderFactory dataProviderFactory;
    private Map<String, Crud> controllers;

	private ControllersManager(ModelRegistry modelRegistry, ControllerRegistry controllerRegistry, DataProviderFactory dataProviderFactory) {
		this.modelRegistry = modelRegistry;
        this.controllerRegistry = controllerRegistry;
        this.dataProviderFactory = dataProviderFactory;
        this.controllers = new HashMap<String, Crud>();
        for (ModelInfo modelInfo : this.modelRegistry.getModels()) {
            F.Option<? extends Crud<?,?>> controller = getController(modelInfo);
            if(controller.isDefined()) {
                controllers.put(modelInfo.getName(), controller.get());
            }
        }
	}

    public static void init(ModelRegistry modelRegistry, ControllerRegistry controllerRegistry, DataProviderFactory dataProviderFactory) {
        instance = new ControllersManager(modelRegistry, controllerRegistry, dataProviderFactory);
    }

    public static ControllersManager getInstance() {
        return instance;
    }

    public Map<String, Crud> getControllers() {
        return controllers;
    }

    public Iterable<String> modelWithController() {
        return ImmutableSortedSet.<String>naturalOrder().addAll(controllers.keySet()).build();
    }

    public F.Option<ModelInfo> getModel(final String name) {
        ModelInfo modelInfo = null;
        try {
            modelInfo = (ModelInfo) Cache.getOrElse(getClass().getName() + "_ModelInfo_" + name, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return Iterables.find(modelRegistry.getModels(), new Predicate<ModelInfo>() {
                        @Override
                        public boolean apply(ModelInfo modelInfo) {
                            return modelInfo.getName().equals(name);
                        }
                    }, null);
                }
            }, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelInfo == null ? F.Option.<ModelInfo>None() : F.Option.Some(modelInfo);
    }

    public F.Option<ModelInfo> getModel(final Class<?> clazz) {
        return getModel(clazz.getSimpleName());
    }

    public F.Option<? extends Crud<?, ?>> getController(ModelInfo model) {
        Crud<?, ?> crud = controllerRegistry.getController(model.getId().getField().getType(), model.getModelClass());
        return crud == null ? F.Option.<Crud<?,?>>None() : F.Option.Some(crud);

    }

    public DataProvider<?, ?> dataProvider(ModelInfo modelInfo) {
        return dataProviderFactory.get(modelInfo.getId().getFieldClass(), modelInfo.getModelClass());
    }

    private F.Either<F.Tuple<ModelInfo,Crud<?,?>>, ? extends Result> controllerOrNotFound(final String name) {
        F.Option<ModelInfo> modelInfo = getModel(name);
        if(!modelInfo.isDefined())
            return F.Either.Right(notFound("Model with name "+name+" not found!"));

        ModelInfo model = modelInfo.get();

        F.Option<? extends Crud<?,?>> crud = getController(model);
        if(!crud.isDefined())
            return F.Either.Right(notFound("Controller for model "+model.getModelClass()+" not found"));

        Crud<?,?> controller = crud.get();

        return F.Either.Left(new F.Tuple<ModelInfo, Crud<?, ?>>(model, controller));
    }

    public Result dashboard() {
        return ok(fr.njin.play.crud.views.html.dashboard.render());
    }

	public Result index(final String name) {
        F.Either<F.Tuple<ModelInfo,Crud<?,?>>, ? extends Result> controller = controllerOrNotFound(name);
        if(controller.right.isDefined())
            return controller.right.get();
        F.Tuple<ModelInfo, Crud<?,?>> tuple = controller.left.get();
        return tuple._2.index(tuple._1, dataProvider(tuple._1));
	}

    public Result list(final String name, boolean forRelation, final int p, final String q, final String o) {
        F.Either<F.Tuple<ModelInfo,Crud<?,?>>, ? extends Result> controller = controllerOrNotFound(name);
        if(controller.right.isDefined())
            return controller.right.get();
        F.Tuple<ModelInfo, Crud<?,?>> tuple = controller.left.get();
        return tuple._2.list(tuple._1, dataProvider(tuple._1), p, q, o, forRelation);
    }

    public Result show(final String name, final String id, boolean modal) {
        F.Either<F.Tuple<ModelInfo,Crud<?,?>>, ? extends Result> controller = controllerOrNotFound(name);
        if(controller.right.isDefined())
            return controller.right.get();
        F.Tuple<ModelInfo, Crud<?,?>> tuple = controller.left.get();
        return tuple._2.show(tuple._1, dataProvider(tuple._1), id, modal);
    }

    public Result create(final String name) {
        F.Either<F.Tuple<ModelInfo,Crud<?,?>>, ? extends Result> controller = controllerOrNotFound(name);
        if(controller.right.isDefined())
            return controller.right.get();
        F.Tuple<ModelInfo, Crud<?,?>> tuple = controller.left.get();
        return tuple._2.create(tuple._1, dataProvider(tuple._1));
    }


    public Result edit(final String name, final String id) {
        F.Either<F.Tuple<ModelInfo,Crud<?,?>>, ? extends Result> controller = controllerOrNotFound(name);
        if(controller.right.isDefined())
            return controller.right.get();
        F.Tuple<ModelInfo, Crud<?,?>> tuple = controller.left.get();
        return tuple._2.edit(tuple._1, dataProvider(tuple._1), id);
    }

    public Result save(final String name) {
        F.Either<F.Tuple<ModelInfo,Crud<?,?>>, ? extends Result> controller = controllerOrNotFound(name);
        if(controller.right.isDefined())
            return controller.right.get();
        F.Tuple<ModelInfo, Crud<?,?>> tuple = controller.left.get();
        return tuple._2.save(tuple._1, dataProvider(tuple._1));
    }

    public Result update(final String name, final String id) {
        F.Either<F.Tuple<ModelInfo,Crud<?,?>>, ? extends Result> controller = controllerOrNotFound(name);
        if(controller.right.isDefined())
            return controller.right.get();
        F.Tuple<ModelInfo, Crud<?,?>> tuple = controller.left.get();
        return tuple._2.update(tuple._1, dataProvider(tuple._1), id);
    }

    public Result delete(final String name, final String id) {
        F.Either<F.Tuple<ModelInfo,Crud<?,?>>, ? extends Result> controller = controllerOrNotFound(name);
        if(controller.right.isDefined())
            return controller.right.get();
        F.Tuple<ModelInfo, Crud<?,?>> tuple = controller.left.get();
        return tuple._2.delete(tuple._1, dataProvider(tuple._1), id);
    }

    public Result attachment(final String name, final String id, final String field) {
        F.Either<F.Tuple<ModelInfo,Crud<?,?>>, ? extends Result> controller = controllerOrNotFound(name);
        if(controller.right.isDefined())
            return controller.right.get();
        F.Tuple<ModelInfo, Crud<?,?>> tuple = controller.left.get();
        return tuple._2.attachment(tuple._1, dataProvider(tuple._1), id, field);
    }
}