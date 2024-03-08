package com.yannqing.dockerdesktop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yannqing.dockerdesktop.domain.Role;
import com.yannqing.dockerdesktop.mapper.RoleMapper;
import com.yannqing.dockerdesktop.service.RoleService;
import org.springframework.stereotype.Service;

/**
* @author 67121
* @description 针对表【yan_role】的数据库操作Service实现
* @createDate 2024-03-08 18:37:29
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService {

}




