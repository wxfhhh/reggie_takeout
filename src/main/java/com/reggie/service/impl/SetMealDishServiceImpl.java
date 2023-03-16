package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.domain.SetmealDish;
import com.reggie.mapper.SetMealDishMapper;
import com.reggie.service.SetMealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper, SetmealDish> implements SetMealDishService {
}
