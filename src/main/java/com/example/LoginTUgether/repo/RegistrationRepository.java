package com.example.LoginTUgether.repo;

import com.example.LoginTUgether.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    List<Registration> findByUserId(Long userId);
    
    List<Registration> findByEventId(Long eventId);
    
    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);
    
    Long countByEventIdAndStatus(Long eventId, String status);
    
    

    Page<Registration> findByEventId(Long eventId, Pageable pageable);

    Page<Registration> findByEventIdAndStatus(Long eventId, String status, Pageable pageable);

    long countByEventId(Long eventId);

    

}