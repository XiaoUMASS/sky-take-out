package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    /**
     * 新增菜品和对应的口味数据
     *
     * @param dishDTO
     */
    @Transactional//事务注解
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        //向菜品表插入一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        //获取insert语句执行后生成的id
        //<insert id="insert" useGeneratedKeys="true" keyProperty="id">
        Long dishId = dish.getId();

        //向口味表插入0、1或者多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否存在其手中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //是起售状态
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断菜品是否被套餐关联
        List<Long> setMealIds = setMealDishMapper.getSetMealIdsByDishIds(ids);
        if (setMealIds != null && setMealIds.size() > 0) {
            //当前菜品被套餐关联了，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中的菜品数据
        dishMapper.deleteByIds(ids);
        //删除菜品关联的口味数据
        dishFlavorMapper.deleteRelatedFlavors(ids);
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        Dish dish = dishMapper.getById(id);
        log.info("相关图片......");
        log.info(dish.getImage());
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        //根据categoryId找categoryName
        Category category = categoryMapper.getById(dishVO.getCategoryId());
        dishVO.setCategoryName(category.getName());
        //根据id查找对应的flavors
        List<DishFlavor> listDishFlavor = dishFlavorMapper.getById(id);
        dishVO.setFlavors(listDishFlavor);
        return dishVO;
    }

    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        //对dish表中的信息进行更新
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //对flavor表中的信息进行更新
        dishFlavorMapper.deleteRelatedFlavors(Arrays.asList(dishDTO.getId()));
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
//        dishFlavorMapper.update(flavors);
    }

    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        return dishMapper.getByCategoryId(categoryId);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        //如果要停售，需要将当前菜品关联的套餐也停售
        if (status == StatusConstant.DISABLE) {
            List<Long> mealIds = setMealDishMapper.getSetMealIdsByDishIds(Arrays.asList(id));
            if(mealIds != null && mealIds.size()>0){
    //            mealIds.forEach(mealId ->{setMealMapper.changeStatus();});
                List<Setmeal> setmeals = mealIds.stream().map(mealId -> setMealMapper.getById(mealId)).collect(Collectors.toList());
                setmeals.forEach(setmeal -> setmeal.setStatus(StatusConstant.DISABLE));
                setmeals.forEach(setmeal -> setMealMapper.changeStatus(setmeal));
            }
        }
        //停售或启售该菜品
        Dish dish = Dish.builder().status(status).id(id).build();
        dishMapper.update(dish);
    }
}
