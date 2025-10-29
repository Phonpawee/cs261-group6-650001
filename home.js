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
});
