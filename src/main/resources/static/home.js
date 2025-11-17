document.addEventListener('DOMContentLoaded', () => {

  // ==========================================
  // CONFIG
  // ==========================================
  const API_BASE_URL = 'http://localhost:8081/api';
  const EVENTS_API = `${API_BASE_URL}/events`;
  const REGISTRATIONS_API = `${API_BASE_URL}/registrations`;

  // เช็ค login
  const studentId = localStorage.getItem('studentId');
  if (!studentId) {
    alert('กรุณา Login ก่อนใช้งาน');
    window.location.href = 'index.html';
    return;
  }

  // ==========================================
  // โหลดโปรไฟล์
  // ==========================================
  async function loadProfile() {
    try {
      const res = await fetch(`${API_BASE_URL}/profile/std-info?id=${studentId}`);
      const profile = await res.json();

      // ดึงอีเมล
      const email =
        profile.data?.email ||
        profile.data?.displayname_en ||
        profile.data?.displayname_th ||
        studentId;

      // ดึง role จาก localStorage
      const role = localStorage.getItem('role') || 'STUDENT';

      // แสดงบนหน้าเว็บ
      if (role === 'ADMIN') {
        document.getElementById('userEmail').textContent = `${email} (Admin)`;
      } else {
        document.getElementById('userEmail').textContent = email;
      }

    } catch (err) {
      console.error(err);
    }
  }


  loadProfile();

  // ดึงข้อมูล user จาก localStorage
  const CURRENT_USER_ID = parseInt(localStorage.getItem('userId'), 10);
  const CURRENT_USER_ROLE = localStorage.getItem('role') || 'STUDENT';

  if (!CURRENT_USER_ID || Number.isNaN(CURRENT_USER_ID)) {
    alert('ไม่พบข้อมูลผู้ใช้ กรุณา Login ใหม่');
    window.location.href = 'index.html';
    return;
  }

  // DOM
  const allEventsListContainer = document.getElementById('allEventsList');
  const myRegistrationsListContainer = document.getElementById('myRegistrationsList');
  const myEventsListContainer = document.getElementById('myEventsList');

  const eventModal = document.getElementById('eventDetailModal');
  const closeModal = document.querySelector('.close');
  const modalRegisterBtn = document.getElementById('modalRegisterBtn');
  const modalCancelBtn = document.getElementById('modalCancelBtn');

  let currentEventId = null;
  let registeredEventIds = new Set();


  
  // ซ่อนส่วนสร้างกิจกรรมถ้าไม่ใช่ ADMIN
  const createEventTab = document.querySelector('[data-target="create-event"]');
  const createEventSection = document.getElementById('create-event');
  if (CURRENT_USER_ROLE !== 'ADMIN') {
    if (createEventTab) {
      createEventTab.style.display = 'none';
    }
    if (createEventSection) {
      createEventSection.style.display = 'none';
    }
  }
  // ซ่อน My Events สำหรับ STUDENT
  const myEventsTab = document.querySelector('[data-target="my-events"]');
  const myEventsSection = document.getElementById('my-events');
  if (CURRENT_USER_ROLE !== 'ADMIN') {
      if (myEventsTab) {
          myEventsTab.style.display = 'none';
      }
      if (myEventsSection) {
          myEventsSection.style.display = 'none';
      }
  }



  function isEventExpired(eventDate) {
    const now = new Date();
    const eventDateTime = new Date(eventDate);
    return eventDateTime < now;
  }

  function getEventStatus(event) {
    if (isEventExpired(event.eventDate)) {
      return 'CLOSED';
    }
    
    if (event.currentParticipants >= event.maxParticipants) {
      return 'FULL';
    }
    
    return event.status;
  }

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
      
      if (targetId === 'all-events') {
        loadAllEvents();
      } else if (targetId === 'my-registrations') {
        loadMyRegistrations();
      } else if (targetId === 'my-events') {
        loadMyEvents();
      }
    });
  });

  async function loadAllEvents() {
    if (!allEventsListContainer) return;
    
    allEventsListContainer.innerHTML = '<p class="loading-message">กำลังโหลดกิจกรรม...</p>';

    try {
      const eventsResponse = await fetch(EVENTS_API);
      if (!eventsResponse.ok) throw new Error('Failed to fetch events');
      const events = await eventsResponse.json();

      await updateRegisteredEvents();

      const searchTerm = document.getElementById('searchInput')?.value.toLowerCase() || '';
      const category = document.getElementById('categoryFilter')?.value || 'all';
      const dateRange = document.getElementById('dateFilter')?.value || 'all';

      let filteredEvents = events.filter(event => {
        if (isEventExpired(event.eventDate)) {
          return false;
        }

        if (event.status !== 'OPEN' && !isEventExpired(event.eventDate)) {
          return false;
        }

        if (searchTerm && !event.name.toLowerCase().includes(searchTerm) && 
            !event.description?.toLowerCase().includes(searchTerm)) {
          return false;
        }

        if (category !== 'all' && event.category !== category) {
          return false;
        }

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

      events.sort((a, b) => {
        const aExpired = isEventExpired(a.eventDate);
        const bExpired = isEventExpired(b.eventDate);
        
        if (aExpired && !bExpired) return 1;
        if (!aExpired && bExpired) return -1;
        
        return new Date(b.eventDate) - new Date(a.eventDate);
      });

      events.forEach(event => {
        const card = createEventCard(event, 'my-event');
        myEventsListContainer.appendChild(card);
      });

    } catch (error) {
      console.error('Error loading my events:', error);
      myEventsListContainer.innerHTML = '<p class="error-message">⚠️ ไม่สามารถโหลดกิจกรรมของคุณได้</p>';
    }
  }

  function createEventCard(event, type, registration = null) {
    const card = document.createElement('div');
    card.className = 'event-card';

    const actualStatus = getEventStatus(event);
    const expired = isEventExpired(event.eventDate);
    
    const isRegistered = registeredEventIds.has(event.id);
    const isFull = event.currentParticipants >= event.maxParticipants;
    const seatsLeft = event.maxParticipants - event.currentParticipants;

    let statusBadge = '';
    if (expired) {
      statusBadge = `<span class="status-badge closed">หมดเวลาลงทะเบียน</span>`;
    } else if (type === 'registration') {
      statusBadge = `<span class="status-badge registered">ลงทะเบียนแล้ว</span>`;
    } else if (actualStatus === 'FULL' || isFull) {
      statusBadge = `<span class="status-badge full">เต็มแล้ว</span>`;
    } else if (actualStatus === 'OPEN') {
      statusBadge = `<span class="status-badge open">เปิดรับสมัคร</span>`;
    } else if (actualStatus === 'CANCELLED') {
      statusBadge = `<span class="status-badge cancelled">ยกเลิก</span>`;
    } else {
      statusBadge = `<span class="status-badge closed">ปิดรับสมัคร</span>`;
    }

    let actionButtons = '';
    if (expired) {
      actionButtons = `<button class="btn-view-details">ดูรายละเอียด</button>`;
    } else if (type === 'all') {
      if (isRegistered) {
        actionButtons = `
          <button class="btn-view-details">ดูรายละเอียด</button>
          <button class="btn-cancel-registration">ยกเลิกการลงทะเบียน</button>
        `;
      } else if (!isFull && actualStatus === 'OPEN') {
        actionButtons = `
          <button class="btn-view-details">ดูรายละเอียด</button>
          <button class="btn-register">ลงทะเบียน</button>
        `;
      } else {
        actionButtons = `<button class="btn-view-details">ดูรายละเอียด</button>`;
      }
    } else if (type === 'registration') {
      if (!expired) {
        actionButtons = `
          <button class="btn-view-details">ดูรายละเอียด</button>
          <button class="btn-cancel-registration">ยกเลิกการลงทะเบียน</button>
        `;
      } else {
        actionButtons = `<button class="btn-view-details">ดูรายละเอียด</button>`;
      }
    } else if (type === 'my-event') {
      actionButtons = `<button class="btn-view-details">ดูรายละเอียด</button>`;
    }

    if (expired) {
      card.style.opacity = '0.7';
      card.style.borderLeft = '5px solid #757575';
    }

    card.innerHTML = `
      <h4>${event.name}</h4>
      <span class="category">${event.category}</span>
      ${statusBadge}
      <div class="event-details">
        <p><strong>📅 วันที่:</strong> ${new Date(event.eventDate).toLocaleString('th-TH')}</p>
        ${expired ? '<p style="color: #f44336; font-weight: bold;">⏰ กิจกรรมนี้หมดเวลาลงทะเบียนแล้ว</p>' : ''}
        <p><strong>👥 ผู้เข้าร่วม:</strong> ${event.currentParticipants}/${event.maxParticipants}</p>
        ${!isFull && type === 'all' && !expired ? `<p class="seats-left">🎫 เหลือที่นั่ง ${seatsLeft} ที่</p>` : ''}
        <p class="description">${event.description || 'ไม่มีรายละเอียด'}</p>
      </div>
      <div class="event-action-row">
        ${actionButtons}
      </div>
    `;

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

  function showEventDetails(event) {
    const actualStatus = getEventStatus(event);
    const expired = isEventExpired(event.eventDate);

    document.getElementById('modalEventName').textContent = event.name;
    document.getElementById('modalCategory').textContent = event.category;
    document.getElementById('modalDate').textContent = new Date(event.eventDate).toLocaleString('th-TH');
    document.getElementById('modalOrganizer').textContent = event.organizer?.name || event.organizer?.email || 'Unknown';
    document.getElementById('modalCapacity').textContent = `${event.currentParticipants}/${event.maxParticipants}`;
    document.getElementById('modalStatus').textContent = expired ? 'หมดเวลาลงทะเบียน' : actualStatus;
    document.getElementById('modalDescription').textContent = event.description || 'ไม่มีรายละเอียด';

    const isRegistered = registeredEventIds.has(event.id);
    const isFull = event.currentParticipants >= event.maxParticipants;

    if (expired) {
      modalRegisterBtn.style.display = 'none';
      modalCancelBtn.style.display = 'none';
    } else if (isRegistered) {
      modalRegisterBtn.style.display = 'none';
      modalCancelBtn.style.display = 'inline-block';
      currentEventId = event.id;
    } else if (isFull || actualStatus !== 'OPEN') {
      modalRegisterBtn.style.display = 'none';
      modalCancelBtn.style.display = 'none';
    } else {
      modalRegisterBtn.style.display = 'inline-block';
      modalCancelBtn.style.display = 'none';
      currentEventId = event.id;
    }

    eventModal.style.display = 'block';
  }

  async function handleRegister(eventId) {
    try {
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
        alert('❌ ' + result.message);
      }
    } catch (error) {
      console.error('Error registering:', error);
      alert('⚠️ เกิดข้อผิดพลาดในการลงทะเบียน');
    }
  }

  async function handleCancelRegistration(eventId) {
    if (!confirm('คุณแน่ใจหรือไม่ที่จะยกเลิกการลงทะเบียน?')) {
      return;
    }

    try {
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

  const searchInput = document.getElementById('searchInput');
  const categoryFilter = document.getElementById('categoryFilter');
  const dateFilter = document.getElementById('dateFilter');

  if (searchInput) searchInput.addEventListener('input', loadAllEvents);
  if (categoryFilter) categoryFilter.addEventListener('change', loadAllEvents);
  if (dateFilter) dateFilter.addEventListener('change', loadAllEvents);

  const createEventForm = document.getElementById('createEventForm');
  if (createEventForm) {
    createEventForm.addEventListener('submit', async (e) => {
      e.preventDefault();

      const eventData = {
        userId: CURRENT_USER_ID,
        name: document.getElementById('eventName').value,
        eventDate: document.getElementById('eventDateTime').value,
        maxParticipants: parseInt(document.getElementById('eventCapacity').value),
        category: document.getElementById('eventCategory').value,
        description: document.getElementById('eventDescription').value
      };

      console.log('📤 Sending event data:', eventData);

      try {
        const response = await fetch(EVENTS_API, {
          method: 'POST',
          headers: { 
            'Content-Type': 'application/json; charset=UTF-8'
          },
          body: JSON.stringify(eventData)
        });

        const result = await response.json();

        if (result.success) {
          alert('✅ สร้างกิจกรรมสำเร็จ!');
          createEventForm.reset();
          
          await loadAllEvents();
          await loadMyEvents();
          
          const myEventsTab = document.querySelector('[data-target="my-events"]');
          if (myEventsTab) {
            myEventsTab.click();
          }
        } else {
          alert('❌ สร้างกิจกรรมไม่สำเร็จ: ' + result.message);
        }
      } catch (error) {
        console.error('Error creating event:', error);
        alert('⚠️ เกิดข้อผิดพลาดในการสร้างกิจกรรม');
      }
    });
  }

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


  const logoutBtn = document.querySelector('.logout');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', () => {
        if (confirm('คุณต้องการออกจากระบบหรือไม่?')) {
          localStorage.removeItem('studentId');
          localStorage.removeItem('userId');
          localStorage.removeItem('role');
          localStorage.removeItem('nameTh');
          localStorage.removeItem('nameEn');
          localStorage.removeItem('email');
          window.location.href = 'index.html';
        }
      });
    }

    loadAllEvents();

  });