package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.os._model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvaCorpAvilanConstants.*;

/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class OsAvilan {
    @NotNull
    @SerializedName("Grupo")
    private final String grupo = COD_GRUPO_AVILAN;
    @NotNull
    @SerializedName("Empresa")
    private final String empresa = COD_EMPRESA_AVILAN;
    @Nullable
    @SerializedName("Filial")
    private final String filial;
    @Nullable
    @SerializedName("Unidade")
    private final String unidade;
    @NotNull
    @SerializedName("TipoManutencao")
    private final String tipoManutencao = COD_TIPO_MANUTENCAO_AVILAN;
    @NotNull
    @SerializedName("ObjetivoOrdemServico")
    private final String objetivoOrdemServico = COD_OBJETIVO_ORDEM_SERVICO_AVILAN;
    @NotNull
    @SerializedName("NumeroExterno")
    private final Long numeroExterno;
    @NotNull
    @SerializedName("DtEmissao")
    private final LocalDateTime dataHoraEmissao;
    @NotNull
    @SerializedName("Dtinc")
    private final LocalDateTime dataHoraInclusao;
    @NotNull
    @SerializedName("CodigoUsuario")
    private final String codigoUsuario = COD_USUARIO_AVILAN;
    @NotNull
    @SerializedName("Veiculo")
    private final String placaVeiculo;
    @NotNull
    @SerializedName("MarcadorVeiculo")
    private final Long kmVeiculo;
    @NotNull
    @SerializedName("Motorista")
    private final String cpfMotorista;
    @NotNull
    @SerializedName("OrdemServicoDefeitoIn")
    private final List<ItemOsAvilan> itensOrdemServico;
}
