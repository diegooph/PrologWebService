package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.insert;

import br.com.zalf.prolog.webservice.commons.util.XlsxConverter;
import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.VeiculoPlanilha;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeReader;
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
public class VeiculoPlanilhaReader {
    private VeiculoPlanilhaReader() {
        throw new IllegalStateException(RaizenProdutividadeReader.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static List<VeiculoPlanilha> readListFromCsvFilePath(@NotNull final File file) {
        final String extension = FilenameUtils.getExtension(file.getName());
        if (extension.equalsIgnoreCase("xlsx")) {
            try {
                new XlsxConverter().convertFileToCsv(file, 0, new SimpleDateFormat("ddMMyyyy"));
            } catch (final IOException ex) {
                throw new RuntimeException("Erro ao converter de XLSX para CSV", ex);
            }
        }

        final CsvParserSettings settings = new CsvParserSettings();
        settings.setDelimiterDetectionEnabled(true, ',', ';');
        settings.setHeaderExtractionEnabled(true);
        settings.setNumberOfRowsToSkip(0);
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
        if (linha[0].isEmpty()) {
            return null;
        }

        final VeiculoPlanilha item = new VeiculoPlanilha();
        // PLACA
        if (!linha[0].isEmpty()) {
            item.setPlaca(linha[0]);
        }
        // KM
        if (!linha[1].isEmpty()) {
            item.setKm(Long.parseLong(linha[1]));
        }
        // MARCA
        if (!linha[2].isEmpty()) {
            item.setMarca(linha[2]);
        }
        // MODELO
        if (!linha[3].isEmpty()) {
            item.setModelo(linha[3]);
        }
        // TIPO
        if (!linha[4].isEmpty()) {
            item.setTipo(linha[4]);
        }
        // DIAGRAMA
        if (!linha[5].isEmpty()) {
            item.setDiagrama(linha[5]);
        }
        return item;
    }
}
