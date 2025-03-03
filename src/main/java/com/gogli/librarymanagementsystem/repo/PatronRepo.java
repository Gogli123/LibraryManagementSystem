package com.gogli.librarymanagementsystem.repo;

import com.gogli.librarymanagementsystem.models.MembershipStatus;
import com.gogli.librarymanagementsystem.models.Patron;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatronRepo extends JpaRepository<Patron, Long> {

    @Query("SELECT p FROM Patron p WHERE p.emailAddress = :email")
    Patron findByEmail(String email);

    @Query("SELECT p FROM Patron p WHERE p.phoneNumber = :phoneNumber")
    Patron findByPhoneNumber(String phoneNumber);

    @Query("SELECT p FROM Patron p " +
            "WHERE p.membershipStatus = " +
            "com.gogli.librarymanagementsystem.models.MembershipStatus.ACTIVE")
    List<Patron> findActivePatrons();

    List<Patron> findAllPatronByFirstName(String firstName);
    List<Patron> findAllPatronByLastName(String lastName);
    List<Patron> findAllByEmailAddress(String trim);
    List<Patron> findAllPatronByMembershipStatus(MembershipStatus status);
}
