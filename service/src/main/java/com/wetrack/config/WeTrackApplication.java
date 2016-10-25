package com.wetrack.config;

import com.wetrack.service.LocationService;
import com.wetrack.service.authen.UserLoginService;
import com.wetrack.service.authen.UserLogoutService;
import com.wetrack.service.authen.UserTokenVerificationService;
import com.wetrack.service.user.UserCreateService;
import com.wetrack.service.user.UserGetService;
import com.wetrack.service.user.UserPasswordUpdateService;
import com.wetrack.service.user.UserUpdateService;
import com.wetrack.util.GsonJerseyProvider;
import org.glassfish.jersey.server.ResourceConfig;

public class WeTrackApplication extends ResourceConfig {

    public WeTrackApplication() {
        register(UserLoginService.class);
        register(UserLogoutService.class);
        register(UserTokenVerificationService.class);
        register(UserCreateService.class);
        register(UserGetService.class);
        register(UserPasswordUpdateService.class);
        register(UserUpdateService.class);
        register(LocationService.class);
        register(GsonJerseyProvider.class);
    }

}
