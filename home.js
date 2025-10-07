
document.addEventListener('DOMContentLoaded', () => {
    const logoutBtn = document.querySelector('.logout');
    const modal = document.getElementById('logoutModal');
    const closeModal = document.querySelector('.modal .close');
    const cancelBtn = document.getElementById('cancelLogout');
    const confirmBtn = document.getElementById('confirmLogout');

    if(!logoutBtn || !modal || !closeModal || !cancelBtn || !confirmBtn){
        console.error('Element ของ logout ไม่ถูกต้อง ตรวจสอบ HTML ให้ตรงกับ JS');
        return;
    }

    logoutBtn.addEventListener('click', () => modal.style.display = 'flex');
    closeModal.addEventListener('click', () => modal.style.display = 'none');
    cancelBtn.addEventListener('click', () => modal.style.display = 'none');
    confirmBtn.addEventListener('click', () => window.location.href = 'login.html');

    window.addEventListener('click', e => {
        if(e.target === modal) modal.style.display = 'none';
    });
});
document.addEventListener('DOMContentLoaded', () => {
  const logoutBtn = document.querySelector('.logout');
  const logoutModal = document.getElementById('logoutModal');
  const closeLogout = document.querySelector('#logoutModal .close');
  const cancelLogout = document.getElementById('cancelLogout');
  const confirmLogout = document.getElementById('confirmLogout');

  logoutBtn.addEventListener('click', () => logoutModal.style.display = 'flex');
  closeLogout.addEventListener('click', () => logoutModal.style.display = 'none');
  cancelLogout.addEventListener('click', () => logoutModal.style.display = 'none');
  confirmLogout.addEventListener('click', () => window.location.href = 'login.html');

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
    el.addEventListener('click', () => eventModal.style.display = 'none');
  });

  window.addEventListener('click', e => {
    if (e.target === eventModal) eventModal.style.display = 'none';
  });

  registerEventBtn.addEventListener('click', () => {
    alert('✅ คุณได้ลงทะเบียนกิจกรรมนี้แล้ว!');
    eventModal.style.display = 'none';
  });
});
