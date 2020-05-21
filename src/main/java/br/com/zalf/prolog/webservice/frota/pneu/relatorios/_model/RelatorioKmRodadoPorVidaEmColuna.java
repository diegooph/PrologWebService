package br.com.zalf.prolog.webservice.frota.pneu.relatorios._model;

import br.com.zalf.prolog.webservice.commons.report.CsvReport;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 2020-05-21
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class RelatorioKmRodadoPorVidaEmColuna implements CsvReport {
    private static final String CARACTERE_SEM_DADOS = "-";
    private static final int TOTAL_VIDAS_BUSCADAS = 10;
    @NotNull
    private static final List<String> HEADER;

    static {
        HEADER = new ArrayList<>();
        // Gerais.
        HEADER.add("UNIDADE ALOCADO");
        HEADER.add("PNEU");
        HEADER.add("DIMENSÃO");

        // Por vida.
        for (int i = 0; i < TOTAL_VIDAS_BUSCADAS; i++) {
            HEADER.add("MARCA VIDA " + (i + 1));
            HEADER.add("MODELO VIDA " + (i + 1));
            HEADER.add("VALOR VIDA " + (i + 1));
            HEADER.add("KM VIDA " + (i + 1));
            HEADER.add("CPK VIDA " + (i + 1));
        }

        // Último geral.
        HEADER.add("KM RODADO TODAS AS VIDAS");
    }

    @NotNull
    private final List<PneuKmRodadoPorVida> data;
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
        final Map<Long, List<PneuKmRodadoPorVida>> grouped = data
                .stream()
                .collect(Collectors.groupingBy(PneuKmRodadoPorVida::getCodPneu));

        final List<List<String>> innerTable = new ArrayList<>();
        grouped.forEach((codPneu, vidasPneu) -> {
            final List<String> row = new ArrayList<>();
            // As colunas devem ser adicionadas na seguinte ordem:
            // -- Infos gerais do pneu: --
            // unidade_alocado.
            // cod_cliente_pneu.
            // dimensao.
            // -- Infos específicas de cada vida: --
            // marca vida 1.
            // modelo vida 1.
            // valor_vida 1.
            // km_rodado_vida 1.
            // valor_por_km_vida 1.
            // ...
            // demais vidas aqui, até a TOTAL_VIDAS_BUSCADAS (constante nessa classe).
            // ...
            // km_rodado_todas_vidas (por último essa).

            // A lista sempre terá, no mínimo, uma entrada, pois qualquer pneu tem entrada para a primeira vida,
            // então pegamos as infos gerais dessa vida.
            final PneuKmRodadoPorVida infoVida1 = vidasPneu.get(0);
            row.add(infoVida1.getUnidadeAlocado());
            row.add(infoVida1.getCodPneuCliente());
            row.add(infoVida1.getDimensao());

            // Processamos a vida 1 novamente, assim todas as infos de vida ficam concentradas nesse 'for'.
            for (int i = 0; i < TOTAL_VIDAS_BUSCADAS; i++) {
                if (i < vidasPneu.size()) {
                    // Vida existe.
                    row.add(vidasPneu.get(i).getMarca());
                    row.add(vidasPneu.get(i).getModelo());
                    row.add(vidasPneu.get(i).getValorVida());
                    row.add(vidasPneu.get(i).getKmRodadoVida());
                    row.add(vidasPneu.get(i).getValorPorKmVida());
                } else {
                    // Precisamos setar valores padrões para a vida.
                    row.add(CARACTERE_SEM_DADOS);
                    row.add(CARACTERE_SEM_DADOS);
                    row.add(CARACTERE_SEM_DADOS);
                    row.add(CARACTERE_SEM_DADOS);
                    row.add(CARACTERE_SEM_DADOS);
                }
            }

            // Por último, adicionamos o total de km rodado em todas as vidas.
            row.add(infoVida1.getKmRodadoTodasVidas());

            innerTable.add(row);
        });

        return innerTable;
    }
}
