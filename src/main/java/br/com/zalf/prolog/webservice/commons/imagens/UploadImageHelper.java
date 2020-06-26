package br.com.zalf.prolog.webservice.commons.imagens;

import br.com.zalf.prolog.webservice.AmazonConstants;
import br.com.zalf.prolog.webservice.commons.util.RandomUtils;
import br.com.zalf.prolog.webservice.commons.util.S3FileSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 22/03/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class UploadImageHelper {

    @NotNull
    public static ImagemProLog uploadCompressedImagem(@NotNull final InputStream inputStream,
                                                      @NotNull final String amazonBucket,
                                                      @NotNull final String imageType)
            throws IOException, S3FileSender.S3FileSenderException, FileFormatNotSupportException {
        final String imageName = createRandomImageNameWithExtension(imageType);
        final File compressFile = ImageCompressUtils.compressFile(inputStream, imageName, imageType);
        return internalImageSender(amazonBucket, imageName, compressFile);
    }

    @NotNull
    private static ImagemProLog internalImageSender(
            @NotNull final String amazonBucket,
            @NotNull final String imageName,
            @NotNull final File imageFile) throws S3FileSender.S3FileSenderException {
        final ImagemProLog imagemProLog = new ImagemProLog();
        final S3FileSender fileSender = new S3FileSender(
                AmazonConstants.AWS_ACCESS_KEY_ID,
                AmazonConstants.AWS_SECRET_KEY);
        fileSender.sendFile(amazonBucket, imageName, imageFile);
        imagemProLog.setUrlImagem(fileSender.generateFileUrl(amazonBucket, imageName));
        return imagemProLog;
    }

    @NotNull
    private static String createRandomImageNameWithExtension(@NotNull final String extension) {
        final String randomString = RandomUtils.randomAlphanumeric(40);
        return System.currentTimeMillis() + "_" + randomString + "." + extension;
    }
}
