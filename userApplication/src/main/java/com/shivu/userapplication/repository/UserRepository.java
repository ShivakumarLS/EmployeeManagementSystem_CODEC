package com.shivu.userapplication.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shivu.userapplication.model.ApplicationUser;
import com.shivu.userapplication.model.ApplicationUser.UserStatus;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {
	Optional<ApplicationUser> findByUsername(String username);

	@Transactional
	Optional<ApplicationUser> deleteByUsername(String username);

	ApplicationUser findByEmail(String email);

	Optional<ApplicationUser> findByResetPasswordToken(String token);

	@Query("SELECT u FROM ApplicationUser u WHERE u.department.departmentName= :departmentName")
	ArrayList<ApplicationUser> findAllByDepartmentName(@Param("departmentName") String departmentName);

	List<ApplicationUser> findByStatus(UserStatus status);
}
