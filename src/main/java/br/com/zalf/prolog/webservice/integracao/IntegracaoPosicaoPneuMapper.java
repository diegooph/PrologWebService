package br.com.zalf.prolog.webservice.integracao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created on 2020-05-13
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class IntegracaoPosicaoPneuMapper {
    @NotNull
    private static final String ESTRUTURA_POSICAO_SEPARATOR = "::";
    @NotNull
    private final String codEstruturaVeiculo;
    @NotNull
    private final Map<String, Integer> posicoesPneuMapper;

    @Nullable
    public Integer mapPosicaoToProlog(@NotNull final String posicao) {
        final String keyEstruturaPosicao = getKeyEstruturaPosicao(posicao.trim());
        // Algumas posições podem ser mapeadas de forma específica para algumas estruturas de veículos. Se existir,
        // priorizamos estas, caso contrário, usamos o mapeamento genérico.
        if (posicoesPneuMapper.containsKey(keyEstruturaPosicao)) {
            return posicoesPneuMapper.get(keyEstruturaPosicao);
        }
        return posicoesPneuMapper.get(posicao.trim());
    }

    @NotNull
    private String getKeyEstruturaPosicao(@NotNull final String posicao) {
        return codEstruturaVeiculo.concat(ESTRUTURA_POSICAO_SEPARATOR).concat(posicao.trim());
    }
}
