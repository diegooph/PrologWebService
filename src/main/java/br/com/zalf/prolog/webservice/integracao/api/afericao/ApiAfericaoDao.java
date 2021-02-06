package br.com.zalf.prolog.webservice.integracao.api.afericao;

import br.com.zalf.prolog.webservice.integracao.api.afericao._model.ApiPneuMedicaoRealizada;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
public interface ApiAfericaoDao {
    
    List<ApiPneuMedicaoRealizada> getAfericoesRealizadas(
            @NotNull final String tokenIntegracao,
            @Nullable final Long codigoProcessoAfericao,
            @Nullable final LocalDateTime dataHoraUltimaAtualizacaoUtc) throws Throwable;
}
