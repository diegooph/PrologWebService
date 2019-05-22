package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model;

import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.VeiculoEnvioTransferencia;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.ProcessoTransferenciaVeiculoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.VeiculoTransferidoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 09/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class VeiculoTransferenciaConverter {

    private VeiculoTransferenciaConverter() {
        throw new IllegalStateException(VeiculoTransferenciaConverter.class.getSimpleName()
                + "cannot be instantiated!");
    }

    @NotNull
    public static PneuTransferenciaRealizacao toPneuTransferenciaRealizacao(
            final long codUnidadeOrigem,
            final long codUnidadeDestino,
            final long codColaboradorRealizacaoTransferencia,
            @NotNull final VeiculoEnvioTransferencia veiculoEnvioTransferencia) {
        return new PneuTransferenciaRealizacao(
                codUnidadeOrigem,
                codUnidadeDestino,
                codColaboradorRealizacaoTransferencia,
                veiculoEnvioTransferencia.getCodPneusAplicadosVeiculo(),
                // TODO - Vamos setar algum texto aqui?
                null);
    }

    @NotNull
    public static TipoVeiculoDiagrama createVeiculoSemDiagrama(@NotNull final ResultSet rSet) throws SQLException {
        return new TipoVeiculoDiagrama(
                rSet.getLong("COD_VEICULO"),
                rSet.getString("PLACA_VEICULO"),
                rSet.getLong("COD_TIPO_VEICULO"),
                rSet.getString("NOME_TIPO_VEICULO"),
                rSet.getBoolean("POSSUI_DIAGAMA"));
    }

    @NotNull
    public static ProcessoTransferenciaVeiculoVisualizacao createProcessoTransferenciaVeiculo(
            @NotNull final ResultSet rSet,
            @NotNull final List<VeiculoTransferidoVisualizacao> veiculosTransferidos) throws SQLException {
        return new ProcessoTransferenciaVeiculoVisualizacao(
                rSet.getLong("COD_PROCESSO_TRANFERENCIA"),
                rSet.getString("NOME_COLABORADOR"),
                rSet.getObject("DATA_HORA_REALIZACAO", LocalDateTime.class),
                rSet.getString("NOME_UNIDADE_ORIGEM"),
                rSet.getString("NOME_UNIDADE_DESTINO"),
                rSet.getString("NOME_REGIONAL_ORIGEM"),
                rSet.getString("NOME_REGIONAL_DESTINO"),
                rSet.getString("OBSERVACAO"),
                veiculosTransferidos,
                rSet.getInt("QTD_PLACAS_TRANSFERIDAS"));
    }

    @NotNull
    public static VeiculoTransferidoVisualizacao createVeiculoTransferidoVisualizacao(
            @NotNull final ResultSet rSet,
            @NotNull final String placaAtual,
            @NotNull final List<String> codPneusTransferidos) throws SQLException {
        return new VeiculoTransferidoVisualizacao(
                placaAtual,
                rSet.getString("NOME_TIPO_VEICULO_MOMENTO_TRANSFERENCIA"),
                rSet.getLong("KM_VEICULO_MOMENTO_TRANSFERENCIA"),
                codPneusTransferidos);
    }
}
