package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * Created on 2021-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@JacksonXmlRootElement(localName = "GerarOrdemDeServicoCorretivaPrologResponse")
final class OrdemServicoResponseHolderDto {
    @JacksonXmlProperty(isAttribute = true)
    private final String xmlns = "http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros";

    private final OrdemServicoResponseDto ordemServicoResponse;

    public OrdemServicoResponseHolderDto(
            @JsonProperty("GerarOrdemDeServicoCorretivaPrologResult") final OrdemServicoResponseDto ordemServicoResponse) {
        this.ordemServicoResponse = ordemServicoResponse;
    }
}
