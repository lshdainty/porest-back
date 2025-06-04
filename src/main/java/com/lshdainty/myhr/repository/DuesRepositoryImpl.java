package com.lshdainty.myhr.repository;

import com.lshdainty.myhr.domain.Dues;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DuesRepositoryImpl implements DuesRepository {
    private final EntityManager em;

    // 신규 회비 저장
    @Override
    public void save(Dues dues) {
        em.persist(dues);
    }

    // 단건 회비 조회(delete용)
    @Override
    public Optional<Dues> findById(Long id) {
        return Optional.ofNullable(em.find(Dues.class, id));
    }

    // 전체 회비 조회
    @Override
    public List<Dues> findDues() {
        return em.createQuery("select d from Dues d order by d.date", Dues.class)
                .getResultList();
    }

    // 년도에 해당하는 회비 조회
    @Override
    public List<Dues> findDuesByYear(String year) {
        return em.createQuery("select d from Dues d where substring(d.date, 0, 4) = :year order by d.date", Dues.class)
                .setParameter("year", year)
                .getResultList();
    }

    // 회비 삭제
    @Override
    public void delete(Dues dues) {
        em.remove(dues);
    }
}
