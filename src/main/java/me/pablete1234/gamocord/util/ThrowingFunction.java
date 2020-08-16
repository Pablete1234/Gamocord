package me.pablete1234.gamocord.util;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> extends Function<T, R> {
    R applyThrowing(T t) throws E;

    @Override
    default R apply(T t) {
        try {
            return applyThrowing(t);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
