package com.shopapp.services;

import com.shopapp.dtos.UpdateUserDTO;
import com.shopapp.dtos.UserDTO;
import com.shopapp.exceptions.DataNotFoundException;
import com.shopapp.models.User;

public interface IUserService {
    User createUser (UserDTO userDTO) throws Exception;

    String login (String phoneNumber, String password, Long roleId) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User updateUser (Long userId, UpdateUserDTO updatedUserDTO) throws Exception;
}
