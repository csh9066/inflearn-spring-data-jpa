package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    void testMember() {
        Member member = new Member("mebmerA");
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    void findByUsernameAndAgeGreaterThan() {
        Member memberA = new Member("mebmerA", 21);
        Member memberB = new Member("mebmerB", 10);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result =
                memberRepository.findByUsernameAndAgeGreaterThan("mebmerA", 20);

        assertThat(result).hasSize(1);
        assertThat(result)
                .first()
                .extracting("age")
                .isEqualTo(21);
    }

    @Test
    void findUsersWithQuery() {
        Member memberA = new Member("memberA", 21);
        Member memberB = new Member("memberB", 10);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.findUsers("memberA", 21);

        assertThat(result).hasSize(1);
        assertThat(result)
                .first()
                .extracting("age")
                .isEqualTo(21);
    }

    @Test
    void findMemberDto() {
        Team team = new Team("babo");
        teamRepository.save(team);

        Member memberA = new Member("memberA", 21, team);
        Member memberB = new Member("memberB", 10, team);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        memberDto.forEach(System.out::println);
    }

    @Test
    void findByNames() {
        Member memberA = new Member("memberA", 21);
        Member memberB = new Member("memberB", 10);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> members = memberRepository
                .findByNames(Arrays.asList("memberA", "memberB"));

        assertThat(members).hasSize(2);
    }

    @Test
    void findByPage() {
        Team team = new Team("asd");
        teamRepository.save(team);
        // 20개 데이터 생성
        for (int i = 1; i <= 20; i++) {
            Member member = new Member("Member" + i, 10, team);
            memberRepository.save(member);
        }

        // 보통 Pageable 구현체인 PageRequest를 사용한다
        // 페이지는 0 페이지부터 시작한다.
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Member> page = memberRepository.findByAge(10, pageRequest);

        assertThat(page.getTotalElements()).isEqualTo(20);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isEmpty()).isFalse();
        assertThat(page.hasNext()).isTrue();
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
    }

    @Test
    void bulkAgePlus() {
        // given
        memberRepository.save(new Member("mebmerA", 10));
        memberRepository.save(new Member("mebmerB", 20));
        memberRepository.save(new Member("mebmerC", 21));
        memberRepository.save(new Member("mebmerD", 23));
        memberRepository.save(new Member("mebmerE", 19));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        Member memberA = memberRepository.findByUsername("mebmerB");

        // then
        assertThat(resultCount).isEqualTo(3);
        assertThat(memberA.getAge()).isEqualTo(21);
    }

    @Test
    void findAllWithEntityGraph() {
        Team team = new Team("team1");
        teamRepository.save(team);
        // given
        memberRepository.save(new Member("mebmerA", 10, team));
        memberRepository.save(new Member("mebmerB", 20, team));
        memberRepository.save(new Member("mebmerC", 21, team));

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();

        assertThat(members.get(0).getTeam())
                .isExactlyInstanceOf(Team.class);
    }
}
