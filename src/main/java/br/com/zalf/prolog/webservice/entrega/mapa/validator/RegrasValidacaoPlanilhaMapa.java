package br.com.zalf.prolog.webservice.entrega.mapa.validator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Map;

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
public final class RegrasValidacaoPlanilhaMapa {
    // O arquivo está localizado na pasta "resources" do projeto.
    private static final String NOME_ARQUIVO_MAPEAMENTO_PLANILHA_MAPA = "regras_colunas_planilha_mapa.yaml";
    private Map<Integer, CampoPlanilhaMapa> campos;

    @NotNull
    public static RegrasValidacaoPlanilhaMapa getInstance() {
        final Yaml yaml = new Yaml(new Constructor(RegrasValidacaoPlanilhaMapa.class));
        final InputStream inputStream = RegrasValidacaoPlanilhaMapa.class
                .getClassLoader()
                .getResourceAsStream(NOME_ARQUIVO_MAPEAMENTO_PLANILHA_MAPA);
        return yaml.load(inputStream);
    }
}
