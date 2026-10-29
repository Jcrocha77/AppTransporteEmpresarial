package com.bancoppel.transporte.report;

import java.io.IOException;
import java.nio.file.Files;
                try (var inputStream = image.getInputStream()) {
                    Files.copy(inputStream, uploadDirectory.resolve(storedName));
                }
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UploadStorageService {
    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif",
            "image/heic", ".heic",
            "image/heif", ".heif"
    );

    private final Path uploadDirectory;

    public UploadStorageService(@Value("${app.upload-dir}") String uploadDirectory) {
        this.uploadDirectory = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }

    public List<String> store(List<MultipartFile> images) {
        if (images.size() > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se permiten hasta 3 imágenes por reporte.");
        }

        List<String> storedNames = new ArrayList<>();
        try {
            List<MultipartFile> validImages = images.stream().filter(image -> !image.isEmpty()).toList();
            for (MultipartFile image : validImages) {
                String contentType = image.getContentType();
                if (!EXTENSIONS.containsKey(contentType == null ? "" : contentType.toLowerCase())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se aceptan imágenes JPEG, PNG, WebP, GIF o HEIC.");
                }
                if (image.getSize() > MAX_IMAGE_SIZE) {
                    throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Cada imagen debe pesar 10 MB o menos.");
                }
            }

            Files.createDirectories(uploadDirectory);
            for (MultipartFile image : validImages) {
                String contentType = image.getContentType();
                String extension = EXTENSIONS.get(contentType == null ? "" : contentType.toLowerCase());
                String storedName = UUID.randomUUID() + extension;
                Files.copy(image.getInputStream(), uploadDirectory.resolve(storedName));
                storedNames.add(storedName);
            }
            return storedNames;
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No fue posible guardar las imágenes.", exception);
        }
    }
}
