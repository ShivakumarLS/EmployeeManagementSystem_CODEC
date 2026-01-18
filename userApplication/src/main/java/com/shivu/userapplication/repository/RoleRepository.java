package com.shivu.userapplication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shivu.userapplication.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findFirstByAuthority(String authority);

    Optional<Role> findByAuthority(String authority);
}