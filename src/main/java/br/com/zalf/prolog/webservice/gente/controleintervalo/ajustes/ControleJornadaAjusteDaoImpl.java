package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicaoInicioFim;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAtivacaoInativacao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteEdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.ConsolidadoMarcacoesDia;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoAjusteHistoricoExibicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoColaboradorAjuste;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacaoInconsistenciaExibicao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ControleJornadaAjusteDaoImpl extends DatabaseConnection implements ControleJornadaAjusteDao {

    @Override
    public void adicionarMarcacaoAjuste(@NotNull final String token,
                                        @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MARCACAO_INSERT_AJUSTE_MARCACAO(?, ?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, marcacaoAjuste.getCodMarcacaoVinculo());
            stmt.setObject(2, marcacaoAjuste.getDataHoraInserida());
            stmt.setLong(3, marcacaoAjuste.getCodJustificativaAjuste());
            stmt.setString(4, marcacaoAjuste.getObservacaoAjuste());
            stmt.setString(5, marcacaoAjuste.getTipoMarcacaoAjuste().asString());
            stmt.setString(6, token);
            stmt.setObject(7, Now.localDateTimeUtc());
            rSet = stmt.executeQuery();
            if (!rSet.next()) {
                throw new SQLException("Não foi possível inserir o ajuste na marcação");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void adicionarMarcacaoAjusteInicioFim(@NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste,
                                                 @NotNull final String token) throws Throwable {

    }

    @Override
    public void ativarInativarMarcacaoAjuste(@NotNull final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste,
                                             @NotNull final String token) throws Throwable {

    }

    @Override
    public void editarMarcacaoAjuste(@NotNull final MarcacaoAjusteEdicao marcacaoAjuste,
                                     @NotNull final String token) throws Throwable {

    }

    @NotNull
    @Override
    public List<ConsolidadoMarcacoesDia> getMarcacoesConsolidadasParaAjuste(@NotNull final Long codUnidade,
                                                                            @NotNull final String codColaborador,
                                                                            @NotNull final String codTipoIntervalo,
                                                                            @NotNull final LocalDate dataInicial,
                                                                            @NotNull final LocalDate dataFinal) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoColaboradorAjuste> getMarcacoesColaboradorParaAjuste(
            @NotNull final Long codUnidade,
            @NotNull final String codColaborador,
            @NotNull final LocalDate data) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoAjusteHistoricoExibicao> getMarcacaoAjusteHistorio(@NotNull final Long codMarcacao) throws
            Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<MarcacaoInconsistenciaExibicao> getMarcacoesInconsistentes(@NotNull final Long codMarcacao) throws
            Throwable {
        return null;
    }
}