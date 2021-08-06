package br.com.zalf.prolog.webservice.interno.suporte._model;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Data
@Builder(setterPrefix = "with")
public final class InternalEmpresa {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @Nullable
    private final String logoThumbnailUrl;
    @NotNull
    private final LocalDateTime dataHoraCadastro;
    @Nullable
    private final String codAuxiliar;
    private final boolean statusAtivo;
    private final boolean logoConstaSiteComercial;
    @Nullable
    private final LocalDateTime dataHoraUltimaAtualizacao;
    @Nullable
    private final String responsavelUltimaAtualizacao;
}
