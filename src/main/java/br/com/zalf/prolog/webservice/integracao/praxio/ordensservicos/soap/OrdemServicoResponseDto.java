package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@JacksonXmlRootElement(localName = "GerarOrdemDeServicoCorretivaPrologResponse")
public final class OrdemServicoResponseDto {
    private final boolean sucesso;
    @Nullable
    private final String mensagemRetorno;
    private final int codigoOs;

    public OrdemServicoResponseDto(
            @JsonProperty("Sucesso") final boolean sucesso,
            @JsonProperty("MensagemDeRetorno") @Nullable final String mensagemRetorno,
            @JsonProperty("CodigoOS") final int codigoOs) {
        this.sucesso = sucesso;
        this.mensagemRetorno = mensagemRetorno;
        this.codigoOs = codigoOs;
    }
}
