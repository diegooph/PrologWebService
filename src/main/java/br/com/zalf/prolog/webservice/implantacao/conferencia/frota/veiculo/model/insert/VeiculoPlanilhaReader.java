package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.insert;

import br.com.zalf.prolog.webservice.commons.util.XlsxConverter;
import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.VeiculoPlanilha;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class VeiculoPlanilhaReader {

    @NotNull
    public static List<VeiculoPlanilha> readListFromCsvFilePath(@NotNull final File file) {
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

        final List<VeiculoPlanilha> veiculoPlanilha = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            final VeiculoPlanilha item = read(rows.get(i));
            if (item != null) {
                //soma para ignorar o header da planilha e o índice que começa em 0.
                item.setLinha(i + 2);
                veiculoPlanilha.add(item);
            }
        }
        return veiculoPlanilha;


    }

    @Nullable
    private static VeiculoPlanilha read(@NotNull final String[] linha) {
        if (linha[0].isEmpty()) {
            return null;
        }
        final VeiculoPlanilha item = new VeiculoPlanilha();
        // PLACA VEICULO
        if (!linha[0].isEmpty()) {
            item.setPlaca(linha[0]);
        }
        // MARCA VEICULO
        if (!linha[1].isEmpty()) {
            item.setMarca(linha[1]);
        }
        //  MODELO VEICULO
        if (!linha[2].isEmpty()) {
            item.setModelo(linha[2]);
        }
        // KM VEICULO
        if (!linha[3].isEmpty()) {
            item.setKm(Long.parseLong(linha[3]));
        }
        // TIPO VEICULO
        if (!linha[4].isEmpty()) {
            item.setTipo(linha[4]);
        }

        System.out.println(item.getPlaca());

        return item;
    }


}
