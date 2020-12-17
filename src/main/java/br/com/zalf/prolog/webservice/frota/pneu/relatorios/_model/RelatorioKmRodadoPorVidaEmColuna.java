package br.com.zalf.prolog.webservice.frota.pneu.relatorios._model;

import br.com.zalf.prolog.webservice.commons.report.CsvReport;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Esta classe é responsável por gerar o relatório de km por vida dos pneus de forma colunada, onde as informações
 * de cada vida são representadas em colunas, ao invés de linhas.
 * <p>
 * Cada pneu terá apenas uma linha no relatório.
 * <p>
 * O relatório é criado processando os dados do atributo {@link RelatorioKmRodadoPorVidaEmColuna#data}.
 * <p>
 * Para controlar a informação de quantas vidas o relatório irá gerar, altere a constante
 * {@link RelatorioKmRodadoPorVidaEmColuna#TOTAL_VIDAS_BUSCADAS}.
 * Quando não houver dados para determinada informação, será adicionado o caractere definido na constante
 * {@link RelatorioKmRodadoPorVidaEmColuna#CARACTERE_SEM_DADOS}.
 * <p>
 * O header do relatório é criado de forma estática, pois ele é fixo, e está armazenado na constante
 * {@link RelatorioKmRodadoPorVidaEmColuna#HEADER}.
 * Importante lembrar que o relatório possui dependência da ordem das colunas entre os dados do
 * {@link RelatorioKmRodadoPorVidaEmColuna#HEADER} com os adicionados na {@link RelatorioKmRodadoPorVidaEmColuna#table}.
 * <p>
 * Created on 2020-05-21
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@RequiredArgsConstructor
public final class RelatorioKmRodadoPorVidaEmColuna implements CsvReport {
    @VisibleForTesting
    public static final String CARACTERE_SEM_DADOS = "-";
    @VisibleForTesting
    public static final int TOTAL_VIDAS_BUSCADAS = 10;
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
        HEADER.add("CPK TODAS AS VIDAS");
        HEADER.add("VALOR VIDA TODAS AS VIDAS");
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

    @VisibleForTesting
    public int getTotalColunasRelatorio() {
        return HEADER.size();
    }

    @NotNull
    private List<List<String>> generateTable() {
        final Map<Long, List<PneuKmRodadoPorVida>> grouped = data
                .stream()
                .collect(Collectors.groupingBy(
                        PneuKmRodadoPorVida::getCodPneu,
                        // Um LinkedHashMap para garantir a manutenção da ordem.
                        LinkedHashMap::new,
                        Collectors.toList()));

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
            IntStream.range(1, TOTAL_VIDAS_BUSCADAS + 1)
                    .forEach(vidaSendoEscritaAgora -> {
                        final Optional<PneuKmRodadoPorVida> vidaPneuEncontrada = vidasPneu
                                .stream()
                                .filter(vidaPneu -> Integer.parseInt(vidaPneu.getVida()) == vidaSendoEscritaAgora)
                                .findFirst();

                        vidaPneuEncontrada.ifPresent(vidaEncontrada -> {
                            row.add(vidaEncontrada.getMarca());
                            row.add(vidaEncontrada.getModelo());
                            row.add(vidaEncontrada.getValorVida());
                            row.add(vidaEncontrada.getKmRodadoVida());
                            row.add(vidaEncontrada.getValorPorKmVida());
                        });
                        if (!vidaPneuEncontrada.isPresent()) {
                            row.add(CARACTERE_SEM_DADOS);
                            row.add(CARACTERE_SEM_DADOS);
                            row.add(CARACTERE_SEM_DADOS);
                            row.add(CARACTERE_SEM_DADOS);
                            row.add(CARACTERE_SEM_DADOS);
                        }
                    });

            final Integer somatorioValorVidaTotal = vidasPneu.stream()
                    .map(PneuKmRodadoPorVida::getValorVida)
                    .map(valorVida -> {
                        if (valorVida.equals("-")) {
                            return 0;
                        }
                        return Integer.parseInt(valorVida);
                    })
                    .reduce(0, Integer::sum);

            final Integer somatorioCpkTotal = vidasPneu.stream()
                    .map(PneuKmRodadoPorVida::getValorPorKmVida)
                    .map(cpk -> {
                        if (cpk.equals("-")) {
                            return 0;
                        }
                        return Integer.parseInt(cpk);
                    })
                    .reduce(0, Integer::sum);

            // Por último, adicionamos o total de km rodado em todas as vidas.
            row.add(infoVida1.getKmRodadoTodasVidas());
            row.add(somatorioCpkTotal.toString());
            row.add(somatorioValorVidaTotal.toString());

            innerTable.add(row);
        });

        return innerTable;
    }
}
