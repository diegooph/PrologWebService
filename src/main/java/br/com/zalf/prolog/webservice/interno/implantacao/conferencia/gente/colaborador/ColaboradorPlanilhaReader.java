package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.gente.colaborador;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.XlsxConverter;
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
        final List<ColaboradorPlanilha> colaboradorPlanilha = new ArrayList<>();
        for (final String[] row : rows) {
            final ColaboradorPlanilha item = read(row);
            if (item != null) {
                colaboradorPlanilha.add(item);
            }
        }
        return colaboradorPlanilha;
    }

    private static ColaboradorPlanilha read(@NotNull final String[] linha) {
        if (linha[1].isEmpty() &&
                linha[2].isEmpty() &&
                linha[3].isEmpty() &&
                linha[4].isEmpty() &&
                linha[5].isEmpty() &&
                linha[6].isEmpty() &&
                linha[7].isEmpty() &&
                linha[8].isEmpty() &&
                linha[9].isEmpty() &&
                linha[10].isEmpty() &&
                linha[11].isEmpty() &&
                linha[12].isEmpty() &&
                linha[13].isEmpty()) {
            return null;
        }
        final ColaboradorPlanilha item = new ColaboradorPlanilha();
        // CPF.
        if (!StringUtils.isNullOrEmpty(linha[1])) {
            item.setCpf(linha[1]);
        }
        // PIS.
        if (!StringUtils.isNullOrEmpty(linha[2])) {
            item.setPis(linha[2]);
        }
        // NOME.
        if (!StringUtils.isNullOrEmpty(linha[3])) {
            item.setNome(linha[3]);
        }
        // DATA NASCIMENTO.
        if (!StringUtils.isNullOrEmpty(linha[4])) {
            item.setDataNascimento(linha[4]);
        }
        // DATA ADMISSAO.
        if (!StringUtils.isNullOrEmpty(linha[5])) {
            item.setDataAdmissao(linha[5]);
        }
        // MATRICULA PROMAX.
        if (!StringUtils.isNullOrEmpty(linha[6])) {
            item.setMatriculaPromax(linha[6]);
        }
        // MATRICULA PONTO.
        if (!StringUtils.isNullOrEmpty(linha[7])) {
            item.setMatriculaPonto(linha[7]);
        }
        // EQUIPE.
        if (!StringUtils.isNullOrEmpty(linha[8])) {
            item.setEquipe(linha[8]);
        }
        // SETOR.
        if (!StringUtils.isNullOrEmpty(linha[9])) {
            item.setSetor(linha[9]);
        }
        // FUNCAO.
        if (!StringUtils.isNullOrEmpty(linha[10])) {
            item.setFuncao(linha[10]);
        }
        // EMAIL.
        if (!StringUtils.isNullOrEmpty(linha[11])) {
            item.setEmail(linha[11]);
        }
        // TELEFONE.
        if (!StringUtils.isNullOrEmpty(linha[12])) {
            item.setTelefone(linha[12]);
        }
        // PAIS.
        if (!StringUtils.isNullOrEmpty(linha[13])) {
            item.setPais(linha[13]);
        }
        return item;
    }
}
