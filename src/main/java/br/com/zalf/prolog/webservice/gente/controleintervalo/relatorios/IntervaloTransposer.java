package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Transposer;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 19/07/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class IntervaloTransposer extends Transposer {

    private static final int COLUNA_CPF_COLABORADOR = 1;
    private static final int COLUNA_NOME_COLABORADOR = 2;
    private static final int COLUNA_CARGO_COLABORADOR = 3;
    private static final String ZERO_HORAS = "00:00:00";
    private List<String> header;
    private Map<String, List<String>> table;

    public IntervaloTransposer(@NotNull final ResultSet rSet) {
        super(rSet);
    }

    @Override
    public List<String> getHeader() throws SQLException {
        if (header == null) {
            processResultSet();
        }
        return header;
    }

    @Override
    public Iterable<?> transpose() throws SQLException {
        if (table == null) {
            processResultSet();
        }
        return table.values();
    }

    private void processResultSet() throws SQLException {
        if (rSet.isClosed()) {
            throw new IllegalStateException("ResultSet can't be closed!!!");
        }
        header = new ArrayList<>();
        table = new HashMap<>();

        String cpfAnterior = null;
        boolean isPrimeiraLinha = true;
        boolean isPrimeiroColaborador = true;
        while (rSet.next()) {
            final String cpfAtual = rSet.getString("CPF_COLABORADOR");
            if (cpfAnterior == null) {
                cpfAnterior = cpfAtual;
            }
            // Verificamos se trocou o colaborador.
            if (cpfAnterior.equals(cpfAtual)) {
                if (isPrimeiraLinha) {
                    montaInfosBasicasHeader();
                }
                if (isPrimeiroColaborador) {
                    addNomeIntervaloHeader();
                }
                addInfosIntervaloToTable();
            } else {
                addInfosIntervaloToTable();
                // Trocou de colaborador, setamos a variável para false.
                isPrimeiroColaborador = false;
            }
            // Acabamos de processar a primeira linha, setamos a variável para false.
            isPrimeiraLinha = false;
        }
    }

    private void addInfosIntervaloToTable() throws SQLException {
        final String cpfColaborador = rSet.getString("CPF_COLABORADOR");
        if (table.containsKey(cpfColaborador)) {
            final List<String> linhaColaborador = table.get(cpfColaborador);
            final String tempoTotal = rSet.getString("TEMPO_TOTAL");
            linhaColaborador.add(tempoTotal != null ? tempoTotal : ZERO_HORAS);
        } else {
            final List<String> linha = new ArrayList<>();
            linha.add(cpfColaborador);
            linha.add(rSet.getString("NOME"));
            linha.add(rSet.getString("CARGO"));
            final String tempoTotal = rSet.getString("TEMPO_TOTAL");
            linha.add(tempoTotal != null ? tempoTotal : ZERO_HORAS);
            table.put(cpfColaborador, linha);
        }
    }

    private void addNomeIntervaloHeader() throws SQLException {
        final String nomeIntervalo = rSet.getString("INTERVALO");
        if (!header.contains(nomeIntervalo)) {
            header.add(nomeIntervalo);
        }
    }

    private void montaInfosBasicasHeader() throws SQLException {
        header.add(rSet.getMetaData().getColumnName(COLUNA_CPF_COLABORADOR));
        header.add(rSet.getMetaData().getColumnName(COLUNA_NOME_COLABORADOR));
        header.add(rSet.getMetaData().getColumnName(COLUNA_CARGO_COLABORADOR));
    }
}
