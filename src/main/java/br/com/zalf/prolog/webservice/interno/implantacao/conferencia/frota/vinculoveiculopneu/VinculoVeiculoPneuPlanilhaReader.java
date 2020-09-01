package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.vinculoveiculopneu;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.XlsxConverter;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.vinculoveiculopneu._model.VinculoVeiculoPneu;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia.gente.colaborador.ColaboradorPlanilhaReader;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 31/08/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VinculoVeiculoPneuPlanilhaReader {
    public static final int NUMERO_COLUNAS_COM_DADOS = 4;

    private VinculoVeiculoPneuPlanilhaReader() {
        throw new IllegalStateException(ColaboradorPlanilhaReader.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static List<VinculoVeiculoPneu> readListFromCsvFilePath(@NotNull final File file) {
        final String extension = FilenameUtils.getExtension(file.getName());
        if (extension.equalsIgnoreCase("xlsx")) {
            try {
                new XlsxConverter().convertFileToCsv(
                        file,
                        3,
                        NUMERO_COLUNAS_COM_DADOS,
                        new SimpleDateFormat("ddMMyyyy"));
            } catch (final IOException ex) {
                throw new RuntimeException("Erro ao converter de XLSX para CSV", ex);
            }
        }
        final CsvParserSettings settings = new CsvParserSettings();
        settings.setDelimiterDetectionEnabled(true, ',', ';');
        settings.setHeaderExtractionEnabled(true);
        settings.setNumberOfRowsToSkip(8);
        final CsvParser parser = new CsvParser(settings);
        final List<String[]> rows = parser.parseAll(file);
        final List<VinculoVeiculoPneu> vinculoVeiculoPneu = new ArrayList<>();
        for (final String[] row : rows) {
            final VinculoVeiculoPneu item = read(row);
            if (item != null) {
                vinculoVeiculoPneu.add(item);
            }
        }
        return vinculoVeiculoPneu;
    }

    private static VinculoVeiculoPneu read(@NotNull final String[] linha) {
        if (linha[1].isEmpty() && linha[2].isEmpty() && linha[3].isEmpty()) {
            return null;
        }
        final VinculoVeiculoPneu item = new VinculoVeiculoPneu();
        // PLACA.
        if (!StringUtils.isNullOrEmpty(linha[1])) {
            item.setPlaca(linha[1]);
        }
        // NÚMERO DE FOGO.
        if (!StringUtils.isNullOrEmpty(linha[2])) {
            item.setNumeroFogo(linha[2]);
        }
        // NOMENCLATURA.
        if (!StringUtils.isNullOrEmpty(linha[3])) {
            item.setNomenclatura(linha[3]);
        }
        return item;
    }
}
