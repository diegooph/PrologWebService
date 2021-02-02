package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.webservice.AmazonConstants;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.files.S3FileSender;
import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by web on 09/05/17.
 */
public class UploadTreinamentoHelper {
    private static final String TAG = UploadTreinamentoHelper.class.getSimpleName();
    private final PDFTransformer transformer;

    public UploadTreinamentoHelper(final PDFTransformer transformer) {
        this.transformer = transformer;
    }

    public Treinamento upload(final Treinamento treinamento, final InputStream inputStream) throws IOException,
            S3FileSender.S3FileSenderException {

        final S3FileSender fileSender = new S3FileSender(
                AmazonConstants.AWS_ACCESS_KEY_ID,
                AmazonConstants.AWS_SECRET_KEY);
        final String pdfName = TreinamentoHelper.createPDFFileName(treinamento);
        // Pasta temporária da JVM
        final File tmpDir = Files.createTempDir();

        // Envia arquivo
        final File pdfFile = createFile(tmpDir, inputStream, pdfName);
        fileSender.sendFile(AmazonConstants.BUCKET_NAME_PDF_TREINAMENTOS, pdfName, pdfFile);
        treinamento.setUrlArquivo(fileSender.generateFileUrl(AmazonConstants.BUCKET_NAME_PDF_TREINAMENTOS, pdfName));

        // Envia Imagens
        final List<String> urls = new ArrayList<>();
        final List<File> imagens = transformer.createImagesJPEG(tmpDir, pdfFile, pdfName);
        for (final File imagem : imagens) {
            fileSender.sendFile(AmazonConstants.BUCKET_NAME_IMAGES_TREINAMENTOS, imagem.getName(), imagem);
            final String imageUrl = fileSender.generateFileUrl(
                    AmazonConstants.BUCKET_NAME_IMAGES_TREINAMENTOS,
                    imagem.getName());
            urls.add(imageUrl);
            Log.d(TAG, "Imagem enviada: " + imageUrl);
        }
        treinamento.setUrlsImagensArquivo(urls);

        return treinamento;
    }

    @SuppressWarnings("Duplicates")
    private File createFile(final File directorySavePDF, final InputStream inputStream, final String pdfName) throws IOException {
        final File file = new File(directorySavePDF, pdfName);
        final FileOutputStream outputStream = new FileOutputStream(file);
        IOUtils.copy(inputStream, outputStream);
        IOUtils.closeQuietly(outputStream);
        return file;
    }
}
