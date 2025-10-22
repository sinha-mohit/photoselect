package com.photoselect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.photoselect.service.PhotoService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class PhotoController {

    private final PhotoService photoService;
    private List<String> files = new ArrayList<>();

    @Autowired
    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
        this.files = photoService.getAllSourcePhotos();
    }

    @GetMapping("/selectedCount")
    public int getSelectedCount() {
        return photoService.getSelectedCount();
    }

    @GetMapping("/count")
    public int getCount() {
        return files.size();
    }

    @GetMapping("/image/{index}")
    public ResponseEntity<FileSystemResource> getImage(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        File file = photoService.getPhotoFile(files.get(index));
        FileSystemResource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "public, max-age=86400, must-revalidate"); // cache for 1 day
        headers.add(HttpHeaders.ETAG, String.valueOf(file.lastModified()));
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @PostMapping("/select/{index}")
    public ResponseEntity<String> selectImage(@PathVariable int index) throws IOException {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        photoService.selectPhoto(files.get(index));
        return ResponseEntity.ok("Copied: " + files.get(index));
    }

    @GetMapping("/isSelected/{index}")
    public ResponseEntity<Boolean> isPhotoSelected(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(photoService.isPhotoSelected(files.get(index)));
    }

    @DeleteMapping("/selected/{index}")
    public ResponseEntity<String> deleteSelected(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        boolean deleted = photoService.deleteSelectedPhoto(files.get(index));
        if (deleted) {
            return ResponseEntity.ok("Deleted: " + files.get(index));
        } else {
            return ResponseEntity.status(404).body("File not found in selected folder: " + files.get(index));
        }
    }

    @PostMapping("/copyTo/haldi/{index}")
    public ResponseEntity<String> copyToHaldi(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        photoService.copyToCategory(files.get(index), photoService.getHaldiDir(), "Haldi");
        return ResponseEntity.ok("Copied to Haldi: " + files.get(index));
    }

    @PostMapping("/copyTo/mehendi/{index}")
    public ResponseEntity<String> copyToMehendi(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        photoService.copyToCategory(files.get(index), photoService.getMehendiDir(), "Mehendi");
        return ResponseEntity.ok("Copied to Mehendi: " + files.get(index));
    }

    @PostMapping("/copyTo/tilak/{index}")
    public ResponseEntity<String> copyToTilak(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        photoService.copyToCategory(files.get(index), photoService.getTilakDir(), "Tilak");
        return ResponseEntity.ok("Copied to Tilak: " + files.get(index));
    }

    @PostMapping("/copyTo/jaimala/{index}")
    public ResponseEntity<String> copyToJaimala(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        photoService.copyToCategory(files.get(index), photoService.getJaimalaDir(), "Jaimala");
        return ResponseEntity.ok("Copied to Jaimala: " + files.get(index));
    }

    @PostMapping("/copyTo/shaadi/{index}")
    public ResponseEntity<String> copyToShaadi(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        photoService.copyToCategory(files.get(index), photoService.getShaadiDir(), "Shaadi");
        return ResponseEntity.ok("Copied to Shaadi: " + files.get(index));
    }

    @PostMapping("/copyTo/vidai/{index}")
    public ResponseEntity<String> copyToVidai(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        photoService.copyToCategory(files.get(index), photoService.getVidaiDir(), "Vidai");
        return ResponseEntity.ok("Copied to Vidai: " + files.get(index));
    }

    @PostMapping("/copyTo/barat/{index}")
    public ResponseEntity<String> copyToBarat(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        photoService.copyToCategory(files.get(index), photoService.getBaratDir(), "Barat");
        return ResponseEntity.ok("Copied to Barat: " + files.get(index));
    }

    @PostMapping("/copyTo/matkor/{index}")
    public ResponseEntity<String> copyToMatkor(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        photoService.copyToCategory(files.get(index), photoService.getMatkorDir(), "Matkor");
        return ResponseEntity.ok("Copied to Matkor: " + files.get(index));
    }

    @GetMapping("/categoryCounts")
    public ResponseEntity<java.util.Map<String, Integer>> getCategoryCounts() {
        java.util.Map<String, Integer> counts = new java.util.HashMap<>();
        counts.put("haldi", photoService.getCategoryCount(photoService.getHaldiDir()));
        counts.put("mehendi", photoService.getCategoryCount(photoService.getMehendiDir()));
        counts.put("tilak", photoService.getCategoryCount(photoService.getTilakDir()));
        counts.put("jaimala", photoService.getCategoryCount(photoService.getJaimalaDir()));
        counts.put("shaadi", photoService.getCategoryCount(photoService.getShaadiDir()));
        counts.put("vidai", photoService.getCategoryCount(photoService.getVidaiDir()));
        counts.put("barat", photoService.getCategoryCount(photoService.getBaratDir()));
        counts.put("matkor", photoService.getCategoryCount(photoService.getMatkorDir()));
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/isInCategory/{category}/{index}")
    public ResponseEntity<Boolean> isPhotoInCategory(@PathVariable String category, @PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.ok(false);
        }
        String categoryPath = getCategoryPath(category);
        if (categoryPath == null) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(photoService.isPhotoInCategory(files.get(index), categoryPath));
    }

    @DeleteMapping("/deleteFrom/{category}/{index}")
    public ResponseEntity<String> deleteFromCategory(@PathVariable String category, @PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        String categoryPath = getCategoryPath(category);
        if (categoryPath == null) {
            return ResponseEntity.badRequest().body("Invalid category: " + category);
        }
        String categoryName = category.substring(0, 1).toUpperCase() + category.substring(1);
        boolean deleted = photoService.deleteFromCategory(files.get(index), categoryPath, categoryName);
        if (deleted) {
            return ResponseEntity.ok("Deleted from " + categoryName + ": " + files.get(index));
        } else {
            return ResponseEntity.status(404).body("File not found in " + categoryName + " folder: " + files.get(index));
        }
    }

    private String getCategoryPath(String category) {
        return switch (category.toLowerCase()) {
            case "haldi" -> photoService.getHaldiDir();
            case "mehendi" -> photoService.getMehendiDir();
            case "tilak" -> photoService.getTilakDir();
            case "jaimala" -> photoService.getJaimalaDir();
            case "shaadi" -> photoService.getShaadiDir();
            case "vidai" -> photoService.getVidaiDir();
            case "barat" -> photoService.getBaratDir();
            case "matkor" -> photoService.getMatkorDir();
            default -> null;
        };
    }
}
