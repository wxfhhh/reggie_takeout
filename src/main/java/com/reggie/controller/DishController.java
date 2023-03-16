package com.reggie.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.Result;
import com.reggie.domain.Category;
import com.reggie.domain.Dish;
import com.reggie.dto.DishDto;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result add(@RequestBody DishDto dishdto){
        log.info("增加菜品");
        dishService.saveWithFlavor(dishdto);
        return Result.succeed("增加菜品成功!");
    }
    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("page")
    public Result page(int page,int pageSize,String name){
        //打印日志
        log.info("page={},pageSize={},name={}",page,pageSize);
        Page<Dish> p=new Page(page,pageSize);

        Page<DishDto> dishDtoPage=new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getSort);
        dishService.page(p,queryWrapper);

        //对象拷贝  忽略recodes 我们要对recodes进行修改
        BeanUtils.copyProperties(p,dishDtoPage,"records");
        List<Dish> records = p.getRecords();
        //对records进行修改 奖修改后的数据传入dishDtoPage的records中
        List<DishDto> dishDtos=new LinkedList<>();
        for (Dish dish:records) {
            DishDto dto=new DishDto();
            BeanUtils.copyProperties(dish,dto);
            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String name1 = category.getName();
                dto.setCategoryName(name1);
            }
            dishDtos.add(dto);
        }
        dishDtoPage.setRecords(dishDtos);
        return Result.succeed(dishDtoPage);
    }

    /**
     * 进行修改页面的回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id){
        DishDto dishDto = dishService.getWithFlavor(id);
        if(dishDto!=null)
        return Result.succeed(dishDto);
        else
            return Result.Error("回显失败!");
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result update(@RequestBody DishDto dishDto){
        dishService.updateById(dishDto);
        dishFlavorService.updateBatchById(dishDto.getFlavors());
        return Result.succeed("修改成功!");
    }

    /**
     * 删除菜品 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result delete(@RequestParam LinkedList<Long> ids){
        log.info("根据{}删除菜品",ids);
        dishService.deleteWithFlavor(ids);
        return Result.succeed("删除成功!");
    }

    /**
     * 批量更改销售状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("status/{status}")
    public Result update_s(@PathVariable int status,@RequestParam List<Long> ids){
        dishService.upData_s(status,ids);
        return Result.succeed("更改成功");
    }
//    @GetMapping("list")
//    public Result category(Long categoryId){
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(categoryId!=null,Dish::getCategoryId,categoryId);
//        queryWrapper.orderByDesc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//        if(list!=null)
//        return Result.succeed(list);
//        else return Result.Error("该分类没有菜品");
//    }
    @GetMapping("list")
    public Result category(Dish dish){
        //根据菜品分类来查询所含菜品 要为出售状态
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.orderByDesc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        //Dto的封装
        List<DishDto> dishDtos = list.stream().map((item)->{
            //菜品名字的封装
            DishDto dishDto = dishService.getWithFlavor(item.getId());
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if(categoryId!=null){
                String name = byId.getName();
                dishDto.setCategoryName(name);
            }
            return dishDto;
        }).collect(Collectors.toList());
        //将Dto数据返回给前端
        return Result.succeed(dishDtos);
    }

}
