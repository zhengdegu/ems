package com.ems.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testLoginLockAfter5Failures() {
        // 测试连续5次登录失败后账号被锁定
        // 1. 创建测试用户
        // 2. 连续5次使用错误密码登录
        // 3. 验证第6次登录返回"账号已锁定"
    }

    @Test
    void testLoginUnlockAfter15Minutes() {
        // 测试锁定15分钟后自动解锁
        // 1. 模拟账号被锁定（设置 lock_time 为16分钟前）
        // 2. 使用正确密码登录
        // 3. 验证登录成功，锁定状态被清除
    }

    @Test
    void testLoginSuccess() {
        // 测试正常登录流程
        // 1. 使用正确的用户名和密码登录
        // 2. 验证返回 token 不为空
        // 3. 验证 login_fail_count 被重置为0
    }
}
