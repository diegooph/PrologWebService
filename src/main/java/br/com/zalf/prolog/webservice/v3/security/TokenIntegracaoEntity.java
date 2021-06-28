package br.com.zalf.prolog.webservice.v3.security;

import br.com.zalf.prolog.webservice.v3.geral.empresa.EmpresaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "integracao", name = "token_integracao")
public class TokenIntegracaoEntity {
    @Id
    @Column(name = "token_integracao", nullable = false)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_empresa", referencedColumnName = "codigo")
    private EmpresaEntity empresa;

    @Column(name = "ativo", nullable = false)
    private Boolean isAtivo;
}
