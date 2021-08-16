package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.PermissoesMovimentacaoValidator;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.Motivo;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.integracao.router.RouterMovimentacao;
import br.com.zalf.prolog.webservice.permissao.Visao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
public class MovimentacaoService {
    @NotNull
    private static final String TAG = MovimentacaoService.class.getSimpleName();
    @NotNull
    private final MovimentacaoDao dao = Injection.provideMovimentacaoDao();

    @NotNull
    public AbstractResponse insert(final String userToken,
                                   final ProcessoMovimentacao movimentacao) throws ProLogException {
        try {
            final ColaboradorService colaboradorService = new ColaboradorService();
            final Colaborador colaborador;
            try {
                // Colocamos outro try/catch aqui pois não queremos que se algum erro acontecer na busca do colaborador,
                // seja devolvido do servidor uma mensagem como: "Erro ao buscar colaborador", que não faria sentido no
                // caso de envio das movimentações.
                colaborador = colaboradorService.getByToken(TokenCleaner.getOnlyToken(userToken));
            } catch (final Throwable throwable) {
                throw new GenericException("Erro ao realizar movimentações");
            }

            final Visao visaoColaborador = colaborador.getVisao();
            final List<Movimentacao> movimentacoes = movimentacao.getMovimentacoes();

            // Verifica se o colaborador tem permissão para fazer todas as movimentações que está tentando.
            final PermissoesMovimentacaoValidator validatorPermissoes = new PermissoesMovimentacaoValidator();
            validatorPermissoes.verificaMovimentacoesRealizadas(visaoColaborador, movimentacoes);

            // Segue o fluxo.
            final Long codigo =
                    RouterMovimentacao
                            .create(dao, userToken)
                            .insert(Injection.provideServicoDao(),
                                    Injection.provideCampoPersonalizadoDao(),
                                    movimentacao,
                                    Now.getOffsetDateTimeUtc(),
                                    true);
            return ResponseWithCod.ok("Movimentações realizadas com sucesso", codigo);
        } catch (final Throwable throwable) {
            final String errorMessage = "Erro ao realizar as movimentações";
            Log.e(TAG, errorMessage, throwable);
            throw Injection.provideProLogExceptionHandler().map(throwable, errorMessage);
        }
    }

    @NotNull
    public AbstractResponse insertMotivo(@NotNull final Motivo motivo, @NotNull final Long codEmpresa)
            throws ProLogException {
        try {
            return ResponseWithCod.ok(
                    "Motivo de descarte inserido com sucesso",
                    dao.insertMotivo(motivo, codEmpresa));
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao inserir um novo motivo de descarte";
            Log.e(TAG, errorMessage, e);
            throw Injection.provideProLogExceptionHandler().map(e, errorMessage);
        }
    }

    public void updateMotivoStatus(@NotNull final Long codEmpresa,
                                   @NotNull final Long codMotivo,
                                   @NotNull final Motivo motivo) throws ProLogException {
        try {
            dao.updateMotivoStatus(codEmpresa, codMotivo, motivo);
        } catch (final Throwable e) {
            final String errorMessage = String.format("Erro ao atualizar motivo de descarte: %d", codMotivo);
            Log.e(TAG, errorMessage, e);
            throw Injection.provideProLogExceptionHandler().map(e, errorMessage);
        }
    }

    @NotNull
    public List<Motivo> getMotivos(@NotNull final Long codEmpresa,
                                   final boolean onlyAtivos) throws ProLogException {
        try {
            return dao.getMotivos(codEmpresa, onlyAtivos);
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao buscar lista de motivos de descarte";
            Log.e(TAG, errorMessage, e);
            throw Injection.provideProLogExceptionHandler().map(e, errorMessage);
        }
    }

    @NotNull
    public List<CampoPersonalizadoParaRealizacao> getCamposPersonalizadosRealizacao(
            @NotNull final String userToken,
            @NotNull final Long codUnidade) throws ProLogException {
        try {
            return RouterMovimentacao
                    .create(dao, userToken)
                    .getCamposParaRealizacaoMovimentacao(codUnidade, Injection.provideCampoPersonalizadoDao());
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar os campos personalizados para a unidade %d", codUnidade), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar os campos personalizados, tente novamente");
        }
    }

}