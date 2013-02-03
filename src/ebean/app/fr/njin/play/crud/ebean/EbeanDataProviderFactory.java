package fr.njin.play.crud.ebean;

import fr.njin.play.crud.core.DataProvider;
import fr.njin.play.crud.core.DataProviderFactory;

public class EbeanDataProviderFactory implements DataProviderFactory {

    @Override
    public <I, T> DataProvider<I, T> get(Class<I> idClass, Class<T> modelClass) {
        return new EbeanDataProvider<I, T>(idClass, modelClass);
    }
}
