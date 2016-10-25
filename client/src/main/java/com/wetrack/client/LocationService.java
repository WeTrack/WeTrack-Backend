package com.wetrack.client;

import com.wetrack.client.model.Location;
import com.wetrack.client.model.Message;
import retrofit2.Response;
import retrofit2.http.*;
import rx.Observable;

import java.util.List;

interface LocationService {

    @GET("/users/{username}/locations/latest")
    Observable<Response<Location>> getLatestLocation(@Path("username") String username);

    @GET("/users/{username}/locations")
    Observable<Response<List<Location>>> getLocationSince(@Path("username") String username,
                                                          @Query("since") String sinceTime);

    @POST("/users/{username}/locations")
    Observable<Response<Message>> uploadLocations(@Path("username") String username,
                                                  @Body LocationsUploadRequest requestBody);

    class LocationsUploadRequest {
        private String token;
        private List<Location> locations;

        LocationsUploadRequest() {}

        LocationsUploadRequest(String token, List<Location> locations) {
            this.token = token;
            this.locations = locations;
        }

        String getToken() {
            return token;
        }
        void setToken(String token) {
            this.token = token;
        }
        List<Location> getLocations() {
            return locations;
        }
        void setLocations(List<Location> locations) {
            this.locations = locations;
        }
    }

}
