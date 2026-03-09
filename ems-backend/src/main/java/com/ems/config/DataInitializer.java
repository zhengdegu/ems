package com.ems.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.entity.User;
import com.ems.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 如果没有用户，创建默认管理员
        if (userMapper.selectCount(null) == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("管理员");
            admin.setPhone("13800001234");
            admin.setEmail("admin@ems.com");
            admin.setDepartment("设备部");
            admin.setRole("admin");
            admin.setEnabled(1);
            userMapper.insert(admin);

            User zhang = new User();
            zhang.setUsername("zhangsan");
            zhang.setPassword(passwordEncoder.encode("123456"));
            zhang.setName("张三");
            zhang.setPhone("13900005678");
            zhang.setEmail("zhang@ems.com");
            zhang.setDepartment("设备部");
            zhang.setRole("supervisor");
            zhang.setEnabled(1);
            userMapper.insert(zhang);

            User li = new User();
            li.setUsername("lisi");
            li.setPassword(passwordEncoder.encode("123456"));
            li.setName("李四");
            li.setPhone("13700009012");
            li.setEmail("li@ems.com");
            li.setDepartment("设备部");
            li.setRole("engineer");
            li.setEnabled(1);
            userMapper.insert(li);

            System.out.println(">>> 初始用户创建完成: admin/admin123, zhangsan/123456, lisi/123456");
        }
    }
}
