package br.com.zalf.prolog.webservice.integracao;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 16/10/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PosicaoPneuMapper {
    @NotNull
    private final BiMap<String, Integer> posicoesPneuMapper;

    public PosicaoPneuMapper(@NotNull final BiMap<String, Integer> posicoesPneuMapper) {
        this.posicoesPneuMapper = posicoesPneuMapper;
    }

    public int mapToProLog(@NotNull final String posicao) {
        return mapToProLog(posicao, true);
    }

    public int mapToProLog(@NotNull final String posicao, final boolean deveEstourar) {
        if (deveEstourar) {
            Preconditions.checkArgument(posicoesPneuMapper.containsKey(posicao),
                    "posicao " + posicao + " não mapeada para as posições utilizadas no ProLog");
        }
        final Integer posicaoProlog = posicoesPneuMapper.get(posicao);
        return posicaoProlog == null ? 0 : posicaoProlog;
    }

    @NotNull
    public String mapToClient(final int posicao) {
        final BiMap<Integer, String> inverseMap = posicoesPneuMapper.inverse();
        Preconditions.checkArgument(inverseMap.containsKey(posicao),
                "posicao " + posicao + " não mapeada para as posições utilizadas no ProLog");

        return inverseMap.get(posicao);
    }
}