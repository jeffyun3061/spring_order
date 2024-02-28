package com.encore.ordering.order.controller;

import com.encore.ordering.common.CommonResponse;
import com.encore.ordering.order.domain.Ordering;
import com.encore.ordering.order.dto.OrderReqDto;
import com.encore.ordering.order.dto.OrderResDto;
import com.encore.ordering.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order/create")
    public ResponseEntity<CommonResponse> orderCreate(@RequestBody List<OrderReqDto> orderReqDtos){
        Ordering ordering = orderService.create(orderReqDtos);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.CREATED, "order successfully created", ordering.getId()), HttpStatus.CREATED);
    }

    //admin이거나 email이 인증된 email인지 controller단에서 확인
    @DeleteMapping("/order/{id}/cancel")
    public ResponseEntity<CommonResponse> ordercancel(@PathVariable Long id){
        Ordering ordering = orderService.cancel(id);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK,
                "cancel successfully created", ordering.getId()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orders")
    public List<OrderResDto> orderList(){
        return orderService.findAll();
    }



}
