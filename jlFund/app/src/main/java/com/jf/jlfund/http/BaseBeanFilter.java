package com.jf.jlfund.http;

import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.exception.ForceUpdateException;
import com.jf.jlfund.exception.ServerException;
import com.jf.jlfund.exception.TokenInvalidException;
import com.jf.jlfund.exception.UnknownException;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by 55 on 2017/11/6.
 */

public class BaseBeanFilter<T> implements Function<BaseBean<T>, T> {
    @Override
    public T apply(@NonNull BaseBean<T> baseBean) throws Exception {
        if (baseBean.isSuccess()) {
            return baseBean.getData();
        }

        if (baseBean.isTokenInvalid()) {
            throw new TokenInvalidException(baseBean.toString());
        }

        if (baseBean.isForceUpdate()) {
            throw new ForceUpdateException(baseBean.toString());
        }

        if (baseBean.isFailed()) {
            throw new ServerException(baseBean.toString());
        }

        throw new UnknownException(baseBean.toString());
    }
}
