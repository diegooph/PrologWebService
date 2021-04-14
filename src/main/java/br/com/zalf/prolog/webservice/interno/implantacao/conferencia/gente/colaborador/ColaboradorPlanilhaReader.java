package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.gente.colaborador;

import br.com.zalf.prolog.webservice.commons.util.files.XlsxConverter;
import br.com.zalf.prolog.webservice.interno.implantacao.conferencia.gente.colaborador._model.ColaboradorPlanilha;
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
 * Created on 29/07/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class ColaboradorPlanilhaReader {
    public static final int NUMERO_COLUNAS_COM_DADOS = 14;

    private ColaboradorPlanilhaReader() {
        throw new IllegalStateException(ColaboradorPlanilhaReader.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static List<ColaboradorPlanilha> readListFromCsvFilePath(@NotNull final File file) {
        final String extension = FilenameUtils.getExtension(file.getName());
        if (extension.equalsIgnoreCase("xlsx")) {
            try {
                new XlsxConverter().convertFileToCsv(
                        file,
                        0,
                        NUMERO_COLUNAS_COM_DADOS,
                        new SimpleDateFormat("ddMMyyyy"));
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
        final List<ColaboradorPlanilha> colaboradores = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            final String[] row = rows.get(i);
            if (row != null) {
                read(row).ifPresent(colaboradores::add);
            } else {
                throw new IllegalStateException("Linha " + i + " nula!");
            }
        }
        return colaboradores;
    }

    @NotNull
    private static Optional<ColaboradorPlanilha> read(@NotNull final String[] linha) {
        if (isNullOrEmpty(linha[1]) &&
                isNullOrEmpty(linha[2]) &&
                isNullOrEmpty(linha[3]) &&
                isNullOrEmpty(linha[4]) &&
                isNullOrEmpty(linha[5]) &&
                isNullOrEmpty(linha[6]) &&
                isNullOrEmpty(linha[7]) &&
                isNullOrEmpty(linha[8]) &&
                isNullOrEmpty(linha[9]) &&
                isNullOrEmpty(linha[10]) &&
                isNullOrEmpty(linha[11]) &&
                isNullOrEmpty(linha[12]) &&
                isNullOrEmpty(linha[13])) {
            return Optional.empty();
        }
        final ColaboradorPlanilha item = new ColaboradorPlanilha();
        // CPF.
        if (!isNullOrEmpty(linha[1])) {
            item.setCpf(linha[1]);
        }
        // PIS.
        if (!isNullOrEmpty(linha[2])) {
            item.setPis(linha[2]);
        }
        // NOME.
        if (!isNullOrEmpty(linha[3])) {
            item.setNome(linha[3]);
        }
        // DATA NASCIMENTO.
        if (!isNullOrEmpty(linha[4])) {
            item.setDataNascimento(linha[4]);
        }
        // DATA ADMISSAO.
        if (!isNullOrEmpty(linha[5])) {
            item.setDataAdmissao(linha[5]);
        }
        // MATRICULA PROMAX.
        if (!isNullOrEmpty(linha[6])) {
            item.setMatriculaPromax(linha[6]);
        }
        // MATRICULA PONTO.
        if (!isNullOrEmpty(linha[7])) {
            item.setMatriculaPonto(linha[7]);
        }
        // EQUIPE.
        if (!isNullOrEmpty(linha[8])) {
            item.setEquipe(linha[8]);
        }
        // SETOR.
        if (!isNullOrEmpty(linha[9])) {
            item.setSetor(linha[9]);
        }
        // FUNCAO.
        if (!isNullOrEmpty(linha[10])) {
            item.setFuncao(linha[10]);
        }
        // EMAIL.
        if (!isNullOrEmpty(linha[11])) {
            item.setEmail(linha[11]);
        }
        // TELEFONE.
        if (!isNullOrEmpty(linha[12])) {
            item.setTelefone(linha[12]);
        }
        // PAIS.
        if (!isNullOrEmpty(linha[13])) {
            item.setPais(linha[13]);
        }
        return Optional.of(item);
    }
}
