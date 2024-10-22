package com.mhkcode.institution.service;

import com.mhkcode.institution.dto.request.LoginRequest;
import com.mhkcode.institution.dto.request.RegistrationRequest;
import com.mhkcode.institution.dto.response.LoginResponse;
import com.mhkcode.institution.dto.response.RegistrationResponse;

public interface UserService {
RegistrationResponse addUser(RegistrationRequest request);
LoginResponse login(LoginRequest request);
}
