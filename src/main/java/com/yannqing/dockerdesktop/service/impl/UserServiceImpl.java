package com.yannqing.dockerdesktop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yannqing.dockerdesktop.domain.RoleUser;
import com.yannqing.dockerdesktop.domain.User;
import com.yannqing.dockerdesktop.mapper.RoleMapper;
import com.yannqing.dockerdesktop.mapper.RoleUserMapper;
import com.yannqing.dockerdesktop.service.UserService;
import com.yannqing.dockerdesktop.mapper.UserMapper;
import com.yannqing.dockerdesktop.utils.JwtUtils;
import com.yannqing.dockerdesktop.utils.RedisCache;
import com.yannqing.dockerdesktop.vo.UserInfoVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
* @author 67121
* @description 针对表【yan_user】的数据库操作Service实现
* @createDate 2024-03-07 17:19:45
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private RoleUserMapper roleUserMapper;

    @Override
    public UserInfoVo getUserInfo(String token) throws JsonProcessingException {
        //从token中获取到用户的信息，以及对应用户的权限信息
        User user = getUserByToken(token);
        List<String> userAuthorization = JwtUtils.getUserAuthorizationFromToken(token);
        int role = roleMapper.selectRoleByUserId(user.getUser_id());
        return new UserInfoVo(user, role, userAuthorization);
    }

    @Override
    public boolean changePassword(String newPassword, String againPassword, String token) throws JsonProcessingException {
        //从token获取到登录用户的信息
        User loginUser = getUserByToken(token);
        //确保两次输入密码相同
        if (!Objects.equals(newPassword, againPassword)) {
            throw new IllegalArgumentException("两次输入密码不同，请重试！");
        }
        String newEncryptPassword = passwordEncoder.encode(newPassword);
        //修改密码
        int result = userMapper.update(new UpdateWrapper<User>()
                .eq("user_id", loginUser.getUser_id())
                .set("password", newEncryptPassword));
        //删除token
        redisCache.deleteObject("token:"+token);

        log.info("修改密码成功，请重新登录！");
        return result == 1;
    }

    @Override
    public boolean resetPassword(Integer userId, String token) throws JsonProcessingException {
        User user = userMapper.selectById(userId);
        int result = userMapper.update(new UpdateWrapper<User>()
                .eq("user_id", userId)
                .set("password", passwordEncoder.encode("123456")));

        //删除token
        redisCache.deleteObject("token:"+token);
        log.info("重置用户{}的密码为123456", user.getUsername());
        return result == 1;
    }

    @Override
    public boolean createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccount_no_expired(1);
        user.setAccount_no_locked(1);
        user.setEnabled(1);
        user.setCredentials_no_expired(1);
        user.setCreate_time(new Date());
        int result = userMapper.insert(user);
        RoleUser roleUser = new RoleUser();
        roleUser.setUid(user.getUser_id());
        roleUser.setRid(0);
        roleUserMapper.insert(roleUser);
        return result == 1;
    }

    @Override
    public boolean deleteUser(Integer userId) {
        int result = userMapper.deleteById(userId);
        return result == 1;
    }

    @Override
    public List<User> getAllUsers(String username) {
        return userMapper.selectList(new QueryWrapper<User>().like("username", username));
    }

    @Override
    public boolean updateUser(User user) {
        int result = userMapper.update(new UpdateWrapper<User>()
                .eq("user_id", user.getUser_id())
                .set("username", user.getUsername())
                .set("phone", user.getPhone())
                .set("email", user.getEmail())
                .set("disk_size", user.getDisk_size())
                .set("internet", user.getInternet())
                .set("nick_name", user.getNick_name())
        );
        return result == 1;
    }

    public User getUserByToken(String token) throws JsonProcessingException {
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(userInfo, User.class);
    }
}




