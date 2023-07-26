package me.thew.todo.gui;

import javax.annotation.Nonnull;
import java.util.*;

public class PaginatedArrayList implements PaginatedList {

    private static final ArrayList<Object> EMPTY_LIST = new ArrayList<>(0);

    private final List<Object> list;

    private List<Object> page;

    private final int pageSize;

    private int index;

    public PaginatedArrayList(Collection<?> c, int pageSize) {
        this.pageSize = pageSize;
        this.index = 0;
        this.list = new ArrayList<>(c);
        repaginate();
    }

    private void repaginate() {
        if (list.isEmpty()) {
            page = EMPTY_LIST;
        } else {
            int start = index * pageSize;
            int end = start + pageSize - 1;
            if (end >= list.size()) {
                end = list.size() - 1;
            }
            if (start >= list.size()) {
                index = 0;
                repaginate();
            } else if (start < 0) {
                index = list.size() / pageSize;
                if (list.size() % pageSize == 0) {
                    index--;
                }
                repaginate();
            } else {
                page = list.subList(start, end + 1);
            }
        }
    }

    public int size() {
        return page.size();
    }

    public boolean isEmpty() {
        return page.isEmpty();
    }

    public boolean contains(Object o) {
        return page.contains(o);
    }

    public Iterator<Object> iterator() {
        return page.iterator();
    }

    public Object[] toArray() {
        return page.toArray();
    }

    @SuppressWarnings("unchecked")
    public Object[] toArray(@Nonnull Object a[]) {
        return page.toArray(a);
    }

    public Object get(int index) {
        return page.get(index);
    }

    public int indexOf(Object o) {
        return page.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return page.lastIndexOf(o);
    }

    public ListIterator<Object> listIterator() {
        return page.listIterator();
    }

    public ListIterator<Object> listIterator(int index) {
        return page.listIterator(index);
    }

    public List<Object> subList(int fromIndex, int toIndex) {
        return page.subList(fromIndex, toIndex);
    }

    public boolean add(Object o) {
        boolean b = list.add(o);
        repaginate();
        return b;
    }

    public boolean remove(Object o) {
        boolean b = list.remove(o);
        repaginate();
        return b;
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return false;
    }

    public boolean addAll(@Nonnull Collection<?> c) {
        boolean b = list.addAll(c);
        repaginate();
        return b;
    }

    public boolean addAll(int index,@Nonnull  Collection<?> c) {
        boolean b = list.addAll(index, c);
        repaginate();
        return b;
    }

    public boolean removeAll(@Nonnull Collection<?> c) {
        boolean b = list.removeAll(c);
        repaginate();
        return b;
    }

    public boolean retainAll(@Nonnull Collection<?> c) {
        boolean b = list.retainAll(c);
        repaginate();
        return b;
    }

    public void clear() {
        list.clear();
        repaginate();
    }

    public Object set(int index, Object element) {
        Object o = list.set(index, element);
        repaginate();
        return o;
    }

    public void add(int index, Object element) {
        list.add(index, element);
        repaginate();
    }

    public Object remove(int index) {
        Object o = list.remove(index);
        repaginate();
        return o;
    }

    public boolean isFirstPage() {
        return index == 0;
    }

    public boolean isLastPage() {
        return list.size() - ((index + 1) * pageSize) < 1;
    }

    public boolean isNextPageAvailable() {
        return !isLastPage();
    }

    public boolean isPreviousPageAvailable() {
        return !isFirstPage();
    }

    public void gotoPage(int pageNumber) {
        index = pageNumber;
        repaginate();
    }
}


interface PaginatedList extends List<Object> {
    boolean isNextPageAvailable();
    boolean isPreviousPageAvailable();
    void gotoPage(int pageNumber);
}
