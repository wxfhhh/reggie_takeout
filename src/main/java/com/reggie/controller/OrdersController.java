package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.Result;
import com.reggie.common.ThreadLocalUtil;
import com.reggie.domain.Orders;
import com.reggie.domain.ShoppingCart;
import com.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @PostMapping("submit")
    public Result submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return Result.succeed("下单成功!");
    }
}
