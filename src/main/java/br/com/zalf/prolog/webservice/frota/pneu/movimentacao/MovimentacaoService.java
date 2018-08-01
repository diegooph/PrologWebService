package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.motivo.Motivo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
public class MovimentacaoService {
    private static final String TAG = MovimentacaoService.class.getSimpleName();
    private final MovimentacaoDao dao = Injection.provideMovimentacaoDao();
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    @NotNull
    public AbstractResponse insert(@NotNull final ProcessoMovimentacao movimentacao) throws ProLogException {
        try {
            final Long codigo = dao.insert(Injection.provideServicoDao(), movimentacao, true);
            return ResponseWithCod.ok("Movimentações realizadas com sucesso", codigo);
        } catch (Throwable e) {
            final String errorMessage = "Erro ao realizar as movimentações";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public AbstractResponse insertMotivo(@NotNull final Motivo motivo, @NotNull final Long codEmpresa)
            throws ProLogException {
        try {
            return ResponseWithCod.ok(
                    "Motivo de descarte inserido com sucesso",
                    dao.insertMotivo(motivo, codEmpresa));
        } catch (Throwable e) {
            final String errorMessage = "Erro ao inserir um novo motivo de descarte";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    public void updateMotivoStatus(@NotNull final Long codEmpresa,
                                   @NotNull final Long codMotivo,
                                   @NotNull final Motivo motivo) throws ProLogException {
        try {
            dao.updateMotivoStatus(codEmpresa, codMotivo, motivo);
        } catch (Throwable e) {
            final String errorMessage = String.format("Erro ao atualizar motivo de descarte: %d", codMotivo);
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    @NotNull
    public List<Motivo> getMotivos(@NotNull final Long codEmpresa,
                                   final boolean onlyAtivos) throws ProLogException {
        try {
            return dao.getMotivos(codEmpresa, onlyAtivos);
        } catch (Throwable e) {
            final String errorMessage = "Erro ao buscar lista de motivos de descarte";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }
}