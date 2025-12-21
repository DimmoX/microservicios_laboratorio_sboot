package com.gestion_users.ms_gestion_users.service.auth;

import com.gestion_users.ms_gestion_users.dto.AuthRequest;
import com.gestion_users.ms_gestion_users.dto.AuthResponse;
import com.gestion_users.ms_gestion_users.dto.ChangePasswordRequest;
import com.gestion_users.ms_gestion_users.dto.ChangePasswordResponse;
import com.gestion_users.ms_gestion_users.dto.ForgotPasswordRequest;
import com.gestion_users.ms_gestion_users.dto.ForgotPasswordResponse;
import com.gestion_users.ms_gestion_users.dto.HashRequest;
import com.gestion_users.ms_gestion_users.dto.HashResponse;
import com.gestion_users.ms_gestion_users.dto.ResetPasswordRequest;
import com.gestion_users.ms_gestion_users.dto.ResetPasswordResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    HashResponse generatePasswordHash(HashRequest request);
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);
    ChangePasswordResponse changePassword(String username, ChangePasswordRequest request);
}