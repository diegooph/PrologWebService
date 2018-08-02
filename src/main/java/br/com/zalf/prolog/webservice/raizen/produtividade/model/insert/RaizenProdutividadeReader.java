package br.com.zalf.prolog.webservice.raizen.produtividade.model.insert;

import br.com.zalf.prolog.webservice.commons.util.XlsxConverter;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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

    @NotNull
    public static List<RaizenProdutividadeItemInsert> readListFromCsvFilePath(@NotNull final File file) {
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

        final List<RaizenProdutividadeItemInsert> raizenItensItens = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            final RaizenProdutividadeItemInsert item = read(rows.get(i));
            if (item != null) {
                //soma para ignorar o header da planilha e o índice que começa em 0.
                item.setLinha(i + 2);
                raizenItensItens.add(item);
            }
        }
        return raizenItensItens;
    }

    @Nullable
    private static RaizenProdutividadeItemInsert read(@NotNull final String[] linha) {
        if (linha[0].isEmpty()) {
            return null;
        }
        final RaizenProdutividadeItemInsert item = new RaizenProdutividadeItemInsert();
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
            item.setValor(new BigDecimal(linha[3].trim().replaceAll(",", ".")));
        }
        // USINA
        if (!linha[4].trim().isEmpty()) {
            item.setUsina(linha[4]);
        }
        // FAZENDA
        if (!linha[5].trim().isEmpty()) {
            item.setFazenda(linha[5]);
        }
        // RAIO
        if (!linha[6].trim().isEmpty()) {
            item.setRaioKm(new BigDecimal(linha[6].trim().replaceAll(",", ".")));
        }
        // TONELADA
        if (!linha[7].trim().isEmpty()) {
            item.setToneladas(new BigDecimal(linha[7].trim().replaceAll(",", ".")));
        }
        return item;
    }
}
