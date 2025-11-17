package com.example.LoginTUgether.scheduler;

import com.example.LoginTUgether.model.Event;
import com.example.LoginTUgether.repo.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Component
public class EventScheduler {

    @Autowired
    private EventRepository eventRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  
    @Scheduled(fixedRate = 60000) // 
    public void updateExpiredEvents() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n🔄 [Scheduler] Checking for expired events at: " + now.format(formatter));
        
        try {
      
            List<Event> expiredEvents = eventRepository.findExpiredEvents(now);
            
            if (expiredEvents.isEmpty()) {
                System.out.println("   ✅ No expired events found - all events are up to date");
                return;
            }
            
            System.out.println("   📋 Found " + expiredEvents.size() + " expired event(s):");
            
            int successCount = 0;
            int errorCount = 0;
            
            for (Event event : expiredEvents) {
                try {
                    String oldStatus = event.getStatus();
                    event.setStatus("CLOSED");
                    eventRepository.save(event);
                    successCount++;
                    
                    System.out.println("   ✅ Event ID " + event.getId() + 
                                     " | \"" + event.getName() + "\"" +
                                     " | " + oldStatus + " → CLOSED" +
                                     " | Date: " + event.getEventDate().format(formatter));
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("   ❌ Failed to update Event ID " + event.getId() + 
                                     ": " + e.getMessage());
                }
            }
            
            System.out.println("\n📊 Summary:");
            System.out.println("   ✅ Successfully updated: " + successCount);
            if (errorCount > 0) {
                System.out.println("   ❌ Failed: " + errorCount);
            }
            System.out.println("   🕐 Next check in 1 hour\n");
            
        } catch (Exception e) {
            System.err.println("❌ [Scheduler] Critical error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Scheduled(initialDelay = 10000, fixedRate = Long.MAX_VALUE)
    public void updateExpiredEventsOnStartup() {
        System.out.println("\n🚀 [Scheduler] Running initial check on server startup...");
        updateExpiredEvents();
    }

}