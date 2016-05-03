package br.com.zalf.prolog.webservice;

import java.io.File;
import java.net.UnknownHostException;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * Created by liliani on 15/12/15.
 */
public class AmazonS3Utils {
	// Credencias do usuário prolog-web-user
    private static final String MY_ACCESS_KEY_ID = "AKIAI6KFIYRHPVSFDFUA";
    private static final String MY_SECRET_KEY = "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";
    private static AmazonS3Client amazonS3Client;

    public static PutObjectRequest createPutObjectRequest(
    		String bucketName, 
    		String objectName,
    		File file) {
        return new PutObjectRequest(bucketName, objectName, file);
    }

    // Exceção UnknownHostException é disparada caso não haja conexão com a internet
    public static void putObject(PutObjectRequest putObjectRequest) throws UnknownHostException {
        getS3Client().putObject(putObjectRequest);
    }

    public static String generateImageUrl(String bucketName, String objectName) {
        return getS3Client().getResourceUrl(bucketName, objectName);
    }

    private static AmazonS3Client getS3Client() {
        if (amazonS3Client == null) {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            // 60 segundos
            clientConfiguration.setSocketTimeout(60 * 1000); // milliseconds
            amazonS3Client = new AmazonS3Client(
            		new BasicAWSCredentials(MY_ACCESS_KEY_ID, MY_SECRET_KEY),
                    clientConfiguration);
            return amazonS3Client;
        }
        return amazonS3Client;
    }
}
