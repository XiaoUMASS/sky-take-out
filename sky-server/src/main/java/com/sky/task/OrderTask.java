package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时未支付的订单
     */
    @Scheduled(cron = "0 * * * * ?")//每分钟触发一次
    public void processTimeOutOrder() {
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        //用户下单后没有支付，超过15分钟自动取消订单
        //建议每分钟触发一次
        //查询看有没有超时的订单
        //select * from orders where status = ? and order_time < ?(当前时间减去15分钟)
        List<Orders> timeOutOrdersList = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-15));
        if (timeOutOrdersList != null && timeOutOrdersList.size() > 0) {
            for (Orders timeOutOrders : timeOutOrdersList) {
                timeOutOrders.setStatus(Orders.CANCELLED);
                timeOutOrders.setCancelReason("订单超时自动取消");
                timeOutOrders.setCancelTime(LocalDateTime.now());
                orderMapper.update(timeOutOrders);
            }
        }
    }

    /**
     * 处理一直未被响应的派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨1点触发一次
//    @Scheduled(cron = "0/10 * * * * *")
    public void processUnresponsiveOrder() {
        //对于一直处于派送中的订单，每日凌晨触发一次，将派送中的订单修改为已完成
        log.info("定时处理未被响应的派送中的订单：{}", LocalDateTime.now());
        List<Orders> unresponsiveOrdersList = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().minusHours(1));
        for (Orders unresponsiveOrders : unresponsiveOrdersList) {
            unresponsiveOrders.setStatus(Orders.COMPLETED);
            orderMapper.update(unresponsiveOrders);
        }
    }
}
