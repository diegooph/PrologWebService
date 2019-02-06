package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model;

import br.com.zalf.prolog.webservice.commons.report.CsvReport;
import br.com.zalf.prolog.webservice.commons.util.date.Durations;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 19/07/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RelatorioTotaisPorTipoIntervalo implements CsvReport {
    private static final int COLUNA_CPF_COLABORADOR = 0;
    private static final int COLUNA_NOME_COLABORADOR = 1;
    private static final int COLUNA_CARGO_COLABORADOR = 2;
    private static final String ZERO_HORAS = "00:00:00";
    private List<String> header;
    private Map<String, List<String>> table;
    @NotNull
    private final ResultSet rSet;
    @NotNull
    private final List<TipoMarcacao> tiposIntervalos;
    @NotNull
    private final Map<Long, Integer> tipoIntervaloTotalIndexColuna;
    @NotNull
    private final Map<Long, Integer> tipoIntervaloHorasNoturnasIndexColuna;

    public RelatorioTotaisPorTipoIntervalo(@NotNull final ResultSet rSet,
                                    @NotNull final List<TipoMarcacao> tiposIntervalos,
                                    @Nullable final Long codTipoIntervaloFiltrado) {
        this.rSet = rSet;
        if (codTipoIntervaloFiltrado != null) {
            // Se tiver filtrado por algum tipo de intervalo, devemos remover os outros tipos.
            this.tiposIntervalos = tiposIntervalos
                    .stream()
                    .filter(tipoIntervalo -> tipoIntervalo.getCodigo().equals(codTipoIntervaloFiltrado))
                    .collect(Collectors.toList());
        } else {
            this.tiposIntervalos = tiposIntervalos;
        }
        this.tipoIntervaloTotalIndexColuna = new HashMap<>(tiposIntervalos.size());
        this.tipoIntervaloHorasNoturnasIndexColuna = new HashMap<>(tiposIntervalos.size());
    }

    @NotNull
    @Override
    public List<String> getHeader() throws SQLException {
        if (header == null) {
            processResultSet();
        }
        return header;
    }

    @NotNull
    @Override
    public Iterable<?> getData() throws SQLException {
        if (table == null) {
            processResultSet();
        }
        return table.values();
    }

    private void processResultSet() throws SQLException {
        if (rSet.isClosed()) {
            throw new IllegalStateException("ResultSet can't be closed!!!");
        }
        header = criaHeader();
        table = new HashMap<>();
        while (rSet.next()) {
            addInfosIntervaloToTable();
        }
    }

    private void addInfosIntervaloToTable() throws SQLException {
        final String cpfColaborador = rSet.getString("CPF_COLABORADOR");
        final List<String> linhaAtual = table.get(cpfColaborador);
        if (linhaAtual != null) {
            final long tempoTotalMillis = rSet.getLong("TEMPO_TOTAL_MILLIS");
            final long tempoTotalHorasNoturnasMillis = rSet.getLong("TEMPO_TOTAL_HORAS_NOTURNAS_MILLIS");
            final Long codTipoIntervalo = rSet.getLong("COD_TIPO_INTERVALO");
            // A query retorna o tempo total em todos os tipos de intervalo, não só os que o colaborador marcou. Caso
            // um filtro esteja aplicado, podemos não ter a coluna para um determinado tipo no relatório, por isso
            // precisamos verificar se é diferente de null.
            final Integer colunaTotal = tipoIntervaloTotalIndexColuna.get(codTipoIntervalo);
            if (colunaTotal != null) {
                linhaAtual.set(colunaTotal, tempoTotalMillis != 0
                        ? Durations.formatDuration(tempoTotalMillis, Durations.Format.HH_MM_SS)
                        : ZERO_HORAS);
            }
            final Integer colunaHorasNoturnas = tipoIntervaloHorasNoturnasIndexColuna.get(codTipoIntervalo);
            if (colunaHorasNoturnas != null) {
                linhaAtual.set(colunaHorasNoturnas, tempoTotalHorasNoturnasMillis != 0
                        ? Durations.formatDuration(tempoTotalHorasNoturnasMillis, Durations.Format.HH_MM_SS)
                        : ZERO_HORAS);
            }
        } else {
            final List<String> linha = criaLinha();
            linha.set(COLUNA_CPF_COLABORADOR, cpfColaborador);
            linha.set(COLUNA_NOME_COLABORADOR, rSet.getString("NOME"));
            linha.set(COLUNA_CARGO_COLABORADOR, rSet.getString("CARGO"));
            final long tempoTotalMillis = rSet.getLong("TEMPO_TOTAL_MILLIS");
            final long tempoTotalHorasNoturnasMillis = rSet.getLong("TEMPO_TOTAL_HORAS_NOTURNAS_MILLIS");
            final Long codTipoIntervalo = rSet.getLong("COD_TIPO_INTERVALO");
            final Integer coluna = tipoIntervaloTotalIndexColuna.get(codTipoIntervalo);
            if (coluna != null) {
                linha.set(coluna, tempoTotalMillis != 0
                        ? Durations.formatDuration(tempoTotalMillis, Durations.Format.HH_MM_SS)
                        : ZERO_HORAS);
            }
            final Integer colunaHorasNoturnas = tipoIntervaloHorasNoturnasIndexColuna.get(codTipoIntervalo);
            if (colunaHorasNoturnas != null) {
                linha.set(colunaHorasNoturnas, tempoTotalHorasNoturnasMillis != 0
                        ? Durations.formatDuration(tempoTotalHorasNoturnasMillis, Durations.Format.HH_MM_SS)
                        : ZERO_HORAS);
            }
            table.put(cpfColaborador, linha);
        }
    }

    @NotNull
    private List<String> criaHeader() {
        final List<String> header = new ArrayList<>();
        header.add("CPF");
        header.add("NOME");
        header.add("CARGO");
        for (final TipoMarcacao tipoIntervalo : tiposIntervalos) {
            header.add(String.format("%d - %s - TOTAL", tipoIntervalo.getCodigoPorUnidade(), tipoIntervalo.getNome()));
            tipoIntervaloTotalIndexColuna.put(tipoIntervalo.getCodigo(), header.size() - 1);
            header.add(String.format("%d - %s  - HORAS NOTURNAS", tipoIntervalo.getCodigoPorUnidade(), tipoIntervalo.getNome()));
            tipoIntervaloHorasNoturnasIndexColuna.put(tipoIntervalo.getCodigo(), header.size() - 1);
        }
        return header;
    }

    @NotNull
    private List<String> criaLinha() {
        final List<String> linha = new ArrayList<>(header.size());
        // Inicializa todas as linhas com null. Caso o colaborador não tenha marcado algum tipo de intervalo, esse valor
        // será enviado no relatório.
        for (int i = 0; i < header.size(); i++) {
            linha.add(null);
        }
        return linha;
    }
}