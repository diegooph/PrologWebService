package br.com.zalf.prolog.webservice.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.base.Preconditions;

import java.io.File;

/**
 * Classe responsável por gerenciar a comunicação com a API da Amazon.
 */
public class S3FileSender {
    private static final String AWS_ACCESS_KEY_ID = "AKIAI6KFIYRHPVSFDFUA";
    private static final String AWS_SECRET_KEY = "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";
    private final String accessKeyId;
    private final String secretKey;
    private AmazonS3Client amazonS3Client;

    public static class S3FileSenderException extends Exception {
        S3FileSenderException() {
            super("Erro ao enviar arquivo para o S3");
        }
    }

    public S3FileSender(final String accessKeyId, final String secretKey) {
        Preconditions.checkNotNull(accessKeyId);
        Preconditions.checkNotNull(secretKey);

        this.accessKeyId = accessKeyId;
        this.secretKey = secretKey;
    }

    public void sendFile(final String bucketName, final String fileName, final File file) throws S3FileSenderException {
        Preconditions.checkNotNull(bucketName);
        Preconditions.checkNotNull(fileName);
        Preconditions.checkNotNull(file);

        PutObjectRequest put = new PutObjectRequest(bucketName, fileName, file);
        try {
            putObject(put);
        } catch (Exception e) {
            // Erro ao enviar arquivo, subimos o erro
            throw new S3FileSenderException();
        }
    }

    public String generateFileUrl(final String bucketName, final String fileName) {
        Preconditions.checkNotNull(bucketName);
        Preconditions.checkNotNull(fileName);

        return getS3Client().getResourceUrl(bucketName, fileName);
    }

    private void putObject(PutObjectRequest putObjectRequest) throws Exception {
        getS3Client().putObject(putObjectRequest);
    }

    private AmazonS3Client getS3Client() {
        if (amazonS3Client == null) {
            ClientConfiguration clientConfiguration = getClientConfiguration();
            return amazonS3Client = new AmazonS3Client(getAwsCredentials(), clientConfiguration);
        }
        return amazonS3Client;
    }

    private ClientConfiguration getClientConfiguration() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSocketTimeout(60 * 1000); // 60 segundos
        return clientConfiguration;
    }

    private AWSCredentials getAwsCredentials() {
        return new BasicAWSCredentials(accessKeyId, secretKey);
    }
}