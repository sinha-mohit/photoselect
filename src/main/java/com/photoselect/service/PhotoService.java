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

    @Value("${photos.dest.haldi}")
    private String haldiDir;

    @Value("${photos.dest.mehendi}")
    private String mehendiDir;

    @Value("${photos.dest.tilak}")
    private String tilakDir;

    @Value("${photos.dest.jaimala}")
    private String jaimalaDir;

    @Value("${photos.dest.shaadi}")
    private String shaadiDir;

    @Value("${photos.dest.vidai}")
    private String vidaiDir;

    @Value("${photos.dest.barat}")
    private String baratDir;

    @Value("${photos.dest.matkor}")
    private String matkorDir;

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

    public void copyToCategory(String filename, String categoryPath, String categoryName) {
        // Validate category path exists
        File categoryDir = new File(categoryPath);
        if (!categoryDir.exists() || !categoryDir.isDirectory()) {
            logger.error("Category directory does not exist: {}", categoryPath);
            throw new PhotoOperationException("Category directory '" + categoryName + "' does not exist at: " + categoryPath);
        }

        // Validate source file exists
        File src = new File(sourceDir, filename);
        if (!src.exists()) {
            logger.error("Source photo not found: {}", filename);
            throw new PhotoNotFoundException("Source photo not found: " + filename);
        }

        // Copy file to category folder (don't replace if exists)
        File dst = new File(categoryPath, filename);
        if (!dst.exists()) {
            try {
                FileCopyUtils.copy(src, dst);
                logger.info("Copied photo '{}' to category '{}' folder.", filename, categoryName);
            } catch (IOException e) {
                logger.error("Failed to copy photo '{}' to category '{}': {}", filename, categoryName, e.getMessage());
                throw new PhotoOperationException("Failed to copy photo to " + categoryName + ": " + filename);
            }
        } else {
            logger.info("Photo '{}' already exists in category '{}' folder.", filename, categoryName);
        }
    }

    public String getHaldiDir() {
        return haldiDir;
    }

    public String getMehendiDir() {
        return mehendiDir;
    }

    public String getTilakDir() {
        return tilakDir;
    }

    public String getJaimalaDir() {
        return jaimalaDir;
    }

    public String getShaadiDir() {
        return shaadiDir;
    }

    public String getVidaiDir() {
        return vidaiDir;
    }

    public String getBaratDir() {
        return baratDir;
    }

    public String getMatkorDir() {
        return matkorDir;
    }

    public int getCategoryCount(String categoryPath) {
        File dir = new File(categoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return 0;
        }
        String[] files = dir.list((d, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png)$"));
        return files == null ? 0 : files.length;
    }

    public boolean isPhotoInCategory(String filename, String categoryPath) {
        File file = new File(categoryPath, filename);
        boolean exists = file.exists();
        logger.debug("Photo '{}' in category: {}", filename, exists);
        return exists;
    }

    public boolean deleteFromCategory(String filename, String categoryPath, String categoryName) {
        File file = new File(categoryPath, filename);
        if (!file.exists()) {
            logger.warn("Photo to delete not found in {} folder: {}", categoryName, filename);
            throw new PhotoNotFoundException("Photo not found in " + categoryName + " folder: " + filename);
        }
        boolean deleted = file.delete();
        if (deleted) {
            logger.info("Deleted photo '{}' from {} folder.", filename, categoryName);
        } else {
            logger.error("Failed to delete photo '{}' from {} folder.", filename, categoryName);
            throw new PhotoOperationException("Failed to delete photo from " + categoryName + ": " + filename);
        }
        return deleted;
    }
}
