package com.shivu.userapplication.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "users")
public class ApplicationUser implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer userId;

	@Column(unique = true)
	private String username;
	private String password;

	private String email;
	@Column(name = "reset_password_token")
	private String resetPasswordToken;

	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role_junction", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "role_id") })
	private Set<Role> authorities;

	public enum UserStatus {
		PENDING, ACTIVE, REJECTED
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private UserStatus status = UserStatus.PENDING;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public ApplicationUser() {
		super();
		authorities = new HashSet<>();
	}

	public ApplicationUser(Integer userId, String username, String password, Set<Role> authorities,
			Department department, String email, String resetPasswordToken) {
		super();
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.email = email;
		this.department = department;
		this.resetPasswordToken = resetPasswordToken;
		this.status = UserStatus.ACTIVE; // Test users are active by default
	}

	public ApplicationUser(String username, String password, Set<Role> authorities, Department department, String email,
			String resetPasswordToken) {
		super();
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.email = email;
		this.department = department;
		this.resetPasswordToken = resetPasswordToken;
		this.status = UserStatus.ACTIVE; // Test users are active by default
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setId(Integer userId) {
		this.userId = userId;
	}

	public void setAuthorities(Set<Role> authorities) {
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/*
	 * If you want account locking capabilities create variables and ways to set
	 * them for the methods below
	 */
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.status == UserStatus.ACTIVE;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

}