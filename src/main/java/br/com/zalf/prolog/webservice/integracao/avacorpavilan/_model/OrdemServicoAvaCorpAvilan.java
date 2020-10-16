package br.com.zalf.prolog.webservice.integracao.avacorpavilan._model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanConstants.*;

/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class OrdemServicoAvaCorpAvilan {
    @SerializedName("Grupo")
    private final short grupo = COD_GRUPO_AVILAN;
    @SerializedName("Empresa")
    private final short empresa = COD_EMPRESA_AVILAN;
    @Nullable
    @SerializedName("Filial")
    private final Integer filial;
    @Nullable
    @SerializedName("Unidade")
    private final Integer unidade;
    @SerializedName("TipoManutencao")
    private final short tipoManutencao = COD_TIPO_MANUTENCAO_AVILAN;
    @SerializedName("ObjetivoOrdemServico")
    private final short objetivoOrdemServico = COD_OBJETIVO_ORDEM_SERVICO_AVILAN;
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
    private final Long codigoUsuario;
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
    private final List<DefeitoAvaCorpAvilan> defeitosAvaCorpAvilan;
}
