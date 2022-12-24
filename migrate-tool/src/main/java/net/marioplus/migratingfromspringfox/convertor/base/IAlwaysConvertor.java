package net.marioplus.migratingfromspringfox.convertor.base;

public interface IAlwaysConvertor<T> extends IFilterConvertor<T> {



    @Override
    default boolean filter(T t) {
        return true;
    }
}
