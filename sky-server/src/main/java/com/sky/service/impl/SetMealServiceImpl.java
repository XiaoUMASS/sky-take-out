package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealMapper setmealMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    @AutoFill(OperationType.INSERT)
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //将套餐信息录入，同时获得主键ID
        setmealMapper.insert(setmeal);
        //拿到上面已经插入的setmeal的主键id
        Long setmealId = setmeal.getId();
        //将套餐绑定的菜品信息录入
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        setMealDishMapper.insert(setmealDishes);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        //删除套餐的菜品组成
        setMealDishMapper.deleteByIds(ids);
        //删除套餐
        setmealMapper.deleteByIds(ids);
    }
}
