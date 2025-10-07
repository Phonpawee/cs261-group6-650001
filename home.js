
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
