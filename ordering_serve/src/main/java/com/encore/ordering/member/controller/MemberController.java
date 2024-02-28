package com.encore.ordering.member.controller;

import com.encore.ordering.common.CommonResponse;
import com.encore.ordering.member.domain.Member;
import com.encore.ordering.member.dto.LoginReqDto;
import com.encore.ordering.member.dto.MemberCreateReqDto;
import com.encore.ordering.member.dto.MemberResponseDto;
import com.encore.ordering.member.service.MemberService;
import com.encore.ordering.order.dto.OrderResDto;
import com.encore.ordering.order.service.OrderService;
import com.encore.ordering.securities.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MemberController {
    private final MemberService memberService;
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public MemberController(MemberService memberService, OrderService orderService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.orderService = orderService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/member/create") // @Valid : Valid 옵션확인 유효한지 아닌지
    public ResponseEntity<CommonResponse> memberCreate(@Valid @RequestBody MemberCreateReqDto memberCreateReqDto) {
        Member member = memberService.create(memberCreateReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.CREATED, "member successfully created", member.getId()), HttpStatus.CREATED);
    }

    @PreAuthorize(("hasRole('ADMIN')"))
    @GetMapping("/members") // admin만 가능해야함
    public List<MemberResponseDto> members() {
        return memberService.findAll();
    }

    @GetMapping("/member/myInfo")
    public MemberResponseDto findMyInfo() {
        return memberService.findMyInfo();
    }


    @PreAuthorize("hasRole('ADMIN')") //orderResDto
    @GetMapping("/member/{id}/orders") // admin에서 사용 -> 사용자별 orders // mypage에서 사용-> 나의 orders
    public List<OrderResDto>findMemberOrders(@PathVariable Long id) {
        return orderService.findByMember(id);
    }

    @GetMapping("/member/myorders")
    public List<OrderResDto> findMemberOrders(){
        return orderService.findMyOrders();
    }


    @PostMapping("/doLogin") //doLogin은 토큰 사용으로 Map형식으로 받아주어야함 // Map<String, Object>
    public ResponseEntity<CommonResponse> memberLogin(@Valid @RequestBody LoginReqDto loginReqDto) {
        Member member = memberService.login(loginReqDto);
//        토큰 생성
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        Map<String, Object> member_info = new HashMap<>();
        member_info.put("id", member.getId());
        member_info.put("token", jwtToken);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "member successfully logined", member_info), HttpStatus.OK);
    }
}
