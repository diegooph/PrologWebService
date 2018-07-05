package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.XlsxConverter;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 03/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeReader {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyy");

    private RaizenProdutividadeReader() {
        throw new IllegalStateException(RaizenProdutividadeReader.class.getSimpleName() + " cannot be instantiated!");
    }

    static List<RaizenProdutividadeItem> readListFromCsvFilePath(@NotNull final File file) {
        final String extension = FilenameUtils.getExtension(file.getName());
        if (extension.equalsIgnoreCase("xlsx")) {
            try {
                new XlsxConverter().convertFileToCsv(file, 0, new SimpleDateFormat("dd/MM/yyyy"));
            } catch (final IOException ex) {
                throw new RuntimeException("Erro ao converter de XLSX para CSV", ex);
            }
        }

        final CsvParserSettings settings = new CsvParserSettings();
        settings.setDelimiterDetectionEnabled(true);
        settings.setHeaderExtractionEnabled(true);
        final CsvParser parser = new CsvParser(settings);
        final List<String[]> rows = parser.parseAll(file);

        final List<RaizenProdutividadeItem> raizenItensItens = new ArrayList<>();
        for (final String[] row : rows) {
            final RaizenProdutividadeItem item = read(row);
            if (item != null) {
                raizenItensItens.add(item);
            }
        }
        return raizenItensItens;
    }

    private static RaizenProdutividadeItem read(@NotNull final String[] linha) {
        if (linha[0].isEmpty()) {
            return null;
        }
        final RaizenProdutividadeItem item = new RaizenProdutividadeItem();
        // CPF MOTORISTA
        if (!linha[0].trim().replaceAll("[^\\d]", "").isEmpty()) {
            item.setCpfMotorista(Long.parseLong(linha[0].trim().replaceAll("[^\\d]", "")));
        }
        // PLACA
        if (!linha[1].trim().replaceAll(" ", "").isEmpty()) {
            item.setPlaca(linha[1].trim().replaceAll(" ", "").toUpperCase());
        }
        // DATA DA VIAGEM
        if (!linha[2].trim().isEmpty()) {
            item.setDataViagem(DateUtils.validateAndParse(linha[2].trim(), DATE_FORMAT));
        }
        // VALOR
        if (!linha[3].trim().isEmpty()) {
            item.setValor(Double.parseDouble(linha[3].trim().replaceAll(",", ".")));
        }
        //USINA
        if (!linha[4].trim().isEmpty()) {
            item.setUsina(linha[4]);
        }
        //FAZENDA
        if (!linha[5].trim().isEmpty()) {
            item.setFazenda(linha[5]);
        }
        //RAIO
        if (!linha[6].trim().isEmpty()) {
            item.setRaio(Double.parseDouble(linha[6].trim().replaceAll(",", ".")));
        }
        //TONELADA
        if (!linha[7].trim().isEmpty()) {
            item.setTonelada(Double.parseDouble(linha[7].trim().replaceAll(",", ".")));
        }
        return item;
    }
}
