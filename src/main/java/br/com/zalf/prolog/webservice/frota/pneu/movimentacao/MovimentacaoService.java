package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;

import java.sql.SQLException;

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

}
