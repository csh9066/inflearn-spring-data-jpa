package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByUsername(String username);

    List<Member> findByUsernameAndAgeGreaterThan(String username, Integer age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUsers(@Param("username") String username, @Param("age") Integer age);

    // DTO 조회
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) " +
            "from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    Page<Member> findByAge(Integer age, Pageable pageable);

    // update, insert, delete 같은 DML(데이터 조작어) 경우 @Modifying 애노테이션 붙이기
    // bulk 연산의 문제점! 영속성 컨텍스트에 있는 데이터들을 무시하고 다 업데이트 한다.
    // clearAutomatically = true로 설정하면 연산 이후 영속성 컨텍스트를 clear 한다.
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    Integer bulkAgePlus(@Param("age") Integer age);

    // EntityGraph 를 사용하면 fetch join 없이 연관된 엔티티를 조회할 수 잇다.
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();
}
