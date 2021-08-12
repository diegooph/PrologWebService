package br.com.zalf.prolog.webservice.interno.suporte._model;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Data
@Builder(setterPrefix = "with")
public final class InternalUnidade {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    private final int totalColaboradoresAtivos;
    private final int totalVeiculosAtivos;
    @NotNull
    private final String timezone;
    @NotNull
    private final LocalDateTime dataHoraCadastro;
    private final boolean statusAtivo;
    @Nullable
    private final String codAuxiliar;
    @Nullable
    private final String pais;
    @Nullable
    private final String estadoProvincia;
    @Nullable
    private final String cidade;
    @Nullable
    private final String cep;
    @Nullable
    private final String endereco;
    @Nullable
    private final String latitude;
    @Nullable
    private final String longitude;
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final String nomeEmpresa;
    @NotNull
    private final Long codGrupo;
    @NotNull
    private final String nomeGrupo;
    @NotNull
    private final Integer[] pilaresLiberados;
    @Nullable
    private final LocalDateTime dataHoraUltimaAtualizacao;
    @Nullable
    private final String responsavelUltimaAtualizacao;
}
