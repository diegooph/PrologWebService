package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosAfericaoAvulsa;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

/**
 * Created on 12/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface SistemaProtheusNepomucenoDao {
    /**
     * Insere uma aferição.
     *
     * @param conn             Conexão que será utilizada para inserir a aferição.
     * @param codUnidade       Código da unidade onde a Aferição foi realizada.
     * @param afericao         Objeto contendo as medidas capturadas no processo de aferição
     * @return Código da aferição inserida.
     * @throws Throwable Se ocorrer erro na inserção.
     */
    @NotNull
    Long insert(@NotNull final Connection conn,
                @NotNull final Long codUnidade,
                @NotNull final Afericao afericao) throws Throwable;

    /**
     * Busca o código auxiliar a partir do código da unidade.
     *
     * @param conn             Conexão que será utilizada para inserir a aferição.
     * @param codUnidade       Código da unidade no Prolog.
     * @return Código auxiliar da unidade, usado na integração.
     * @throws Throwable Se ocorrer algum erro na busca.
     */
    @NotNull
    String getCodAuxiliarUnidade(@NotNull final Connection conn,
                                 @NotNull final Long codUnidade) throws  Throwable;

    /**
     * Busca as possíveis informações de aferições integradas de acordo com a lista de pneus e unidade.
     *
     * @param conn             Conexão que será utilizada para inserir a aferição.
     * @param codUnidade       Código da unidade.
     * @param codPneus         Lista de códigos de pneus buscado do cliente.
     * @return Lista de registros de aferições integradas.
     * @throws Throwable Se ocorrer algum erro na busca.
     */
    @NotNull
    List<InfosAfericaoAvulsa> getInfosAfericaoAvulsa(@NotNull final Connection conn,
                                                     @NotNull final Long codUnidade,
                                                     @NotNull final List<String> codPneus) throws  Throwable;
}
