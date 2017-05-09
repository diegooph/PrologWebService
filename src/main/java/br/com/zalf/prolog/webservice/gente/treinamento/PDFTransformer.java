package br.com.zalf.prolog.webservice.gente.treinamento;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by web on 09/05/17.
 */
public class PDFTransformer {

    public List<File> createImagesPNG(InputStream file, String pdfFilename) throws IOException {
        final List<File> imagens = new ArrayList<>();
        final PDDocument document = PDDocument.load(file);
        final PDFRenderer pdfRenderer = new PDFRenderer(document);
        for (int page = 0; page < document.getNumberOfPages(); page++) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 85, ImageType.RGB);
            String fileName = pdfFilename + "-" + (page+1) + ".png";
            ImageIOUtil.writeImage(bim, fileName, 85);
            imagens.add(new File(fileName));
        }
        document.close();
        return imagens;
    }
}
