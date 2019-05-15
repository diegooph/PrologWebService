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
     * placas de uma unidade de origem para a mesma unidade de Destino. O proceso de transferência é realizado apenas
     * de uma Unidade para a outra, por vez, não é possível, em um processo, transferir placas de diferentes unidades
     * para um único destino. Também não é possível transferir veículos de uma unidade para várias destinos em um único
     * processo.
     * <p>
     * O processo de transferência de placas também transfere os pneus que estão aplicados na placa.
     *
     * @param processoTransferenciaVeiculo Objeto que contém as placas que serão transferidas.
     * @return Código do processo de transferência que foi inserido.
     * @throws Throwable Se algum erro ocorrer no processo de transferência.
     */
    @NotNull
    Long insertProcessoTranseferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo) throws Throwable;
}
