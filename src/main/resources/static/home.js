document.addEventListener('DOMContentLoaded', () => {

  // ==========================================
  // 🎯 Configuration & State
  // ==========================================
  const API_BASE_URL = 'http://localhost:8081/api'; 
  const EVENTS_API = `${API_BASE_URL}/events`;
  const REGISTRATIONS_API = `${API_BASE_URL}/registrations`;
  
  // 🔑 ตรวจสอบว่ามีการ Login หรือยัง (ใช้ studentId)
  const studentId = localStorage.getItem('studentId');
  
  // ถ้ายังไม่ได้ login ให้ redirect กลับไปหน้า login
  if (!studentId) {
    alert('กรุณา Login ก่อนใช้งาน');
    window.location.href = 'index.html';
    throw new Error('Not logged in');
  }
  
  // 🔑 User ID (ในระบบจริงควรได้จาก API/JWT)
  const CURRENT_USER_ID = 1; // ⬅️ ปรับตามฐานข้อมูลของคุณ
  const CURRENT_USER_STUDENT_ID = studentId; // ใช้ studentId จาก localStorage

  // DOM Elements
  const allEventsListContainer = document.getElementById('allEventsList');
  const myRegistrationsListContainer = document.getElementById('myRegistrationsList');
  const myEventsListContainer = document.getElementById('myEventsList');
  const eventModal = document.getElementById('eventDetailModal');
  const closeModal = document.querySelector('.close');
  const modalRegisterBtn = document.getElementById('modalRegisterBtn');
  const modalCancelBtn = document.getElementById('modalCancelBtn');

  // State
  let currentEventId = null;
  let registeredEventIds = new Set();

  // Set user email
  document.getElementById('userEmail').textContent = CURRENT_USER_STUDENT_ID;


  // ==========================================
  // 🔄 Tab Navigation
  // ==========================================
  const tabs = document.querySelectorAll('.tab');
  const contents = document.querySelectorAll('.tab-content');

  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      tabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');

      contents.forEach(c => c.classList.remove('active'));

      const targetId = tab.dataset.target;
      const target = document.getElementById(targetId);
      target.classList.add('active');
      
      // Load data when switching tabs
      if (targetId === 'all-events') {
        loadAllEvents();
      } else if (targetId === 'my-registrations') {
        loadMyRegistrations();
      } else if (targetId === 'my-events') {
        loadMyEvents();
      }
    });
  });

  // ==========================================
  // 📥 Load All Events
  // ==========================================
  async function loadAllEvents() {
    if (!allEventsListContainer) return;
    
    allEventsListContainer.innerHTML = '<p class="loading-message">กำลังโหลดกิจกรรม...</p>';

    try {
      // Get all events
      const eventsResponse = await fetch(EVENTS_API);
      if (!eventsResponse.ok) throw new Error('Failed to fetch events');
      const events = await eventsResponse.json();

      // Get user's registrations to check status
      await updateRegisteredEvents();

      // Apply filters
      const searchTerm = document.getElementById('searchInput')?.value.toLowerCase() || '';
      const category = document.getElementById('categoryFilter')?.value || 'all';
      const dateRange = document.getElementById('dateFilter')?.value || 'all';

      let filteredEvents = events.filter(event => {
        // Only show OPEN events
        if (event.status !== 'OPEN') return false;

        // Search filter
        if (searchTerm && !event.name.toLowerCase().includes(searchTerm) && 
            !event.description?.toLowerCase().includes(searchTerm)) {
          return false;
        }

        // Category filter
        if (category !== 'all' && event.category !== category) {
          return false;
        }

        // Date filter
        if (dateRange !== 'all') {
          const eventDate = new Date(event.eventDate);
          const now = new Date();
          const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

          if (dateRange === 'today') {
            const tomorrow = new Date(today);
            tomorrow.setDate(tomorrow.getDate() + 1);
            if (eventDate < today || eventDate >= tomorrow) return false;
          } else if (dateRange === 'this-week') {
            const weekEnd = new Date(today);
            weekEnd.setDate(weekEnd.getDate() + 7);
            if (eventDate < today || eventDate >= weekEnd) return false;
          } else if (dateRange === 'next-week') {
            const nextWeekStart = new Date(today);
            nextWeekStart.setDate(nextWeekStart.getDate() + 7);
            const nextWeekEnd = new Date(nextWeekStart);
            nextWeekEnd.setDate(nextWeekEnd.getDate() + 7);
            if (eventDate < nextWeekStart || eventDate >= nextWeekEnd) return false;
          }
        }

        return true;
      });

      allEventsListContainer.innerHTML = '';

      if (filteredEvents.length === 0) {
        allEventsListContainer.innerHTML = '<p class="no-events">⚠️ ไม่พบกิจกรรมที่ตรงกับเงื่อนไข</p>';
        return;
      }

      filteredEvents.forEach(event => {
        const card = createEventCard(event, 'all');
        allEventsListContainer.appendChild(card);
      });

    } catch (error) {
      console.error('Error loading events:', error);
      allEventsListContainer.innerHTML = '<p class="error-message">⚠️ ไม่สามารถโหลดกิจกรรมได้ กรุณาลองใหม่อีกครั้ง</p>';
    }
  }

  // ==========================================
  // 📥 Load My Registrations
  // ==========================================
  async function loadMyRegistrations() {
    if (!myRegistrationsListContainer) return;
    
    myRegistrationsListContainer.innerHTML = '<p class="loading-message">กำลังโหลดรายการลงทะเบียน...</p>';

    try {
      const response = await fetch(`${REGISTRATIONS_API}/my-registrations/${CURRENT_USER_ID}`);
      if (!response.ok) throw new Error('Failed to fetch registrations');
      
      const registrations = await response.json();
      
      myRegistrationsListContainer.innerHTML = '';

      if (registrations.length === 0) {
        myRegistrationsListContainer.innerHTML = '<p class="no-events">ยังไม่มีการลงทะเบียนกิจกรรม</p>';
        return;
      }

      registrations.forEach(registration => {
        const card = createEventCard(registration.event, 'registration', registration);
        myRegistrationsListContainer.appendChild(card);
      });

    } catch (error) {
      console.error('Error loading registrations:', error);
      myRegistrationsListContainer.innerHTML = '<p class="error-message">⚠️ ไม่สามารถโหลดรายการลงทะเบียนได้</p>';
    }
  }

  // ==========================================
  // 📥 Load My Events (Events I Organized)
  // ==========================================
  async function loadMyEvents() {
    if (!myEventsListContainer) return;
    
    myEventsListContainer.innerHTML = '<p class="loading-message">กำลังโหลดกิจกรรมของคุณ...</p>';

    try {
      const response = await fetch(`${EVENTS_API}/my-events/${CURRENT_USER_ID}`);
      if (!response.ok) throw new Error('Failed to fetch my events');
      
      const events = await response.json();
      
      myEventsListContainer.innerHTML = '';

      if (events.length === 0) {
        myEventsListContainer.innerHTML = '<p class="no-events">คุณยังไม่ได้สร้างกิจกรรมใด ๆ</p>';
        return;
      }

      events.forEach(event => {
        const card = createEventCard(event, 'my-event');
        myEventsListContainer.appendChild(card);
      });

    } catch (error) {
      console.error('Error loading my events:', error);
      myEventsListContainer.innerHTML = '<p class="error-message">⚠️ ไม่สามารถโหลดกิจกรรมของคุณได้</p>';
    }
  }

  // ==========================================
  // 🎨 Create Event Card
  // ==========================================
  function createEventCard(event, context = 'all', registration = null) {
    const card = document.createElement('div');
    card.className = 'event-card';
    card.dataset.eventId = event.id;

    const isRegistered = registeredEventIds.has(event.id);
    const isFull = event.currentParticipants >= event.maxParticipants;
    const seatsLeft = event.maxParticipants - event.currentParticipants;

    let statusBadge = '';
    let actionButton = '';

    // Determine status and button based on context
    if (context === 'all') {
      // Test case 4: รองรับสถานะต่างๆ
      if (event.status === 'CANCELLED') {
        statusBadge = '<span class="status-badge cancelled">ยกเลิก</span>';
        actionButton = '';
      } else if (event.status === 'CLOSED') {
        statusBadge = '<span class="status-badge closed">ปิดรับ</span>';
        actionButton = '';
      } else if (isRegistered) {
        statusBadge = '<span class="status-badge registered">ลงทะเบียนแล้ว</span>';
        actionButton = '<button class="btn-cancel-registration" data-event-id="' + event.id + '">ยกเลิกการลงทะเบียน</button>';
      } else if (isFull) {
        statusBadge = '<span class="status-badge full">เต็ม</span>';
        actionButton = '<button class="btn-register" disabled>เต็มแล้ว</button>';
      } else {
        statusBadge = '<span class="status-badge open">เปิดรับสมัคร</span>';
        // Test case 3: ปุ่ม "เข้าร่วม" แสดงเมื่อยังไม่เต็มและยังไม่ได้เข้าร่วม
        actionButton = '<button class="btn-register" data-event-id="' + event.id + '">เข้าร่วม</button>';
      }
    } else if (context === 'registration') {
      statusBadge = '<span class="status-badge registered">' + registration.status + '</span>';
      actionButton = '<button class="btn-cancel-registration" data-event-id="' + event.id + '">ยกเลิกการลงทะเบียน</button>';
    } else if (context === 'my-event') {
      statusBadge = '<span class="status-badge ' + event.status.toLowerCase() + '">' + event.status + '</span>';
      actionButton = '';
    }

    const organizerName = event.organizer?.name || event.organizer?.email || 'Unknown';
    const description = event.description ? 
      (event.description.length > 100 ? event.description.substring(0, 100) + '...' : event.description) : 
      'ไม่มีรายละเอียด';

    card.innerHTML = `
      <h4>${event.name}</h4>
      <div class="category">${event.category}</div>
      ${statusBadge}
      <div class="event-details">
        <p><strong>📅 Date:</strong> ${new Date(event.eventDate).toLocaleString('th-TH')}</p>
        <p><strong>👥 Capacity:</strong> ${event.currentParticipants}/${event.maxParticipants} 
          ${!isFull && context === 'all' ? `<span class="seats-left">(เหลือ ${seatsLeft} ที่นั่ง)</span>` : ''}</p>
        ${context !== 'all' ? `<p><strong>👤 Organizer:</strong> ${organizerName}</p>` : ''}
        <p class="description">${description}</p>
      </div>
      <div class="event-action-row">
        <button class="btn-view-details" data-event-id="${event.id}">ดูรายละเอียด</button>
        ${actionButton}
      </div>
    `;

    // Add event listeners
    const viewBtn = card.querySelector('.btn-view-details');
    if (viewBtn) {
      viewBtn.addEventListener('click', () => showEventDetails(event));
    }

    const registerBtn = card.querySelector('.btn-register');
    if (registerBtn && !registerBtn.disabled) {
      registerBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        handleRegister(event.id);
      });
    }

    const cancelBtn = card.querySelector('.btn-cancel-registration');
    if (cancelBtn) {
      cancelBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        handleCancelRegistration(event.id);
      });
    }

    return card;
  }

  // ==========================================
  // 🔍 Show Event Details in Modal
  // ==========================================
  function showEventDetails(event) {
    document.getElementById('modalEventName').textContent = event.name;
    document.getElementById('modalCategory').textContent = event.category;
    document.getElementById('modalDate').textContent = new Date(event.eventDate).toLocaleString('th-TH');
    document.getElementById('modalOrganizer').textContent = event.organizer?.name || event.organizer?.email || 'Unknown';
    document.getElementById('modalCapacity').textContent = `${event.currentParticipants}/${event.maxParticipants}`;
    document.getElementById('modalStatus').textContent = event.status;
    document.getElementById('modalDescription').textContent = event.description || 'ไม่มีรายละเอียด';

    const isRegistered = registeredEventIds.has(event.id);
    const isFull = event.currentParticipants >= event.maxParticipants;

    // Show/hide buttons based on status
    if (isRegistered) {
      modalRegisterBtn.style.display = 'none';
      modalCancelBtn.style.display = 'inline-block';
      currentEventId = event.id;
    } else if (isFull || event.status !== 'OPEN') {
      modalRegisterBtn.style.display = 'none';
      modalCancelBtn.style.display = 'none';
    } else {
      modalRegisterBtn.style.display = 'inline-block';
      modalCancelBtn.style.display = 'none';
      currentEventId = event.id;
    }

    eventModal.style.display = 'block';
  }

  // ==========================================
  // ✅ Handle Register
  // ==========================================
  async function handleRegister(eventId) {
    try {
      // Test case 5: บันทึกข้อมูลและลดจำนวนที่นั่ง
      const response = await fetch(`${REGISTRATIONS_API}/register?userId=${CURRENT_USER_ID}&eventId=${eventId}`, {
        method: 'POST'
      });

      const result = await response.json();

      if (result.success) {
        alert('✅ ลงทะเบียนสำเร็จ!');
        registeredEventIds.add(eventId);
        await loadAllEvents();
        eventModal.style.display = 'none';
      } else {
        // Test case 6: แสดงข้อความเมื่อเต็ม
        // Test case 7: แสดงข้อความเมื่อลงทะเบียนซ้ำ
        alert('❌ ' + result.message);
      }
    } catch (error) {
      console.error('Error registering:', error);
      alert('⚠️ เกิดข้อผิดพลาดในการลงทะเบียน');
    }
  }

  // ==========================================
  // ❌ Handle Cancel Registration
  // ==========================================
  async function handleCancelRegistration(eventId) {
    if (!confirm('คุณแน่ใจหรือไม่ที่จะยกเลิกการลงทะเบียน?')) {
      return;
    }

    try {
      // Test case 2: สามารถยกเลิกได้
      const response = await fetch(`${REGISTRATIONS_API}/cancel?userId=${CURRENT_USER_ID}&eventId=${eventId}`, {
        method: 'DELETE'
      });

      const result = await response.json();

      if (result.success) {
        alert('✅ ยกเลิกการลงทะเบียนสำเร็จ!');
        registeredEventIds.delete(eventId);
        await loadAllEvents();
        await loadMyRegistrations();
        eventModal.style.display = 'none';
      } else {
        alert('❌ ' + result.message);
      }
    } catch (error) {
      console.error('Error cancelling registration:', error);
      alert('⚠️ เกิดข้อผิดพลาดในการยกเลิก');
    }
  }

  // ==========================================
  // 🔄 Update Registered Events
  // ==========================================
  async function updateRegisteredEvents() {
    try {
      const response = await fetch(`${REGISTRATIONS_API}/my-registrations/${CURRENT_USER_ID}`);
      if (!response.ok) throw new Error('Failed to fetch registrations');
      
      const registrations = await response.json();
      registeredEventIds = new Set(registrations.map(r => r.event.id));
    } catch (error) {
      console.error('Error updating registered events:', error);
    }
  }

  // ==========================================
  // 🔍 Search & Filter
  // ==========================================
  const searchInput = document.getElementById('searchInput');
  const categoryFilter = document.getElementById('categoryFilter');
  const dateFilter = document.getElementById('dateFilter');

  if (searchInput) searchInput.addEventListener('input', loadAllEvents);
  if (categoryFilter) categoryFilter.addEventListener('change', loadAllEvents);
  if (dateFilter) dateFilter.addEventListener('change', loadAllEvents);

  // ==========================================
  // ➕ Create Event
  // ==========================================
  const createEventForm = document.getElementById('createEventForm');
  if (createEventForm) {
    createEventForm.addEventListener('submit', async (e) => {
      e.preventDefault();

      const eventData = {
        name: document.getElementById('eventName').value,
        eventDate: document.getElementById('eventDateTime').value,
        maxParticipants: parseInt(document.getElementById('eventCapacity').value),
        category: document.getElementById('eventCategory').value,
        description: document.getElementById('eventDescription').value,
        organizer: { id: CURRENT_USER_ID }
      };

      try {
        const response = await fetch(EVENTS_API, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(eventData)
        });

        if (response.ok) {
          alert('✅ สร้างกิจกรรมสำเร็จ!');
          createEventForm.reset();
          await loadAllEvents();
          await loadMyEvents();
        } else {
          const error = await response.text();
          alert('❌ สร้างกิจกรรมไม่สำเร็จ: ' + error);
        }
      } catch (error) {
        console.error('Error creating event:', error);
        alert('⚠️ เกิดข้อผิดพลาดในการสร้างกิจกรรม');
      }
    });
  }

  // ==========================================
  // 🚪 Modal Controls
  // ==========================================
  if (closeModal) {
    closeModal.addEventListener('click', () => {
      eventModal.style.display = 'none';
    });
  }

  if (modalRegisterBtn) {
    modalRegisterBtn.addEventListener('click', () => {
      if (currentEventId) handleRegister(currentEventId);
    });
  }

  if (modalCancelBtn) {
    modalCancelBtn.addEventListener('click', () => {
      if (currentEventId) handleCancelRegistration(currentEventId);
    });
  }

  window.addEventListener('click', (e) => {
    if (e.target === eventModal) {
      eventModal.style.display = 'none';
    }
  });

  // ==========================================
  // 🚪 Logout
  // ==========================================
   const logoutBtn = document.querySelector('.logout');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', () => {
      if (confirm('คุณต้องการออกจากระบบหรือไม่?')) {
        localStorage.removeItem('studentId');
        window.location.href = 'index.html';
      }
    });
  }

  // ==========================================
  // 🚀 Initial Load
  // ==========================================
  loadAllEvents();

});