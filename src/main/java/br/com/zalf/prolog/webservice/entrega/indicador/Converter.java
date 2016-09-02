package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.models.indicador.indicadores.quantidade.CaixaViagem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jean on 02/09/16.
 * classe que converte os itens que vem em um ResultSet para os objetos
 */
public class Converter {

    public static List<CaixaViagem> createExtratoCaixaViagem(ResultSet rSet)throws SQLException{
        List<CaixaViagem> itens = new ArrayList<>();
        while (rSet.next()){
            CaixaViagem item = new CaixaViagem();
            item.setData(rSet.getDate("DATA"))
                    .setMapa(rSet.getInt("MAPA"))
                    .setCxsCarregadas(rSet.getInt("CXCARREG"))
                    .setViagens(1)
                    .calculaResultado();
        }
        return itens;
    }
}
