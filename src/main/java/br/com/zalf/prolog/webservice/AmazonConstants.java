package br.com.zalf.prolog.webservice;

/**
 * Created by webservice on 08/11/17.
 */
public class AmazonConstants {
    public static final String AWS_ACCESS_KEY_ID = "AKIAI6KFIYRHPVSFDFUA";
    public static final String AWS_SECRET_KEY = "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";

    private static final String BUCKET_TEST_TREINAMENTO = "prolog-teste/treinamento";
    private static final String BUCKET_TEST_CHECKLIST = "prolog-teste/checklist";
    public static final String BUCKET_NAME_PDF_TREINAMENTOS = BuildConfig.DEBUG
            ? BUCKET_TEST_TREINAMENTO
            : "treinamentos-prolog/pdf";
    public static final String BUCKET_NAME_IMAGES_TREINAMENTOS = BuildConfig.DEBUG
            ? BUCKET_TEST_TREINAMENTO
            : "treinamentos-prolog/images";
    public static final String BUCKET_CHECKLIST_GALERIA_IMAGENS = BuildConfig.DEBUG
            ? BUCKET_TEST_CHECKLIST
            : "checklist-imagens";

    private AmazonConstants() {
        throw new IllegalStateException(AmazonConstants.class.getSimpleName() + " cannot be instantiated!");
    }
}