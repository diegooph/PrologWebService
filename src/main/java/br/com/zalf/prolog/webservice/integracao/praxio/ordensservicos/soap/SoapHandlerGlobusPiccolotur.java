package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import br.com.zalf.prolog.webservice.integracao.praxio.GlobusPiccoloturConstants;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class SoapHandlerGlobusPiccolotur {
    @NotNull
    public static final ObjectMapper XML_MAPPER = new XmlMapper()
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .registerModule(new JavaTimeModule());

    private static final String SOAP_CHARSET = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String SOAP_HEADER = "<soap:Header>" +
            "<AutenticacaoWebService xmlns=\"http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros\">" +
            "<Token>" + GlobusPiccoloturConstants.TOKEN_AUTENTICACAO_OS + "</Token>" +
            "<ShortCode>" + GlobusPiccoloturConstants.SHORT_CODE_AUTENTICACAO_OS + "</ShortCode>" +
            "<NomeMetodo>" + GlobusPiccoloturConstants.METODO_PARA_LIBERAR + "</NomeMetodo>" +
            "</AutenticacaoWebService>" +
            "</soap:Header>";
    private static final String SOAP_BODY_OPEN = "<soap:Body>";
    private static final String SOAP_BODY_CLOSE = "</soap:Body>";
    private static final String SOAP_ENVELOPE_OPEN =
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas" +
                    ".xmlsoap.org/soap/envelope/\">";
    private static final String SOAP_ENVELOPE_CLOSE = "</soap:Envelope>";

    @NotNull
    public String generateSoapRequestOrdemServico(@NotNull final OrdemServicoHolderDto ordemServico) {
        try {
            return SOAP_CHARSET
                    + SOAP_ENVELOPE_OPEN
                    + SOAP_HEADER
                    + SOAP_BODY_OPEN
                    + XML_MAPPER.writeValueAsString(ordemServico)
                    + SOAP_BODY_CLOSE
                    + SOAP_ENVELOPE_CLOSE;
        } catch (final JsonProcessingException e) {
            throw new GlobusPiccoloturException("Erro ao realizar o parse do objeto em XML para envio.", null, e);
        }
    }

    @NotNull
    public OrdemServicoResponseDto parseSoapResponseOrdemServico(@NotNull final String xml) {
        final String ordemServicoResponse = StringUtils.substringBetween(xml, SOAP_BODY_OPEN, SOAP_BODY_CLOSE);
        try {
            final OrdemServicoResponseHolderDto responseHolder =
                    XML_MAPPER.readValue(ordemServicoResponse, OrdemServicoResponseHolderDto.class);
            return responseHolder.getOrdemServicoResponse();
        } catch (final JsonProcessingException e) {
            throw new GlobusPiccoloturException("Erro ao realizar o parse do XML em objeto no recebimento.", null, e);
        }
    }
}
