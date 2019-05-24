package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.AfericaoIntegracaoPraxioConverter;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.OrdemServicoAbertaGlobus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class IntegracaoPraxioDaoImpl extends DatabaseConnection implements IntegracaoPraxioDao {

    @NotNull
    @Override
    public List<MedicaoIntegracaoPraxio> getAfericoesRealizadas(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUltimaAfericao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_INTEGRACAO_BUSCA_AFERICOES_EMPRESA(?, ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setLong(2, codUltimaAfericao);
            rSet = stmt.executeQuery();
            final List<MedicaoIntegracaoPraxio> medicoes = new ArrayList<>();
            while (rSet.next()) {
                medicoes.add(AfericaoIntegracaoPraxioConverter.convert(rSet));
            }
            return medicoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void inserirOrdensServicoGlobus(
            @NotNull final String tokenIntegracao,
            @NotNull final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("");
            rSet = stmt.executeQuery();
            while (rSet.next()) {
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}