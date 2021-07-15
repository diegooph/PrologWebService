package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import br.com.zalf.prolog.webservice.v3.geral.empresa.EmpresaEntity;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.ZoneId;

/**
 * Created on 2021-04-22
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "colaborador")
public final class ColaboradorEntity {
    @Id
    @Column(name = "cpf")
    private Long cpf;
    @Column(name = "codigo", unique = true)
    private Long codigo;
    @Column(name = "nome")
    private String nome;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_unidade")
    private UnidadeEntity unidade;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_empresa")
    private EmpresaEntity empresa;

    @NotNull
    public ZoneId getColaboradorZoneId() {
        return ZoneId.of(this.unidade.getTimezone());
    }

    @NotNull
    public String getCpfFormatado() {
        return String.format("%011d", this.cpf);
    }
}
