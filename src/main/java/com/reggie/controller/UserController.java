package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.Result;
import com.reggie.domain.User;
import com.reggie.service.UserService;
import com.reggie.utils.SMSUtils;
import com.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 根据手机号生成验证码,发送给手机
     * @return
     */
    @PostMapping("sendMsg")
    public  Result setMsg(@RequestBody User user, HttpSession session){
        String phone = user.getPhone();
        if(phone!=null){
            //生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            //发送验证码到手机
//            SMSUtils.sendMessage("reggie","您的验证码为：${code}，请勿泄露于他人！",phone,code);
            //将验证码存入session中
            session.setAttribute("phone",code);
            return Result.succeed("短信发送成功!");
        }
        return Result.Error("短信发送失败!");
    }
    @PostMapping("login")
    public Result login(@RequestBody Map users, HttpSession session){
        //请求中为发回的数据为phone ,code.phone中不包含code,可以构造dto或者直接用map接收
        //获取 phone 和 code
        String phone = users.get("phone").toString();
        String code = users.get("code").toString();
        log.info("phone={},code={}",phone,code);
        //code和session中保留的code进行比较
        Object code1 = session.getAttribute("phone");
        if(code!=null && code1.equals(code)){
            //比对成功 ,登录成功
            //判断此手机号是否已经注册
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(lambdaQueryWrapper);
//            int count = userService.count();
            if(user==null){
                //没有进行注册过
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return Result.succeed(user);
        }
        return Result.Error("登录失败!");
    }
}
