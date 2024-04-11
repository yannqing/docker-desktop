package com.yannqing.dockerdesktop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yannqing.dockerdesktop.common.Code;
import com.yannqing.dockerdesktop.domain.User;
import com.yannqing.dockerdesktop.service.UserService;
import com.yannqing.dockerdesktop.utils.ResultUtils;
import com.yannqing.dockerdesktop.vo.BaseResponse;
import com.yannqing.dockerdesktop.vo.UserInfoVo;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 修改密码
     * @param oldPassword
     * @param newPassword
     * @param againPassword
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping("/changePassword")
    public BaseResponse<Object> changePassword(@RequestParam("newPassword") String newPassword,
                                               @RequestParam("againPassword") String againPassword,
                                               HttpServletRequest request) throws JsonProcessingException {
        String token = request.getHeader("token");
        boolean result = userService.changePassword(newPassword, againPassword, token);
        if (result) {
            return ResultUtils.success(Code.SUCCESS, null, "修改密码成功");
        }
        return ResultUtils.failure(Code.FAILURE, null, "修改密码失败");
    }
    @PostMapping("/resetPassword")
    public BaseResponse<Object> resetPassword(HttpServletRequest request) throws JsonProcessingException {
        boolean result = userService.resetPassword(request.getHeader("token"));
        if (result) {
            return ResultUtils.success(Code.SUCCESS, null, "重设密码成功！");
        }else {
            return ResultUtils.failure(Code.FAILURE, null, "重设密码失败");
        }

    }
    @PostMapping("/add")
    public BaseResponse<Object> addUser(User user){
        boolean result = userService.createUser(user);
        if (result) {
            return ResultUtils.success(Code.SUCCESS, null, "新增用户成功");
        }else {
            return ResultUtils.failure(Code.FAILURE, null, "新增用户失败");
        }
    }

    @DeleteMapping("/delete")
    public BaseResponse<Object> deleteUser(Integer userId){
        boolean result = userService.deleteUser(userId);
        if (result) {
            return ResultUtils.success(Code.SUCCESS, null, "删除用户成功");
        }else {
            return ResultUtils.failure(Code.FAILURE, null, "删除用户成失败");
        }
    }

    @GetMapping("/getAllInfo")
    public BaseResponse<Object> getInfo(String username) {
        List<User> allUsers = userService.getAllUsers(username);
        return ResultUtils.success(Code.SUCCESS, allUsers, "查询用户成功");
    }

    @PostMapping("/update")
    public BaseResponse<Object> updateUser(User user){
        boolean result = userService.updateUser(user);
        return ResultUtils.success(Code.SUCCESS, result, "修改用户成功");
    }

}
