package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.domain.Dish;
import com.reggie.domain.Setmeal;
import com.reggie.domain.SetmealDish;
import com.reggie.dto.SetMealDto;
import com.reggie.mapper.SetMealMapper;
import com.reggie.service.SetMealDishService;
import com.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService{
    @Autowired
    private SetMealDishService setMealDishService;

    /**
     * 新增菜品
     * @param setMealDto
     */
    @Override
    public void saveWithDish(SetMealDto setMealDto) {
        this.save(setMealDto);//为啥来着
        Long mealId = setMealDto.getId();
        List<SetmealDish> dishes = setMealDto.getDish();
        for (SetmealDish dish:
             dishes) {
            //给每一个菜品设置套餐id
            dish.setSetmealId(mealId);
        }
        setMealDishService.saveBatch(dishes);
    }

    /**
     * 删除套餐 批量删除
     * @param ids
     */
    @Override
    public void deleteWithDish(LinkedList<Long> ids) {
        //判断该套餐是否可删除 在售不可删
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if (count>0)
            throw new CustomException("在售不可删除!");
        //根据id删除套餐
        this.removeByIds(ids);
        //删除关联表
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setMealDishService.remove(queryWrapper1);
    }
    /**
     * 批量改变销售方式
     * @param status
     * @param ids
     */
    @Override
    public void upData_s(int status, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        List<Setmeal> setmeals = this.list(queryWrapper);
        queryWrapper.eq(Setmeal::getStatus,status);
        int count = this.count(queryWrapper);
        //判断选择的菜品是否都为同一个销售状态
        if(count>0)
            throw new CustomException("操作失败,存在不同的销售状态");
        for (Setmeal setmeal: setmeals) {
            if(status==0)
                setmeal.setStatus(0);
            else setmeal.setStatus(1);
        }
        this.updateBatchById(setmeals);
    }
}
