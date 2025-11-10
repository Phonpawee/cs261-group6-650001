document.addEventListener('DOMContentLoaded', () => {

  /* ---------- Logout Modal ---------- */
  const logoutBtn = document.querySelector('.logout');
  const logoutModal = document.getElementById('logoutModal');
  const closeLogout = document.querySelector('#logoutModal .close');
  const cancelLogout = document.getElementById('cancelLogout');
  const confirmLogout = document.getElementById('confirmLogout');

  if (logoutBtn && logoutModal) {
    logoutBtn.addEventListener('click', () => logoutModal.style.display = 'flex');
    closeLogout?.addEventListener('click', () => logoutModal.style.display = 'none');
    cancelLogout?.addEventListener('click', () => logoutModal.style.display = 'none');
    confirmLogout?.addEventListener('click', () => window.location.href = 'index.html');

    window.addEventListener('click', e => {
      if (e.target === logoutModal) logoutModal.style.display = 'none';
    });
  }

  /* ---------- Event Detail Modal ---------- */
  const eventModal = document.getElementById('eventDetailModal');
  const closeEventModal = document.querySelector('#eventDetailModal .close');
  const cancelEventModal = document.getElementById('closeEventModal');
  const registerEventBtn = document.getElementById('registerEvent');

  const registerBtns = document.querySelectorAll('.register-btn');
  registerBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      eventModal.style.display = 'flex';
    });
  });

  [closeEventModal, cancelEventModal].forEach(el => {
    el?.addEventListener('click', () => eventModal.style.display = 'none');
  });

  window.addEventListener('click', e => {
    if (e.target === eventModal) eventModal.style.display = 'none';
  });

  registerEventBtn?.addEventListener('click', () => {
    alert('✅ คุณได้ลงทะเบียนกิจกรรมนี้แล้ว!');
    eventModal.style.display = 'none';
  });

  /* ---------- Tabs Navigation ---------- */
  const tabs = document.querySelectorAll('.tab');
  const contents = document.querySelectorAll('.tab-content');

  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      // ปุ่ม active
      tabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');

      // ซ่อนทุก section
      contents.forEach(c => c.classList.remove('active'));

      // แสดง section ที่เลือก
      const target = document.getElementById(tab.dataset.target);
      target.classList.add('active');
    });
  });
  
  /* ---------- API & Event Search ---------- */
const API_URL = 'http://localhost:8081/api/events';

async function searchEvents() {
  const keyword = document.getElementById('searchInput').value.trim();
  const category = document.getElementById('categoryFilter').value;
  const dateFilter = document.getElementById('dateFilter').value;
  
  let url = `${API_URL}/advanced-search?`;
  
  if (keyword) url += `keyword=${encodeURIComponent(keyword)}&`;
  if (category) url += `category=${encodeURIComponent(category)}&`;
  
  if (dateFilter) {
    const { startDate, endDate } = getDateRange(dateFilter);
    if (startDate) url += `startDate=${startDate}&`;
    if (endDate) url += `endDate=${endDate}&`;
  }
  
  try {
    const response = await fetch(url);
    if (!response.ok) throw new Error('ไม่สามารถค้นหากิจกรรมได้');
    
    const events = await response.json();
    displayEvents(events);
  } catch (error) {
    console.error('Error:', error);
    displayError('เกิดข้อผิดพลาดในการค้นหา กรุณาลองใหม่อีกครั้ง');
  }
}

function getDateRange(filter) {
  const now = new Date();
  let startDate = null;
  let endDate = null;
  
  switch(filter) {
    case 'today':
      startDate = new Date(now.setHours(0, 0, 0, 0)).toISOString();
      endDate = new Date(now.setHours(23, 59, 59, 999)).toISOString();
      break;
      
    case 'this-week':
      const startOfWeek = new Date(now);
      startOfWeek.setDate(now.getDate() - now.getDay());
      startOfWeek.setHours(0, 0, 0, 0);
      
      const endOfWeek = new Date(startOfWeek);
      endOfWeek.setDate(startOfWeek.getDate() + 6);
      endOfWeek.setHours(23, 59, 59, 999);
      
      startDate = startOfWeek.toISOString();
      endDate = endOfWeek.toISOString();
      break;
      
    case 'next-week':
      const nextWeekStart = new Date(now);
      nextWeekStart.setDate(now.getDate() + (7 - now.getDay()));
      nextWeekStart.setHours(0, 0, 0, 0);
      
      const nextWeekEnd = new Date(nextWeekStart);
      nextWeekEnd.setDate(nextWeekStart.getDate() + 6);
      nextWeekEnd.setHours(23, 59, 59, 999);
      
      startDate = nextWeekStart.toISOString();
      endDate = nextWeekEnd.toISOString();
      break;
  }
  
  return { startDate, endDate };
}

function displayEvents(events) {
  const eventList = document.querySelector('.event-list');
  
  if (!events || events.length === 0) {
    eventList.innerHTML = '<div class="no-events"><p>ไม่พบกิจกรรม</p></div>';
    return;
  }
  
  eventList.innerHTML = events.map(event => {
    const seatsAvailable = event.maxParticipants - event.currentParticipants;
    const eventDate = new Date(event.eventDate);
    const formattedDate = eventDate.toLocaleDateString('th-TH', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
    const formattedTime = eventDate.toLocaleTimeString('th-TH', {
      hour: '2-digit',
      minute: '2-digit'
    });
    
    return `
      <div class="event-card">
        <h4>${event.name}</h4>
        <div class="category">${event.category}</div>
        <div class="event-details">
          <p>Date: ${formattedDate} at ${formattedTime}</p>
          <p>${event.currentParticipants}/${event.maxParticipants} registered</p>
          <p>${event.description || 'ไม่มีคำอธิบาย'}</p>
          <hr />
        </div>
        <div class="event-action-row">
          <p class="seat"><strong>${seatsAvailable} seats available</strong></p>
          <button class="register-btn"><strong>Register</strong></button>
        </div>
      </div>
    `;
  }).join('');
  
  document.querySelectorAll('.register-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      eventModal.style.display = 'flex';
    });
  });
}

function displayError(message) {
  const eventList = document.querySelector('.event-list');
  eventList.innerHTML = `<div class="error-message"><p>${message}</p></div>`;
}

async function loadAllEvents() {
  try {
    const response = await fetch(`${API_URL}`);
    const events = await response.json();
    displayEvents(events);
  } catch (error) {
    console.error('Error loading events:', error);
    displayError('ไม่สามารถโหลดกิจกรรมได้');
  }
}

loadAllEvents(); 

const searchInput = document.getElementById('searchInput');
searchInput.addEventListener('input', searchEvents);

const categoryFilter = document.getElementById('categoryFilter');
categoryFilter.addEventListener('change', searchEvents);

const dateFilter = document.getElementById('dateFilter');
dateFilter.addEventListener('change', searchEvents);
  
});
