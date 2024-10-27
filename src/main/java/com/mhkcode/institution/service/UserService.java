package com.mhkcode.institution.service;

import com.mhkcode.institution.dto.request.ChangePasswordRequest;
import com.mhkcode.institution.dto.request.LoginRequest;
import com.mhkcode.institution.dto.request.RegistrationRequest;
import com.mhkcode.institution.dto.response.ChangePasswordResponse;
import com.mhkcode.institution.dto.response.LoginResponse;
import com.mhkcode.institution.dto.response.RegistrationResponse;
import com.mhkcode.institution.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
RegistrationResponse addUser(RegistrationRequest request);
LoginResponse login(LoginRequest request);
ChangePasswordResponse changePassword(ChangePasswordRequest request);
    ChangePasswordResponse resetPassword(ChangePasswordRequest request);
User getById(Long id);
User findByEmail(String email);
List<User>allUsers();
RegistrationResponse updateUser(RegistrationRequest request);
void deleteUser(Long userId);
User setProfile(User user, MultipartFile file) throws IOException;

}
