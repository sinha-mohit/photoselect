// app.js - Main application logic
import PhotoApi from './photoApi.js';
import { photoElem, statusElem, deleteBtn, fadeInPhoto, updateStatus, toggleDeleteBtn, setPhotoSrc } from './ui.js';

let index = 0;
let total = 0;

async function loadPhoto() {
    if (index >= 0 && index < total) {
        fadeInPhoto();
        const imgUrl = PhotoApi.getImageUrl(index);
        setPhotoSrc(imgUrl);
        const selectedCount = await PhotoApi.getSelectedCount();
        updateStatus(`Photo ${index+1} of ${total} | Selected: ${selectedCount}`);
        toggleDeleteBtn(await PhotoApi.isPhotoSelected(index));
    }
}

async function selectPhoto() {
    await PhotoApi.selectPhoto(index);
    updateStatus(`✅ Copied photo ${index+1}`);
    index++;
    if (index < total) {
        loadPhoto();
    } else {
        photoElem.src = "";
        updateStatus("All photos done ✅");
        toggleDeleteBtn(false);
    }
}

async function deletePhoto() {
    await PhotoApi.deletePhoto(index);
    updateStatus(`🗑️ Deleted photo ${index+1} from selected`);
    toggleDeleteBtn(false);
    loadPhoto();
}

function handleKeydown(e) {
    if (e.code === "ArrowRight") {
        index = Math.min(index + 1, total - 1);
        loadPhoto();
    } else if (e.code === "ArrowLeft") {
        index = Math.max(index - 1, 0);
        loadPhoto();
    } else if (e.code === "Space") {
        selectPhoto();
    }
}

async function init() {
    total = await PhotoApi.getTotalPhotos();
    if (total > 0) {
        loadPhoto();
    } else {
        updateStatus("All photos done ✅");
        photoElem.src = "";
        toggleDeleteBtn(false);
    }
}

document.addEventListener("keydown", handleKeydown);
deleteBtn.onclick = deletePhoto;

init();
