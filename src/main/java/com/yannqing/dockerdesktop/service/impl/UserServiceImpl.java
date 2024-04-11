package com.yannqing.dockerdesktop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yannqing.dockerdesktop.domain.User;
import com.yannqing.dockerdesktop.mapper.RoleMapper;
import com.yannqing.dockerdesktop.service.UserService;
import com.yannqing.dockerdesktop.mapper.UserMapper;
import com.yannqing.dockerdesktop.utils.JwtUtils;
import com.yannqing.dockerdesktop.vo.UserInfoVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Autowired
    private ObjectMapper objectMapper;

    @Resource
    private RoleMapper roleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserMapper userMapper;

    @Override
    public UserInfoVo getUserInfo(String token) throws JsonProcessingException {
        //从token中获取到用户的信息，以及对应用户的权限信息
        User user = getUserByToken(token);
        List<String> userAuthorization = JwtUtils.getUserAuthorizationFromToken(token);
        int role = roleMapper.selectRoleByUserId(user.getUser_id());
        return new UserInfoVo(user, role, userAuthorization);
    }

    @Override
    public boolean changePassword(String oldPassword, String newPassword, String againPassword, String token) throws JsonProcessingException {
        //确保两次输入密码相同
        if (!Objects.equals(newPassword, againPassword)) {
            throw new IllegalArgumentException("两次输入密码不同，请重试！");
        }
        String oldEncryptPassword = passwordEncoder.encode(oldPassword);
        String newEncryptPassword = passwordEncoder.encode(newPassword);
        //判断原密码是否相同
        User loginUser = getUserByToken(token);
        User userStatus = userMapper.selectById(loginUser.getUser_id());
        //TODO: 这里，无法对比两个加密后的密码
        if (!oldEncryptPassword.equals(userStatus.getPassword())) {
            throw new IllegalArgumentException("原密码错误，请重试！");
        }
        //修改密码
        int result = userMapper.update(new UpdateWrapper<User>()
                .eq("id", loginUser.getUser_id())
                .set("userPassword", newEncryptPassword));


        log.info("修改密码成功，请重新登录！");
        return result == 1;
    }

    @Override
    public boolean createUser(User user) {
        int result = userMapper.insert(user);
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
    public void updateUser(User user) {
        User updateUser = new User();
        updateUser.setUser_id(user.getUser_id());
        updateUser.setUsername(user.getUsername());
        updateUser.setPhone(user.getPhone());
        updateUser.setEmail(user.getEmail());
        updateUser.setDisk_size(user.getDisk_size());
        updateUser.setInternet(user.getInternet());
        updateUser.setNick_name(user.getNick_name());

        int result = userMapper.updateById(updateUser);
    }

    public User getUserByToken(String token) throws JsonProcessingException {
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(userInfo, User.class);
    }
}




