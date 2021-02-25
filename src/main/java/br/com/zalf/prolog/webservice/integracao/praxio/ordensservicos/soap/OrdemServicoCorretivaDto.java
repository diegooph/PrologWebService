package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2021-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class OrdemServicoCorretivaDto {
    @JsonProperty("CodUnidadeChecklist")
    private final int codUnidadeChecklist;
    @JsonProperty("CodChecklistRealizado")
    private final int codChecklistRealizado;
    @JsonProperty("CodModeloChecklist")
    private final int codModeloChecklist;
    @JsonProperty("CpfColaboradorRealizacao")
    private final String cpfColaboradorRealizacao;
    @JsonProperty("PlacaVeiculoChecklist")
    private final String placaVeiculoChecklist;
    @JsonProperty("KmColetadoChecklist")
    private final int kmColetadoChecklist;
    @JsonProperty("TipoChecklist")
    private final String tipoChecklist;
    @JsonProperty("dataHoraRealizacaoUtc")
    private final LocalDateTime dataHoraRealizacaoUtc;
    @JsonProperty("Usuario")
    private final String usuario;
    @JacksonXmlElementWrapper(localName = "ListaPerguntasNokVO")
    @JsonProperty("PerguntasNokVO")
    private final List<PerguntaNokDto> perguntasNokHolder;
}
