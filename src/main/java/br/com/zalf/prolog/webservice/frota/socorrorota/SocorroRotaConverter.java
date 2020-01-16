package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.frota.socorrorota._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.VeiculoTransferenciaConverter;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Created on 12/19/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class SocorroRotaConverter {
    private SocorroRotaConverter() {
        throw new IllegalStateException(VeiculoTransferenciaConverter.class.getSimpleName()
                + "cannot be instantiated!");
    }

    @NotNull
    public static UnidadeAberturaSocorro createUnidadeAberturaSocorro(
            @NotNull final ResultSet rSet) throws SQLException {
        return new UnidadeAberturaSocorro(
                rSet.getLong("CODIGO_UNIDADE"),
                rSet.getString("NOME_UNIDADE"));
    }

    @NotNull
    public static VeiculoAberturaSocorro createVeiculoAberturaSocorro(
            @NotNull final ResultSet rSet) throws SQLException {
        return new VeiculoAberturaSocorro(
                rSet.getLong("CODIGO"),
                rSet.getString("PLACA"),
                rSet.getLong("KM"));
    }

    @NotNull
    public static OpcaoProblemaAberturaSocorro createOpcaoProblemaAberturaSocorro(
            @NotNull final ResultSet rSet) throws SQLException {
        return new OpcaoProblemaAberturaSocorro(
                rSet.getLong("CODIGO"),
                rSet.getString("DESCRICAO"),
                rSet.getBoolean("OBRIGA_DESCRICAO"));
    }

    @NotNull
    public static SocorroRotaListagem createSocorroRotaListagem(
            @NotNull final ResultSet rSet) throws SQLException {
        return new SocorroRotaListagem(
                rSet.getLong("COD_SOCORRO_ROTA"),
                rSet.getString("PLACA_VEICULO"),
                rSet.getBoolean("VEICULO_DELETADO"),
                rSet.getString("NOME_RESPONSAVEL_ABERTURA_SOCORRO"),
                rSet.getBoolean("COLABORADOR_DELETADO"),
                rSet.getString("DESCRICAO_FORNECIDA_ABERTURA_SOCORRO"),
                rSet.getString("DESCRICAO_OPCAO_PROBLEMA_ABERTURA_SOCORRO"),
                rSet.getObject("DATA_HORA_ABERTURA_SOCORRO", LocalDateTime.class),
                rSet.getString("ENDERECO_AUTOMATICO_ABERTURA_SOCORRO"),
                StatusSocorroRota.fromString(rSet.getString("STATUS_ATUAL_SOCORRO_ROTA"))
        );
    }

    @NotNull
    public static SocorroRotaAberturaVisualizacao createSocorroRotaAberturaVisualizacao(
            @NotNull final ResultSet rSet) throws SQLException {
        return new SocorroRotaAberturaVisualizacao(
                rSet.getString("PLACA_VEICULO_ABERTURA"),
                rSet.getLong("COD_COLABORADOR_ABERTURA"),
                rSet.getString("NOME_RESPONSAVEL_ABERTURA"),
                rSet.getString("DESCRICAO_OPCAO_PROBLEMA_ABERTURA"),
                rSet.getObject("DATA_HORA_ABERTURA", LocalDateTime.class),
                new LocalizacaoSocorroRota(
                        rSet.getString("LATITUDE_ABERTURA"),
                        rSet.getString("LONGITUDE_ABERTURA"),
                        0F),
                rSet.getString("ENDERECO_AUTOMATICO_ABERTURA"),
                rSet.getString("MARCA_APARELHO_ABERTURA"),
                rSet.getString("MODELO_APARELHO_ABERTURA"),
                rSet.getString("IMEI_APARELHO_ABERTURA")
        );
    }

    @NotNull
    public static SocorroRotaAtendimentoVisualizacao createSocorroRotaAtendimentoVisualizacao(
            @NotNull final ResultSet rSet) throws SQLException {
        return new SocorroRotaAtendimentoVisualizacao(
                rSet.getString("NOME_RESPONSAVEL_ATENDIMENTO"),
                rSet.getObject("DATA_HORA_ATENDIMENTO", LocalDateTime.class),
                new LocalizacaoSocorroRota(
                        rSet.getString("LATITUDE_ATENDIMENTO"),
                        rSet.getString("LONGITUDE_ATENDIMENTO"),
                        0F),
                rSet.getString("ENDERECO_AUTOMATICO_ATENDIMENTO"),
                rSet.getString("MARCA_APARELHO_ATENDIMENTO"),
                rSet.getString("MODELO_APARELHO_ATENDIMENTO"),
                rSet.getString("IMEI_APARELHO_ATENDIMENTO")
        );
    }


    @NotNull
    public static SocorroRotaInvalidacaoVisualizacao createSocorroRotaInvalidacaoVisualizacao(
            @NotNull final ResultSet rSet) throws SQLException {
        return new SocorroRotaInvalidacaoVisualizacao(
                rSet.getString("NOME_RESPONSAVEL_INVALIDACAO"),
                rSet.getObject("DATA_HORA_INVALIDACAO", LocalDateTime.class),
                new LocalizacaoSocorroRota(
                        rSet.getString("LATITUDE_INVALIDACAO"),
                        rSet.getString("LONGITUDE_INVALIDACAO"),
                        0F),
                rSet.getString("ENDERECO_AUTOMATICO_INVALIDACAO"),
                rSet.getString("MARCA_APARELHO_INVALIDACAO"),
                rSet.getString("MODELO_APARELHO_INVALIDACAO"),
                rSet.getString("IMEI_APARELHO_INVALIDACAO")
        );
    }


    @NotNull
    public static SocorroRotaFinalizacaoVisualizacao createSocorroRotaFinalizacaoVisualizacao(
            @NotNull final ResultSet rSet) throws SQLException {
        return new SocorroRotaFinalizacaoVisualizacao(
                rSet.getString("NOME_RESPONSAVEL_FINALIZACAO"),
                rSet.getObject("DATA_HORA_FINALIZACAO", LocalDateTime.class),
                new LocalizacaoSocorroRota(
                        rSet.getString("LATITUDE_FINALIZACAO"),
                        rSet.getString("LONGITUDE_FINALIZACAO"),
                        0F),
                rSet.getString("ENDERECO_AUTOMATICO_FINALIZACAO"),
                rSet.getString("MARCA_APARELHO_FINALIZACAO"),
                rSet.getString("MODELO_APARELHO_FINALIZACAO"),
                rSet.getString("IMEI_APARELHO_FINALIZACAO")
        );
    }

    @NotNull
    public static OpcaoProblemaSocorroRotaListagem createOpcaoProblemaSocorroRota(
            @NotNull final ResultSet rSet) throws SQLException {
        return new OpcaoProblemaSocorroRotaListagem(
                rSet.getLong("COD_OPCAO_PROBLEMA"),
                rSet.getString("DESCRICAO"),
                rSet.getBoolean("OBRIGA_DESCRICAO"),
                rSet.getBoolean("STATUS_ATIVO"));
    }

    public static OpcaoProblemaSocorroRotaVisualizacao createOpcaoProblemaSocorroRotaVisualizacao(
            @NotNull final ResultSet rSet) throws SQLException {
        return new OpcaoProblemaSocorroRotaVisualizacao(
                rSet.getLong("COD_OPCAO_PROBLEMA"),
                rSet.getString("DESCRICAO"),
                rSet.getBoolean("OBRIGA_DESCRICAO"),
                rSet.getBoolean("STATUS_ATIVO"),
                rSet.getString("NOME_COLABORADOR_ULTIMA_ATUALIZACAO"),
                rSet.getString("DATA_HORA_ULTIMA_ATUALIZACAO"));
    }
}