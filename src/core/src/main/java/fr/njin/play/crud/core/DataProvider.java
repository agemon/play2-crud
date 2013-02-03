package fr.njin.play.crud.core;

import java.util.List;

public interface DataProvider<I,T> {

    T get(I id);

    List<T> all();

    Page<T> get(int page, int count, String search, Sort sort,
                Iterable<String> selectable, Iterable<String> searchable, Iterable<String> fetchable);

    T save(T object);

    T update(T object);

    void delete(I id);

}
