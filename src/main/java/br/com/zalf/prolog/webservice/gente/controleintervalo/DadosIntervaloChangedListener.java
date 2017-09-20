package br.com.zalf.prolog.webservice.gente.controleintervalo;


import com.sun.istack.internal.NotNull;

import java.sql.Connection;

public interface DadosIntervaloChangedListener {
    void onTiposIntervaloChanged(@NotNull final Connection connection, @NotNull final Long codUnidade) throws Throwable;
    void onCargoAtualizado();
}