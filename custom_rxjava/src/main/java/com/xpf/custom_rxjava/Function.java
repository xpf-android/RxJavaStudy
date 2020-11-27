package com.xpf.custom_rxjava;

import androidx.annotation.NonNull;

public interface Function<T,R> {

    /**
     * Apply some calculation to the input value and return some other value.
     * @param t the input value
     * @return the output value
     * @throws Exception on error
     */
    @NonNull
    R apply(@NonNull T t);//变换的行为标准
}
