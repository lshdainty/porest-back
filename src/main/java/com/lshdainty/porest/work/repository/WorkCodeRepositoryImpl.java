package com.lshdainty.porest.work.repository;

import com.lshdainty.porest.work.domain.WorkCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkCodeRepositoryImpl implements WorkCodeRepository {
    private final EntityManager em;

    @Override
    public Optional<WorkCode> findByCode(String code) {
        return em.createQuery("select w from WorkCode w where w.code = :code", WorkCode.class)
                .setParameter("code", code)
                .getResultStream()
                .findFirst();
    }
}
