package br.com.zalf.prolog.webservice.entrega.mapa.validator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Map;

/**
 * Created on 2020-05-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public final class CamposPlanilhaMapa {
    private Map<Integer, CampoPlanilhaMapa> campos;

    public void validate() {
        final Yaml yaml = new Yaml(new Constructor(CamposPlanilhaMapa.class));
        final InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("campos_planilha_mapa.yaml");
        final CamposPlanilhaMapa camposPlanilhaMapa = yaml.load(inputStream);
        System.out.println(camposPlanilhaMapa);
    }
}
