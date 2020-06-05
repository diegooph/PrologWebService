package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.ProtheusNepomucenoConstants.CODIGOS_MODELOS_NEPOMUCENO_MAPEAR_POSICOES;

/**
 * Created on 2020-05-13
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ProtheusNepomucenoPosicaoPneuMapper {
    @NotNull
    private static final Map<String, Integer> MAP_POSICOES_DIFERENCIADAS = new HashMap<String, Integer>() {{
        put("DE", 111);
        put("DD", 121);
        put("1TEE", 211);
        put("1TEI", 212);
        put("1TDE", 221);
        put("1TDI", 222);
        put("1LEE", 311);
        put("1LEI", 312);
        put("1LDE", 321);
        put("1LDI", 322);
        put("ZZZ", 900);
        put("ZZZ1", 901);
        put("ESTEPE", 902);
    }};
    @NotNull
    private final String codEstruturaVeiculo;
    @NotNull
    private final Map<String, Integer> posicoesPneuMapper;
    private boolean mapeamentoDiferenciado;

    public ProtheusNepomucenoPosicaoPneuMapper(@NotNull final String codEstruturaVeiculo,
                                               @NotNull final Map<String, Integer> mapPosicoesProlog) {
        this.codEstruturaVeiculo = codEstruturaVeiculo;
        this.posicoesPneuMapper = mapPosicoesProlog;
        mapeamentoDiferenciado = false;
        for (final String codModelo : CODIGOS_MODELOS_NEPOMUCENO_MAPEAR_POSICOES) {
            if (codEstruturaVeiculo.equals(codModelo)) {
                mapeamentoDiferenciado = true;
                break;
            }
        }
    }

    @NotNull
    public String getCodEstruturaVeiculo() {
        return codEstruturaVeiculo;
    }

    @NotNull
    public Map<String, Integer> getPosicoesPneuMapper() {
        return posicoesPneuMapper;
    }

    @Nullable
    public Integer mapPosicaoToProlog(@NotNull final String posicao) {
        if (mapeamentoDiferenciado) {
            return MAP_POSICOES_DIFERENCIADAS.get(posicao);
        }
        return posicoesPneuMapper.get(posicao);
    }
}
