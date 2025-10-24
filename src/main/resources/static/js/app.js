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
        await updateCategoryButtons();
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

async function copyToCategory(category) {
    try {
        const categoryCapitalized = category.charAt(0).toUpperCase() + category.slice(1);
        const apiFunctionName = `copyTo${categoryCapitalized}`;
        
        if (PhotoApi[apiFunctionName]) {
            const response = await PhotoApi[apiFunctionName](indexRef.value);
            if (response.ok) {
                updateStatus(`✅ Copied photo ${indexRef.value+1} to ${categoryCapitalized}`);
                // Update category counts
                await updateCategoryCounts();
                // Move to next photo
                indexRef.value++;
                if (indexRef.value < totalRef.value) {
                    loadPhoto();
                } else {
                    photoElem.src = "";
                    updateStatus("All photos done ✅");
                    toggleDeleteBtn(false);
                }
            } else {
                const errorText = await response.text();
                updateStatus(`❌ Error: ${errorText}`);
            }
        }
    } catch (error) {
        updateStatus(`❌ Failed to copy to ${category}: ${error.message}`);
    }
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

async function updateCategoryCounts() {
    try {
        const counts = await PhotoApi.getCategoryCounts();
        const countsDisplay = document.getElementById('categoryCounts');
        if (countsDisplay && counts) {
            const total = (counts.haldi || 0) + (counts.mehendi || 0) + (counts.tilak || 0) + 
                         (counts.jaimala || 0) + (counts.shaadi || 0) + (counts.vidai || 0) + 
                         (counts.barat || 0) + (counts.matkor || 0);
            
            countsDisplay.innerHTML = `
                <div class="count-title">Category Counts:</div>
                <div class="count-item">Haldi: ${counts.haldi || 0}</div>
                <div class="count-item">Mehendi: ${counts.mehendi || 0}</div>
                <div class="count-item">Tilak: ${counts.tilak || 0}</div>
                <div class="count-item">Jaimala: ${counts.jaimala || 0}</div>
                <div class="count-item">Shaadi: ${counts.shaadi || 0}</div>
                <div class="count-item">Vidai: ${counts.vidai || 0}</div>
                <div class="count-item">Barat: ${counts.barat || 0}</div>
                <div class="count-item">Matkor: ${counts.matkor || 0}</div>
                <div class="count-total">Total: ${total}</div>
            `;
        }
    } catch (error) {
        console.error('Failed to update category counts:', error);
    }
}

async function updateCategoryButtons() {
    const categories = ['haldi', 'mehendi', 'tilak', 'jaimala', 'shaadi', 'vidai', 'barat', 'matkor'];
    for (const category of categories) {
        const isInCategory = await PhotoApi.isInCategory(category, indexRef.value);
        const btn = document.getElementById(`${category}Btn`);
        if (btn) {
            const categoryCapitalized = category.charAt(0).toUpperCase() + category.slice(1);
            if (isInCategory) {
                btn.textContent = `Delete from ${categoryCapitalized}`;
                btn.classList.add('delete-mode');
                btn.setAttribute('data-mode', 'delete');
            } else {
                btn.textContent = categoryCapitalized;
                btn.classList.remove('delete-mode');
                btn.setAttribute('data-mode', 'copy');
            }
        }
    }
}

async function handleCategoryButtonClick(category) {
    const btn = document.getElementById(`${category}Btn`);
    const mode = btn.getAttribute('data-mode');
    
    if (mode === 'delete') {
        await deleteFromCategory(category);
    } else {
        await copyToCategory(category);
    }
}

async function deleteFromCategory(category) {
    try {
        const categoryCapitalized = category.charAt(0).toUpperCase() + category.slice(1);
        const response = await PhotoApi.deleteFromCategory(category, indexRef.value);
        if (response.ok) {
            updateStatus(`🗑️ Deleted photo ${indexRef.value+1} from ${categoryCapitalized}`);
            await updateCategoryCounts();
            await updateCategoryButtons();
        } else {
            const errorText = await response.text();
            updateStatus(`❌ Error: ${errorText}`);
        }
    } catch (error) {
        updateStatus(`❌ Failed to delete from ${category}: ${error.message}`);
    }
}

async function init() {
    totalRef.value = await PhotoApi.getTotalPhotos();
    setupJump(indexRef, totalRef, loadPhoto);
    await updateCategoryCounts();
    
    // Check if there's a photo number in URL
    const urlParams = new URLSearchParams(window.location.search);
    const photoParam = urlParams.get('photo');
    if (photoParam) {
        const photoNum = parseInt(photoParam, 10);
        if (!isNaN(photoNum) && photoNum >= 1 && photoNum <= totalRef.value) {
            indexRef.value = photoNum - 1; // Convert to 0-based index
        }
    }
    
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

// Wire up category buttons
const categoryButtons = document.querySelectorAll('.category-btn');
categoryButtons.forEach(btn => {
    btn.onclick = () => {
        const category = btn.getAttribute('data-category');
        handleCategoryButtonClick(category);
    };
});

init();
