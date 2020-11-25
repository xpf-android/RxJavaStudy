package com.xpf.rxjava.study.retrofit_okhttp_rxjava.retrofit_okhttp;

import io.reactivex.Observable;
import retrofit2.http.Body;

public interface IRequestNetwork {

    //请求注册功能
    Observable<RegisterResponse> registerAction(@Body RegisterRequest registerRequest);

    //请求登录功能
    Observable<LoginResponse> loginAction(@Body LoginRequest loginRequest);

}
