package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.Result;
import com.reggie.common.ThreadLocalUtil;
import com.reggie.domain.ShoppingCart;
import com.reggie.service.ShoppingCartService;
import com.sun.prism.impl.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    /**
     * 加入购物车
     * @return
     */
    @PostMapping("add")
    public Result add(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        log.info("将此菜品/套餐加入购物车");
        //设置用户id,指定是哪个用户的id 可以用session,ThreadLocalUtil.getCurrentId() 自定义的线程类
        Long userId =(long)session.getAttribute("user");
        shoppingCart.setUserId(userId);
        Long dishId = shoppingCart.getDishId();
        //判断当前菜品/套餐是否在购物车内 通过userid和菜品套餐id来唯一确认
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        //判断是菜品还是套餐
        if(dishId!=null){
            //说明是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            if(shoppingCart.getDishFlavor()!=null)
            queryWrapper.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        } else queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one==null){
            //如果不存在,添加到购物车,数量默认为1
            one=shoppingCart;
            one.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(one);

        }else{
            //如果存在,在之前数量的基础上加1
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);
        }
        return Result.succeed(one);
    }

    /**
     * 列出购物车清单
     * @return
     */
    @GetMapping("list")
    public Result list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, ThreadLocalUtil.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return Result.succeed(list);
    }

    /**
     * 清除购物车
     * @return
     */
    @DeleteMapping("clean")
    public Result clean(){
        Long userId = ThreadLocalUtil.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> q = new LambdaQueryWrapper<>();
        q.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(q);
        return Result.succeed("清空成功");
    }

    /**
     * 减购
     * @return
     */
    @PostMapping("sub")
    public Result sub(@RequestBody ShoppingCart shoppingCart){
        Long userId = ThreadLocalUtil.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        Long dishId = shoppingCart.getDishId();
        if(dishId!=null){
            //为菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
//            queryWrapper.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        } else queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        Integer number = one.getNumber();
        if(number==1)
            shoppingCartService.removeById(one);
        else{
            one.setNumber(number-1);
            shoppingCartService.updateById(one);
        }
        return Result.succeed(one);
    }
}
