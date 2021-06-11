package com.nh.share.api;

public interface ActionResultListener<T> {

    void onResponseResult(T result);

    void onResponseError(String message);

}
