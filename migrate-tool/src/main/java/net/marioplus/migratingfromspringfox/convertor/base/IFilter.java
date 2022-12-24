package net.marioplus.migratingfromspringfox.convertor.base;

public interface IFilter<T> {

    boolean filter(T t);
}
