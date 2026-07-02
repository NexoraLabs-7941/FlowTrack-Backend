package com.nexoralabs.flowtrack.reports.infrastructure.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final String folder;
    private final boolean configured;

    public CloudinaryService(
            Cloudinary cloudinary,
            @Value("${cloudinary.folder:flowtrack/inventario-yolo}") String folder,
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret) {
        this.cloudinary = cloudinary;
        this.folder = folder;
        this.configured = cloudName != null && !cloudName.isBlank()
                && apiKey != null && !apiKey.isBlank()
                && apiSecret != null && !apiSecret.isBlank();
    }

    @SuppressWarnings("unchecked")
    public String uploadImage(MultipartFile file) throws IOException {
        if (!configured) {
            throw new IllegalStateException(
                    "Cloudinary no está configurado. Define CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY y CLOUDINARY_API_SECRET.");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("La imagen es obligatoria.");
        }

        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image");

        Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), options);
        Object secureUrl = result.get("secure_url");
        if (secureUrl == null) {
            throw new IOException("Cloudinary no devolvió la URL de la imagen.");
        }
        return secureUrl.toString();
    }
}
