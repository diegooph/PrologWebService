package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.colaborador.Funcao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;

import java.util.List;

/**
 * Created by jean on 24/05/16.
 */
public class ModeloChecklist {

    private Long codigo;
    private String nome;
    private Long codUnidade;
    private List<TipoVeiculo> listTipoVeiculo;
    private List<Funcao> listFuncao;
    private List<PerguntaRespostaChecklist> listPerguntas;

    public ModeloChecklist() {
    }

    public List<TipoVeiculo> getListTipoVeiculo() {
        return listTipoVeiculo;
    }

    public void setListTipoVeiculo(List<TipoVeiculo> listTipoVeiculo) {
        this.listTipoVeiculo = listTipoVeiculo;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public List<Funcao> getListFuncao() {
        return listFuncao;
    }

    public void setListFuncao(List<Funcao> listFuncao) {
        this.listFuncao = listFuncao;
    }

    public List<PerguntaRespostaChecklist> getListPerguntas() {
        return listPerguntas;
    }

    public void setListPerguntas(List<PerguntaRespostaChecklist> listPerguntas) {
        this.listPerguntas = listPerguntas;
    }

    @Override
    public String toString() {
        return "ModeloChecklist{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", codUnidade=" + codUnidade +
                ", listTipoVeiculo=" + listTipoVeiculo +
                ", listFuncao=" + listFuncao +
                ", listPerguntas=" + listPerguntas +
                '}';
    }
}
