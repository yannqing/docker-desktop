package com.yannqing.dockerdesktop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yannqing.dockerdesktop.domain.User;
import com.yannqing.dockerdesktop.service.UserService;
import com.yannqing.dockerdesktop.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 67121
* @description 针对表【yan_user】的数据库操作Service实现
* @createDate 2024-03-07 17:03:39
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




