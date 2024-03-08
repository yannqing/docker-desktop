package com.yannqing.dockerdesktop.mapper;

import com.yannqing.dockerdesktop.domain.Permissions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 67121
* @description 针对表【yan_permissions】的数据库操作Mapper
* @createDate 2024-03-07 17:20:33
* @Entity generator.domain.Permissions
*/
public interface PermissionsMapper extends BaseMapper<Permissions> {

        List<String> queryPermissionsByUserId(@Param("userId") Integer userId);
}




