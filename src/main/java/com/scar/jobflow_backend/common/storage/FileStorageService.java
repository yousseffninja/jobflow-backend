package com.scar.jobflow_backend.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String upload(MultipartFile file, String folder);
    void delete(String publicId);
}