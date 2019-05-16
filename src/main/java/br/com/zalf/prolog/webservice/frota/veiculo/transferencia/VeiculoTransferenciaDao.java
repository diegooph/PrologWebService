package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 29/04/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface VeiculoTransferenciaDao {

    /**
     * Este método realiza a transferência de placas, e pneus aplicados, entre unidades de uma mesma empresa.
     * <p>
     * Um {@link ProcessoTransferenciaVeiculoRealizacao processo de transferência} pode conter a transferência de várias
     * placas de uma unidade de origem para outra unidade de destino. Origem e destino não podem ser iguais.
     * Não é possível transferir placas de diferentes unidades para um único destino. O processo deve incluir sempre,
     * somente duas unidades, a origem e a destino.
     * <p>
     * O processo de transferência de placas também transfere os pneus que estão aplicados na placa.
     *
     * @param processoTransferenciaVeiculo Objeto que contém as placas que serão transferidas.
     * @return Código do processo de transferência que foi inserido.
     * @throws Throwable Se algum erro ocorrer ao realizar o processo de transferência.
     */
    @NotNull
    Long insertProcessoTranseferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo) throws Throwable;
}
