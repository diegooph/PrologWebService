package br.com.zalf.prolog.webservice.integracao.integrador._model;

import com.google.common.collect.Table;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public final class TipoVeiculoConfigAfericaoHolder {
    @NotNull
    final Table<String, String, TipoVeiculoConfigAfericao> tipoVeiculoConfiguracao;
}
