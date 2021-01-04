package br.com.zalf.prolog.webservice.cs.nps;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsBloqueio;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsDisponivel;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsRealizada;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

/**
 * Created on 2019-10-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PesquisaNpsDaoImpl extends DatabaseConnection implements PesquisaNpsDao {

    @NotNull
    @Override
    public Optional<PesquisaNpsDisponivel> getPesquisaNpsColaborador(@NotNull final Long codColaborador)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{CALL CS.FUNC_NPS_BUSCA_PESQUISA_DISPONIVEL(" +
                    "F_COD_COLABORADOR := ?," +
                    "F_DATA_ATUAL      := ?)}");
            stmt.setLong(1, codColaborador);
            stmt.setObject(2, Now.getLocalDateUtc());
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.getLong("COD_PESQUISA_NPS") > 0) {
                return Optional.of(
                        new PesquisaNpsDisponivel(
                                rSet.getLong("COD_PESQUISA_NPS"),
                                rSet.getString("TITULO_PESQUISA"),
                                rSet.getString("BREVE_DESCRICAO_PESQUISA"),
                                rSet.getString("TITULO_PERGUNTA_ESCALA"),
                                rSet.getString("LEGENDA_ESCALA_BAIXA"),
                                rSet.getString("LEGENDA_ESCALA_ALTA"),
                                rSet.getString("TITULO_PERGUNTA_DESCRITIVA")));
            } else {
                return Optional.empty();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long insereRespostasPesquisaNps(@NotNull final PesquisaNpsRealizada pesquisaRealizada)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{CALL CS.FUNC_NPS_INSERE_RESPOSTAS_PESQUISA(" +
                    "F_COD_PESQUISA_NPS              := ?," +
                    "F_COD_COLABORADOR_REALIZACAO    := ?," +
                    "F_DATA_HORA_REALIZACAO_PESQUISA := ?," +
                    "F_RESPOSTA_PERGUNTA_ESCALA      := ?," +
                    "F_RESPOSTA_PERGUNTA_DESCRITIVA  := ?)}");
            stmt.setLong(1, pesquisaRealizada.getCodPesquisaNps());
            stmt.setLong(2, pesquisaRealizada.getCodColaboradorRealizacao());
            stmt.setObject(3, Now.getOffsetDateTimeUtc());
            stmt.setShort(4, pesquisaRealizada.getRespostaPerguntaEscala());
            stmt.setString(5, pesquisaRealizada.getRespostaPerguntaDescritiva());
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.getLong(1) != 0) {
                return rSet.getLong(1);
            } else {
                throw new IllegalStateException("Erro ao inserir pesquisa de NPS respondida");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void bloqueiaPesquisaNpsColaborador(final @NotNull PesquisaNpsBloqueio pesquisaBloqueio) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{CALL CS.FUNC_NPS_BLOQUEIA_PESQUISA(" +
                    "F_COD_PESQUISA_NPS            := ?," +
                    "F_COD_COLABORADOR_BLOQUEIO    := ?," +
                    "F_DATA_HORA_BLOQUEIO_PESQUISA := ?)}");
            stmt.setLong(1, pesquisaBloqueio.getCodPesquisaNps());
            stmt.setLong(2, pesquisaBloqueio.getCodColaboradorBloqueio());
            stmt.setObject(3, Now.getOffsetDateTimeUtc());
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }
}