package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.motivo.Motivo;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
public class MovimentacaoService {
    private final MovimentacaoDao dao = Injection.provideMovimentacaoDao();
    private static final String TAG = MovimentacaoService.class.getSimpleName();

    public AbstractResponse insert(ProcessoMovimentacao movimentacao) {
        try {
            final Long codigo = dao.insert(movimentacao, Injection.provideServicoDao(), true);
            return ResponseWithCod.ok("Movimentações realizadas com sucesso", codigo);
        } catch (SQLException | OrigemDestinoInvalidaException e) {
            Log.e(TAG, "Erro ao inserir uma movimentação", e);
            return Response.error("Erro ao realizar as movimentações");
        }
    }

    public AbstractResponse insertMotivo(@NotNull final Motivo motivo, @NotNull final Long codEmpresa) {
        try {
            return ResponseWithCod.ok("Motivo de descarte inserido com sucesso", dao.insertMotivo(motivo, codEmpresa));
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir um novo motivo de descarte", e);
            return Response.error("Erro ao inserir um novo motivo de descarte");
        }
    }

    public List<Motivo> getMotivos(@NotNull Long codEmpresa, boolean onlyAtivos) {
        try {
            return dao.getMotivos(codEmpresa, onlyAtivos);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar lista de Motivos", e);
            return null;
        }
    }

    public boolean updateMotivoStatus(@NotNull final Long codEmpresa,
                                      @NotNull final Long codMotivo,
                                      @NotNull final Motivo motivo) {
        try {
            dao.updateMotivoStatus(codEmpresa, codMotivo, motivo);
            return true;
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao atualizar Motivo: %d", codMotivo), e);
            return false;
        }
    }
}