package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.util.List;

/**
 * Created on 2020-03-18
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class MotivoMovimentoService {
    private static final String TAG = MotivoMovimentoService.class.getSimpleName();
    @NotNull
    private final MotivoMovimentoDao dao = Injection.provideMotivoDao();

    @NotNull
    public AbstractResponse insert(@NotNull final MotivoMovimentoInsercao motivo,
                                   @NotNull final Long codigoColaborador) {
        try {
            return ResponseWithCod.ok(
                    "Motivo de troca inserido com sucesso.",
                    dao.insert(motivo, codigoColaborador));
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao inserir motivo %s", motivo.getDescricaoMotivoRetirada()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir motivo, tente novamente.");
        }
    }

    @NotNull
    public MotivoMovimentoVisualizacao getMotivoByCodigo(@NotNull final Long codMotivo,
                                                         @NotNull final ZoneId timeZone) {
        try {
            return dao.getMotivoByCodigo(codMotivo, timeZone);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar motivo %d", codMotivo), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar motivo, tente novamente.");
        }
    }

    @NotNull
    public List<MotivoMovimentoListagem> getMotivosListagem(@NotNull final Long codEmpresa,
                                                            final boolean apenasAtivos,
                                                            @NotNull final ZoneId timeZone) {
        try {
            return dao.getMotivosListagem(codEmpresa, apenasAtivos, timeZone);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar motivos, código da empresa: %d", codEmpresa), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar motivos, tente novamente.");
        }
    }

    public void update(@NotNull final MotivoMovimentoEdicao motivoMovimentoEdicao,
                       @NotNull final Long codColaboradorUpdate) {
        try {
            dao.update(motivoMovimentoEdicao, codColaboradorUpdate);
        } catch (final Throwable t) {
            Log.e(TAG, String.format(
                    "Erro ao atualizar motivo, código do motivo: %d",
                    motivoMovimentoEdicao.getCodMotivoRetirada()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar motivo, tente novamente.");
        }
    }

    @NotNull
    public List<MotivoMovimentoHistoricoListagem> getHistoricoByMotivo(@NotNull final Long codMotivoRetirada,
                                                                       @NotNull final ZoneId timeZone) {
        try {
            return dao.getHistoricoByMotivo(codMotivoRetirada, timeZone);
        } catch (final Throwable t) {
            Log.e(TAG, String.format(
                    "Erro ao buscar a lista de histórico do motivo, código do motivo: %d",
                    codMotivoRetirada), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar lista de histórico de motivo, tente novamente.");
        }
    }
}
