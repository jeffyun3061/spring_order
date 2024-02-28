package com.encore.ordering.member.service;

import com.encore.ordering.member.domain.Address;
import com.encore.ordering.member.domain.Member;
import com.encore.ordering.member.domain.Role;
import com.encore.ordering.member.dto.LoginReqDto;
import com.encore.ordering.member.dto.MemberCreateReqDto;
import com.encore.ordering.member.dto.MemberResponseDto;
import com.encore.ordering.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member create(MemberCreateReqDto memberCreateReqDto) {
//        memberRepository.findByEmail(memberCreateReqDto.getEmail())
//                .orElseThrow(()->new IllegalArgumentException("이미 존재하는 회원정보 입니다."));
        Optional<Member> byEmail = memberRepository.findByEmail((memberCreateReqDto.getEmail()));
        if(byEmail.isPresent()){
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
        memberCreateReqDto.setPassword(passwordEncoder.encode(memberCreateReqDto.getPassword()));
        Member member = Member.toEntity(memberCreateReqDto);
        return memberRepository.save(member);
    }

    public MemberResponseDto findMyInfo(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(email);
        Member member = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        return MemberResponseDto.toMemberResponseDto(member);
    }

    public List<MemberResponseDto> findAll(){
        List<Member> members = memberRepository.findAll();
        // members에서 stream으로 map m을 꺼낸 후 반환해서 collect
        return members.stream().map(m->MemberResponseDto.toMemberResponseDto(m)).collect(Collectors.toList());
    }

    public Member login(LoginReqDto loginReqDto) throws IllegalArgumentException{
//        email 존재여부
        Member member = memberRepository.findByEmail(loginReqDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
//        password 일치여부 //일치하지 않으면 에러 터트림
        if (!passwordEncoder.matches(loginReqDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }
        return member;
    }


}
