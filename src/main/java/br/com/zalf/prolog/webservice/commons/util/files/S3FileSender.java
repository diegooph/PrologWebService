package br.com.zalf.prolog.webservice.commons.util.files;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class S3FileSender {
    @NotNull
    private final String accessKeyId;
    @NotNull
    private final String secretKey;
    private AmazonS3 amazonS3Client;

    public S3FileSender(@NotNull final String accessKeyId, @NotNull final String secretKey) {
        this.accessKeyId = accessKeyId;
        this.secretKey = secretKey;
    }

    public void sendFile(@NotNull final String bucketName, @NotNull final String fileName, @NotNull final File file)
            throws S3FileSenderException {
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file);
        try {
            putObject(putObjectRequest);
        } catch (final Exception e) {
            throw new S3FileSenderException(e);
        }
    }

    @NotNull
    public String generateFileUrl(@NotNull final String bucketName, @NotNull final String fileName) {
        return getS3Client().getUrl(bucketName, fileName).toString();
    }

    private void putObject(@NotNull final PutObjectRequest putObjectRequest) {
        getS3Client().putObject(putObjectRequest);
    }

    @NotNull
    private AmazonS3 getS3Client() {
        if (amazonS3Client == null) {
            final ClientConfiguration clientConfiguration = getClientConfiguration();
            final AWSCredentials awsCreds = getAwsCredentials();
            amazonS3Client = AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Regions.SA_EAST_1)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .withClientConfiguration(clientConfiguration)
                    .build();
            return amazonS3Client;
        }
        return amazonS3Client;
    }

    @NotNull
    private ClientConfiguration getClientConfiguration() {
        final ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSocketTimeout(getSocketTimeoutInSeconds());
        return clientConfiguration;
    }

    private int getSocketTimeoutInSeconds() {
        return 60 * 1000;
    }

    @NotNull
    private AWSCredentials getAwsCredentials() {
        return new BasicAWSCredentials(accessKeyId, secretKey);
    }

    public static class S3FileSenderException extends Exception {
        S3FileSenderException(@NotNull final Exception e) {
            super("Erro ao enviar arquivo para o S3", e);
        }
    }
}