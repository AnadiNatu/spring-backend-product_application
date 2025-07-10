package com.newSystem.ProductManagementSystemImplemented.respository;


import com.newSystem.ProductManagementSystemImplemented.enitity.Users;
import com.newSystem.ProductManagementSystemImplemented.enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    @Query("SELECT u FROM Users u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<Users> findByEmail(@Param("email") String username);

    @Query("SELECT u FROM Users u WHERE LOWER(u.fname) LIKE LOWER(:name) OR LOWER(u.lname) LIKE LOWER(:name)")
    Optional<Users> findUsersByNameContaining(@Param("name") String name);

    @Query("SELECT u FROM Users u WHERE u.userRoles = :userRoles")
    List<Users> findUserByUserRoles(@Param("userRoles") UserRoles userRoles);

    @Query("SELECT u FROM Users u WHERE (LOWER(u.fname) LIKE LOWER(:name) OR LOWER(u.lname) LIKE LOWER(:name)) AND u.userRoles = :userRoles")
    Optional<Users> findUserByNameAndRoles(@Param("name") String name , @Param("userRoles") UserRoles userRoles);

    Optional<Users> findByUserRoles(UserRoles userRoles);
}
