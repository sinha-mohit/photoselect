package com.photoselect.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import com.photoselect.exception.PhotoNotFoundException;
import com.photoselect.exception.PhotoOperationException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhotoService {
    private static final Logger logger = LoggerFactory.getLogger(PhotoService.class);
    @Value("${photos.source}")
    private String sourceDir;

    @Value("${photos.dest}")
    private String destDir;

    public List<String> getAllSourcePhotos() {
    File src = new File(sourceDir);
    String[] files = src.list((d, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png)$"));
    if (files == null) {
        logger.warn("No source images found in directory: {}", sourceDir);
        return Collections.emptyList();
    }
    logger.info("Loaded {} images from source directory {}", files.length, sourceDir);
    return Arrays.stream(files).sorted().collect(Collectors.toList());
    }

    public int getSelectedCount() {
    File dst = new File(destDir);
    String[] selected = dst.list((d, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png)$"));
    int count = selected == null ? 0 : selected.length;
    logger.info("Selected images count: {}", count);
    return count;
    }

    public boolean isPhotoSelected(String filename) {
    File dst = new File(destDir, filename);
    boolean exists = dst.exists();
    logger.debug("Photo '{}' selected: {}", filename, exists);
    return exists;
    }

    public File getPhotoFile(String filename) {
        File file = new File(sourceDir, filename);
        if (!file.exists()) {
            logger.warn("Requested photo not found: {}", filename);
            throw new PhotoNotFoundException("Photo not found: " + filename);
        }
        logger.debug("Returning photo file: {}", file.getAbsolutePath());
        return file;
    }

    public void selectPhoto(String filename) throws IOException {
        File src = new File(sourceDir, filename);
        File dst = new File(destDir, filename);
        if (!src.exists()) {
            logger.error("Source photo not found: {}", filename);
            throw new PhotoNotFoundException("Source photo not found: " + filename);
        }
        if (!dst.exists()) {
            try {
                FileCopyUtils.copy(src, dst);
                logger.info("Copied photo '{}' to selected folder.", filename);
            } catch (IOException e) {
                logger.error("Failed to copy photo '{}': {}", filename, e.getMessage());
                throw new PhotoOperationException("Failed to copy photo: " + filename);
            }
        } else {
            logger.info("Photo '{}' already exists in selected folder.", filename);
        }
    }

    public boolean deleteSelectedPhoto(String filename) {
        File dst = new File(destDir, filename);
        if (!dst.exists()) {
            logger.warn("Photo to delete not found in selected folder: {}", filename);
            throw new PhotoNotFoundException("Photo not found in selected folder: " + filename);
        }
        boolean deleted = dst.delete();
        if (deleted) {
            logger.info("Deleted photo '{}' from selected folder.", filename);
        } else {
            logger.error("Failed to delete photo '{}' from selected folder.", filename);
            throw new PhotoOperationException("Failed to delete photo: " + filename);
        }
        return deleted;
    }
}
