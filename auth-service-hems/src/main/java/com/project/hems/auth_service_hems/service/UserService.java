package com.project.hems.auth_service_hems.service;

import com.project.hems.auth_service_hems.model.User;

public interface UserService {
     User loginOrRegister(String email, String subject);
}
