package com.example.LoginTUgether.repo;

import com.example.LoginTUgether.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByCategory(String category);
    
    List<Event> findByStatus(String status);
    
    List<Event> findByOrganizerId(Long organizerId);
    
    List<Event> findByNameContainingIgnoreCase(String name);
}