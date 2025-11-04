package com.example.LoginTUgether.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ActivityResponseDTO {
	private Long id;
    private String name;
    private String category;
    private LocalDate date;
    private LocalTime time;
    private Integer maxSeats;
    private String description;
    private String organizerEmail;
    private String status;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public Integer getMaxSeats() {
		return maxSeats;
	}
	public void setMaxSeats(Integer maxSeats) {
		this.maxSeats = maxSeats;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOrganizerEmail() {
		return organizerEmail;
	}
	public void setOrganizerEmail(String organizerEmail) {
		this.organizerEmail = organizerEmail;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
