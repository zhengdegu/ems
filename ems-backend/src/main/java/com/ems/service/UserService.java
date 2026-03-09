package com.ems.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.dto.LoginRequest;
import com.ems.dto.RegisterRequest;
import com.ems.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {

    Map<String, Object> login(LoginRequest request);

    void register(RegisterRequest request);

    Page<User> listPage(int page, int pageSize, String keyword, String role, Integer enabled);

    void createUser(User user);

    void updateUser(Long id, User user);

    void deleteUser(Long id);

    void toggleEnabled(Long id);

    void resetPassword(Long id);

    void updateProfile(Long id, User user);

    void changePassword(Long id, String oldPassword, String newPassword);
}
