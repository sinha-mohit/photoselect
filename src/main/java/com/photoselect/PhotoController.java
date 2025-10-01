package com.photoselect;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PhotoController {
    /**
     * Returns the count of selected photos in the destination folder.
     */
    @GetMapping("/selectedCount")
    public int getSelectedCount() {
        File dst = new File(destDir);
        String[] selected = Optional.ofNullable(dst.list((d, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png)$"))).orElse(new String[0]);
        return selected.length;
    }

    @Value("${photos.source}")
    private String sourceDir;

    @Value("${photos.dest}")
    private String destDir;

    private List<String> files = new ArrayList<>();

    @PostConstruct
    public void init() {
        File src = new File(sourceDir);
        File dst = new File(destDir);

        if (!dst.exists()) {
            dst.mkdirs();
        }

        // Get all source files (case-insensitive)
        files = Arrays.stream(
                        Optional.ofNullable(src.list((d, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png)$")))
                                .orElse(new String[0])
                )
                .sorted()
                .collect(Collectors.toList());

        System.out.println("Source: " + sourceDir);
        System.out.println("Destination: " + destDir);
        System.out.println("Loaded " + files.size() + " images (all source images)");
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
        File file = new File(sourceDir, files.get(index));
        return ResponseEntity.ok(new FileSystemResource(file));
    }

    @PostMapping("/select/{index}")
    public ResponseEntity<String> selectImage(@PathVariable int index) throws IOException {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }

        File src = new File(sourceDir, files.get(index));
        File dst = new File(destDir, files.get(index));

        if (!dst.exists()) {
            FileCopyUtils.copy(src, dst);
        }

        return ResponseEntity.ok("Copied: " + files.get(index));
    }

    /**
     * Returns true if the photo at the given index is present in the selected (destination) folder.
     */
    @GetMapping("/isSelected/{index}")
    public ResponseEntity<Boolean> isPhotoSelected(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.ok(false);
        }
        File dst = new File(destDir, files.get(index));
        return ResponseEntity.ok(dst.exists());
    }

    /**
     * Deletes the photo at the given index from the selected (destination) folder only.
     */
    @DeleteMapping("/selected/{index}")
    public ResponseEntity<String> deleteSelected(@PathVariable int index) {
        if (index < 0 || index >= files.size()) {
            return ResponseEntity.notFound().build();
        }
        File dst = new File(destDir, files.get(index));
        if (dst.exists()) {
            if (dst.delete()) {
                return ResponseEntity.ok("Deleted: " + files.get(index));
            } else {
                return ResponseEntity.status(500).body("Failed to delete: " + files.get(index));
            }
        } else {
            return ResponseEntity.status(404).body("File not found in selected folder: " + files.get(index));
        }
    }
}
