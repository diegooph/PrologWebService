package br.com.zalf.prolog.webservice.interceptors.v3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created on 2021-03-22
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
@PropertySource("classpath:configs/integracoes/operacoes_bloqueadas.yaml")
@ConfigurationProperties(prefix = "pneu")
public class OperacoesBloqueadasYaml {

    @Value("${unidades}")
    List<Long> unidades;
    @Value("${empresas}")
    List<Long> empresas;

    public void validateUnidade(Long unidade) {
        if (unidades.isEmpty()) {
            return;
        }
        if (unidades.stream().anyMatch(u -> u.equals(unidade))) {
            throw new RuntimeException("Para inserir pneus utilize seu sistema de gestão");
        }
    }

    public void validateEmpresa(Long empresa) {
        if (empresas.isEmpty()) {
            return;
        }
        if (empresas.stream().anyMatch(e -> e.equals(empresa))) {
            throw new RuntimeException("Para inserir pneus utilize seu sistema de gestão");
        }
    }
}
