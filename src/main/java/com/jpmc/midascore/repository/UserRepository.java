package com.jpmc.midascore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpmc.midascore.entity.UserRecord;

public interface UserRepository extends JpaRepository<UserRecord, Long> {
}
