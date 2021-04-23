package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.pneu;

import br.com.zalf.prolog.webservice.commons.util.files.XlsxConverter;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.pneu._model.PneuPlanilha;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.zalf.prolog.webservice.commons.util.StringUtils.isNullOrEmpty;

/**
 * Created on 19/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuPlanilhaReader {

    private PneuPlanilhaReader() {
        throw new IllegalStateException(PneuPlanilhaReader.class.getSimpleName() + " cannot be instantiated!");
    }

    /**
     * @noinspection Duplicates
     */
    @NotNull
    public static List<PneuPlanilha> readListFromCsvFilePath(@NotNull final File file) {
        final String extension = FilenameUtils.getExtension(file.getName());
        if (extension.equalsIgnoreCase("xlsx")) {
            try {
                new XlsxConverter().convertFileToCsv(file, 1, new SimpleDateFormat("ddMMyyyy"));
            } catch (final IOException ex) {
                throw new RuntimeException("Erro ao converter de XLSX para CSV", ex);
            }
        }
        final CsvParserSettings settings = new CsvParserSettings();
        settings.setDelimiterDetectionEnabled(true, ',', ';');
        settings.setHeaderExtractionEnabled(true);
        settings.setNumberOfRowsToSkip(36);
        final CsvParser parser = new CsvParser(settings);
        final List<String[]> rows = parser.parseAll(file);
        final List<PneuPlanilha> pneus = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            final String[] row = rows.get(i);
            if (row != null) {
                read(row).ifPresent(pneus::add);
            } else {
                throw new IllegalStateException("Linha " + i + " nula!");
            }
        }
        return pneus;
    }

    @NotNull
    private static Optional<PneuPlanilha> read(@NotNull final String[] linha) {
        if (isNullOrEmpty(linha[1]) &&
                isNullOrEmpty(linha[4]) &&
                isNullOrEmpty(linha[5]) &&
                isNullOrEmpty(linha[6]) &&
                isNullOrEmpty(linha[7]) &&
                isNullOrEmpty(linha[8]) &&
                isNullOrEmpty(linha[9]) &&
                isNullOrEmpty(linha[10]) &&
                isNullOrEmpty(linha[11]) &&
                isNullOrEmpty(linha[12]) &&
                isNullOrEmpty(linha[13]) &&
                isNullOrEmpty(linha[14]) &&
                isNullOrEmpty(linha[15]) &&
                isNullOrEmpty(linha[16]) &&
                isNullOrEmpty(linha[17]) &&
                isNullOrEmpty(linha[18])) {
            return Optional.empty();
        }
        final PneuPlanilha item = new PneuPlanilha();
        // NUMERO DE FOGO.
        if (!isNullOrEmpty(linha[1])) {
            item.setNumeroFogo(linha[1]);
        }
        // MARCA.
        if (!isNullOrEmpty(linha[4])) {
            item.setMarca(linha[4]);
        }
        // MODELO.
        if (!isNullOrEmpty(linha[5])) {
            item.setModelo(linha[5]);
        }
        // DOT.
        if (!isNullOrEmpty(linha[6])) {
            item.setDot(linha[6]);
        }
        // DIMENSÃO.
        if (!isNullOrEmpty(linha[7])) {
            item.setDimensao(linha[7]);
        }
        // PRESSÃO IDEAL.
        if (!isNullOrEmpty(linha[8])) {
            item.setPressaoIdeal(linha[8]);
        }

        // QUANTIDADE DE SULCOS.
        if (!isNullOrEmpty(linha[9])) {
            item.setQtdSulcos(linha[9]);
        }

        // ALTURA DOS SULCOS
        if (!isNullOrEmpty(linha[10])) {
            item.setAlturaSulcos(linha[10]);
        }

        // VALOR DA COMPRA DO PNEU.
        if (!isNullOrEmpty(linha[11])) {
            item.setValorPneu(linha[11]);
        }

        // VALOR DA BANDA.
        if (!isNullOrEmpty(linha[12])) {
            item.setValorBanda(linha[12]);
        }

        // VIDA ATUAL.
        if (!isNullOrEmpty(linha[13])) {
            item.setVidaAtual(linha[13]);
        }

        // MARCA DE BANDA.
        if (!isNullOrEmpty(linha[14])) {
            item.setMarcaBanda(linha[14]);
        }

        // MODELO DE BANDA.
        if (!isNullOrEmpty(linha[15])) {
            item.setModeloBanda(linha[15]);
        }

        // NUMERO DE SULCOS DE BANDA.
        if (!isNullOrEmpty(linha[16])) {
            item.setQtdSulcosBanda(linha[16]);
        }

        // VIDAS TOTAL.
        if (!isNullOrEmpty(linha[17])) {
            item.setVidaTotal(linha[17]);
        }

        // PNEU NOVO NUNCA RODADO.
        if (!isNullOrEmpty(linha[18])) {
            item.setPneuNovoNuncaRodado(linha[18]);
        }
        return Optional.of(item);
    }
}
