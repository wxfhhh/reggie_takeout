package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.Result;
import com.reggie.domain.Category;
import com.reggie.domain.Employee;
import com.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品1 套装2 分类
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Category category){
        log.info("新增分类");
        categoryService.save(category);
        return Result.succeed(category);
    }

    /**
     * 分类分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public Result page(int page,int pageSize){
        Page<Category> p=new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //进行升序排序
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(p,queryWrapper);
        return Result.succeed(p);
    }

    /**
     * 分类删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result delete(Long ids){
        log.info("根据{}删除",ids);
        categoryService.removeById(ids);
        return Result.succeed("删除成功!");
    }

    /**
     * 分类修改
     * @return
     */
    @PutMapping
    public  Result update(@RequestBody Category category){
        categoryService.updateById(category);
        return Result.succeed(category);
    }

    /**
     *列出所有分类
     * @param category
     * @return
     */
    @GetMapping("list")
    public Result list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return Result.succeed(list);
    }
}
