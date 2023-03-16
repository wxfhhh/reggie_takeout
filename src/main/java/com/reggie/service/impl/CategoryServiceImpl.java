package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.domain.Category;
import com.reggie.domain.Dish;
import com.reggie.domain.Setmeal;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setMealService;
    @Override
    public void removeById(Long id) {
        //查看当前分类是否关联了菜品,关联即自定义异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if(count1>0)
            //抛出 自定义异常
            throw new CustomException("当前分类关联了菜品,删除失败!");
        //查看当前分类是否关联了套装,关联即自定义异常
        LambdaQueryWrapper<Setmeal> setMealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setMealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setMealService.count(setMealLambdaQueryWrapper);
        if(count2>0)
            //抛出 自定义异常
            throw new CustomException("当前分类关联了套装,删除失败!");
        //没有关联 删除成功
        super.removeById(id);

    }
}
