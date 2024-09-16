package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "商家订单管理接口")
@Slf4j
@RequestMapping("/admin/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 订单分页查询
     * @return
     */
    @ApiOperation("订单分页查询")
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> getStatistics(){
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.getStatistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @ApiOperation("接单")
    @PutMapping("/confirm")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("接单:{}", ordersConfirmDTO);
        orderService.confirmOrder(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 接单
     * @param ordersRejectionDTO
     * @return
     */
    @ApiOperation("拒单")
    @PutMapping("/rejection")
    public Result rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒单:{}", ordersRejectionDTO);
        orderService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    @ApiOperation("拒单")
    @PutMapping("/cancel")
    public Result rejectOrder(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单:{}", ordersCancelDTO);
        orderService.cancelOrderAdmin(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @ApiOperation("派送订单")
    @PutMapping("/delivery/{id}")
    public Result deliver(@PathVariable Long id){
        log.info("派送订单:{}", id);
        orderService.deliver(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @ApiOperation("完成订单")
    @PutMapping("/complete/{id}")
    public Result completeOrder(@PathVariable Long id){
        log.info("完成订单:{}", id);
        orderService.completeOrder(id);
        return Result.success();
    }

    /**
     * 查询订单详情
     * @return
     */
    @ApiOperation("查询订单详情")
    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrderDetails(@PathVariable Long id){
        log.info("查询订单详情:{}",id);
        OrderVO orderVO = orderService.getByOrderId(id);
        return Result.success(orderVO);
    }

}
