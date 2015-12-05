package br.com.empresa.oprojeto.webservice.domain.gsd;


import java.util.Date;
import java.util.List;
import java.util.Map;

import br.com.empresa.oprojeto.webservice.domain.Colaborador;
import br.com.empresa.oprojeto.webservice.domain.Pergunta;
import br.com.empresa.oprojeto.webservice.domain.Resposta;

/**
 * Created by luiz on 12/3/15.
 */
public class Gsd {
    private long codigo;
    private long cpfMotorista;
    private long cpfAjudante1;
    private long cpfAjudante2;
    private Date dataHora;
    private String placaVeiculo;
    private List<Pdv> pdvs;
    private Map<Colaborador, Pergunta> colaboradorPerguntaMap;
    private Map<Colaborador, Resposta> colaboradorRespostaMap;
    private String obervacoes;
    private String condicoesVeiculo;
    private String condicoesVia;
    private String urlFoto;
}
