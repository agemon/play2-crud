package fr.njin.play.crud.core;

public interface Page<T> {

    java.util.List<T> getList();

    int getTotalRowCount();

    int getTotalPageCount();

    int getPageIndex();

    boolean hasNext();

    boolean hasPrev();

}
