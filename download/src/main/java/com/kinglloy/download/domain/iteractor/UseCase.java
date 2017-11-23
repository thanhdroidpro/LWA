package com.kinglloy.download.domain.iteractor;

/**
 * @author jinyalin
 * @since 2017/5/28.
 */

public abstract class UseCase<T, Params> {
    public abstract T execute(Params params);
}
