package br.com.zalf.prolog.webservice.integracao.api.afericao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import br.com.zalf.prolog.webservice.integracao.api.afericao._model.ApiPneuMedicaoRealizada;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Created on 2020-05-17
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiAfericaoConverter {
    @NotNull
    public static ApiPneuMedicaoRealizada createAfericaoRealizada(@NotNull final ResultSet rSet) throws SQLException {
        return new ApiPneuMedicaoRealizada(
                rSet.getLong("COD_AFERICAO"),
                rSet.getLong("COD_UNIDADE_AFERICAO"),
                rSet.getString("CPF_COLABORADOR"),
                rSet.getString("PLACA_VEICULO_AFERIDO"),
                rSet.getLong("COD_PNEU_AFERIDO"),
                rSet.getString("NUMERO_FOGO"),
                rSet.getDouble("ALTURA_SULCO_INTERNO"),
                rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"),
                rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"),
                rSet.getDouble("ALTURA_SULCO_EXTERNO"),
                rSet.getDouble("PRESSAO"),
                rSet.getLong("KM_VEICULO_MOMENTO_AFERICAO"),
                rSet.getLong("TEMPO_REALIZACAO_AFERICAO_EM_SEGUNDOS"),
                rSet.getInt("VIDA_MOMENTO_AFERICAO"),
                rSet.getInt("POSICAO_PNEU_MOMENTO_AFERICAO"),
                rSet.getObject("DATA_HORA_AFERICAO", LocalDateTime.class),
                TipoMedicaoColetadaAfericao.fromString(rSet.getString("TIPO_MEDICAO_COLETADA")),
                TipoProcessoColetaAfericao.fromString(rSet.getString("TIPO_PROCESSO_COLETA")));
    }
}
