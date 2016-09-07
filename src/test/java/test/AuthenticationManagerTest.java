package test;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthenticationManager;
import junit.framework.TestCase;
import org.junit.Test;

import static test.TestConstants.*;

/**
 * Created by luiz on 07/09/16.
 */
public class AuthenticationManagerTest extends TestCase {
    private final AutenticacaoService service = new AutenticacaoService();

    @Test
    public void testAuthenticationFakeToken() throws InterruptedException {
        testForToken(FAKE_TOKEN);
    }

    @Test
    public void testAuthenticationRealToken() throws InterruptedException {
        testForToken(REAL_TOKEN);
    }

    private void testForToken(String token) throws InterruptedException {
        // AuthenticationManager foi criado corretamente
        assertNotNull(getManager());

        // Começou sem os tokens de teste setados
        assertFalse(getManager().isTokenInCache(token));
        assertFalse(getManager().isTokenInCache(token));

        // Busca pelo token falso no BD
        if (token.endsWith(FAKE_TOKEN))
            assertFalse(getManager().verifyIfTokenExists(token));
        else
            assertTrue(getManager().verifyIfTokenExists(token));
        // Agora ele deve estar na cache
        assertTrue(getManager().isTokenInCache(token));

        // Temos apenas um token em cache, o FAKE
        assertEquals(getManager().getCacheSize(), 1);

        // Assumindo que a unidade de tempo para expirar esteja em segundos
        sleep(1);
        assertTrue(getManager().isTokenInCache(token));
        assertEquals(getManager().getCacheSize(), 1);

        // Agora não deve estar mais em cache
        sleep(2);
        assertFalse(getManager().isTokenInCache(token));
        assertEquals(getManager().getCacheSize(), 0);
    }

    private AuthenticationManager getManager() {
        return AuthenticationManager.getInstance();
    }

    private void sleep(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
    }
}