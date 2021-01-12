package br.com.zalf.prolog.webservice.raizen.produtividade.model.insert;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.files.XlsxConverter;
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
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

    private RaizenProdutividadeReader() {
        throw new IllegalStateException(RaizenProdutividadeReader.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static List<RaizenProdutividadeItemInsert> readListFromCsvFilePath(@NotNull final File file) {
        final String extension = FilenameUtils.getExtension(file.getName());
        if (extension.equalsIgnoreCase("xlsx")) {
            try {
                new XlsxConverter().convertFileToCsv(file, 0, new SimpleDateFormat("ddMMyyyy"));
            } catch (final IOException ex) {
                throw new RuntimeException("Erro ao converter de XLSX para CSV", ex);
            }
        }

        final CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        settings.setDelimiterDetectionEnabled(true, ',', ';');
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
            item.setDataViagem(DateUtils.validateAndParse(StringUtils.getOnlyNumbers(linha[2].trim()), DATE_FORMAT));
        }
        // VALOR
        if (!linha[3].trim().isEmpty()) {
            item.setValor(createBigDecimal(linha[3]));
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
            item.setRaioKm(createBigDecimal(linha[6]));
        }
        // TONELADA
        if (!linha[7].trim().isEmpty()) {
            item.setToneladas(createBigDecimal(linha[7]));
        }
        return item;
    }

    @NotNull
    private static BigDecimal createBigDecimal(@NotNull final String s) {
        final String regex = "[^0-9 .,]|(?<!\\d)[.,]|[.,](?!\\d)";
        // Se não tiver nenhuma vírgula, assumimos que já está formatado corretamente.
        if (!s.contains(",")) {
            return new BigDecimal(s
                    .replaceAll(regex, "")
                    .trim());
        }

        return new BigDecimal(s
                .replaceAll(regex, "")
                .replace(".", "")
                .replace(",", ".")
                .trim());
    }
}