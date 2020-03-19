package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoAtivacaoDesativacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2020-03-18
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class MotivoService {

    private static final String TAG = MotivoService.class.getSimpleName();

    @NotNull
    private final MotivoDao dao = Injection.provideMotivoDao();

    @NotNull
    public Long insert(@NotNull final MotivoInsercao motivo) {
        try {
            return dao.insert(motivo);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao inserir motivo %s", motivo.getDescricaoMotivo()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir motivo, tente novamente.");
        }
    }

    @NotNull
    public MotivoVisualizacaoListagem getMotivoByCodigo(@NotNull final Long codMotivo,
                                                        @NotNull final String tokenAutenticacao) {
        try {
            return dao.getMotivoByCodigo(codMotivo, tokenAutenticacao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar motivo %d", codMotivo), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar motivo, tente novamente.");
        }
    }

    @NotNull
    public List<MotivoVisualizacaoListagem> getMotivosListagem(@NotNull final Long codEmpresa,
                                                               @Nullable final Boolean apenasAtivos,
                                                               @NotNull final String tokenAutenticacao) {
        try {
            return dao.getMotivosListagem(codEmpresa, apenasAtivos, tokenAutenticacao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar motivos, código da empresa: %d", codEmpresa), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar motivos, tente novamente.");
        }
    }

    @Nullable
    public void update(final MotivoEdicao motivoEdicao) {
        try {
            dao.update(motivoEdicao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao atualizar motivo, código do motivo: %d", motivoEdicao.getCodMotivo()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar motivo, tente novamente.");
        }
    }

    @Nullable
    public void ativaDesativaMotivo(final MotivoAtivacaoDesativacao motivo) {
        try {
            dao.ativaDesativaMotivo(motivo);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao ativar ou desativar um motivo, código do motivo: %d", motivo.getCodMotivo()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao ativar ou desativar um motivo, tente novamente.");
        }
    }

}
