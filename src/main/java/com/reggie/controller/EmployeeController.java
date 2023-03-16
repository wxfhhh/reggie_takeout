package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.Result;
import com.reggie.common.ThreadLocalUtil;
import com.reggie.domain.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 用户登录功能
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("login")
    public Result login(HttpServletRequest request, @RequestBody Employee employee){
        //要进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据页面提交的userName查询数据库
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);//emp为数据库查询到的对象
        //没有查询到则登陆失败
        if(emp == null)
            return Result.Error("登陆失败,用户名不存在");
        //查询到 但是密码不一致 登陆失败
        if(!emp.getPassword().equals(password))
            return Result.Error("登录失败,密码错误");
        //查询员工状态是否正常 1为正常 0为已锁定
        if(emp.getStatus() == 0)
            return Result.Error("员工已锁定");
        //登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employeeId",emp.getId());
        return Result.succeed(emp);
    }

    /**
     * 用户退出功能
     * @param request
     * @return
     */
    @PostMapping("logout")
    public Result logout(HttpServletRequest request){
        request.getSession().removeAttribute("employeeId");
        return Result.succeed("退出成功");
    }

    /**
     * 添加员工信息 注册
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public Result addEmp(HttpServletRequest request,@RequestBody Employee employee){
        // log.info("添加一条员工信息");
        //将密码进行MD5加密 身份证后六位
        String password = DigestUtils.md5DigestAsHex(employee.getIdNumber().substring(12).getBytes());
        employee.setPassword(password);
        //添加信息
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());
        //
        // employee.setCreateUser((Long) request.getSession().getAttribute("employeeId"));
        // employee.setUpdateUser((Long) request.getSession().getAttribute("employeeId"));
        //mybatis plus 的简单sql 保存语句
        employeeService.save(employee);
        return Result.succeed("添加成功");
    }

    /**
     * 分页查询员工信息  泛型 Result<Page> 为返回值
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("page")
    public Result page(int page,int pageSize,String name){
        //打印日志
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page p = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件 模糊查询 name是否为空 不为空查询name关键字
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询 分页查询
        employeeService.page(p,queryWrapper);
        return Result.succeed(p);
    }

    /**
     * 更新数据
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public Result update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        // Long employeeId = (Long)request.getSession().getAttribute("employeeId");
        // employee.setUpdateTime(LocalDateTime.now());
        // employee.setUpdateUser(employeeId);
        log.info("线程id:"+Thread.currentThread().getId());//判断登录后是不是同一个线程id
        employeeService.updateById(employee);
        return  Result.succeed("更改数据成功!");
    }

    /**
     * 查找员工信息 实现回显
     * 利用url来获取id参数
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id){
        log.info("根据id查询");
        Employee employee = employeeService.getById(id);
        if(employee != null)
        return Result.succeed(employee);
        else
            return Result.Error("查询失败") ;
    }
}
