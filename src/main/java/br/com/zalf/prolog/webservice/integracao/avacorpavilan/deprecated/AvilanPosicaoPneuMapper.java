package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.validation.constraints.NotNull;

/**
 * Classe responsável por mapear as posições utilizadas nos pneus pela Avilan para o equivalente no ProLog.
 */
@VisibleForTesting
@Deprecated
public class AvilanPosicaoPneuMapper {
    private static final ImmutableMap<String, Integer> MAP_POSICOES;

    static {
        MAP_POSICOES = new ImmutableMap.Builder<String, Integer>()
                /* CAVALO TRUCK */
                .put("DE", 111)
                .put("2ºEE", 211)
                .put("2ºEI", 212)
                .put("3_EE", 311)
                .put("3_EI", 312)
                .put("3_DE", 321)
                .put("3_DI", 322)
                .put("2ºDE", 221)
                .put("2ºDI", 222)
                .put("DD", 121)
                .put("TDE", 221)
                .put("TDI", 222)
                .put("TEE", 211)
                .put("TEI", 212)
                /* BITRUCK */
                .put("2_DD", 221)
                .put("2_DE", 211)
                .put("4_EE", 411)
                .put("4_EI", 412)
                .put("4_DI", 422)
                .put("4_DE", 421)
                /* ESTEPES */
                .put("EST_1", 900)
                .put("EST_2", 901)
                .put("EST_3", 902)
                .put("EST_4", 903)
                .put("EST_5", 904)
                /* CARRETA 3 EIXOS */
                .put("A_EE", 211)
                .put("A_EI", 212)
                .put("B_EE", 311)
                .put("B_EI", 312)
                .put("C_EE", 411)
                .put("C_EI", 412)
                .put("C_DE", 421)
                .put("C_DI", 422)
                .put("B_DE", 321)
                .put("B_DI", 322)
                .put("A_DE", 221)
                .put("A_DI", 222)
                .build();
    }

    @VisibleForTesting
    public static int mapToProLog(@NotNull final String posicao) {
        Preconditions.checkNotNull(posicao, "posicao não pode ser null!");
        Preconditions.checkArgument(MAP_POSICOES.containsKey(posicao),
                "posicao " + posicao + " não mapeada para as posições utilizadas no ProLog");

        return MAP_POSICOES.get(posicao);
    }
}