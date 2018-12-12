package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoProcessoColetaAfericao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class AfericaoIntegracaoPraxioConverter {

    public AfericaoIntegracaoPraxioConverter() {
        throw new IllegalStateException(AfericaoIntegracaoPraxioConverter.class.getSimpleName()
                + " cannot be instatiated!");
    }

    @NotNull
    static AfericaoIntegracaoPraxio convert(@NotNull final ResultSet rSet) throws SQLException {
        final AfericaoIntegracaoPraxio afericao = new AfericaoIntegracaoPraxio();
        afericao.setCodigo(rSet.getLong("COD_AFERICAO"));
        afericao.setCodUnidadeAfericao(rSet.getLong("COD_UNIDADE"));
        afericao.setCpfColaborador(String.valueOf(rSet.getLong("CPF_COLABORADOR")));
        afericao.setCodPneuAferido(rSet.getLong("COD_PNEU"));
        afericao.setNumeroFogoPneu(rSet.getString("NUMERO_FOGO"));
        afericao.setTempoRealizacaoMilis(rSet.getLong("TEMPO_REALIZACAO"));
        afericao.setVidaPneuMomentoAfericao(rSet.getInt("VIDA_MOMENTO_AFERICAO"));
        afericao.setDataHoraRealizacao(rSet.getObject("DATA_HORA", LocalDateTime.class));

        final TipoMedicaoColetadaAfericao tipoMedicao =
                TipoMedicaoColetadaAfericao.fromString(rSet.getString("TIPO_MEDICAO_COLETADA"));
        final TipoProcessoColetaAfericao tipoProcesso =
                TipoProcessoColetaAfericao.fromString(rSet.getString("TIPO_PROCESSO_COLETA"));
        afericao.setTipoMedicaoColetadaAfericao(tipoMedicao);
        afericao.setTipoProcessoColetaAfericao(tipoProcesso);

        if (tipoProcesso.equals(TipoProcessoColetaAfericao.PLACA)
                && tipoMedicao.equals(TipoMedicaoColetadaAfericao.SULCO_PRESSAO)) {
            return createAfericaoPlacaSulcoPressao(rSet, afericao);
        }

        if (tipoProcesso.equals(TipoProcessoColetaAfericao.PLACA)
                && tipoMedicao.equals(TipoMedicaoColetadaAfericao.SULCO)) {
            return createAfericaoPlacaSulco(rSet, afericao);
        }

        if (tipoProcesso.equals(TipoProcessoColetaAfericao.PLACA)
                && tipoMedicao.equals(TipoMedicaoColetadaAfericao.PRESSAO)) {
            return createAfericaoPlacaPressao(rSet, afericao);
        }

        if (tipoProcesso.equals(TipoProcessoColetaAfericao.PNEU_AVULSO)
                && tipoMedicao.equals(TipoMedicaoColetadaAfericao.SULCO)) {
            return createAfericaoPneuAvulsoSulco(rSet, afericao);
        }

        throw new IllegalStateException(
                "Não é possível existir uma aferição de PNEU_AVULSO que não seja com medição de SULCO");
    }

    @NotNull
    private static AfericaoIntegracaoPraxio createAfericaoPlacaSulcoPressao(
            @NotNull final ResultSet rSet,
            @NotNull final AfericaoIntegracaoPraxio afericao) throws SQLException {
        afericao.setPlacaVeiculoAferido(rSet.getString("PLACA_VEICULO"));
        afericao.setAlturaSulcoInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        afericao.setAlturaSulcoCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        afericao.setAlturaSulcoCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        afericao.setAlturaSulcoExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        afericao.setPressao(rSet.getDouble("PRESSAO"));
        afericao.setKmVeiculoMomentoAfericao(rSet.getLong("KM_VEICULO"));
        afericao.setPosicaoPneuMomentoAfericao(rSet.getInt("POSICAO"));
        return afericao;
    }

    @NotNull
    private static AfericaoIntegracaoPraxio createAfericaoPlacaSulco(
            @NotNull final ResultSet rSet,
            @NotNull final AfericaoIntegracaoPraxio afericao) throws SQLException {
        afericao.setPlacaVeiculoAferido(rSet.getString("PLACA_VEICULO"));
        afericao.setAlturaSulcoInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        afericao.setAlturaSulcoCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        afericao.setAlturaSulcoCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        afericao.setAlturaSulcoExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        afericao.setKmVeiculoMomentoAfericao(rSet.getLong("KM_VEICULO"));
        afericao.setPosicaoPneuMomentoAfericao(rSet.getInt("POSICAO"));
        return afericao;
    }

    @NotNull
    private static AfericaoIntegracaoPraxio createAfericaoPlacaPressao(
            @NotNull final ResultSet rSet,
            @NotNull final AfericaoIntegracaoPraxio afericao) throws SQLException {
        afericao.setPlacaVeiculoAferido(rSet.getString("PLACA_VEICULO"));
        afericao.setPressao(rSet.getDouble("PRESSAO"));
        afericao.setKmVeiculoMomentoAfericao(rSet.getLong("KM_VEICULO"));
        afericao.setPosicaoPneuMomentoAfericao(rSet.getInt("POSICAO"));
        return afericao;
    }

    @NotNull
    private static AfericaoIntegracaoPraxio createAfericaoPneuAvulsoSulco(
            @NotNull final ResultSet rSet,
            @NotNull final AfericaoIntegracaoPraxio afericao) throws SQLException {
        afericao.setAlturaSulcoInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        afericao.setAlturaSulcoCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        afericao.setAlturaSulcoCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        afericao.setAlturaSulcoExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        return afericao;
    }
}
