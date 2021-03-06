package br.com.zalf.prolog.webservice.v3.security;

import br.com.zalf.prolog.webservice.v3.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "token_autenticacao")
public final class AuthenticationTokenEntity {
    @Id
    @Column(name = "token", nullable = false)
    private String token;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_colaborador", referencedColumnName = "codigo")
    private UserEntity user;
}
