package com.gestion_users.ms_gestion_users.service.auth;

import com.gestion_users.ms_gestion_users.dto.*;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    HashResponse generatePasswordHash(HashRequest request);
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
}