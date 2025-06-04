package com.lshdainty.myhr.repository;

import com.lshdainty.myhr.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final EntityManager em;

    // 신규 사용자 저장
    @Override
    public void save(User user) {
        em.persist(user);
    }

    // userId로 단일 유저 검색
    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(em.find(User.class, userId));
    }

    // 전체 유저 목록 조회
    @Override
    public List<User> findUsers() {
        return em.createQuery("select u from User u where u.delYN = :delYN", User.class)
                .setParameter("delYN", "N")
                .getResultList();
    }

    // 유저가 가지고 있는 휴가 리스트 조회
    @Override
    public List<User> findUsersWithVacations() {
        return em.createQuery("select u from User u join fetch u.vacations v where u.delYN = :uDelYN", User.class)
                .setParameter("uDelYN", "N")
                .getResultList();
    }
}


