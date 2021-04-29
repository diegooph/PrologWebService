package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

/**
 * Created on 2021-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@JsonRootName("AlternativasNokVO")
public final class AlternativaNokDto {
    @JsonProperty("CodAlternativaNok")
    private final int codAlternativaNok;
    @JsonProperty("DescricaoAlternativaNok")
    private final String descricaoAlternativaNok;
    @JsonProperty("PrioridadeAlternativaNok")
    private final String prioridadeAlternativaNok;
}
