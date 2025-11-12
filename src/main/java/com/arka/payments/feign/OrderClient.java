package com.arka.payments.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ORDERS-SERVICE",path = "/orders")
public interface OrderClient {
    @PostMapping("/accept/{orderId}/{userId}")
    public void acceptOrder(@PathVariable("orderId") String orderId,
                            @PathVariable("userId") String userId);
}
