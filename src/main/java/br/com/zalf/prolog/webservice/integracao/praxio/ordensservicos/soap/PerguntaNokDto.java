package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

import java.util.List;

/**
 * Created on 2021-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class PerguntaNokDto {
    @JsonProperty("CodPerguntaNok")
    private final int codPerguntaNok;
    @JsonProperty("DescricaoPerguntaNok")
    private final String descricaoPerguntaNok;
    @JacksonXmlElementWrapper(localName = "ListaAlternativasNok")
    @JsonProperty("AlternativasNokVO")
    private final List<AlternativaNokDto> alternativasNok;
}
