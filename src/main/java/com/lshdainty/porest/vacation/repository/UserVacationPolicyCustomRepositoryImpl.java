package com.lshdainty.porest.vacation.repository;

import com.lshdainty.porest.vacation.domain.UserVacationPolicy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.lshdainty.porest.vacation.domain.QUserVacationPolicy.userVacationPolicy;

@Repository
@RequiredArgsConstructor
public class UserVacationPolicyCustomRepositoryImpl implements UserVacationPolicyCustomRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    @Override
    public void save(UserVacationPolicy userVacationPolicy) {
        em.persist(userVacationPolicy);
    }

    @Override
    public void saveAll(List<UserVacationPolicy> userVacationPolicies) {
        for (UserVacationPolicy uvp : userVacationPolicies) {
            em.persist(uvp);
        }
    }

    @Override
    public List<UserVacationPolicy> findByUserId(String userId) {
        return query
                .selectFrom(userVacationPolicy)
                .join(userVacationPolicy.vacationPolicy).fetchJoin()
                .where(userVacationPolicy.user.id.eq(userId))
                .fetch();
    }

    @Override
    public boolean existsByUserIdAndVacationPolicyId(String userId, Long vacationPolicyId) {
        Integer count = query
                .selectOne()
                .from(userVacationPolicy)
                .where(userVacationPolicy.user.id.eq(userId)
                        .and(userVacationPolicy.vacationPolicy.id.eq(vacationPolicyId)))
                .fetchFirst();
        return count != null;
    }

    @Override
    public Optional<UserVacationPolicy> findById(Long userVacationPolicyId) {
        return Optional.ofNullable(query
                .selectFrom(userVacationPolicy)
                .join(userVacationPolicy.vacationPolicy).fetchJoin()
                .join(userVacationPolicy.user).fetchJoin()
                .where(userVacationPolicy.id.eq(userVacationPolicyId))
                .fetchOne()
        );
    }

    @Override
    public Optional<UserVacationPolicy> findByUserIdAndVacationPolicyId(String userId, Long vacationPolicyId) {
        return Optional.ofNullable(query
                .selectFrom(userVacationPolicy)
                .join(userVacationPolicy.vacationPolicy).fetchJoin()
                .join(userVacationPolicy.user).fetchJoin()
                .where(userVacationPolicy.user.id.eq(userId)
                        .and(userVacationPolicy.vacationPolicy.id.eq(vacationPolicyId)))
                .fetchOne()
        );
    }

    @Override
    public List<UserVacationPolicy> findByVacationPolicyId(Long vacationPolicyId) {
        return query
                .selectFrom(userVacationPolicy)
                .join(userVacationPolicy.vacationPolicy).fetchJoin()
                .join(userVacationPolicy.user).fetchJoin()
                .where(userVacationPolicy.vacationPolicy.id.eq(vacationPolicyId))
                .fetch();
    }
}
