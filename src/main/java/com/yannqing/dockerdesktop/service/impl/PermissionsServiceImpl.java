package com.yannqing.dockerdesktop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yannqing.dockerdesktop.domain.Permissions;
import com.yannqing.dockerdesktop.service.PermissionsService;
import com.yannqing.dockerdesktop.mapper.PermissionsMapper;
import org.springframework.stereotype.Service;

/**
* @author 67121
* @description 针对表【yan_permissions】的数据库操作Service实现
* @createDate 2024-03-07 17:20:33
*/
@Service
public class PermissionsServiceImpl extends ServiceImpl<PermissionsMapper, Permissions>
    implements PermissionsService{

}




