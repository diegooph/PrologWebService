package br.com.zalf.prolog.webservice.entrega.mapa;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;

/**
 * Created on 2020-05-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PlanilhaMapaReader {

    @NotNull
    public static List<String[]> readFromCsv(@NotNull final InputStream inputStream) {
        final CsvParserSettings settings = new CsvParserSettings();
        settings.setDelimiterDetectionEnabled(true, ',', ';');
        settings.setHeaderExtractionEnabled(true);
        final CsvParser parser = new CsvParser(settings);
        parser.parse(inputStream);
        return parser.parseAll(inputStream);
    }
}
