package com.example.usercenter.Service;

import com.example.usercenter.Model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("kyle");
        user.setAccount("123");
        user.setAvatarUrl("https://vcg01.cfp.cn/creative/vcg/800/new/VCG211306120708.jpg");
        user.setGender(0);
        user.setPassword("123456");
        user.setPhone("123");
        user.setEmail("456");
        user.setStatus(0);
        user.setIsDelete(0);

        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);
    }

    @Test
    void userRegister() {
        String account = "kyle666";
        String password = "";
        String checkPassword = "123456";
        String planetCode = "1";
        long result = userService.userRegister(account, password, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        account = "kyle_dolph";
        result = userService.userRegister(account, password, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        account = "kyle";
        password = "123456";
        result = userService.userRegister(account, password, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        account = "kyle dolph";
        password = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(account, password, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        checkPassword = "123456789";
        result = userService.userRegister(account, password, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        account = "kyle";
        checkPassword = "12345678";
        result = userService.userRegister(account, password, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        account = "kyle123";
        password = "password123";
        checkPassword = "password123";
        result = userService.userRegister(account, password, checkPassword,planetCode);
        Assertions.assertTrue(result>0);

    }
}