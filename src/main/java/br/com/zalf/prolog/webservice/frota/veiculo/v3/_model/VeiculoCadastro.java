package br.com.zalf.prolog.webservice.frota.veiculo.v3._model;

import lombok.Value;

/**
 * Created on 2021-03-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */

@Value(staticConstructor = "of")
public class VeiculoCadastro {
    Long codEmpresa;
    Long codUnidade;
    String placa;
    String identificadorFrota;
    Long codMarca;
    Long codModelo;
    Long codTipo;
    long kmAtual;
    boolean possuiHubodometro;
}