package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 07/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuTransferenciaDao {

    /**
     * Insere a realização de uma transferência.
     *
     * @param pneuTransferenciaRealizacao   A {@link PneuTransferenciaRealizacao} contendo os dados da transferência.
     * @param pneuTransferenciaDao Código da {@link PneuTransferenciaDao}.
     * @throws Throwable Se ocorrer erro no banco.
     */
    void insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                             @NotNull final PneuTransferenciaDao pneuTransferenciaDao) throws Throwable;

    /**
     * Método para buscar uma lista de transferências realizadas
     *
     * @param codUnidadesOrigem Lista de códigos das unidades de origem aplicado na busca.
     * @param codUnidadesDestino Lista de códigos das unidades de origem destino na busca.
     * @param dataInicial Data inicial da busca.
     * @param dataFinal Data final da busca.
     * @return Uma lista de transferências que foram realizadas com base no filtro aplicado.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    List<PneuTransferenciaListagem> getListagem(@NotNull final List<Long> codUnidadesOrigem,
                                                @NotNull final List<Long> codUnidadesDestino,
                                                @NotNull final LocalDate dataInicial,
                                                @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método para as informações de uma transferência realizada
     *
     * @param codTransferencia código de uma transferência realizada na busca.
     * @return Informações sobre a transferência.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    PneuTransferenciaProcessoVisualizacao getVisualizacao(@NotNull final Long codTransferencia) throws Throwable;
}