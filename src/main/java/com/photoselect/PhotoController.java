package com.photoselect;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PhotoController {

    @Value("${photos.source}")
    private String sourceDir;

    @Value("${photos.dest}")
    private String destDir;

    private List<String> files;

    @PostConstruct
    public void init() {
        File dir = new File(sourceDir);

        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("❌ Source directory not found: " + sourceDir);
            files = Collections.emptyList();
            return;
        }

        // Filter jpg, jpeg, png
        String[] arr = dir.list((d, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png)$"));
        if (arr == null) {
            files = Collections.emptyList();
        } else {
            files = Arrays.stream(arr).sorted().collect(Collectors.toList());
        }

        // Ensure destination exists
        File dest = new File(destDir);
        if (!dest.exists()) {
            dest.mkdirs();
        }

        System.out.println("Source: " + sourceDir);
        System.out.println("Destination: " + destDir);
        System.out.println("Loaded " + files.size() + " images");
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

        // Copy file, do not move
        FileCopyUtils.copy(src, dst);

        return ResponseEntity.ok("Copied: " + files.get(index));
    }
}
