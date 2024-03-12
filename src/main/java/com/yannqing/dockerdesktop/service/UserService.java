package com.yannqing.dockerdesktop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yannqing.dockerdesktop.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yannqing.dockerdesktop.vo.UserInfoVo;

/**
* @author 67121
* @description 针对表【yan_user】的数据库操作Service
* @createDate 2024-03-07 17:19:45
*/
public interface UserService extends IService<User> {
    UserInfoVo getUserInfo(String token) throws JsonProcessingException;

    boolean changePassword(String oldPassword, String newPassword, String againPassword, String token) throws JsonProcessingException;
}
