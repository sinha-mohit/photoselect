// details.js - Category details page logic

async function fetchCategoryDetails() {
    try {
        const response = await fetch('/api/categoryDetails');
        return await response.json();
    } catch (error) {
        console.error('Failed to fetch category details:', error);
        return {};
    }
}

async function loadDetails() {
    const details = await fetchCategoryDetails();
    const categoriesGrid = document.getElementById('categoriesGrid');
    const summaryDiv = document.getElementById('summary');
    
    const categoryNames = {
        'haldi': 'Haldi',
        'mehendi': 'Mehendi',
        'tilak': 'Tilak',
        'jaimala': 'Jaimala',
        'shaadi': 'Shaadi',
        'vidai': 'Vidai',
        'barat': 'Barat',
        'matkor': 'Matkor'
    };
    
    let totalPhotos = 0;
    let summaryHTML = '<div class="summary-grid">';
    
    // Create category cards
    for (const [category, photos] of Object.entries(details)) {
        const categoryName = categoryNames[category] || category;
        const count = photos.length;
        totalPhotos += count;
        
        summaryHTML += `<div class="summary-item">
            <span class="summary-label">${categoryName}:</span>
            <span class="summary-count">${count}</span>
        </div>`;
        
        const card = document.createElement('div');
        card.className = 'category-card';
        card.innerHTML = `
            <div class="category-header">
                <h3>${categoryName}</h3>
                <span class="category-count">${count} photo${count !== 1 ? 's' : ''}</span>
            </div>
            <div class="photo-list">
                ${photos.length > 0 
                    ? photos.map((photo, index) => `
                        <div class="photo-item">
                            <span class="photo-seq">${index + 1}.</span>
                            <span class="photo-name">${photo.filename}</span>
                            <a href="/?photo=${photo.photoNumber}" class="photo-link" title="Jump to this photo">
                                Photo #${photo.photoNumber}
                            </a>
                        </div>
                    `).join('')
                    : '<div class="no-photos">No photos in this category</div>'
                }
            </div>
        `;
        categoriesGrid.appendChild(card);
    }
    
    summaryHTML += '</div>';
    summaryHTML += `<div class="summary-total">
        <span class="summary-label">Total Photos:</span>
        <span class="summary-count">${totalPhotos}</span>
    </div>`;
    
    summaryDiv.innerHTML = summaryHTML;
}

loadDetails();

