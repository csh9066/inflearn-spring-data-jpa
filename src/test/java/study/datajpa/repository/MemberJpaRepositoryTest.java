package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void testMember() {
        Member member = new Member("mebmerA");
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(member.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    void findByUsernameAndAgeGraterThen() {
        Member memberA = new Member("mebmerA", 21);
        Member memberB = new Member("mebmerB", 10);
        Member memberC = new Member("mebmerC", 30);

        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);
        memberJpaRepository.save(memberC);

        List<Member> result =
                memberJpaRepository.findByUsernameAndAgeGreaterThen("mebmerA", 20);

        assertThat(result).hasSize(1);
        assertThat(result)
                .first()
                .extracting("age")
                .isEqualTo(21);
    }

    @Test
    void findByPage() {
        for (int i = 1; i <= 20; i++) {
            Member member = new Member("Member" + i, 10);
            memberJpaRepository.save(member);
        }

        List<Member> members = memberJpaRepository.findByPage(10, 10, 5);
        Long totalCount = memberJpaRepository.totalCount(10);

        members.forEach(System.out::println);

        assertThat(members)
                .element(0)
                .extracting("id")
                .isEqualTo(11L);

        assertThat(totalCount).isEqualTo(20);
    }

    @Test
    void bulkAgePlus() {
        // given
        memberJpaRepository.save(new Member("memberA", 10));
        memberJpaRepository.save(new Member("memberB", 20));
        memberJpaRepository.save(new Member("memberC", 21));
        memberJpaRepository.save(new Member("memberD", 23));
        memberJpaRepository.save(new Member("memberE", 19));

        // when
        // 이상인 맴버의 나이를 +1 한다.
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        // then
        assertThat(resultCount).isEqualTo(3);
    }
}
