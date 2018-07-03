package br.com.zalf.prolog.webservice.raizen.produtividade;

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
public class RaizenReader {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyy");

    private RaizenReader() {
        throw new IllegalStateException(Raizen.class.getSimpleName() + " cannot be instantiated!");
    }

    static List<Raizen> readListFromCsvFilePath(@NotNull final File file) {
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

        final List<Raizen> raizenItens = new ArrayList<>();
/*        for (final String[] row : rows) {
            final Raizen item = read(row);
            if (item != null) {
                raizenItens.add(item);
            }
        }*/
        return raizenItens;
    }
/*
    private static Raizen read(@NotNull final String[] linha) {
        if (linha[0].isEmpty()) {
            return null;
        }
        final Raizen item = new Raizen();
        // CPF MOTORISTA
        if (!linha[3].trim().replaceAll("[^\\d]", "").isEmpty()) {
            item.setCpf(Long.parseLong(linha[3].trim().replaceAll("[^\\d]", "")));
        }
        // PLACA
        if (!linha[1].trim().replaceAll(" ", "").isEmpty()) {
            item.setPlaca(linha[1].trim().replaceAll(" ", "").toUpperCase());
        }
        // DATA DA VIAGEM
        if (!linha[2].trim().isEmpty()) {
            item.setDataViagem(DateUtils.validateAndParse(linha[0].trim(), DATE_FORMAT));
        }

        return item;
    }*/
}
