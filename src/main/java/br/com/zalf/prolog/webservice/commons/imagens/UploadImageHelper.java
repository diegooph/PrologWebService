package br.com.zalf.prolog.webservice.commons.imagens;

import br.com.zalf.prolog.webservice.AmazonConstants;
import br.com.zalf.prolog.webservice.commons.util.RandomUtils;
import br.com.zalf.prolog.webservice.commons.util.S3FileSender;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 22/03/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class UploadImageHelper {

    public static ImagemProLog uploadImagem(@NotNull final ImagemProLog imagemProLog,
                                            @NotNull final InputStream inputStream,
                                            @NotNull final String amazonBucket)
            throws IOException, S3FileSender.S3FileSenderException {

        final S3FileSender fileSender = new S3FileSender(
                AmazonConstants.AWS_ACCESS_KEY_ID,
                AmazonConstants.AWS_SECRET_KEY);
        final String imageName = createRandomImageName();
        // Pasta temporária da JVM
        final File tmpDir = Files.createTempDir();
        final File imageFile = createImageFile(tmpDir, inputStream, imageName);
        final String mimeType = java.nio.file.Files.probeContentType(imageFile.toPath());
        if (mimeType == null || !mimeType.split("/")[0].equals("image")) {
            throw new IllegalArgumentException("Arquivo recebido não é uma imagem");
        }
        fileSender.sendFile(amazonBucket, imageName, imageFile);
        imagemProLog.setUrlImagem(fileSender.generateFileUrl(amazonBucket, imageName));
        return imagemProLog;
    }

    @NotNull
    private static String createRandomImageName() {
        final String randomString = RandomUtils.randomAlphanumeric(16);
        return System.currentTimeMillis() + "_" + randomString;
    }

    @SuppressWarnings("Duplicates")
    private static File createImageFile(@NotNull final File directory,
                                        @NotNull final InputStream inputStream,
                                        @NotNull final String imageName) throws IOException {
        final File file = new File(directory, imageName);
        final FileOutputStream outputStream = new FileOutputStream(file);
        IOUtils.copy(inputStream, outputStream);
        IOUtils.closeQuietly(outputStream);
        return file;
    }
}
