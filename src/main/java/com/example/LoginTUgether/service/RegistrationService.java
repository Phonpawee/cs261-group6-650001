package com.example.LoginTUgether.service;

import java.util.List;

import com.example.LoginTUgether.model.Registration;
import com.example.LoginTUgether.repo.RegistrationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RegistrationService {
	
	@Autowired
	private RegistrationRepository registrationRepository;

    public List<Registration> getRegistrationsByEventId(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }
}
