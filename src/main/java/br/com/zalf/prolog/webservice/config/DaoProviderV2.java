package br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDaoV2;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2021-04-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Configuration
public class DaoProviderV2 {

    @Bean
    @NotNull
    public AfericaoDaoV2 provideAfericaoDao() {
        return Injection.provideAfericaoDao();
    }
}
