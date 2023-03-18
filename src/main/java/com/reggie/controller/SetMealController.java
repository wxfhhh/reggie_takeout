package com.reggie.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.domain.Category;
import com.reggie.domain.Dish;
import com.reggie.domain.Setmeal;
import com.reggie.domain.SetmealDish;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.Result;
import com.reggie.dto.DishDto;
import com.reggie.dto.SetMealDto;
import com.reggie.service.CategoryService;
import com.reggie.service.SetMealDishService;
import com.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private CategoryService categoryService;


    /**
     * 套装的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("page")
    public Result page(int page,int pageSize,String name){
        Page<Setmeal> p = new Page(page,pageSize);
        Page<SetMealDto> setMealDtoPage = new Page<>();
        //条件构造
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        setMealService.page(p,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(p,setMealDtoPage,"records");
        List<Setmeal> records = p.getRecords();
        LinkedList<SetMealDto> setMealDtos = new LinkedList<>();
        for(Setmeal setmeal:records){
            SetMealDto dto=new SetMealDto();
            BeanUtils.copyProperties(setmeal,dto);
            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String name1 = category.getName();
                dto.setCategoryName(name1);
            }
            setMealDtos.add(dto);
        }
        setMealDtoPage.setRecords(setMealDtos);
        return Result.succeed(setMealDtoPage);
    }
    /**
     * 添加套餐 在service层建立saveWithDish的方法
     * @param setMealDto
     * @return
     */
    @CacheEvict(value="setMeal",allEntries = true)
    @PostMapping()
    public Result add(@RequestBody SetMealDto setMealDto){
        log.info("增加套餐");
        setMealService.saveWithDish(setMealDto);
        return Result.succeed("套餐添加成功");
    }

    /**
     * 套餐的停起售
     * @param status
     * @param ids
     * @return
     */
    @CacheEvict(value="setMeal",allEntries = true)
    @PostMapping("status/{status}")
    public Result update_s(@PathVariable int status,@RequestParam List<Long> ids){
//        Setmeal setmeal=setMealService.getById(ids);
//        if(status==0)
//            setmeal.setStatus(0);
//        else setmeal.setStatus(1);
//        setMealService.updateById(setmeal);
        setMealService.upData_s(status,ids);
        return Result.succeed("修改成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @CacheEvict(value="setMeal",allEntries = true)
    //删除缓存
    @DeleteMapping
    public Result delete(@RequestParam LinkedList<Long> ids){
        setMealService.deleteWithDish(ids);
        return Result.succeed("删除成功!");
    }

    /**
     * 列出套餐
     * @param setMeal
     * @return
     */
    @Cacheable(value="setMeal",key="#setMeal.categoryId+'_'+#setMeal.status")
    @GetMapping("list")
    public Result list(Setmeal setMeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,setMeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,setMeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setMealService.list(queryWrapper);
        return Result.succeed(list);
    }
}
