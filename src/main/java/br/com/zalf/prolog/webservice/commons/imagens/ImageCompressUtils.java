package br.com.zalf.prolog.webservice.commons.imagens;

import br.com.zalf.prolog.webservice.commons.util.files.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created on 05/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ImageCompressUtils {

    private ImageCompressUtils() {
        throw new IllegalStateException(ImageCompressUtils.class.getSimpleName() + " cannot be instanciated!");
    }

    @NotNull
    public static File compressFile(@NotNull final InputStream inputStream,
                                    @NotNull final String imageName,
                                    @NotNull final String imageType) throws IOException, FileFormatNotSupportException {
        return compressFile(inputStream, imageName, imageType, 0.5F);
    }

    @NotNull
    public static File compressFile(@NotNull final InputStream inputStream,
                                    @NotNull final String imageName,
                                    @NotNull final String imageType,
                                    @NotNull final Float quality) throws IOException, FileFormatNotSupportException {
        final BufferedImage image = ImageIO.read(inputStream);
        if (image == null) {
            throw new FileFormatNotSupportException("O arquivo precisa ser uma imagem");
        }

        final File output = new File(FileUtils.getTempDir(), imageName);
        final OutputStream out = new FileOutputStream(output);

        final ImageWriter writer = ImageIO.getImageWritersByFormatName(imageType).next();
        final ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        writer.setOutput(ios);

        final ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }
        writer.write(null, new IIOImage(image, null, null), param);

        out.close();
        ios.close();
        writer.dispose();
        return output;
    }
}
