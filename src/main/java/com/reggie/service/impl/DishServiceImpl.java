package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.domain.Dish;
import com.reggie.domain.DishFlavor;
import com.reggie.dto.DishDto;
import com.reggie.mapper.DishMapper;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor:flavors) {
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品
     * @param ids
     */
    @Override
    public void deleteWithFlavor(List<Long> ids) {
        //判断该套餐是否可删除 在售不可删
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<Dish>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        int count = this.count(queryWrapper);
        if(count>0)
            throw new CustomException("存在套餐正在销售中,不可删除!");
        else
            //删除dish表的套餐
            this.removeByIds(ids);
        //删除关联表的套餐
//        dishFlavorService.removeByIds(ids) 不能用,ids不是dishflavor的主键不能直接用
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper1);
    }

    /**
     * 批量改变销售方式
     * @param status
     * @param ids
     */
    @Override
    public void upData_s(int status, List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        List<Dish> dishes = this.list(queryWrapper);
        queryWrapper.eq(Dish::getStatus,status);
        int count = this.count(queryWrapper);
        //判断选择的菜品是否都为同一个销售状态
        if(count>0)
            throw new CustomException("操作失败,存在不同的销售状态");
        for (Dish dish: dishes) {
            if(status==0)
                dish.setStatus(0);
            else dish.setStatus(1);
        }
        this.updateBatchById(dishes);

    }

    /**
     * 通过dishId返回DishDto对象
     * @param id
     * @return
     */
    @Override
    public DishDto getWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto=new DishDto();
        //将dish的数据拷贝到DishDto中
        BeanUtils.copyProperties(dish,dishDto);
        //通过id查询DishFlavor list数组
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);
        return dishDto;
    }
}
