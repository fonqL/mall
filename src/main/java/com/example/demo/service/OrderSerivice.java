package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Order;
import com.example.demo.mapper.OrderMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderSerivice extends ServiceImpl<OrderMapper, Order> {
}
