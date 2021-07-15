package br.com.zalf.prolog.webservice.dashboard;

import br.com.zalf.prolog.webservice.dashboard.base.IdentificadorTipoComponente;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class DashboardDaoImpl extends DatabaseConnection implements DashboardDao {

    @NotNull
    @Override
    public ComponentDataHolder getComponenteByCodigo(@NotNull final Integer codigo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                                                 "  DCT.CODIGO AS CODIGO_TIPO_COMPONENTE, " +
                                                 "  DCT.NOME AS NOME_TIPO_COMPONENTE, " +
                                                 "  DCT.DESCRICAO AS DESCRICAO_TIPO_COMPONENTE, " +
                                                 "  DCT.MAXIMO_BLOCOS_HORIZONTAIS AS MAXIMO_BLOCOS_HORIZONTAIS, " +
                                                 "  DCT.MAXIMO_BLOCOS_VERTICAIS AS MAXIMO_BLOCOS_VERTICAIS, " +
                                                 "  DCT.MINIMO_BLOCOS_HORIZONTAIS AS MINIMO_BLOCOS_HORIZONTAIS, " +
                                                 "  DCT.MINIMO_BLOCOS_VERTICAIS AS MINIMO_BLOCOS_VERTICAIS, " +
                                                 "  DC.CODIGO AS CODIGO_COMPONENTE, " +
                                                 "  DC.COD_PILAR_PROLOG_COMPONENTE AS CODIGO_PILAR_PROLOG_COMPONENTE," +
                                                 "  DC.TITULO AS TITULO_COMPONENTE, " +
                                                 "  DC.SUBTITULO AS SUBTITULO_COMPONENTE, " +
                                                 "  DC.DESCRICAO AS DESCRICAO_COMPONENTE, " +
                                                 "  DC.QTD_BLOCOS_HORIZONTAIS AS QTD_BLOCOS_HORIZONTAIS, " +
                                                 "  DC.QTD_BLOCOS_VERTICAIS AS QTD_BLOCOS_VERTICAIS, " +
                                                 "  DC.URL_ENDPOINT_DADOS AS URL_ENDPOINT_DADOS, " +
                                                 "  DC.COR_BACKGROUND_HEX AS COR_BACKGROUND_HEX, " +
                                                 "  DC.URL_ICONE AS URL_ICONE, " +
                                                 "  DC.LABEL_EIXO_X AS LABEL_EIXO_X, " +
                                                 "  DC.LABEL_EIXO_Y AS LABEL_EIXO_Y " +
                                                 "FROM PUBLIC.DASHBOARD_COMPONENTE DC " +
                                                 "  JOIN PUBLIC.DASHBOARD_COMPONENTE_TIPO DCT ON DC" +
                                                 ".COD_TIPO_COMPONENTE = DCT.CODIGO " +
                                                 "WHERE DC.CODIGO = ?;");
            stmt.setInt(1, codigo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final ComponentDataHolder component = new ComponentDataHolder();
                // Tipo Componente:
                component.codigoTipoComponente = rSet.getInt("CODIGO_TIPO_COMPONENTE");
                component.nomeTipoComponente = rSet.getString("NOME_TIPO_COMPONENTE");
                component.descricaoTipoComponente = rSet.getString("DESCRICAO_TIPO_COMPONENTE");
                component.maximoBlocosHorizontais = rSet.getInt("MAXIMO_BLOCOS_HORIZONTAIS");
                component.maximoBlocosVerticais = rSet.getInt("MAXIMO_BLOCOS_VERTICAIS");
                component.minimoBlocosHorizontais = rSet.getInt("MINIMO_BLOCOS_HORIZONTAIS");
                component.minimoBlocosVerticais = rSet.getInt("MINIMO_BLOCOS_VERTICAIS");

                // Componente:
                component.codigoComponente = rSet.getInt("CODIGO_COMPONENTE");
                component.codigoPilarProLogComponente = rSet.getInt("CODIGO_PILAR_PROLOG_COMPONENTE");
                component.tituloComponente = rSet.getString("TITULO_COMPONENTE");
                component.subtituloComponente = rSet.getString("SUBTITULO_COMPONENTE");
                component.descricaoComponente = rSet.getString("DESCRICAO_COMPONENTE");
                component.qtdBlocosHorizontais = rSet.getInt("QTD_BLOCOS_HORIZONTAIS");
                component.qtdBlocosVerticais = rSet.getInt("QTD_BLOCOS_VERTICAIS");
                component.urlEndpointDados = rSet.getString("URL_ENDPOINT_DADOS");
                component.corBackgroundHex = rSet.getString("COR_BACKGROUND_HEX");
                component.urlIcone = rSet.getString("URL_ICONE");
                component.labelEixoX = rSet.getString("LABEL_EIXO_X");
                component.labelEixoY = rSet.getString("LABEL_EIXO_Y");
                return component;
            } else {
                throw new IllegalStateException("Nenhum componente encontrado com o código: " + codigo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<DashboardPilarComponents> getComponentesColaborador(
            @NotNull final String userToken) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * " +
                                                 "from func_dashboard_get_componentes_colaborador(f_user_token => ?);");
            stmt.setString(1, userToken);
            rSet = stmt.executeQuery();
            final List<DashboardPilarComponents> componentsPilar = new ArrayList<>();
            List<DashboardComponentResumido> components = new ArrayList<>();
            int codPilarUltimoComponente = -1;
            while (rSet.next()) {
                if (!components.isEmpty()) {
                    final int codPilarResultSet = rSet.getInt("COD_PILAR_PROLOG_COMPONENTE");
                    if (codPilarUltimoComponente != codPilarResultSet) {
                        // Trocou de pilar.
                        componentsPilar.add(new DashboardPilarComponents(codPilarUltimoComponente, components));
                        components = new ArrayList<>();
                    }
                }
                components.add(createComponentResumido(rSet));
                codPilarUltimoComponente = components.get(components.size() - 1).getCodPilarProLog();
            }
            if (!components.isEmpty()) {
                componentsPilar.add(new DashboardPilarComponents(codPilarUltimoComponente, components));
            }
            return componentsPilar;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private DashboardComponentResumido createComponentResumido(@NotNull final ResultSet rSet) throws SQLException {
        return DashboardComponentResumido.builder()
                .withCodigoComponente(rSet.getInt("codigo_componente"))
                .withTitulo(rSet.getString("titulo_componente"))
                .withSubtitulo(rSet.getString("subtitulo_componente"))
                .withDescricao(rSet.getString("descricao_componente"))
                .withCodPilarProLog(rSet.getInt("cod_pilar_prolog_componente"))
                .withQtdBlocosHorizontais(rSet.getInt("qtd_blocos_horizontais"))
                .withQtdBlocosVerticais(rSet.getInt("qtd_blocos_verticais"))
                .withUrlEndpointDados(rSet.getString("url_endpoint_dados"))
                .withIdentificadorTipo(IdentificadorTipoComponente.fromString(rSet.getString("identificador_tipo")))
                .withCodAgrupamento(rSet.getInt("codigo_agrupamento"))
                .withNomeAgrupamento(rSet.getString("nome_agrupamento"))
                .build();
    }
}