package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.pneu;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.XlsxConverter;
import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.pneu._model.PneuPlanilha;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 19/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuPlanilhaReader {

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
        settings.setNumberOfRowsToSkip(12);
        final CsvParser parser = new CsvParser(settings);
        final List<String[]> rows = parser.parseAll(file);
        final List<PneuPlanilha> veiculoPlanilha = new ArrayList<>();
        for (final String[] row : rows) {
            final PneuPlanilha item = read(row);
            if (item != null) {
                veiculoPlanilha.add(item);
            }
        }
        return veiculoPlanilha;
    }

    private static PneuPlanilha read(@NotNull final String[] linha) {
        if (linha[1].isEmpty()) {
            return null;
        }
        final PneuPlanilha item = new PneuPlanilha();
        // NUMERO DE FOGO.
        if (!StringUtils.isNullOrEmpty(linha[1])) {
            item.setNumeroFogo(linha[1]);
        }
        // MARCA.
        if (!StringUtils.isNullOrEmpty(linha[4])) {
            item.setMarca(linha[4]);
        }
        // MODELO.
        if (!StringUtils.isNullOrEmpty(linha[5])) {
            item.setModelo(linha[5]);
        }
        // DOT.
        if (!StringUtils.isNullOrEmpty(linha[6])) {
            item.setDot(linha[6]);
        }
        // DIMENSÃO.
        if (!StringUtils.isNullOrEmpty(linha[7])) {
            item.setDimensao(linha[7]);
        }
        // PRESSÃO IDEAL.
        if (!StringUtils.isNullOrEmpty(linha[8])) {
            item.setPressaoIdeal(Double.parseDouble(linha[8]));
        }

        // QUANTIDADE DE SULCOS.
        if (!StringUtils.isNullOrEmpty(linha[9])) {
            item.setQtdSulcos(Integer.parseInt(linha[9]));
        }

        // ALTURA DOS SULCOS
        if (!StringUtils.isNullOrEmpty(linha[10])) {
            item.setAlturaSulcos(Double.parseDouble(linha[10]));
        }

        // VALOR DA COMPRA DO PNEU.
        if (!StringUtils.isNullOrEmpty(linha[11])) {
            item.setValorPneu(new BigDecimal(linha[11]));
        }

        // VALOR DA BANDA.
        if (!StringUtils.isNullOrEmpty(linha[12])) {
            item.setValorBanda(new BigDecimal(linha[12]));
        }

        // VIDA ATUAL.
        if (!StringUtils.isNullOrEmpty(linha[13])) {
            item.setVidaAtual(Integer.parseInt(linha[13]));
        }

        // MARCA DE BANDA.
        if (!StringUtils.isNullOrEmpty(linha[14])) {
            item.setMarcaBanda(linha[14]);
        }

        // MODELO DE BANDA.
        if (!StringUtils.isNullOrEmpty(linha[15])) {
            item.setModeloBanda(linha[15]);
        }

        // NUMERO DE SULCOS DE BANDA.
        if (!StringUtils.isNullOrEmpty(linha[16])) {
            item.setQtdSulcosBanda(Integer.parseInt(linha[16]));
        }

        // VIDAS TOTAL.
        if (!StringUtils.isNullOrEmpty(linha[17])) {
            item.setVidaTotal(Integer.parseInt(linha[17]));
        }

        // PNEU NOVO NUNCA RODADO.
        if (!StringUtils.isNullOrEmpty(linha[18])) {
            item.setPneuNovoNuncaRodado(linha[18]);
        }
        return item;
    }
}
