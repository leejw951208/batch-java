package com.example.batch.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.LinkedList;
import java.util.List;

public class OrderByUtils {

    public static OrderSpecifier[] dynamicOrderBy(Pageable pageable, Path<?> path) {
        List<OrderSpecifier> orders = new LinkedList<>();
        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
            orders.add(new OrderSpecifier(direction, Expressions.path(Object.class, path, order.getProperty())));
        }
        return orders.toArray(OrderSpecifier[]::new);
    }

    public static Order direction(Pageable pageable) {
        Order direction = null;
        for (Sort.Order order : pageable.getSort()) {
            if (order.isAscending()) {
                direction = Order.ASC;
            } else {
                direction = Order.DESC;
            }
            break;
        }
        return direction;
    }
}
