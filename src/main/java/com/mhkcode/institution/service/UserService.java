package com.mhkcode.institution.service;

import com.mhkcode.institution.dto.request.ChangePasswordRequest;
import com.mhkcode.institution.dto.request.LoginRequest;
import com.mhkcode.institution.dto.request.RegistrationRequest;
import com.mhkcode.institution.dto.response.ChangePasswordResponse;
import com.mhkcode.institution.dto.response.LoginResponse;
import com.mhkcode.institution.dto.response.RegistrationResponse;
import com.mhkcode.institution.model.User;

import java.util.List;

public interface UserService {
RegistrationResponse addUser(RegistrationRequest request);
LoginResponse login(LoginRequest request);
ChangePasswordResponse changePassword(ChangePasswordRequest request);
User getById(Long id);
List<User>allUsers();
RegistrationResponse updateUser(RegistrationRequest request);
void deleteUser(String email);

}
