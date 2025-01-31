package com.example.jpatablemapping;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @GetMapping("/save")
    public String test() {
        Member member = Member.builder()
            .build();
        memberRepository.save(member);
        postRepository.saveAll(
            List.of(
                Post.builder().title("title1").content("content1").member(member).build(),
                Post.builder().title("title2").content("content2").member(member).build()
            )
        );

        return "저장 완료";
    }

    @GetMapping("/find/{memberId}")
    public ResponseEntity<?> get(@PathVariable("memberId") Long memberId) {
        Member member = memberRepository.findById(memberId).get();
        List<String> contents = member.getPosts().stream()
            .map(Post::getContent)
            .toList();
        return ResponseEntity.ok(contents);
    }
}
