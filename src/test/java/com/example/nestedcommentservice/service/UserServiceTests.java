package com.example.nestedcommentservice.service;

import com.example.nestedcommentservice.entities.User;
import com.example.nestedcommentservice.error.CustomException;
import com.example.nestedcommentservice.model.user.UserRequestModel;
import com.example.nestedcommentservice.model.user.UserResponseModel;
import com.example.nestedcommentservice.repository.UserRepository;
import com.mongodb.assertions.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.assertions.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserActionService userActionService;

    @Mock
    private ContentService contentService;

    @InjectMocks
    private UserService userService;

    @Test
    public void testAddUser_WithException() {
        UserRequestModel userRequestModel = new UserRequestModel();
        userRequestModel.setUserName("TestUser");
        when(userRepository.save(any())).thenThrow(new RuntimeException("Something went wrong!"));
        try {
            userService.addUser(userRequestModel);
        }catch (CustomException e){
            Assertions.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        String userId = "invalidUserId";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        try {
            assertFalse(userService.deleteUser(userId));
        }catch (CustomException e){
            Assertions.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testAddUser() {
        UserRequestModel userRequestModel = new UserRequestModel();
        userRequestModel.setUserName("TestUser");
        User user = new User();
        user.setUserName("TestUser");
        user.setId("testUserId");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponseModel response = userService.addUser(userRequestModel);
        assertEquals("testUserId", response.getUserId());
        assertEquals("TestUser", response.getUserName());
    }

    @Test
    public void testFetchUser() {
        User user1 = new User();
        user1.setUserName("User1");
        user1.setId("userId1");
        User user2 = new User();
        user2.setUserName("User2");
        user2.setId("userId2");
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        when(userRepository.findAll()).thenReturn(users);
        List<UserResponseModel> response = userService.fetchUser();
        assertEquals(2, response.size());
        assertEquals("userId1", response.get(0).getUserId());
        assertEquals("User1", response.get(0).getUserName());
        assertEquals("userId2", response.get(1).getUserId());
        assertEquals("User2", response.get(1).getUserName());
    }

}
