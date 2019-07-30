package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.insert;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.util.Files;
import br.com.zalf.prolog.webservice.commons.util.XlsxConverter;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeReader;

import com.google.common.base.Charsets;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.validation.constraints.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created on 24/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoPlanilhaReader {
    private VeiculoPlanilhaReader() {
        throw new IllegalStateException(RaizenProdutividadeReader.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static String readListFromCsvFilePath(@NotNull final File file) {
        final String extension = FilenameUtils.getExtension(file.getName());
        if (true) {
            try {
                new XlsxConverter().convertFileToCsv(file, 0, new SimpleDateFormat("ddMMyyyy"));
            } catch (final IOException ex) {
                throw new RuntimeException("Erro ao converter de XLSX para CSV", ex);
            }
        }

        final CsvParserSettings settings = new CsvParserSettings();
        settings.setDelimiterDetectionEnabled(true, ',', ';');
        settings.setHeaderExtractionEnabled(true);
        settings.setNumberOfRowsToSkip(1);
        final CsvParser parser = new CsvParser(settings);
        final List<String[]> rows = parser.parseAll(file);

        String jsonPlanilha = GsonUtils.getGson().toJson(rows);
//        try {
//            String dados = String.valueOf(java.nio.file.Files.readAllLines(Paths.get(nossoJson)));
//            System.out.println(dados);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String dados = new String(Files.readAllBytes(file.toPath()));
        try {
            final File json = Files.createTempDir();
            json.createNewFile();
            IOUtils.write(GsonUtils.getGson().toJson(rows), new FileOutputStream(json), Charsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonPlanilha;
    }
}
