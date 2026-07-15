package com.ufide.practicasemanal.service;
/*
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;

/**
 * Wrapper sencillo de Firebase Storage para subir imagenes.
 *
 * Si NO existe firebase-key.json en resources/, el servicio queda inactivo
 * y la app sigue arrancando normalmente. Esto permite que los alumnos que no
 * hagan el bonus puedan correr la app sin errores.
 */
/*@Service
public class FirebaseService {

    @Value("${firebase.bucket:}")
    private String bucket;

    private boolean activo = false;

    @PostConstruct
    public void init() {
        try (InputStream key = new ClassPathResource("firebase-key.json").getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(key))
                    .setStorageBucket(bucket)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            activo = true;
            System.out.println("[Firebase] inicializado con bucket " + bucket);
        } catch (IOException ex) {
            System.out.println("[Firebase] NO inicializado (sin firebase-key.json). Bonus desactivado.");
        }
    }

    public boolean isActivo() {
        return activo;
    }

    /**
     * Sube el archivo a Firebase Storage y devuelve la URL publica.
     */
   /* public String subir(MultipartFile file) throws IOException {
        if (!activo) {
            throw new IllegalStateException("FirebaseService no esta inicializado");
        }
        String nombre = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Bucket b = StorageClient.getInstance().bucket();
        Blob blob = b.create(nombre, file.getBytes(), file.getContentType());
        // Hacer la imagen publica para que la pueda mostrar el navegador
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        return String.format("https://storage.googleapis.com/%s/%s", bucket, nombre);
    }
}*/
