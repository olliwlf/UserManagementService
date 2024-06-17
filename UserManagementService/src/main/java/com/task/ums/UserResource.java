package com.task.ums;

import com.task.ums.models.User;
import com.task.ums.services.UserService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * REST API resource for managing users.
 * This class handles the CRUD operations for the User entity.
 *
 * @see User
 * @see UserService
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    private UserService userService;

    @Inject
    private Validator validator;

    /**
     * Retrieves a list of all users.
     *
     * @return a list of User objects.
     */
    @GET
    public List<User> listAllUsers() {
        logger.info("GET users/: Getting all users");
        return userService.findAll();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user.
     * @return a Response containing the User object or a NOT_FOUND status if the user does not exist.
     */
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        logger.info("GET users/" + id + ": Getting user by id");

        User user = userService.find(id);

        // user not found
        if (user == null) {
            logger.info("User doesn't exist in database.");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("There is no user with the ID " + id + ".")
                    .build();
        }

        // user found
        logger.info(user.toString());
        return Response
                .ok(user)
                .build();
    }

    /**
     * Adds a new user to the database. The user creation is carried out as a transaction.
     *
     * @param user the User object to add.
     * @return a Response indicating the outcome of the operation.
     */
    @POST
    @Transactional
    public Response addUser(User user) {
        logger.info("POST users: Add user to database.");

        // validate user information
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            logger.info("User data is invalid.");

            String violationMessages = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Validation errors: " + violationMessages)
                    .build();
        }

        // create user in database
        User createdUser = userService.create(user);

        if(createdUser == null) {
            logger.info("User can't be created in database.");
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }

        logger.info("New user has been created in database.");
        return Response
                .status(Response.Status.CREATED)
                .entity(createdUser)
                .build();
    }

    /**
     * Updates an existing user in the database. The update is carried out as a transaction.
     *
     * @param id the ID of the user to update.
     * @param updatedUser the User object with updated information.
     * @return a Response indicating the outcome of the operation.
     */
    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, User updatedUser) {
        logger.info("PUT users/" + id + ": Update existing user in database.");

        // validate user information
        Set<ConstraintViolation<User>> violations = validator.validate(updatedUser);
        if (!violations.isEmpty()) {
            logger.info("User data is invalid.");

            String violationMessages = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Validation errors: " + violationMessages)
                    .build();
        }

        // update user in database
        User user = userService.find(id);
        if (user == null) {
            logger.info("The user to be updated (ID = " + id + ") does not exist in the database.");
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("There is no user with the ID " + id + ".")
                    .build();
        }
        user.setFirstname(updatedUser.getFirstname());
        user.setLastname(updatedUser.getLastname());
        user.setEmail(updatedUser.getEmail());
        user.setBirthday(updatedUser.getBirthday());
        user.setPassword(updatedUser.getPassword());
        userService.update(user);

        logger.info("The user with the ID = " + id + " has been updated in the database.");
        return Response
                .ok(user)
                .build();
    }

    /**
     * Deletes a user from the database.
     *
     * @param id the ID of the user to delete.
     * @return a Response indicating the outcome of the operation.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        logger.info("DELETE users/" + id + ": Delete user from database.");

        // find user
        User user = userService.find(id);
        if (user == null) {
            logger.info("The user to be deleted (ID = " + id + ") does not exist in the database.");
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("There is no user with the ID " + id + ".")
                    .build();
        }

        // delete user from database
        userService.delete(id);

        logger.info("The user with the ID = " + id + " has been removed from database.");
        return Response
                .noContent()
                .build();
    }
}