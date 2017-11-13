package br.com.zalf.prolog.webservice.entrega.produtividade;

import java.util.Date;

/**
 * Created by Zart on 13/11/2017.
 */
public class PeriodoProdutividade {

    private final Date dataInicio;
    private final Date dataTermino;

    public PeriodoProdutividade(Date dataInicio, Date dataTermino) {
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }
}
