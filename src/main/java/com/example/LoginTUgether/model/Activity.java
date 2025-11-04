package com.example.LoginTUgether.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String category;

    @Column(name = "event_date")
    private LocalDate date;

    @Column(name = "event_time")
    private LocalTime time;

    @Min(0)
    private Integer maxSeats;

    @Lob
    private String description;

    private String organizerEmail;

    // status: OPEN, FULL, CANCELLED
    private String status = "OPEN";

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public Integer getMaxSeats() { return maxSeats; }
    public void setMaxSeats(Integer maxSeats) { this.maxSeats = maxSeats; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOrganizerEmail() { return organizerEmail; }
    public void setOrganizerEmail(String organizerEmail) { this.organizerEmail = organizerEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
	public void setName(LocalTime name2) {
		// TODO Auto-generated method stub
		
	}
}