package br.com.zalf.prolog.webservice.entrega.escaladiaria;

import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.files.XlsxConverter;
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
 * Created on 11/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
class EscalaDiariaReader {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private EscalaDiariaReader() {
        throw new IllegalStateException(EscalaDiariaReader.class.getSimpleName() + " cannot be instantiated!");
    }

    static List<EscalaDiariaItem> readListFromCsvFilePath(@NotNull final File file) {
        final String extension = FilenameUtils.getExtension(file.getName());
        if (extension.equalsIgnoreCase("xlsx")) {
            try {
                new XlsxConverter().convertFileToCsv(file, 0, new SimpleDateFormat("dd/MM/yyyy"));
            } catch (final IOException ex) {
                throw new RuntimeException("Erro ao converter de XLSX para CSV", ex);
            }
        }

        final CsvParserSettings settings = new CsvParserSettings();
        settings.setDelimiterDetectionEnabled(true, ',', ';');
        settings.setHeaderExtractionEnabled(true);
        final CsvParser parser = new CsvParser(settings);
        final List<String[]> rows = parser.parseAll(file);

        final List<EscalaDiariaItem> escalaItens = new ArrayList<>();
        for (final String[] row : rows) {
            final EscalaDiariaItem item = read(row);
            if (item != null) {
                escalaItens.add(item);
            }
        }
        return escalaItens;
    }

    private static EscalaDiariaItem read(@NotNull final String[] linha) {
        if (linha[0].isEmpty()) {
            return null;
        }

        final EscalaDiariaItem item = new EscalaDiariaItem();
        // DATA DA ESCALA
        if (!linha[0].trim().isEmpty()) {
            item.setData(DateUtils.validateAndParse(linha[0].trim(), DATE_FORMAT));
        }
        // PLACA
        if (!linha[1].trim().replaceAll(" ", "").isEmpty()) {
            item.setPlaca(linha[1].trim().replaceAll(" ", "").toUpperCase());
        }
        // CODIGO DO MAPA
        if (linha[2] != null && !linha[2].trim().isEmpty()) {
            item.setCodMapa(Long.parseLong(linha[2].trim()));
        }
        // CPF MOTORISTA
        if (!linha[3].trim().replaceAll("[^\\d]", "").isEmpty()) {
            item.setCpfMotorista(Long.parseLong(linha[3].trim().replaceAll("[^\\d]", "")));
        }
        // CPF AJUDANTE 1
        // Caso seja enviado um XLSX, o parse acaba comprometendo células vazias (ou nulas) e elas não são mapeadas.
        // Isso faz com que tenhamos erro ao realizar o parse de algo opcional.
        if (linha.length >= 5
                && linha[4] != null
                && !linha[4].trim().replaceAll("[^\\d]", "").isEmpty()) {
            item.setCpfAjudante1(Long.parseLong(linha[4].trim().replaceAll("[^\\d]", "")));
        }
        // CPF AJUDANTE 2
        if (linha.length == 6
                && linha[5] != null
                && !linha[5].trim().replaceAll("[^\\d]", "").isEmpty()) {
            item.setCpfAjudante2(Long.parseLong(linha[5].trim().replaceAll("[^\\d]", "")));
        }
        return item;
    }
}
