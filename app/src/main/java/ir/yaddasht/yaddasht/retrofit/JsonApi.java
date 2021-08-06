package ir.yaddasht.yaddasht.retrofit;

import java.util.List;

import ir.yaddasht.yaddasht.model.retro.NoteR;
import ir.yaddasht.yaddasht.model.retro.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JsonApi {
    @GET("api/notes")
    Call<List<NoteR>> getNotes(@Header("auth-token") String token);

    @GET("api/notes/{id}")
    Call<NoteR> getSingleNote(@Header("auth-token") String token, @Path("id") String id);

    @POST("api/notes")
    Call<NoteR> insertNote(@Header("auth-token") String token, @Body NoteR note);

    @PATCH("api/notes/{id}")
    Call<NoteR> updateNote(@Header("auth-token") String token, @Path("id") String id, @Body NoteR note);

    @DELETE("api/notes/{id}")
    Call<Void> deleteNote(@Header("auth-token") String token, @Path("id") String id);

    ////////
    @POST("api/users/login")
    Call<User> login(@Body User user);

    @POST("api/users/register")
    Call<User> register(@Body User user);

    ////
    @POST("api/users/verify")
    Call<User> verify(@Body User user);

    @POST("api/users/resendcode")
    Call<User> resend(@Body User user);
}
