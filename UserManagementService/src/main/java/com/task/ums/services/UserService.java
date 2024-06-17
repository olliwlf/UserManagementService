package com.task.ums.services;

import com.task.ums.models.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

/**
 * UserService is a stateless session bean that provides methods to perform CRUD operations
 * on User entities. It interacts with the database using JPA (Java Persistence API).
 *
 * The following operations are supported:
 * - Retrieve all users
 * - Retrieve a user by ID
 * - Create a new user
 * - Update an existing user
 * - Delete a user by ID
 *
 * This service class uses an EntityManager to manage persistence and is associated with
 * the persistence unit "UserPU".
 */
@Stateless
public class UserService {
    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    /**
     * Retrieves a list of all User entities from the database.
     *
     * @return a List of User objects.
     */
    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    /**
     * Finds a User entity by its primary key (ID).
     *
     * @param id the ID of the User entity to find.
     * @return the User entity found or null if not found.
     */
    public User find(Long id) { return em.find(User.class, id); }

    /**
     * Persists a new User entity in the database.
     *
     * @param user the User entity to create.
     * @return the created User entity.
     */
    public User create(User user) {
        em.persist(user);
        return user;
    }

    /**
     * Merges the state of the given User entity into the current persistence context.
     *
     * @param user the User entity to update.
     * @return the updated User entity.
     */
    public User update(User user) {
        return em.merge(user);
    }

    /**
     * Removes a User entity identified by its primary key (ID) from the database.
     *
     * @param id the ID of the User entity to delete.
     */
    public void delete(Long id) {
        User user = em.find(User.class, id);
        if (user != null) {
            em.remove(user);
        }
    }
}
