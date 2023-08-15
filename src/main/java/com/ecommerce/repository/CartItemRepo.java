package com.ecommerce.repository;

import com.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CartItemRepo extends JpaRepository<CartItem,Integer> {

    @Modifying
    @Query("DELETE FROM CartItem WHERE product.id= :id")
    void deleteByProductId(@Param("id") Integer id);
}
