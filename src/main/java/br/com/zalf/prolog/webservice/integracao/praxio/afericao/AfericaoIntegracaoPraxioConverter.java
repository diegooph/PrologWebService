package br.com.zalf.prolog.webservice.integracao.praxio.afericao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AfericaoIntegracaoPraxioConverter {
    public AfericaoIntegracaoPraxioConverter() {
        throw new IllegalStateException(
                AfericaoIntegracaoPraxioConverter.class.getSimpleName() + " cannot be instatiated!");
    }

    @NotNull
    public static MedicaoIntegracaoPraxio convert(@NotNull final ResultSet rSet) throws SQLException {
        final MedicaoIntegracaoPraxio medicao = new MedicaoIntegracaoPraxio();
        medicao.setCodigo(rSet.getLong("COD_AFERICAO"));
        medicao.setCodUnidadeAfericao(rSet.getLong("COD_UNIDADE_AFERICAO"));
        medicao.setCpfColaborador(rSet.getString("CPF_COLABORADOR"));
        medicao.setCodPneuAferido(rSet.getLong("COD_PNEU_AFERIDO"));
        medicao.setNumeroFogoPneu(rSet.getString("NUMERO_FOGO"));
        medicao.setTempoRealizacaoEmSegundos(TimeUnit.MILLISECONDS.toSeconds(
                rSet.getLong("TEMPO_REALIZACAO_AFERICAO_EM_MILIS")));
        medicao.setVidaPneuMomentoAfericao(rSet.getInt("VIDA_MOMENTO_AFERICAO"));
        medicao.setDataHoraAfericao(rSet.getObject("DATA_HORA_AFERICAO", LocalDateTime.class));

        final TipoMedicaoColetadaAfericao tipoMedicao =
                TipoMedicaoColetadaAfericao.fromString(rSet.getString("TIPO_MEDICAO_COLETADA"));
        final TipoProcessoColetaAfericao tipoProcesso =
                TipoProcessoColetaAfericao.fromString(rSet.getString("TIPO_PROCESSO_COLETA"));
        medicao.setTipoMedicaoColetadaAfericao(tipoMedicao);
        medicao.setTipoProcessoColetaAfericao(tipoProcesso);

        if (tipoProcesso.equals(TipoProcessoColetaAfericao.PLACA)
                && tipoMedicao.equals(TipoMedicaoColetadaAfericao.SULCO_PRESSAO)) {
            return createMedicaoPlacaSulcoPressao(rSet, medicao);
        }

        if (tipoProcesso.equals(TipoProcessoColetaAfericao.PLACA)
                && tipoMedicao.equals(TipoMedicaoColetadaAfericao.SULCO)) {
            return createMedicaoPlacaSulco(rSet, medicao);
        }

        if (tipoProcesso.equals(TipoProcessoColetaAfericao.PLACA)
                && tipoMedicao.equals(TipoMedicaoColetadaAfericao.PRESSAO)) {
            return createMedicaoPlacaPressao(rSet, medicao);
        }

        if (tipoProcesso.equals(TipoProcessoColetaAfericao.PNEU_AVULSO)
                && tipoMedicao.equals(TipoMedicaoColetadaAfericao.SULCO)) {
            return createMedicaoPneuAvulsoSulco(rSet, medicao);
        }

        if (tipoProcesso.equals(TipoProcessoColetaAfericao.PNEU_AVULSO)
                && tipoMedicao.equals(TipoMedicaoColetadaAfericao.PRESSAO)) {
            return createMedicaoPneuAvulsoPressao(rSet, medicao);
        }

        return createMedicaoPneuAvulsoSulcoPressao(rSet, medicao);
    }

    @NotNull
    private static MedicaoIntegracaoPraxio createMedicaoPlacaSulcoPressao(
            @NotNull final ResultSet rSet,
            @NotNull final MedicaoIntegracaoPraxio medicao) throws SQLException {
        medicao.setPlacaVeiculoAferido(rSet.getString("PLACA_VEICULO_AFERIDO"));
        medicao.setAlturaSulcoInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        medicao.setAlturaSulcoCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        medicao.setAlturaSulcoCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        medicao.setAlturaSulcoExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        medicao.setPressao(rSet.getDouble("PRESSAO"));
        medicao.setKmVeiculoMomentoAfericao(rSet.getLong("KM_VEICULO_MOMENTO_AFERICAO"));
        medicao.setPosicaoPneuMomentoAfericao(rSet.getInt("POSICAO_PNEU_MOMENTO_AFERICAO"));
        return medicao;
    }

    @NotNull
    private static MedicaoIntegracaoPraxio createMedicaoPlacaSulco(
            @NotNull final ResultSet rSet,
            @NotNull final MedicaoIntegracaoPraxio medicao) throws SQLException {
        medicao.setPlacaVeiculoAferido(rSet.getString("PLACA_VEICULO_AFERIDO"));
        medicao.setAlturaSulcoInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        medicao.setAlturaSulcoCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        medicao.setAlturaSulcoCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        medicao.setAlturaSulcoExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        medicao.setKmVeiculoMomentoAfericao(rSet.getLong("KM_VEICULO_MOMENTO_AFERICAO"));
        medicao.setPosicaoPneuMomentoAfericao(rSet.getInt("POSICAO_PNEU_MOMENTO_AFERICAO"));
        return medicao;
    }

    @NotNull
    private static MedicaoIntegracaoPraxio createMedicaoPlacaPressao(
            @NotNull final ResultSet rSet,
            @NotNull final MedicaoIntegracaoPraxio medicao) throws SQLException {
        medicao.setPlacaVeiculoAferido(rSet.getString("PLACA_VEICULO_AFERIDO"));
        medicao.setPressao(rSet.getDouble("PRESSAO"));
        medicao.setKmVeiculoMomentoAfericao(rSet.getLong("KM_VEICULO_MOMENTO_AFERICAO"));
        medicao.setPosicaoPneuMomentoAfericao(rSet.getInt("POSICAO_PNEU_MOMENTO_AFERICAO"));
        return medicao;
    }

    @NotNull
    private static MedicaoIntegracaoPraxio createMedicaoPneuAvulsoSulco(
            @NotNull final ResultSet rSet,
            @NotNull final MedicaoIntegracaoPraxio medicao) throws SQLException {
        medicao.setAlturaSulcoInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        medicao.setAlturaSulcoCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        medicao.setAlturaSulcoCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        medicao.setAlturaSulcoExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        return medicao;
    }

    @NotNull
    private static MedicaoIntegracaoPraxio createMedicaoPneuAvulsoPressao(
            @NotNull final ResultSet rSet,
            @NotNull final MedicaoIntegracaoPraxio medicao) throws SQLException {
        medicao.setPressao(rSet.getDouble("PRESSAO"));
        return medicao;
    }

    @NotNull
    private static MedicaoIntegracaoPraxio createMedicaoPneuAvulsoSulcoPressao(
            @NotNull final ResultSet rSet,
            @NotNull final MedicaoIntegracaoPraxio medicao) throws SQLException {
        medicao.setAlturaSulcoInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        medicao.setAlturaSulcoCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        medicao.setAlturaSulcoCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        medicao.setAlturaSulcoExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        medicao.setPressao(rSet.getDouble("PRESSAO"));
        return medicao;
    }
}