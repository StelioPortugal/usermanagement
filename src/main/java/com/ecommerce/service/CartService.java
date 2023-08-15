package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
public interface CartService {

    Cart addItemToCart(Product product, String username);
}
