// photoApi.js - API layer for photo operations
const PhotoApi = {
    getTotalPhotos: async () => await fetchJson('/api/count'),
    getSelectedCount: async () => await fetchJson('/api/selectedCount'),
    isPhotoSelected: async idx => await fetchJson(`/api/isSelected/${idx}`),
    getImageUrl: idx => `/api/image/${idx}`,
    selectPhoto: async idx => await fetch(`/api/select/${idx}`, { method: "POST" }),
    deletePhoto: async idx => await fetch(`/api/selected/${idx}`, { method: "DELETE" }),
    copyToHaldi: async idx => await fetch(`/api/copyTo/haldi/${idx}`, { method: "POST" }),
    copyToMehendi: async idx => await fetch(`/api/copyTo/mehendi/${idx}`, { method: "POST" }),
    copyToTilak: async idx => await fetch(`/api/copyTo/tilak/${idx}`, { method: "POST" }),
    copyToJaimala: async idx => await fetch(`/api/copyTo/jaimala/${idx}`, { method: "POST" }),
    copyToShaadi: async idx => await fetch(`/api/copyTo/shaadi/${idx}`, { method: "POST" }),
    copyToVidai: async idx => await fetch(`/api/copyTo/vidai/${idx}`, { method: "POST" }),
    copyToBarat: async idx => await fetch(`/api/copyTo/barat/${idx}`, { method: "POST" }),
    copyToMatkor: async idx => await fetch(`/api/copyTo/matkor/${idx}`, { method: "POST" }),
    getCategoryCounts: async () => await fetchJson('/api/categoryCounts'),
    isInCategory: async (category, idx) => await fetchJson(`/api/isInCategory/${category}/${idx}`),
    deleteFromCategory: async (category, idx) => await fetch(`/api/deleteFrom/${category}/${idx}`, { method: "DELETE" })
};

async function fetchJson(url, options) {
    const res = await fetch(url, options);
    return await res.json();
}

export default PhotoApi;
