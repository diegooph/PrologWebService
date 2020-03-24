package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaVisualizacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2020-03-18
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class MotivoRetiradaService {

    private static final String TAG = MotivoRetiradaService.class.getSimpleName();

    @NotNull
    private final MotivoRetiradaDao dao = Injection.provideMotivoDao();

    @NotNull
    public Long insert(@NotNull final MotivoRetiradaInsercao motivo, @NotNull final String tokenAutenticacao) {
        try {
            return dao.insert(motivo, tokenAutenticacao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao inserir motivo %s", motivo.getDescricaoMotivoRetirada()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir motivo, tente novamente.");
        }
    }

    @NotNull
    public MotivoRetiradaVisualizacao getMotivoByCodigo(@NotNull final Long codMotivo,
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
    public List<MotivoRetiradaListagem> getMotivosListagem(@NotNull final Long codEmpresa,
                                                           @NotNull final String tokenAutenticacao) {
        try {
            return dao.getMotivosListagem(codEmpresa, tokenAutenticacao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar motivos, código da empresa: %d", codEmpresa), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar motivos, tente novamente.");
        }
    }

    @Nullable
    public void update(@NotNull final MotivoRetiradaEdicao motivoRetiradaEdicao,
                       @NotNull final String tokenAutenticacao) {
        try {
            dao.update(motivoRetiradaEdicao, tokenAutenticacao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao atualizar motivo, código do motivo: %d", motivoRetiradaEdicao.getCodMotivoRetirada()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar motivo, tente novamente.");
        }
    }

    @Nullable
    public void delete(@NotNull final Long codMotivo, @NotNull final String tokenAutenticacao) {
        try {
            dao.delete(codMotivo, tokenAutenticacao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao deletar motivo, código do motivo: %d", codMotivo), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao deletar motivo, tente novamente.");
        }
    }

}
