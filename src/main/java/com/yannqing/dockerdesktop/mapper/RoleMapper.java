package com.yannqing.dockerdesktop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yannqing.dockerdesktop.domain.Role;

/**
* @author 67121
* @description 针对表【yan_role】的数据库操作Mapper
* @createDate 2024-03-08 18:37:29
* @Entity generator.domain.Role
*/
public interface RoleMapper extends BaseMapper<Role> {
    int selectRoleByUserId (Integer userId);
}




