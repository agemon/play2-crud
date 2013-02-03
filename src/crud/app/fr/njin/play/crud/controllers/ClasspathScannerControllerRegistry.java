package fr.njin.play.crud.controllers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import play.Application;
import play.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.Map;

public class ClasspathScannerControllerRegistry implements ControllerRegistry {

    private Logger.ALogger logger = Logger.of(ClasspathScannerControllerRegistry.class);
    private Map<Class<?>, Crud> controllers;

    public ClasspathScannerControllerRegistry(Application app) {
        final Reflections reflections =  new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("", app.classloader()))
                .setScanners(new SubTypesScanner(),
                        new TypeAnnotationsScanner()));


        controllers = Maps.uniqueIndex(
                Iterables.transform(
                        Iterables.filter(reflections.getSubTypesOf(Crud.class), new Predicate<Class<? extends Crud>>() {
                            @Override
                            public boolean apply(Class<? extends Crud> aClass) {
                                return !Modifier.isAbstract(aClass.getModifiers());
                            }
                        }),
                        new Function<Class<? extends Crud>, Crud>() {
                            @Nullable
                            @Override
                            public Crud apply(Class<? extends Crud> aClass) {
                                logger.debug("Registering crud controller "+aClass.getName());
                                try {
                                    return aClass.newInstance();
                                } catch (Exception e) {
                                    logger.error("Unable to create crud controller " + aClass.getName() + ". Cause : " + e);
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                ),
                new Function<Crud, Class<?>>() {
                    @Override
                    public Class<?> apply(@Nullable Crud crud) {
                        return crud == null ? null : crud.getModelClass();
                    }
                }

        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I, T> Crud<I, T> getController(I idClass, T modelClass) {
        return controllers.get(modelClass);
    }
}
