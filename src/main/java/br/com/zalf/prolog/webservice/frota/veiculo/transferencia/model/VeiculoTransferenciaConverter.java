package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model;

import br.com.zalf.prolog.webservice.frota.pneu._model.Sulcos;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.listagem.ProcessoTransferenciaVeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.VeiculoSelecaoTransferencia;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.DetalhesVeiculoTransferido;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.PneuVeiculoTransferido;
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
            @NotNull final List<Long> codPneusAplicadosVeiculo) {
        return new PneuTransferenciaRealizacao(
                codUnidadeOrigem,
                codUnidadeDestino,
                codColaboradorRealizacaoTransferencia,
                codPneusAplicadosVeiculo,
                null);
    }

    @NotNull
    public static VeiculoSelecaoTransferencia createVeiculoSelecaoTransferencia(@NotNull final ResultSet rSet)
            throws Throwable {
        return new VeiculoSelecaoTransferencia(
                rSet.getLong("COD_VEICULO"),
                rSet.getString("PLACA_VEICULO"),
                rSet.getLong("KM_ATUAL_VEICULO"),
                rSet.getInt("QTD_PNEUS_APLICADOS_VEICULO"));
    }

    @NotNull
    public static TipoVeiculoDiagrama createVeiculoSemDiagrama(@NotNull final ResultSet rSet) throws Throwable {
        return new TipoVeiculoDiagrama(
                rSet.getLong("COD_VEICULO"),
                rSet.getString("PLACA_VEICULO"),
                rSet.getLong("COD_TIPO_VEICULO"),
                rSet.getString("NOME_TIPO_VEICULO"),
                rSet.getBoolean("POSSUI_DIAGAMA"));
    }

    @NotNull
    public static ProcessoTransferenciaVeiculoListagem createProcessoTransferenciaVeiculoListagem(
            @NotNull final ResultSet rSet,
            @NotNull final Long codProcesso,
            @NotNull final List<String> placasTransferidas) throws Throwable {
        return new ProcessoTransferenciaVeiculoListagem(
                codProcesso,
                rSet.getString("NOME_COLABORADOR"),
                rSet.getObject("DATA_HORA_REALIZACAO", LocalDateTime.class),
                rSet.getString("NOME_UNIDADE_ORIGEM"),
                rSet.getString("NOME_UNIDADE_DESTINO"),
                rSet.getString("NOME_REGIONAL_ORIGEM"),
                rSet.getString("NOME_REGIONAL_DESTINO"),
                rSet.getString("OBSERVACAO"),
                placasTransferidas,
                rSet.getInt("QTD_PLACAS_TRANSFERIDAS"));
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
            @NotNull final Long codVeiculoAtual,
            @NotNull final List<String> codPneusTransferidos) throws SQLException {
        return new VeiculoTransferidoVisualizacao(
                codVeiculoAtual,
                rSet.getString("PLACA_TRANSFERIDA"),
                rSet.getString("NOME_TIPO_VEICULO_MOMENTO_TRANSFERENCIA"),
                rSet.getLong("KM_VEICULO_MOMENTO_TRANSFERENCIA"),
                codPneusTransferidos);
    }

    @NotNull
    public static DetalhesVeiculoTransferido createDetalhesVeiculoTransferido(
            @NotNull final ResultSet rSet,
            @NotNull final List<PneuVeiculoTransferido> pneusAplicadosMomentoTransferencia) throws SQLException {
        return new DetalhesVeiculoTransferido(
                rSet.getString("PLACA_VEICULO"),
                rSet.getLong("COD_DIAGRAMA_VEICULO"),
                rSet.getString("NOME_TIPO_VEICULO_MOMENTO_TRANSFERENCIA"),
                pneusAplicadosMomentoTransferencia);
    }

    @NotNull
    public static PneuVeiculoTransferido createPneuVeiculoTransferido(
            @NotNull final ResultSet rSet) throws SQLException {
        final double alturaSulcoExterno = rSet.getDouble("ALTURA_SULCO_EXTERNO");
        final Sulcos sulcos;
        if (!rSet.wasNull()) {
            sulcos = new Sulcos();
            sulcos.setExterno(alturaSulcoExterno);
            sulcos.setCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
            sulcos.setCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
            sulcos.setInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        } else {
            sulcos = null;
        }
        return new PneuVeiculoTransferido(
                rSet.getLong("COD_PNEU"),
                rSet.getString("CODIGO_CLIENTE"),
                sulcos,
                rSet.getDouble("PRESSAO_PNEU"),
                rSet.getInt("VIDA_MOMENTO_TRANSFERENCIA"),
                rSet.getInt("POSICAO_PNEU_TRANSFERENCIA"));
    }
}
