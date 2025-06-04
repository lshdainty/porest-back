package com.lshdainty.myhr.repository;

import com.lshdainty.myhr.domain.Dues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DuesRepository {
    void save(Dues dues);
    Optional<Dues> findById(Long id);
    List<Dues> findDues();
    List<Dues> findDuesByYear(String year);
    void delete(Dues dues);
}
