// app.js - Main application logic
import PhotoApi from './photoApi.js';
import { photoElem, statusElem, deleteBtn, fadeInPhoto, updateStatus, toggleDeleteBtn, setPhotoSrc } from './ui.js';
import { setupJump } from './jump.js';

const indexRef = { value: 0 };
const totalRef = { value: 0 };

async function loadPhoto() {
    if (indexRef.value >= 0 && indexRef.value < totalRef.value) {
        fadeInPhoto();
        const imgUrl = PhotoApi.getImageUrl(indexRef.value);
        setPhotoSrc(imgUrl);
        const selectedCount = await PhotoApi.getSelectedCount();
        updateStatus(`Photo ${indexRef.value+1} of ${totalRef.value} | Selected: ${selectedCount}`);
        toggleDeleteBtn(await PhotoApi.isPhotoSelected(indexRef.value));
    }
}

async function selectPhoto() {
    await PhotoApi.selectPhoto(indexRef.value);
    updateStatus(`✅ Copied photo ${indexRef.value+1}`);
    indexRef.value++;
    if (indexRef.value < totalRef.value) {
        loadPhoto();
    } else {
        photoElem.src = "";
        updateStatus("All photos done ✅");
        toggleDeleteBtn(false);
    }
}

async function deletePhoto() {
    await PhotoApi.deletePhoto(indexRef.value);
    updateStatus(`🗑️ Deleted photo ${indexRef.value+1} from selected`);
    toggleDeleteBtn(false);
    loadPhoto();
}

function handleKeydown(e) {
    if (e.code === "ArrowRight") {
        indexRef.value = Math.min(indexRef.value + 1, totalRef.value - 1);
        loadPhoto();
    } else if (e.code === "ArrowLeft") {
        indexRef.value = Math.max(indexRef.value - 1, 0);
        loadPhoto();
    } else if (e.code === "Space") {
        selectPhoto();
    }
}

async function init() {
    totalRef.value = await PhotoApi.getTotalPhotos();
    setupJump(indexRef, totalRef, loadPhoto);
    if (totalRef.value > 0) {
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
