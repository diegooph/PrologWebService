package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created on 2020-05-13
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ProtheusNepomucenoPosicaoPneuMapper {
    @NotNull
    private final Map<String, Integer> posicoesPneuMapper;

    public ProtheusNepomucenoPosicaoPneuMapper(@NotNull final Map<String, Integer> mapPosicoesProlog) {
        this.posicoesPneuMapper = mapPosicoesProlog;
    }

    @NotNull
    public Map<String, Integer> getPosicoesPneuMapper() {
        return posicoesPneuMapper;
    }

    @Nullable
    public Integer mapPosicaoToProlog(@NotNull final String posicao) {
        return posicoesPneuMapper.get(posicao);
    }
}
