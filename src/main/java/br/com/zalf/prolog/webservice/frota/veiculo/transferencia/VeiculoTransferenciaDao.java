package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 29/04/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface VeiculoTransferenciaDao {
    @NotNull
    Long insertProcessoTranseferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo) throws Throwable;
}
