package br.com.zalf.prolog.webservice.entrega.mapa.validator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Essa classe não pode ter os campos final e nem os atributos anotados com @NotNull pois ela é instanciada pelo
 * SnakeYaml e se tiver essas propriedades a instanciação não funciona.
 * <p>
 * Created on 2020-05-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public final class ColunaPlanilhaMapa {
    private String nomeColunaPlanilha;
    private String nomeColunaLegivel;
    private boolean colunaObrigatoria;
    private PadraoPrenchimentoCampo padraoPreenchimentoColuna;
    private String padraoPreenchimentoColunaLegivel;
    private String regexValidacaoPadraoPreenchimento;
    private String exemploPreenchimento;
}
