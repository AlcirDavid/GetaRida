package com.getaride.android.util;

import com.getaride.android.api.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import timber.log.Timber;

public class AppCallAdapterFactory extends CallAdapter.Factory {

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        Timber.e("TEST CallAdapter.Factory.getRawType(returnType) = "
                + CallAdapter.Factory.getRawType(returnType));

        if (CallAdapter.Factory.getRawType(returnType) != ApiResponse.class) {
            return null;
        }

        return new ApiResponseCallAdapter(returnType);
//        final CallAdapter delegate = retrofit.nextCallAdapter(this, returnType, annotations);

//        return new CallAdapter<Object, Object>() {
//            @Override
//            public Type responseType() {
//                return delegate.responseType();
//            }
//
//            @Override
//            public Object adapt(Call<Object> call) {
//                return delegate.adapt(call);
//            }
//        };
    }


    class ApiResponseCallAdapter<R> implements CallAdapter<R, ApiResponse<R>> {

        private final Type responseType;

        public ApiResponseCallAdapter (Type responseType) {
            this.responseType = responseType;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public ApiResponse<R> adapt(Call<R> call) {
            return new ApiResponse<>();
        }
    }
}