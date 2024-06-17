package com.task.ums;

import com.task.ums.models.User;
import com.task.ums.services.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link UserService} class.
 *
 * Mocks: TypedQuery, EntityManager, UserService
 *
 * @see UserService
 * @see TypedQuery
 * @see EntityManager
 **/
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private TypedQuery<User> query;

    @Mock
    private EntityManager em;

    @InjectMocks
    private UserService userService;

    private User user;

    /**
     * Initialize user before each test case.
     * */
    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setFirstname("Max");
        user.setLastname("Mustermann");
        user.setEmail("max.mustermann@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setPassword("password");
    }

    /**
     * Test for FindAll method.
     * Verify the invocation of the Entity Manager for createQuery.
     * */
    @Test
    public void test_FindAll() {
        User user2 = new User();
        user2.setId(2L);
        user2.setFirstname("Maria");
        user2.setLastname("Musterfrau");
        user2.setEmail("maria.musterfrau@example.com");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        user2.setPassword("password");

        List<User> users = Arrays.asList(user, user2);

        when(em.createQuery("SELECT u FROM User u", User.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(2, result.size());
        verify(em, times(1)).createQuery("SELECT u FROM User u", User.class);
    }

    /**
     * Test for FindUser method.
     * Verify the invocation of the Entity Manager for find.
     * */
    @Test
    public void test_FindUser() {
        when(em.find(User.class, 1L)).thenReturn(user);

        User result = userService.find(1L);

        assertNotNull(result);
        assertEquals("Max", result.getFirstname());
        verify(em, times(1)).find(User.class, 1L);
    }

    /**
     * Test for CreateUser method.
     * Verify the invocation of the Entity Manager for persist.
     * */
    @Test
    public void test_CreateUser() {
        userService.create(user);

        verify(em, times(1)).persist(user);
    }

    /**
     * Test for UpdateUser method.
     * Verify the invocation of the Entity Manager for merge.
     * */
    @Test
    public void test_UpdateUser() {
        when(em.merge(user)).thenReturn(user);

        User result = userService.update(user);

        assertNotNull(result);
        assertEquals("Max", result.getFirstname());
        verify(em, times(1)).merge(user);
    }

    /**
     * Test for DeleteUser method - success.
     * Verify the invocation of the Entity Manager for remove.
     * */
    @Test
    public void test_DeleteUser() {
        when(em.find(User.class, 1L)).thenReturn(user);

        userService.delete(1L);

        verify(em, times(1)).remove(user);
    }

    /**
     * Test for DeleteUser method - failure.
     * Verify the invocation of the Entity Manager for remove.
     * */
    @Test
    public void test_DeleteUser_NonExistentUser() {
        when(em.find(User.class, 1L)).thenReturn(null);

        userService.delete(1L);

        verify(em, times(0)).remove(any(User.class));
    }
}
