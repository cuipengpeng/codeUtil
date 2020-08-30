package com.moxiang.common.base;

import android.support.annotation.NonNull;

/**
 * Created by admin on 2019/10/25.
 */

public interface IRepositoryManager {
    @NonNull
    <T> T obtainRetrofitService(@NonNull Class<T> service);
}
