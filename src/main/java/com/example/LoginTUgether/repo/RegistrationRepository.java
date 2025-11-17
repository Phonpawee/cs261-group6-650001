package com.example.LoginTUgether.repo;

import com.example.LoginTUgether.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

	// ดึงรายการลงทะเบียนของ user โดยใช้ user.id
    List<Registration> findByUser_Id(Long userId);
    
    // รายชื่อผู้ลงทะเบียนใน event แบบ List
    List<Registration> findByEventId(Long eventId);

    // รายชื่อผู้ลงทะเบียนใน event แบบ Page (ใช้ใน RegistrationService)
    Page<Registration> findByEventId(Long eventId, Pageable pageable);

    // รายชื่อผู้ลงทะเบียนใน event + filter status แบบ Page (ใช้ใน RegistrationService)
    Page<Registration> findByEventIdAndStatus(Long eventId, String status, Pageable pageable);

    // ใช้เช็คลงทะเบียนซ้ำ
    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);

    // ใช้นับจำนวนผู้สมัครทั้งหมด
    long countByEventId(Long eventId);

    // ใช้นับตาม status (ใช้ใน stats)
    long countByEventIdAndStatus(Long eventId, String status);

    // สำหรับลบ event
    void deleteByEventId(Long eventId);
}
