package com.yannqing.dockerdesktop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yannqing.dockerdesktop.common.Code;
import com.yannqing.dockerdesktop.domain.User;
import com.yannqing.dockerdesktop.service.UserService;
import com.yannqing.dockerdesktop.utils.ResultUtils;
import com.yannqing.dockerdesktop.vo.BaseResponse;
import com.yannqing.dockerdesktop.vo.UserInfoVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取个人信息，管理员，用户
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/getInfo")
    public BaseResponse<UserInfoVo> getUserInfo(HttpServletRequest request) throws JsonProcessingException {
        String token = request.getHeader("token");
        UserInfoVo userInfo = userService.getUserInfo(token);
        return ResultUtils.success(Code.SUCCESS, userInfo, "获取个人信息成功");
    }

    @PostMapping("/changePassword")
    public BaseResponse<Object> changePassword(@RequestParam("oldPassword") String oldPassword,
                                               @RequestParam("newPassword") String newPassword,
                                               @RequestParam("againPassword") String againPassword,
                                               HttpServletRequest request) throws JsonProcessingException {
        String token = request.getHeader("token");
        boolean result = userService.changePassword(oldPassword, newPassword, againPassword, token);
        if (result) {
            return ResultUtils.success(Code.SUCCESS, null, "修改密码成功");
        }
        return ResultUtils.failure(Code.FAILURE, null, "修改密码失败");
    }

}
