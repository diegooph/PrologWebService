package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.util.List;

/**
 * Created on 2020-03-17
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface MotivoMovimentoDao {

    @NotNull
    Long insert(@NotNull final MotivoMovimentoInsercao motivoMovimentoInsercao,
                @NotNull final Long codigoColaborador) throws Throwable;

    @NotNull
    MotivoMovimentoVisualizacao getMotivoByCodigo(@NotNull final Long codMotivo,
                                                  @NotNull final ZoneId timeZone) throws Throwable;

    @NotNull
    List<MotivoMovimentoListagem> getMotivosListagem(@NotNull final Long codEmpresa,
                                                     final boolean apenasAtivos,
                                                     @NotNull final ZoneId timeZone) throws Throwable;

    void update(@NotNull final MotivoMovimentoEdicao motivoMovimentoEdicao,
                @NotNull final Long codColaboradorUpdate) throws Throwable;

    @NotNull
    List<MotivoMovimentoHistoricoListagem> getHistoricoByMotivo(@NotNull final Long codMotivoMovimento,
                                                                @NotNull final ZoneId timeZone) throws Throwable;

}
