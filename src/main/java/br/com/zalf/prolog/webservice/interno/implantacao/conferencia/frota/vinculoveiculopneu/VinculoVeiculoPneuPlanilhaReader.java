package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.vinculoveiculopneu;

import br.com.zalf.prolog.webservice.commons.util.files.XlsxConverter;
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
import java.util.Optional;

import static br.com.zalf.prolog.webservice.commons.util.StringUtils.isNullOrEmpty;

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
        final List<VinculoVeiculoPneu> vinculos = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            final String[] row = rows.get(i);
            if (row != null) {
                read(row).ifPresent(vinculos::add);
            } else {
                throw new IllegalStateException("Linha " + i + " nula!");
            }
        }
        return vinculos;
    }

    @NotNull
    private static Optional<VinculoVeiculoPneu> read(@NotNull final String[] linha) {
        if (isNullOrEmpty(linha[1]) &&
                isNullOrEmpty(linha[2]) &&
                isNullOrEmpty(linha[3])) {
            return Optional.empty();
        }
        final VinculoVeiculoPneu item = new VinculoVeiculoPneu();
        // PLACA.
        if (!isNullOrEmpty(linha[1])) {
            item.setPlaca(linha[1]);
        }
        // NÚMERO DE FOGO.
        if (!isNullOrEmpty(linha[2])) {
            item.setNumeroFogo(linha[2]);
        }
        // NOMENCLATURA.
        if (!isNullOrEmpty(linha[3])) {
            item.setNomenclatura(linha[3]);
        }
        return Optional.of(item);
    }
}
