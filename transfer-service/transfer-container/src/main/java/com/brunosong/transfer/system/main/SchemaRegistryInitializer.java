package com.brunosong.transfer.system.main;

import com.brunosong.transfer.system.kafka.config.data.KafkaConfigData;
import com.brunosong.transfer.system.main.config.TransferServiceProperties;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class SchemaRegistryInitializer {

    private final ResourceLoader resourceLoader;
    private final KafkaConfigData kafkaConfigData;
    private final TransferServiceProperties transferServiceProperties;

    @PostConstruct
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public void registerSchema() throws Exception {
        CachedSchemaRegistryClient client = new CachedSchemaRegistryClient(kafkaConfigData.getSchemaRegistryUrl(), 100);

        for (String schemaPath : transferServiceProperties.getDataMigrationSchemaSubjects()) {
            String resourcePath = "classpath:" + schemaPath;

            Resource resource = resourceLoader.getResource(resourcePath);
            if (!resource.exists()) {
                throw new IllegalStateException("Schema file not found: " + resourcePath);
            }
            try (InputStream inputStream = resource.getInputStream()) {
                String schemaString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                registerSchema(client, schemaString, schemaPath);
            }

        }
    }

    public String getSubjectNameFromAvroFile(String avroFilePath) {

        String avroFileName = avroFilePath.substring(avroFilePath.lastIndexOf("/") + 1).replace(".avsc", "");

        avroFileName = avroFileName.replace("_","-");
        avroFileName += "-value";
        if (avroFileName == null) {
            throw new IllegalArgumentException("No subject found for Avro file: " + avroFilePath);
        }
        return avroFileName;
    }

    private void registerSchema(CachedSchemaRegistryClient client, String schemaString, String schemaPath) throws Exception {
        Schema schema = new Schema.Parser().parse(schemaString);
        String subject = getSubjectNameFromAvroFile(schemaPath);

        String schemaHash = calculateSchemaHash(schemaString);

        try {
            SchemaMetadata metadata = client.getLatestSchemaMetadata(subject);
            String existingSchema = metadata.getSchema();
            String existingSchemaHash = calculateSchemaHash(existingSchema);

            if (Objects.equals(schemaHash, existingSchemaHash)) {
                log.info("Schema already registered for subject {} with ID: {} ", subject , metadata.getId());
                return;
            }
        } catch (Exception e) {
            log.info("No existing schema found for subject {} or error occurred: {}", subject , e.getMessage());
        }

        int schemaId = client.register(subject, schema);
        log.info("Schema registered for subject {} with ID: {} " , subject, schemaId);
    }

    private String calculateSchemaHash(String schemaString) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(schemaString.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
