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

        final VeiculoCadastro veiculoCadastro = new VeiculoCadastro(
                3L,
                103L,
                "THA-001",
                null,
                1L,
                120L,
                579L,
                1111L);

        Veiculo v = new Veiculo();
        ModeloVeiculo m = new ModeloVeiculo();
        m.setCodigo(1187L);
        TipoVeiculo t = new TipoVeiculo();
        t.setCodigo(610L);

        v.setKmAtual(5L);
        v.setNumeroFrota("THAIS3");
        v.setModelo(m);
        v.setTipo(t);


        VeiculoService vs = new VeiculoService();
        vs.update("hrte9i88ffec6vr256egqskifq","THA-001", v);
    }
}