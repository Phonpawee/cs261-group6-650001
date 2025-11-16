package com.example.LoginTUgether.service;

import com.example.LoginTUgether.dto.ActivityStatsDTO;
import com.example.LoginTUgether.dto.RegistrationSummaryDTO;
import com.example.LoginTUgether.model.Registration;
import com.example.LoginTUgether.repo.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepo;

    // ----------------------------
    // 1) List Registration by Event
    // ----------------------------
    public Page<RegistrationSummaryDTO> listByEvent(Long eventId, String status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Registration> registrations =
                (status == null || status.isBlank())
                        ? registrationRepo.findByEventId(eventId, pageable)
                        : registrationRepo.findByEventIdAndStatus(eventId, status, pageable);

        return registrations.map(this::toDto);
    }

    // ----------------------------
    // 2) Update Registration Status
    // ----------------------------
    @Transactional
    public RegistrationSummaryDTO updateStatus(Long id, com.example.LoginTUgether.req.UpdateStatusRequest req) {

        Registration reg = registrationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found: " + id));

        // set status
        reg.setStatus(req.getStatus());

        // ถ้าใน Registration ไม่มี field note ให้ Comment บรรทัดนี้
        // reg.setNote(req.getNote());

        Registration saved = registrationRepo.save(reg);

        return toDto(saved);
    }

    // ----------------------------
    // 3) Event Stats
    // ----------------------------
    public ActivityStatsDTO stats(Long eventId, int capacity) {

        long applied = registrationRepo.countByEventId(eventId);
        long selected = registrationRepo.countByEventIdAndStatus(eventId, "SELECTED");
        long waitlisted = registrationRepo.countByEventIdAndStatus(eventId, "WAITLISTED");
        long rejected = registrationRepo.countByEventIdAndStatus(eventId, "REJECTED");
        long remaining = Math.max(0, capacity - selected);

        ActivityStatsDTO dto = new ActivityStatsDTO();
        dto.setEventId(eventId);
        dto.setCapacity(capacity);
        dto.setApplied(applied);
        dto.setSelected(selected);
        dto.setWaitlisted(waitlisted);
        dto.setRejected(rejected);
        dto.setRemaining(remaining);

        return dto;
    }

    // ----------------------------
    // 4) Convert Entity → DTO
    // ----------------------------
    private RegistrationSummaryDTO toDto(Registration r) {

        RegistrationSummaryDTO dto = new RegistrationSummaryDTO();

        dto.setId(r.getId());
        dto.setUserId(r.getUser().getId());
        dto.setEventId(r.getEvent().getId());
        dto.setStatus(r.getStatus());
        // ถ้าใน Registration ไม่มี field note ให้ Comment บรรทัดนี้
        // dto.setNote(r.getNote());
        dto.setRegisteredAt(r.getRegisteredAt());

        return dto;
    }
}

