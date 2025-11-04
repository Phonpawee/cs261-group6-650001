package com.example.LoginTUgether.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class ActivityRequestDTO {
    @NotBlank
    private String name;
    private String category;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime time;

    @NotNull
    @Min(1)
    private Integer maxSeats;

    private String description;
    @Email
    private String organizerEmail;
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalTime getName() {
		// TODO Auto-generated method stub
		return null;
	}
	public LocalDate getDate() {
		// TODO Auto-generated method stub
		return null;
	}
	public Integer getMaxSeats() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getOrganizerEmail() {
		// TODO Auto-generated method stub
		return null;
	}
}