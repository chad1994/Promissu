package com.simsimhan.promissu.network;

import com.simsimhan.promissu.network.model.Promise;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthAPI {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("user/login")
    Observable<Login.Response> loginKakao(@Body Login.Request loginRequest);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("appointment")
    Observable<Promise.Response> createPromise(@Header("Authorization") String token, @Body Promise.Request promiseRequest);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("participation/invite")
    Observable<Promise.Response> inviteFriends(@Header("Authorization") String token, @Body Promise.Request promiseRequest);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("appointments")
    Observable<List<Promise.Response>> getMyPromise(@Header("Authorization") String token, @Query("offset") int offset, @Query("limit") int limit, @Query("type") String type);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("participation/join/{room_id}")
    Observable<Promise.Response> enterPromise(@Header("Authorization") String token, @Path("room_id") String roomId);
}
