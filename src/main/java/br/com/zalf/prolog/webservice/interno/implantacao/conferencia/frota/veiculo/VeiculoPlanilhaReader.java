package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.files.XlsxConverter;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.veiculo._model.VeiculoPlanilha;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.io.FilenameUtils;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 24/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoPlanilhaReader {

    private VeiculoPlanilhaReader() {
        throw new IllegalStateException(VeiculoPlanilhaReader.class.getSimpleName() + " cannot be instantiated!");
    }

    /**
     * @noinspection Duplicates
     */
    @NotNull
    public static List<VeiculoPlanilha> readListFromCsvFilePath(@NotNull final File file) {
        final String extension = FilenameUtils.getExtension(file.getName());
        if (extension.equalsIgnoreCase("xlsx")) {
            try {
                new XlsxConverter().convertFileToCsv(file, 2, new SimpleDateFormat("ddMMyyyy"));
            } catch (final IOException ex) {
                throw new RuntimeException("Erro ao converter de XLSX para CSV", ex);
            }
        }
        final CsvParserSettings settings = new CsvParserSettings();
        settings.setDelimiterDetectionEnabled(true, ',', ';');
        settings.setHeaderExtractionEnabled(true);
        settings.setNumberOfRowsToSkip(14);
        final CsvParser parser = new CsvParser(settings);
        final List<String[]> rows = parser.parseAll(file);
        final List<VeiculoPlanilha> veiculoPlanilha = new ArrayList<>();
        for (final String[] row : rows) {
            final VeiculoPlanilha item = read(row);
            if (item != null) {
                veiculoPlanilha.add(item);
            }
        }
        return veiculoPlanilha;
    }

    private static VeiculoPlanilha read(@NotNull final String[] linha) {
        if (linha[1].isEmpty()) {
            return null;
        }
        final VeiculoPlanilha item = new VeiculoPlanilha();
        // PLACA.
        if (!StringUtils.isNullOrEmpty(linha[1])) {
            item.setPlaca(linha[1]);
        }
        // MARCA.
        if (!StringUtils.isNullOrEmpty(linha[2])) {
            item.setMarca(linha[2]);
        }
        // MODELO.
        if (!StringUtils.isNullOrEmpty(linha[3])) {
            item.setModelo(linha[3]);
        }
        // KM.
        if (!StringUtils.isNullOrEmpty(linha[4])) {
            item.setKm(Long.parseLong(linha[4]));
        }
        // TIPO.
        if (!StringUtils.isNullOrEmpty(linha[5])) {
            item.setTipo(linha[5]);
        }
        // DIAGRAMA.
        if (!StringUtils.isNullOrEmpty(linha[6])) {
            item.setQtdEixos(linha[6]);
        }
        // IDENTIFICADOR FROTA.
        item.setIdentificadorFrota(linha[7]);

        // POSSUI HUBODOMETRO.
        item.setPossuiHubodometro(linha[8]);
        return item;
    }
}
