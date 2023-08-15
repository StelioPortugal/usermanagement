package com.ecommerce.service.impl;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartRepo;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepo cartRepo;

    @Override
    public Cart addItemToCart(Product product, String username) {

        User user = this.userRepository.findByUsername(username).orElseThrow();
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
        }
        cartItem.setCart(cart);
        cart.setUser(user);
        Set<CartItem> items = cart.getItems();
        AtomicReference<Boolean> updatedQuantity = new AtomicReference<>(false);
        Set<CartItem> newCartItems = items.stream().map((cartItem1 -> {
            if (cartItem1.getProduct().getId() == product.getId()) {
                cartItem1.setQuantity(cartItem1.getQuantity()+1);
                updatedQuantity.set(true);
            }
            return cartItem1;
        })).collect(Collectors.toSet());
        if (updatedQuantity.get()) {
            cart.setItems(newCartItems);
        } else {
            items.add(cartItem);
        }
        Cart updatedCart = this.cartRepo.save(cart);
        return updatedCart;
    }
}
