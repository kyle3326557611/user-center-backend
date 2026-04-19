package com.example.usercenter.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.usercenter.Model.User;
import com.example.usercenter.Model.request.UserLoginRequest;
import com.example.usercenter.Model.request.UserRegisterRequest;
import com.example.usercenter.Service.UserService;
import com.example.usercenter.common.BaseResponse;
import com.example.usercenter.common.ErrorCode;
import com.example.usercenter.common.ResultUtils;
import com.example.usercenter.contant.userConstant;
import com.example.usercenter.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口（封装给前端）
 *
 * @author kyle
 */

@Tag(name="用户模块",description = "包括注册、登录、查询等接口")
@RestController  //适用于编写restful风格的api，返回值默认为json类型
@RequestMapping("/user")
public class UserController implements userConstant {
    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;  //使用Redis

    @Resource
    private ObjectMapper objectMapper;  //User与JSON字符串互相转换

    @Operation(summary = "用户注册",description = "新用户需要输入账号（不少于4位）、密码（不少于8位）、校验码、以及编号（不能重复）即可注册")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest registerRequest) {  //获取客户端的请求
        if (registerRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String checkPassword = registerRequest.getCheckPassword();
        String planetCode = registerRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result= userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        return ResultUtils.success(result);  //用来返回给服务端的错误码
    }



    @Operation(summary = "用户登录",description = "登录成功后返回脱敏信息，并在session中保留登录状态")
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        if (loginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = loginRequest.getUserAccount();
        String userPassword = loginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user= userService.userLogin(userAccount, userPassword, request);
        try{
            String token=java.util.UUID.randomUUID().toString();
            String userJson=objectMapper.writeValueAsString(user);
            stringRedisTemplate.opsForValue().set("user:login:" + token, userJson, 30, java.util.concurrent.TimeUnit.MINUTES);

            //把Token送回前端
            jakarta.servlet.http.Cookie cookie=new jakarta.servlet.http.Cookie("token",token);
            cookie.setPath("/");
            response.addCookie(cookie);

        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Redis 存储登录状态失败");
        }
        return ResultUtils.success(user);
    }


    @Operation(summary = "注销登录")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request, HttpServletResponse response) {
        String token = getToken(request);
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未提供登录凭证");
        }
        boolean result = userService.userLogout(token);
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResultUtils.success(result);
    }




    @Operation(summary = "获取当前用户的登陆态")
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        String token=getToken(request);
        if(StringUtils.isBlank(token)){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未取得登录凭证");
        }
        try{
            String userJson= stringRedisTemplate.opsForValue().get("user:login:"+token);
            if(StringUtils.isBlank(userJson)){
                throw new BusinessException(ErrorCode.NOT_LOGIN,"登录已过期，请重新登录");
            }
            stringRedisTemplate.expire("user:login:" + token, 30, java.util.concurrent.TimeUnit.MINUTES);
            User user = objectMapper.readValue(userJson, User.class);
            return ResultUtils.success(user);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解析登录状态失败");
        }
    }



    @Operation(summary = "搜索用户",description = "仅管理员可调用，同时匹配用户名模糊搜索")
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list= userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }



    @Operation(summary = "用户删除",description = "同样仅管理员可调用，删除指定id的用户")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean b =userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 是否为管理员
     *
     * @author kyle
     */
    private boolean isAdmin(HttpServletRequest request) {

        String token = getToken(request);
        if (StringUtils.isBlank(token)) {
            return false;
        }
        try {
            String userJson = stringRedisTemplate.opsForValue().get("user:login:" + token);
            if (StringUtils.isBlank(userJson)) {
                return false;
            }
            User user = objectMapper.readValue(userJson, User.class);
            return user != null && user.getRole() == ADMIN_ROLE;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从Cookie取出Token
     *
     * @author kyle
     */
    private String getToken(HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
