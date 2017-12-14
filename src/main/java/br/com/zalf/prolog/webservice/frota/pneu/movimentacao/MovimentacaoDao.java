package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created on 12/14/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface MovimentacaoDao {
    Long insert(ProcessoMovimentacao processoMovimentacao,
                ServicoDao servicoDao,
                boolean fecharServicosAutomaticamente) throws SQLException, OrigemDestinoInvalidaException;

    Long insert(ProcessoMovimentacao processoMovimentacao,
                ServicoDao servicoDao,
                boolean fecharServicosAutomaticamente,
                Connection conn) throws SQLException, OrigemDestinoInvalidaException;
}