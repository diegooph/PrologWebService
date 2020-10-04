package br.com.zalf.prolog.webservice.frota.veiculo.historico.relatorio;

import br.com.zalf.prolog.webservice.commons.report.CsvReport;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.EdicaoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.HistoricoEdicaoVeiculo;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Essa classe permite a geração do histórico de edições de um pneu, em forma de CSV.
 * </p>
 * <p>
 * O header do relatório é criado de forma estática, pois ele é fixo, e está armazenado na constante
 * {@link HistoricoEdicaoVeiculoCsv#HEADER}.
 * </p>
 * <p>
 * Muito importante: O relatório recebe uma lista de histórico de edições de veículo, que é a mesma lista utilizada no
 * endpoint que retorna um json para montagem da tela de histórico, não replicamos nenhuma lógica, usamos a mesma lista
 * e a reorganizamos para formatar para o csv. Sendo assim, se os models {@link HistoricoEdicaoVeiculo} ou
 * {@link EdicaoVeiculo} forem modificados, esse relatório precisará ser revisado!!!
 * </p>
 * Created on 2020-09-28
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@RequiredArgsConstructor
public final class HistoricoEdicaoVeiculoCsv implements CsvReport {
    @NotNull
    private static final List<String> HEADER;

    static {
        HEADER = new ArrayList<>();
        // Gerais.
        HEADER.add("COLABORADOR EDIÇÃO");
        HEADER.add("ORIGEM EDIÇÃO");
        HEADER.add("DATA EDIÇÃO");
        HEADER.add("HORA EDIÇÃO");
        HEADER.add("INFORMAÇÕES EXTRAS");
        HEADER.add("TIPO ALTERAÇÃO");
        HEADER.add("VALOR ANTIGO");
        HEADER.add("VALOR NOVO");
    }

    @NotNull
    private final List<HistoricoEdicaoVeiculo> data;
    /**
     * Uma lista de listas representando a tabela de dados.
     * A lista de fora representa as linhas e a lista de Strings representa as colunas. Cada String é o valor de uma
     * coluna.
     * Todas as linhas terão uma lista de Strings de mesmo tamanho.
     */
    @Nullable
    private List<List<String>> table;

    @NotNull
    @Override
    public List<String> getHeader() {
        return HEADER;
    }

    @NotNull
    @Override
    public Iterable<?> getData() {
        if (table == null) {
            table = generateTable();
        }
        return table;
    }

    @NotNull
    private List<List<String>> generateTable() {
        final List<List<String>> innerTable = new ArrayList<>();
        data.forEach(historico ->
                historico.getEdicoes().forEach(edicao -> {
                    final List<String> row = new ArrayList<>();
                    row.add(historico.getNomeColaboradorEdicao());
                    row.add(historico.getOrigemEdicaoLegivel());
                    row.add(DateTimeFormatter
                            .ofPattern("dd/MM/yyyy")
                            .format(historico.getDataHoraEdicao()));
                    row.add(DateTimeFormatter
                            .ofPattern("HH:mm:ss")
                            .format(historico.getDataHoraEdicao()));
                    row.add(historico.getInformacoesExtras());
                    row.add(edicao.getTipoAlteracao().getLegibleString());
                    row.add(String.valueOf(edicao.getValorAntigo()));
                    row.add(String.valueOf(edicao.getValorNovo()));
                    innerTable.add(row);
                }));
        return innerTable;
    }
}
