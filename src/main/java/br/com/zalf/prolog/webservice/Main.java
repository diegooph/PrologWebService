package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoService;
import br.com.zalf.prolog.webservice.frota.veiculo.model.ModeloVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoCadastro;

public class Main {

    public static void main(final String[] args) {
        DatabaseManager.init();
    }
}