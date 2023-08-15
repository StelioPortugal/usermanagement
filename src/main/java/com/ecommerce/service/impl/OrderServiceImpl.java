package com.ecommerce.service.impl;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartRepo;
import com.ecommerce.repository.OrderRepo;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepo cartRepo;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepo orderRepo;

    @Override
    public void createOrder(Integer cartId, String username) {

        Cart cart = this.cartRepo.findById(cartId).orElseThrow();

        Order order = new Order();
        Set<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> new OrderItem(cartItem.getProduct(), cartItem.getQuantity(), order)).collect(Collectors.toSet());

        order.setItems(orderItems);
        order.setOrderCreated(new Date());

        final double[] totalPrice = {0};
        cart.getItems().forEach(cartItem -> {
            totalPrice[0] = totalPrice[0] + cartItem.getTotalPrice();
        });

        order.setTotalAmount(totalPrice[0]);
        User user = this.userRepository.findByUsername(username).orElseThrow();
        order.setUser(user);
        order.setDeliveryStatus(false);

        this.orderRepo.save(order);
    }

    @Override
    public List<Order> getOrders() {
        List<Order> all = this.orderRepo.findAll();
        return all;
    }

    @Override
    public Optional<Order> getOrder(Integer id) {
        return orderRepo.findById(id);
    }

    @Override
    public Order markStatus(Integer id) {
        Order order = orderRepo.findById(id).get();
        if(order.getDeliveryStatus())
            order.setDeliveryStatus(false);
        else
            order.setDeliveryStatus(true);
        return orderRepo.save(order);
    }
}
