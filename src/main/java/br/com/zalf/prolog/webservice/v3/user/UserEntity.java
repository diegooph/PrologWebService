package br.com.zalf.prolog.webservice.v3.user;

import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import br.com.zalf.prolog.webservice.v3.general.company.CompanyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.Serializable;
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
public final class UserEntity implements Serializable {
    @Id
    @Column(name = "codigo", unique = true)
    private Long id;
    @Column(name = "cpf")
    private Long cpf;
    @Column(name = "nome")
    private String name;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_unidade")
    private BranchEntity branchEntity;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_empresa")
    private CompanyEntity companyEntity;

    @NotNull
    public ZoneId getUserZoneId() {
        return ZoneId.of(this.branchEntity.getTimezone());
    }

    @NotNull
    public String getFormattedCpf() {
        return String.format("%011d", this.cpf);
    }
}
