package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

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
public final class ProtheusNepomucenoPosicaoPneuMapper {
    @NotNull
    private final String codEstruturaVeiculo;
    @NotNull
    private final Map<String, Integer> posicoesPneuMapper;

    @Nullable
    public Integer mapPosicaoToProlog(@NotNull final String posicao) {
        final String keyEstruturaPosicao = getKeyEstruturaPosicao(posicao.trim());
        if (posicoesPneuMapper.containsKey(keyEstruturaPosicao)) {
            return posicoesPneuMapper.get(keyEstruturaPosicao);
        }
        return posicoesPneuMapper.get(posicao.trim());
    }

    @NotNull
    private String getKeyEstruturaPosicao(@NotNull final String posicao) {
        return codEstruturaVeiculo.concat("::").concat(posicao.trim());
    }
}
