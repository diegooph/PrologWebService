package br.com.zalf.prolog.webservice.frota.checklist.OLD;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jean on 24/05/16.
 */
@Deprecated
public class ModeloChecklist {
    private Long codigo;
    private String nome;
    private Long codUnidade;
    private List<TipoVeiculo> tiposVeiculoLiberados;
    private List<Cargo> cargosLiberados;
    private List<PerguntaRespostaChecklist> perguntas;
    private boolean ativo;

    public ModeloChecklist() {

    }

    @NotNull
    public List<Long> getCodigosTiposVeiculosLiberados() {
        final List<Long> codTiposVeiculo = new ArrayList<>();
        for (final TipoVeiculo tipoVeiculo : tiposVeiculoLiberados) {
            codTiposVeiculo.add(tipoVeiculo.getCodigo());
        }
        return codTiposVeiculo;
    }

    @NotNull
    public List<Long> getCodigosCargosLiberados() {
        final List<Long> codCargos = new ArrayList<>();
        for (final Cargo cargo : cargosLiberados) {
            codCargos.add(cargo.getCodigo());
        }
        return codCargos;
    }

    public List<TipoVeiculo> getTiposVeiculoLiberados() {
        return tiposVeiculoLiberados;
    }

    public void setTiposVeiculoLiberados(List<TipoVeiculo> tiposVeiculoLiberados) {
        this.tiposVeiculoLiberados = tiposVeiculoLiberados;
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

    public List<Cargo> getCargosLiberados() {
        return cargosLiberados;
    }

    public void setCargosLiberados(List<Cargo> listCargo) {
        this.cargosLiberados = listCargo;
    }

    public List<PerguntaRespostaChecklist> getPerguntas() {
        return perguntas;
    }

    public void setPerguntas(List<PerguntaRespostaChecklist> perguntas) {
        this.perguntas = perguntas;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(final boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "ModeloChecklist{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", codUnidade=" + codUnidade +
                ", tiposVeiculoLiberados=" + tiposVeiculoLiberados +
                ", cargosLiberados=" + cargosLiberados +
                ", perguntas=" + perguntas +
                ", ativo=" + ativo +
                '}';
    }
}
