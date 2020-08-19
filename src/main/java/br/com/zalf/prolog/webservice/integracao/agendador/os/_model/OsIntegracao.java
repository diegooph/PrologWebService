package br.com.zalf.prolog.webservice.integracao.agendador.os._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2020-08-18
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class OsIntegracao {

    @NotNull
    private final String urlParaEnvio;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final Long codOsProlog;
    @NotNull
    private final LocalDateTime dataHoraAbertura;
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final Long kmVeiculoNaAbertura;
    @NotNull
    private final String cpfCriadorChecklist;
    @NotNull
    private final List<ItemOsIntegracao> alternativasNok;

}
