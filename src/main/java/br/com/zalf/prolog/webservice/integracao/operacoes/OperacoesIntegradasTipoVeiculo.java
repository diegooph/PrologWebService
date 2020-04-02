package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-03-23
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface OperacoesIntegradasTipoVeiculo {
    @NotNull
    Long insertTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable;

    void updateTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable;
}
