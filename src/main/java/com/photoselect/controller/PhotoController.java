package com.photoselect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.ok(new FileSystemResource(file));
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
}
