package com.example.accountservice.repo;

import com.example.accountservice.model.AccountDTO;
import com.example.accountservice.model.StatisticDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepo extends JpaRepository<AccountDTO, Integer> {
    @Query(value = "SELECT path_file FROM accountdto WHERE file_name = :fileName and email = :email", nativeQuery = true)
    Optional findByPathFile(String fileName, String email);

    @Query(value = "SELECT path_file FROM accountdto WHERE email = :email", nativeQuery = true)
    List<String> findAllPathFile(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM accountdto WHERE file_name = :fileName and email = :email", nativeQuery = true)
    void deleteFile(String fileName, String email);
}
