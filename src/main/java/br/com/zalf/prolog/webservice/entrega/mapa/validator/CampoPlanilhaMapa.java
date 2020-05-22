package br.com.zalf.prolog.webservice.entrega.mapa.validator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created on 2020-05-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public final class CampoPlanilhaMapa {
    private String nomeCampoPlanilha;
    private String nomeCampoLegivel;
    private boolean campoObrigatorio;
    private PadraoPrenchimentoCampo padraoPreenchimentoCampo;
    private String padraoPreenchimentoCampoLegivel;
    private String regexValidacaoPadraoPreenchimento;
    private String exemploPreenchimento;
}
