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

        final List<String> urls = new ArrayList<>();
        final String pdfName = TreinamentoHelper.createFileName(treinamento);
        final List<File> imagens = transformer.createImagesPNG(inputStream, pdfName);

        final S3FileSender fileSender = new S3FileSender(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        for (File imagem : imagens) {
            fileSender.sendFile(BUCKET_NAME_IMAGES, imagem.getName(), imagem);
            final String imageName = fileSender.generateFileUrl(BUCKET_NAME_IMAGES, imagem.getName());
            urls.add(imageName);
            L.d(TAG, "Imagem enviada: " + imageName);
        }
        treinamento.setUrlsImagensArquivo(urls);

        fileSender.sendFile(BUCKET_NAME_PDF, pdfName, createFile(inputStream, pdfName));
        treinamento.setUrlArquivo(fileSender.generateFileUrl(BUCKET_NAME_PDF, pdfName));
        return treinamento;
    }

    private File createFile(InputStream inputStream, String pdfName) throws IOException {
        // Pasta temporária da JVM
        File tmpDir = Files.createTempDir();
        File file = new File(tmpDir, pdfName);
        FileOutputStream outputStream = new FileOutputStream(file);
        IOUtils.copy(inputStream, outputStream);
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(outputStream);
        return file;
    }
}
