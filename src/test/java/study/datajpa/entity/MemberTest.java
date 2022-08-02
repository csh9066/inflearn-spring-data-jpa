package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberTest {

    @Autowired
    private MemberRepository memberRepository;
    
    @PersistenceContext
    private EntityManager em;

    @Test
    void creation() throws Exception {
        Member member = new Member("memberA", 20);

        memberRepository.save(member);

        System.out.println(member.getCreatedDate());
        System.out.println(member.getUpdatedDate());

        Thread.sleep(100);

        member.setAge(21);

        em.flush();
        em.clear();

        Member updatedMember = memberRepository.findById(member.getId()).get();

        assertThat(member.getUpdatedDate()).isAfter(member.getCreatedDate());
    }

}
