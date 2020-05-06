package br.com.zalf.prolog.webservice.integracao.api.afericao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import br.com.zalf.prolog.webservice.integracao.api.afericao._model.AfericaoRealizada;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@Data
public final class AfericaoConverter {
    @NotNull
    public static AfericaoRealizada convert(@NotNull final ResultSet rSet) throws SQLException {
        final AfericaoRealizada medicao = new AfericaoRealizada();
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

        throw new IllegalStateException(
                "Não é possível existir uma aferição de PNEU_AVULSO que não seja com medição de SULCO");
    }

    @NotNull
    private static AfericaoRealizada createMedicaoPlacaSulcoPressao(
            @NotNull final ResultSet rSet,
            @NotNull final AfericaoRealizada medicao) throws SQLException {
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
    private static AfericaoRealizada createMedicaoPlacaSulco(
            @NotNull final ResultSet rSet,
            @NotNull final AfericaoRealizada medicao) throws SQLException {
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
    private static AfericaoRealizada createMedicaoPlacaPressao(
            @NotNull final ResultSet rSet,
            @NotNull final AfericaoRealizada medicao) throws SQLException {
        medicao.setPlacaVeiculoAferido(rSet.getString("PLACA_VEICULO_AFERIDO"));
        medicao.setPressao(rSet.getDouble("PRESSAO"));
        medicao.setKmVeiculoMomentoAfericao(rSet.getLong("KM_VEICULO_MOMENTO_AFERICAO"));
        medicao.setPosicaoPneuMomentoAfericao(rSet.getInt("POSICAO_PNEU_MOMENTO_AFERICAO"));
        return medicao;
    }

    @NotNull
    private static AfericaoRealizada createMedicaoPneuAvulsoSulco(
            @NotNull final ResultSet rSet,
            @NotNull final AfericaoRealizada medicao) throws SQLException {
        medicao.setAlturaSulcoInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        medicao.setAlturaSulcoCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        medicao.setAlturaSulcoCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        medicao.setAlturaSulcoExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        return medicao;
    }
}
