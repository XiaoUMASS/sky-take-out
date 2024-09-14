package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //当添加的商品是否已经存在于购物车中
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //如果已经存在，只需要将数量加一
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        }
        //如果不存在，需要向数据库中插入数据
        else{
            if(shoppingCartDTO.getDishId() != null){
                //本次添加的是菜品
                //查询菜品数据
                Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
                //插入购物车表
                shoppingCartMapper.insert(shoppingCart);
            }
            if(shoppingCartDTO.getSetmealId() != null){
                //本次添加的是套餐
                Setmeal setmeal = setMealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
                //插入购物车表
                shoppingCartMapper.insert(shoppingCart);
            }
        }
    }

    @Override
    public List<ShoppingCart> getShoppingCart() {
        //select * from shopping_cart where user_id = ?
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        return shoppingCartMapper.list(shoppingCart);
    }

    @Override
    public void cleanShoppingCart() {
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(currentId);
        shoppingCartMapper.delete(shoppingCart);
    }

    @Override
    public void subtractFromShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list != null && list.size()>0){
            ShoppingCart cart = list.get(0);
            if(cart.getNumber() == 1){
                //若该商品只有一个，则从数据库中删除
                shoppingCartMapper.delete(shoppingCart);
            }else{
                //若该商品有多个，则数量减一
                cart.setNumber(list.get(0).getNumber()-1);
                shoppingCartMapper.updateNumberById(cart);
            }
        }else{
            throw new ShoppingCartBusinessException(MessageConstant.UNKNOWN_ERROR);
        }


    }
}
