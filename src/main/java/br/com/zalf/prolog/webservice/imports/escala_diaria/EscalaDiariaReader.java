package br.com.zalf.prolog.webservice.imports.escala_diaria;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 11/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
class EscalaDiariaReader {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private EscalaDiariaReader() {
        throw new IllegalStateException(EscalaDiariaReader.class.getSimpleName() + " cannot be instantiated!");
    }

    static List<EscalaDiariaItem> readListFromCsvFilePath(@NotNull final String path)
            throws IOException, ParseException {
        final List<EscalaDiariaItem> escalaItens = new ArrayList<>();
        final Reader in = new FileReader(path);
        final List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
        for (int i = 1; i < tabela.size(); i++) {
            final EscalaDiariaItem item = read(tabela.get(i));
            if (item != null) {
                escalaItens.add(item);
            }
        }
        return escalaItens;
    }

    private static EscalaDiariaItem read(@NotNull final CSVRecord linha) throws ParseException {
        if (linha.get(0).isEmpty()) {
            return null;
        }
        final EscalaDiariaItem item = new EscalaDiariaItem();
        // DATA DA ESCALA
        if (!linha.get(0).trim().isEmpty()) {
            final Date data = new Date(DATE_FORMAT.parse(linha.get(0).trim()).getTime());
            item.setData(data);
        }
        // PLACA
        if (!linha.get(1).trim().replaceAll(" ", "").isEmpty()) {
            item.setPlaca(linha.get(1).trim().replaceAll(" ", "").toUpperCase());
        }
        // CODIGO DO MAPA
        if (!linha.get(2).trim().isEmpty()) {
            item.setCodMapa(Integer.parseInt(linha.get(2).trim()));
        }
        // CPF MOTORISTA
        if (!linha.get(3).trim().replaceAll("[^\\d]", "").isEmpty()) {
            item.setCpfMotorista(Long.parseLong(linha.get(3).trim().replaceAll("[^\\d]", "")));
        }
        // CPF AJUDANTE 1
        if (!linha.get(4).trim().replaceAll("[^\\d]", "").isEmpty()) {
            item.setCpfAjudante1(Long.parseLong(linha.get(4).trim().replaceAll("[^\\d]", "")));
        }
        // CPF AJUDANTE 2
        if (!linha.get(5).trim().replaceAll("[^\\d]", "").isEmpty()) {
            item.setCpfAjudante2(Long.parseLong(linha.get(5).trim().replaceAll("[^\\d]", "")));
        }
        return item;
    }
}
