package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.gente.treinamento.Treinamento;
import br.com.zalf.prolog.webservice.util.L;
import br.com.zalf.prolog.webservice.util.S3FileSender;
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
    private static final String AWS_ACCESS_KEY_ID = "AKIAI6KFIYRHPVSFDFUA";
    private static final String AWS_SECRET_KEY = "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";
    private static final String BUCKET_NAME_PDF = "treinamentos-prolog/pdf";
    private static final String BUCKET_NAME_IMAGES = "treinamentos-prolog/images";
    private static final String TAG = UploadTreinamentoHelper.class.getSimpleName();
    private PDFTransformer transformer;

    public UploadTreinamentoHelper(PDFTransformer transformer) {
        this.transformer = transformer;
    }

    public Treinamento upload(Treinamento treinamento, InputStream inputStream) throws IOException,
            S3FileSender.S3FileSenderException {

        final S3FileSender fileSender = new S3FileSender(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        final String pdfName = TreinamentoHelper.createPDFFileName(treinamento);
        // Pasta temporária da JVM
        final File tmpDir = Files.createTempDir();

        // Envia arquivo
        File pdfFile = createFile(tmpDir, inputStream, pdfName);
        fileSender.sendFile(BUCKET_NAME_PDF, pdfName, pdfFile);
        treinamento.setUrlArquivo(fileSender.generateFileUrl(BUCKET_NAME_PDF, pdfName));

        // Envia Imagens
        final List<String> urls = new ArrayList<>();
        final List<File> imagens = transformer.createImagesJPEG(tmpDir, pdfFile, pdfName);
        for (File imagem : imagens) {
            fileSender.sendFile(BUCKET_NAME_IMAGES, imagem.getName(), imagem);
            final String imageUrl = fileSender.generateFileUrl(BUCKET_NAME_IMAGES, imagem.getName());
            urls.add(imageUrl);
            L.d(TAG, "Imagem enviada: " + imageUrl);
        }
        treinamento.setUrlsImagensArquivo(urls);

        return treinamento;
    }

    private File createFile(File directorySavePDF, InputStream inputStream, String pdfName) throws IOException {
        File file = new File(directorySavePDF, pdfName);
        FileOutputStream outputStream = new FileOutputStream(file);
        IOUtils.copy(inputStream, outputStream);
        IOUtils.closeQuietly(outputStream);
        return file;
    }
}
