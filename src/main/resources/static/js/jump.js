// jump.js - Handles jump-to-photo UI logic
export function setupJump(indexRef, totalRef, loadPhotoFn) {
    const jumpInput = document.getElementById('jumpInput');
    const jumpBtn = document.getElementById('jumpBtn');

    jumpBtn.onclick = () => {
        const val = parseInt(jumpInput.value, 10);
        if (!isNaN(val) && val >= 1 && val <= totalRef.value) {
            indexRef.value = val - 1;
            loadPhotoFn();
        } else {
            jumpInput.classList.add('error');
            setTimeout(() => jumpInput.classList.remove('error'), 1200);
        }
    };

    jumpInput.onkeydown = (e) => {
        if (e.key === 'Enter') jumpBtn.onclick();
    };
}
