package br.com.zalf.prolog.webservice.v3.security;

import br.com.zalf.prolog.webservice.v3.general.company.CompanyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "integracao", name = "token_integracao")
public final class IntegrationTokenEntity {
    @Id
    @Column(name = "token_integracao", nullable = false)
    @NotNull
    private String token;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_empresa", referencedColumnName = "codigo")
    @NotNull
    private CompanyEntity company;
    @Column(name = "ativo", nullable = false)
    @NotNull
    private Boolean isActive;
}
