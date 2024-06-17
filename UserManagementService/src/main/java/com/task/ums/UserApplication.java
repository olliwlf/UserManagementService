package com.task.ums;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * The UserApplication class extends the {@link Application} class
 * to define the base URI for all JAX-RS web services in this application.
 * <p>
 * The web service in this project is the {@link UserResource} that is accessible under the path "/api/users".
 * </p>
 *
 * @see UserResource
 * @see Application
 */
@ApplicationPath("/api")
public class UserApplication extends Application {

}