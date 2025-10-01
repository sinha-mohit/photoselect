// ui.js - UI layer for DOM manipulation
export const photoElem = document.getElementById("photo");
export const statusElem = document.getElementById("status");
export const deleteBtn = document.getElementById("deleteBtn");

export function fadeInPhoto() {
    photoElem.style.opacity = 0;
    setTimeout(() => { photoElem.style.opacity = 1; }, 100);
}

export function setPhotoSrc(src) {
    photoElem.src = src;
    photoElem.onerror = function() {
        updateStatus('Failed to load image: ' + src);
        photoElem.alt = 'Image failed to load: ' + src;
    };
    photoElem.onload = function() {
        photoElem.alt = '';
    };
}

export function updateStatus(text) {
    const statusTextElem = document.getElementById("statusText");
    if (statusTextElem) {
        statusTextElem.textContent = text;
    } else {
        statusElem.textContent = text;
    }
}

export function toggleDeleteBtn(show) {
    if (show) {
        deleteBtn.classList.add("show");
    } else {
        deleteBtn.classList.remove("show");
    }
}
