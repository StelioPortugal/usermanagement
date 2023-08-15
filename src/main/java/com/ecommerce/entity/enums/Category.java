package com.ecommerce.entity.enums;

import lombok.Getter;

@Getter
public enum Category {
    BASKETBALL("BASKETBALL SHOES"), RUNNING("RUNNING SHOES"), HIKING("HIKING SHOES"),
    CLIMBING("CLIMBING SHOES"), SOCCER("SOCCER SHOES");

    public final String category;

    Category(String category){
        this.category=category;
    }

}
