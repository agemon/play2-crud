package	fr.njin.play.crud.ebean;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fr.njin.play.crud.core.FieldInfo;
import fr.njin.play.crud.core.ModelInfo;
import fr.njin.play.crud.core.ModelRegistry;
import fr.njin.play.crud.core.annotation.Crud;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import play.Application;
import play.Logger;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EbeanModelRegistry implements ModelRegistry {

    private Logger.ALogger logger = Logger.of(EbeanModelRegistry.class);
    private List<ModelInfo> modelInfos;

    public EbeanModelRegistry(Application app) {

        final Reflections reflections =  new Reflections(new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage("", app.classloader()))
                        .setScanners(new SubTypesScanner(),
                                new TypeAnnotationsScanner()));

        Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);
        this.modelInfos = Lists.newArrayList(Iterables.transform(
                Iterables.filter(entities, Predicates.not(ReflectionUtils.withAnnotation(Crud.Exclude.class))),
                new Function<Class<?>, ModelInfo>() {
                    @Override
                    public ModelInfo apply(Class<?> entity) {
                        logger.debug("Registering entity " + entity.getName());
                        return getInfo(entity);
                    }
                })
        );

    }

    private FieldInfo toFieldInfo(Field field) {
        return new EbeanFieldInfo(field);
    }

    private Function<Field, FieldInfo> toFieldInfo() {
       return new Function<Field, FieldInfo>() {
           @Override
           public FieldInfo apply(Field field) {
               return toFieldInfo(field);
           }
       };
    }

    private Function<FieldInfo, String> fieldInfoName() {
        return new Function<FieldInfo, String>() {
            @Override
            public String apply(FieldInfo fieldInfo) {
                return fieldInfo.getField().getName();
            }
        };
    }

    @SuppressWarnings("unchecked")
    private ModelInfo getInfo(final Class<?> entity) {
        List<Field> modelFields = Lists.newArrayList(entity.getDeclaredFields());

        Class<?> superClass = entity.getSuperclass();
        while(superClass != null &&
                Iterables.tryFind(Lists.newArrayList(superClass.getAnnotations()), new Predicate<Annotation>() {
                    @Override
                    public boolean apply(Annotation annotation) {
                        return annotation.annotationType().equals(MappedSuperclass.class);
                    }
                }).isPresent()) {

            modelFields.addAll(Lists.newArrayList(superClass.getDeclaredFields()));
            superClass = superClass.getSuperclass();
        }

        Map<String, FieldInfo> fields = Maps.uniqueIndex(
                Iterables.transform(
                        Iterables.filter(modelFields,
                                Predicates.<Field>and(
                                        Predicates.not(Predicates.and(ReflectionUtils.withAnnotation(Transient.class), Predicates.not(ReflectionUtils.withAnnotation(Crud.class)))),
                                        Predicates.not(ReflectionUtils.withModifier(Modifier.STATIC)),
                                        Predicates.not(ReflectionUtils.withModifier(Modifier.FINAL)),
                                        Predicates.not(new Predicate<Field>() {
                                            @Override
                                            public boolean apply(Field field) {
                                                return field.getName().startsWith("_ebean");
                                            }
                                        })
                                )
                        ), toFieldInfo()
                ),
                fieldInfoName()
        );


        FieldInfo id = Iterables.find(fields.values(), new Predicate<FieldInfo>() {
            @Override
            public boolean apply(FieldInfo fieldInfo) {
                return fieldInfo.isId();
            }
        });

        ModelInfo modelInfo = new ModelInfo(entity.getSimpleName(), entity, id, fields);
        logger.debug("-> "+modelInfo);
        return modelInfo;
    }

    @Override
    public List<ModelInfo> getModels() {
        return this.modelInfos;
    }
}