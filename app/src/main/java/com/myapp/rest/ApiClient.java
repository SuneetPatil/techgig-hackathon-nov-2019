package com.myapp.rest;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class ApiClient {

    private static ApiInterface service;
    public static ApiInterface getInstance(String base_url){
        if(service == null){
            OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(
                            new Interceptor() {
                                @Override
                                public Response intercept(Interceptor.Chain chain) throws IOException {
                                    Request request = chain.request().newBuilder()
                                            .addHeader("Key", "value")
                                            .build();
                                    return chain.proceed(request);
                                }
                            }).build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .client(defaultHttpClient) //you can customize it
                    .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(new Persister(new AnnotationStrategy())))
                    .build();

            service = retrofit.create(ApiInterface.class);
        }
        return service;
    }










    
}
