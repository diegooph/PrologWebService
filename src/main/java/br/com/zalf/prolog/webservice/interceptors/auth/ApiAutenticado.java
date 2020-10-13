package br.com.zalf.prolog.webservice.interceptors.auth;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.Subject;
import java.security.Principal;

/**
 * Created on 08/10/2020
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public class ApiAutenticado implements Principal {
    private static final String TAG = ApiAutenticado.class.getSimpleName();
    @NotNull
    private final String token;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean implies(final Subject subject) {
        return false;
    }
}