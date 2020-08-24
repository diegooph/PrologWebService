package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.os._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class OsAvilan {

    @NotNull
    private final String grupo = "1";
    @NotNull
    private final String empresa = "1";
    @Nullable
    private final String filial;
    @Nullable
    private final String unidade;
    @NotNull
    private final String tipoManutencao = "2";
    @NotNull
    private final String objetivoOrdemServico = "1";
    @NotNull
    private final Long numeroExterno;
    @NotNull
    private final LocalDateTime dtEmissao;
    @NotNull
    private final LocalDateTime dtinc;
    @NotNull
    private final String codigoUsuario = "100";
    @NotNull
    private final String veiculo;
    @NotNull
    private final Long marcadorVeiculo;
    @NotNull
    private final String motorista;
    @NotNull
    private final List<ItemOsAvilan> ordemServicoDefeitoIn;

}
