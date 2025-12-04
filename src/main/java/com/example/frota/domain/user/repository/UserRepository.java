package com.example.frota.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.frota.domain.user.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
