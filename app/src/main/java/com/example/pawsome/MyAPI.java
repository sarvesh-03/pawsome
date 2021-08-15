package com.example.pawsome;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MyAPI {
    //https://api.thedogapi.com/      v1/breeds
    @GET("v1/breeds")
    Call<List<Animal>> getAnimals(
            @Query("page") int page,
            @Query("limit") int limit,@Header("x-api-key") String key
    );

    @GET("v1/breeds/search")
    Call<List<Animal>> getDog(@Query("page") int page,
                              @Query("limit") int limit, @Query("q") String dog,@Header("x-api-key") String key);

    @POST("v1/favourites")
    Call<ResponseBody> postVote(@Body Fav fav, @Header("x-api-key") String key);

    @GET("v1/images/{image_id}")
    Call<Image> getImageUrl(@Path("image_id") String id, @Header("x-api-key") String key);

    @GET("v1/favourites")
    Call<List<Animal>> GetFav(@Query("sub_id") String sub_id, @Query("page") int page, @Query("limit") int limit, @Header("x-api-key") String key);

    @DELETE("v1/favourites/{favourite_id}")
    Call<ResponseBody> DeleteFav(@Path("favourite_id") String fav_id, @Header("x-api-key") String key);

    @Multipart
    @POST("v1/images/upload")
    Call<ResponseBody> UploadImage(@Part MultipartBody.Part file, @Part("sub_id") RequestBody sub_id, @Header("x-api-key") String key);

}
