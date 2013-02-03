package fr.njin.play.crud.core;

public interface DataProviderFactory {

    <I,T> DataProvider<I,T> get(Class<I> idClass, Class<T> modelClass);

}
