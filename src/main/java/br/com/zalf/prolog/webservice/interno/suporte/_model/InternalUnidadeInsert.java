package br.com.zalf.prolog.webservice.interno.suporte._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public final class InternalUnidadeInsert {
    @NotNull
    private final String nome;
    @NotNull
    private final String timezone;
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
    private final Long codGrupo;
}
