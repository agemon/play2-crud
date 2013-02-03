package fr.njin.play.crud.ebean;

import com.avaje.ebean.*;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import fr.njin.play.crud.core.DataProvider;
import fr.njin.play.crud.core.Page;
import fr.njin.play.crud.core.Sort;
import play.db.ebean.Model;

import javax.annotation.Nullable;
import java.util.List;

public class EbeanDataProvider<I, T> implements DataProvider<I, T> {

    private Model.Finder<I, T> finder;
    private Class<T> modelClass;

    public EbeanDataProvider(Class<I> idClass, Class<T> modelClass) {
        this.finder = new Model.Finder<I, T>(idClass, modelClass);
        this.modelClass = modelClass;
    }

    @Override
    public T get(I id) {
        return finder.byId(id);
    }

    @Override
    public List<T> all() {
        return finder.all();
    }

    @Override
    public Page<T> get(int page, int count, String search, Sort sort,
                       Iterable<String> selectables, Iterable<String> searchables,
                       Iterable<String> fetchables) {

        Query<T> query = finder.select(Joiner.on(",").join(selectables));
        Junction<T> where = query.where().conjunction();

        if(search != null) {
            String s = "%"+search+"%";
            Junction<T> searchQuery = where.disjunction();

            for(String searchable : searchables) {
                searchQuery.ilike(searchable, s);
            }
            searchQuery.endJunction();
        }
        Query<T> q = where.endJunction().query();

        if(sort != null && !sort.getOrders().isEmpty()) {
            q = q.orderBy(Joiner.on(",").join(Iterables.transform(sort.getOrders(), new Function<Sort.Order, String>() {
                @Override
                public String apply(Sort.Order order) {
                    return order.key+" "+order.direction.name();
                }
            })))
            ;
        }

        final com.avaje.ebean.Page<T> results = q.findPagingList(count).getPage(page);

        return new Page<T>() {
            @Override
            public List<T> getList() {
                return results.getList();
            }

            @Override
            public int getTotalRowCount() {
                return results.getTotalRowCount();
            }

            @Override
            public int getTotalPageCount() {
                return results.getTotalPageCount();
            }

            @Override
            public int getPageIndex() {
                return results.getPageIndex();
            }

            @Override
            public boolean hasNext() {
                return results.hasNext();
            }

            @Override
            public boolean hasPrev() {
                return results.hasPrev();
            }
        };
    }

    @Override
    public T save(T object) {
        Ebean.save(object);
        return object;
    }

    @Override
    public T update(T object) {
        Ebean.update(object);
        return object;
    }

    @Override
    public void delete(I id) {
        Ebean.delete(modelClass, id);
    }
}
