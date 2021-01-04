package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model;

import br.com.zalf.prolog.webservice.commons.report.CsvReport;
import br.com.zalf.prolog.webservice.commons.util.date.DurationUtils;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
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
    private static int COLUNA_JORNADA_BRUTA;
    private static int COLUNA_JORNADA_LIQUIDA;
    @NotNull
    private final ResultSet rSet;
    @NotNull
    private final List<TipoMarcacao> tiposIntervalosParaExibir;
    @NotNull
    private final List<TipoMarcacao> todosIntervalosAtivos;
    @NotNull
    private final Map<Long, Integer> tipoIntervaloTotalIndexColuna;
    @NotNull
    private final Map<Long, Integer> tipoIntervaloHorasNoturnasIndexColuna;
    @NotNull
    private final Map<String, Long> totaisJornadaBruta;
    @NotNull
    private final Map<String, Long> totaisJornadaLiquida;
    @Nullable
    private TipoMarcacao tipoJornada;
    @Nullable
    private Set<Long> tiposDescontadosJornadaBruta;
    @Nullable
    private Set<Long> tiposDescontadosJornadaLiquida;
    @Nullable
    private List<String> header;
    @Nullable
    private Map<String, List<Object>> table;

    public RelatorioTotaisPorTipoIntervalo(@NotNull final ResultSet rSet,
                                           @NotNull final List<TipoMarcacao> todosIntervalos,
                                           @Nullable final Long codTipoIntervaloFiltrado) {
        this.rSet = rSet;
        if (codTipoIntervaloFiltrado != null) {
            // Se tiver filtrado por algum tipo de intervalo, devemos remover os outros tipos.
            this.tiposIntervalosParaExibir = todosIntervalos
                    .stream()
                    .filter(tipoIntervalo -> tipoIntervalo.getCodigo().equals(codTipoIntervaloFiltrado))
                    .collect(Collectors.toList());
        } else {
            this.tiposIntervalosParaExibir = todosIntervalos;
        }
        // Também é registrado todos os intervalos ativos, para poder usá-los no cálculo de total de jornada bruta
        // e líquida.
        this.todosIntervalosAtivos = todosIntervalos;
        this.tipoIntervaloTotalIndexColuna = new HashMap<>(this.tiposIntervalosParaExibir.size());
        this.tipoIntervaloHorasNoturnasIndexColuna = new HashMap<>(this.tiposIntervalosParaExibir.size());
        // Os maps dos totais das jornadas são uma relação de <cpfColaborador, totalJornada>
        this.totaisJornadaBruta = new HashMap<>();
        this.totaisJornadaLiquida = new HashMap<>();
        setupTiposDescontados();
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

        // Neste ponto as jornadas já estão calculadas, em Long. Então, convertemos para duration e setamos na table.
        for (final String cpf : table.keySet()) {
            for (int i = COLUNA_CARGO_COLABORADOR + 1; i < table.get(cpf).size(); i++) {
                final long valorMarcacao = ((Long) table.get(cpf).get(i));
                table.get(cpf).set(i, valorMarcacao != 0
                        ? DurationUtils.formatDurationHandleNegative(valorMarcacao, DurationUtils.Format.HH_MM_SS)
                        : ZERO_HORAS);
            }
            if (tipoJornada != null) {
                final Long totalJornadaBruta = totaisJornadaBruta.get(cpf);
                table.get(cpf).set(COLUNA_JORNADA_BRUTA, totalJornadaBruta != 0
                        ? DurationUtils.formatDurationHandleNegative(totalJornadaBruta, DurationUtils.Format.HH_MM_SS)
                        : ZERO_HORAS);
                final Long totalJornadaLiquida = totaisJornadaLiquida.get(cpf);
                table.get(cpf).set(COLUNA_JORNADA_LIQUIDA, totalJornadaLiquida != 0
                        ? DurationUtils.formatDurationHandleNegative(totalJornadaLiquida, DurationUtils.Format.HH_MM_SS)
                        : ZERO_HORAS);
            } else {
                table.get(cpf).set(COLUNA_JORNADA_BRUTA, "-");
                table.get(cpf).set(COLUNA_JORNADA_LIQUIDA, "-");
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private void addInfosIntervaloToTable() throws SQLException {
        final String cpfColaborador = rSet.getString("CPF_COLABORADOR");
        //noinspection ConstantConditions
        final List<Object> linhaAtual = table.get(cpfColaborador);
        if (linhaAtual != null) {
            final long tempoTotalMillis = rSet.getLong("TEMPO_MARCACAO_MILLIS");
            final long tempoTotalHorasNoturnasMillis = rSet.getLong("TEMPO_MARCACAO_HORAS_NOTURNAS_MILLIS");
            final Long codTipoIntervalo = rSet.getLong("COD_TIPO_INTERVALO");
            // A query retorna o tempo total em todos os tipos de intervalo, não só os que o colaborador marcou. Caso
            // um filtro esteja aplicado, podemos não ter a coluna para um determinado tipo no relatório, por isso
            // precisamos verificar se é diferente de null.
            final Integer colunaTotal = tipoIntervaloTotalIndexColuna.get(codTipoIntervalo);
            if (colunaTotal != null) {
                linhaAtual.set(colunaTotal,
                        linhaAtual.get(colunaTotal) != null
                                ? tempoTotalMillis + ((Long) linhaAtual.get(colunaTotal))
                                : tempoTotalMillis);
            }
            final Integer colunaHorasNoturnas = tipoIntervaloHorasNoturnasIndexColuna.get(codTipoIntervalo);
            if (colunaHorasNoturnas != null) {
                linhaAtual.set(colunaHorasNoturnas, linhaAtual.get(colunaHorasNoturnas) != null
                        ? tempoTotalHorasNoturnasMillis + ((Long) linhaAtual.get(colunaHorasNoturnas))
                        : tempoTotalHorasNoturnasMillis);
            }
            if (rSet.getBoolean("MARCACAO_DENTRO_JORNADA") || rSet.getBoolean("TIPO_JORNADA")) {
                recalculaTotaisJornada(cpfColaborador, codTipoIntervalo, tempoTotalMillis);
            }
        } else {
            final List<Object> linha = criaLinha();
            linha.set(COLUNA_CPF_COLABORADOR, cpfColaborador);
            linha.set(COLUNA_NOME_COLABORADOR, rSet.getString("NOME"));
            linha.set(COLUNA_CARGO_COLABORADOR, rSet.getString("CARGO"));
            final long tempoTotalMillis = rSet.getLong("TEMPO_MARCACAO_MILLIS");
            final long tempoTotalHorasNoturnasMillis = rSet.getLong("TEMPO_MARCACAO_HORAS_NOTURNAS_MILLIS");
            final Long codTipoIntervalo = rSet.getLong("COD_TIPO_INTERVALO");
            final Integer coluna = tipoIntervaloTotalIndexColuna.get(codTipoIntervalo);
            if (coluna != null) {
                linha.set(coluna,
                        linha.get(coluna) != null
                                ? tempoTotalMillis + ((Long) linha.get(coluna))
                                : tempoTotalMillis);
            }
            final Integer colunaHorasNoturnas = tipoIntervaloHorasNoturnasIndexColuna.get(codTipoIntervalo);
            if (colunaHorasNoturnas != null) {
                linha.set(colunaHorasNoturnas,
                        linha.get(colunaHorasNoturnas) != null
                                ? tempoTotalHorasNoturnasMillis + ((Long) linha.get(colunaHorasNoturnas))
                                : tempoTotalHorasNoturnasMillis);
            }
            if (rSet.getBoolean("MARCACAO_DENTRO_JORNADA") || rSet.getBoolean("TIPO_JORNADA")) {
                recalculaTotaisJornada(cpfColaborador, codTipoIntervalo, tempoTotalMillis);
            }
            table.put(cpfColaborador, linha);
        }
    }

    private void recalculaTotaisJornada(@NotNull final String cpfColaborador,
                                        @NotNull final Long codTipoIntervalo,
                                        final long tempoTotalMillis) {
        // Aqui utilizamos o merge porque ele já realiza a soma do valor anterior com o novo valor que passamos
        // por parâmetro. Além disso, no caso de haver um nulo para uma key, ele já cria uma entrada e associa o valor
        // que passamos como parâmetro. Para substrações, não existe algo oposto a "sum". Sendo assim, passamos um
        // valor negativo, para permitir, somando o valor anterior com um negativo.
        if (this.tipoJornada != null && this.tipoJornada.getCodigo().equals(codTipoIntervalo)) {
            totaisJornadaBruta.merge(cpfColaborador, tempoTotalMillis, Long::sum);
            totaisJornadaLiquida.merge(cpfColaborador, tempoTotalMillis, Long::sum);
        } else if (verifyIfDescontaJornadaBruta(codTipoIntervalo)) {
            totaisJornadaBruta.merge(cpfColaborador, -tempoTotalMillis, Long::sum);
            totaisJornadaLiquida.merge(cpfColaborador, -tempoTotalMillis, Long::sum);
        } else if (verifyIfDescontaJornadaLiquida(codTipoIntervalo)) {
            totaisJornadaLiquida.merge(cpfColaborador, -tempoTotalMillis, Long::sum);
        }
    }

    private boolean verifyIfDescontaJornadaBruta(@NotNull final Long codigo) {
        if (tiposDescontadosJornadaBruta != null) {
            return tiposDescontadosJornadaBruta.contains(codigo);
        }
        return false;
    }

    private boolean verifyIfDescontaJornadaLiquida(@NotNull final Long codigo) {
        if (tiposDescontadosJornadaLiquida != null) {
            return tiposDescontadosJornadaLiquida.contains(codigo);
        }
        return false;
    }

    private void setupTiposDescontados() {
        this.todosIntervalosAtivos.forEach(ti -> {
            if (ti.isTipoJornada()) {
                this.tipoJornada = ti;
                if (ti.getFormulaCalculoJornada() != null) {
                    this.tiposDescontadosJornadaBruta = new HashSet<>();
                    ti.getFormulaCalculoJornada().getTiposDescontadosJornadaBruta().forEach(
                            tdjb -> this.tiposDescontadosJornadaBruta.add(tdjb.getCodTipo()));
                    this.tiposDescontadosJornadaLiquida = new HashSet<>();
                    ti.getFormulaCalculoJornada().getTiposDescontadosJornadaLiquida().forEach(
                            tdjl -> this.tiposDescontadosJornadaLiquida.add(tdjl.getCodTipo()));
                }
            }
        });
    }

    @NotNull
    private List<String> criaHeader() {
        final List<String> header = new ArrayList<>();
        header.add("CPF");
        header.add("NOME");
        header.add("CARGO");
        for (final TipoMarcacao tipoIntervalo : tiposIntervalosParaExibir) {
            header.add(String.format("%d - %s - TOTAL", tipoIntervalo.getCodigoPorUnidade(), tipoIntervalo.getNome()));
            tipoIntervaloTotalIndexColuna.put(tipoIntervalo.getCodigo(), header.size() - 1);
            header.add(String.format("%d - %s  - HORAS NOTURNAS", tipoIntervalo.getCodigoPorUnidade(), tipoIntervalo.getNome()));
            tipoIntervaloHorasNoturnasIndexColuna.put(tipoIntervalo.getCodigo(), header.size() - 1);
        }
        header.add("JORNADA BRUTA");
        COLUNA_JORNADA_BRUTA = header.size() - 1;
        header.add("JORNADA LÍQUIDA");
        COLUNA_JORNADA_LIQUIDA = header.size() - 1;
        return header;
    }

    @NotNull
    private List<Object> criaLinha() {
        //noinspection ConstantConditions
        final List<Object> linha = new ArrayList<>(header.size());
        // Inicializa todas as linhas com null. Caso o colaborador não tenha marcado algum tipo de intervalo, esse valor
        // será enviado no relatório.
        for (int i = 0; i < header.size(); i++) {
            linha.add(0L);
        }
        return linha;
    }
}