package br.com.zalf.prolog.webservice.gente.treinamento;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by web on 09/05/17.
 */
public class PDFTransformer {

    public List<File> createImagesPNG(File directorySaveImages, File pdf, String pdfFilename) throws IOException {
        return createImages(directorySaveImages, pdf, pdfFilename, "png");
    }

    public List<File> createImagesJPEG(File directorySaveImages, File pdf, String pdfFilename) throws IOException {
        return createImages(directorySaveImages, pdf, pdfFilename, "jpeg");
    }

    private List<File> createImages(File directorySaveImages, File pdf, String pdfFilename, String formato) throws IOException {
        final List<File> imagens = new ArrayList<>();
        final PDDocument document = PDDocument.load(pdf);
        final PDFRenderer pdfRenderer = new PDFRenderer(document);
        // Remove a extensão caso exista para não ser incluída no meio do nome da imagem
        pdfFilename = pdfFilename.replace(".pdf", "");
        for (int page = 0; page < document.getNumberOfPages(); page++) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 85, ImageType.RGB);
            String fileName = pdfFilename + "_" + (page+1) + "." + formato;
            File file = new File(directorySaveImages, fileName);
            ImageIO.write(bim, formato, file);
            imagens.add(file);
        }
        document.close();
        return imagens;
    }
}
