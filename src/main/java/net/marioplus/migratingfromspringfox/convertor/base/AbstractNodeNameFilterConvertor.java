package net.marioplus.migratingfromspringfox.convertor.base;

public abstract class AbstractNodeNameFilterConvertor<T> implements IFilterConvertor<T> {

    protected IFilter<T> filter;

    protected IConvertor<T> convertor;

    @Override
    public boolean filter(T t) {
        return filter.filter(t);
    }

    @Override
    public void convert(T t) {
        filter.filter(t);
    }
}
