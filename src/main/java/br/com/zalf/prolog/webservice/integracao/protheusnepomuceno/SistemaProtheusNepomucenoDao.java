package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosAfericaoRealizadaPlaca;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosTipoVeiculoConfiguracaoAfericao;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosUnidadeRestricao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created on 12/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface SistemaProtheusNepomucenoDao {
    /**
     * Insere uma aferição.
     *
     * @param conn       Conexão que será utilizada para inserir a aferição.
     * @param codUnidade Código da unidade onde a Aferição foi realizada.
     * @param afericao   Objeto contendo as medidas capturadas no processo de aferição
     * @return Código da aferição inserida.
     * @throws Throwable Se ocorrer erro na inserção.
     */
    @NotNull
    Long insert(@NotNull final Connection conn,
                @NotNull final Long codUnidade,
                @NotNull final Afericao afericao) throws Throwable;

    @NotNull
    String getCodAuxiliarUnidade(@NotNull final Connection conn,
                                 @NotNull final Long codUnidade) throws Throwable;

    @NotNull
    String getCodFiliais(@NotNull final Connection conn, @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Map<String, InfosUnidadeRestricao> getInfosUnidadeRestricao(@NotNull final Connection conn,
                                                                @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Map<String, InfosTipoVeiculoConfiguracaoAfericao> getInfosTipoVeiculoConfiguracaoAfericao(
            @NotNull final Connection conn,
            @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Map<String, InfosAfericaoRealizadaPlaca> getInfosAfericaoRealizadaPlaca(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final List<String> placasNepomuceno) throws Throwable;
}
