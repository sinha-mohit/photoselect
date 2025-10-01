// photoApi.js - API layer for photo operations
const PhotoApi = {
    getTotalPhotos: async () => await fetchJson('/api/count'),
    getSelectedCount: async () => await fetchJson('/api/selectedCount'),
    isPhotoSelected: async idx => await fetchJson(`/api/isSelected/${idx}`),
    getImageUrl: idx => `/api/image/${idx}`,
    selectPhoto: async idx => await fetch(`/api/select/${idx}`, { method: "POST" }),
    deletePhoto: async idx => await fetch(`/api/selected/${idx}`, { method: "DELETE" })
};

async function fetchJson(url, options) {
    const res = await fetch(url, options);
    return await res.json();
}

export default PhotoApi;
