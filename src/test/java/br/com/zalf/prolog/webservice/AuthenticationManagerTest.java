package br.com.zalf.prolog.webservice;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by luiz on 07/09/16.
 */
public class AuthenticationManagerTest extends TestCase {

    @Test
    public void testAuthenticationFakeToken() throws InterruptedException {
//        testForToken(FAKE_TOKEN);
    }

    @Test
    public void testAuthenticationRealToken() throws InterruptedException {
//        testForToken(REAL_TOKEN);
    }

    @Deprecated
    private void testForToken(String token) throws InterruptedException {
//        // AuthenticationManager foi criado corretamente
//        assertNotNull(getManager());
//
//        // Começou sem os tokens de teste setados
//        assertFalse(getManager().isTokenInCache(token));
//
//        // Busca pelo token falso no BD
//        if (token.equals(FAKE_TOKEN))
//            assertFalse(getManager().verifyIfTokenExists(token));
//        else
//            assertTrue(getManager().verifyIfTokenExists(token));
//        // Agora ele deve estar na cache
//        assertTrue(getManager().isTokenInCache(token));
//
//        // Temos apenas um token em cache
//        assertEquals(getManager().getCacheSize(), 1);
//
//        // Assumindo que a unidade de tempo para expirar esteja em segundos
//        sleepSeconds(1);
//        // 1 segundo não é tempo suficiente para a cache remover o token, então ele ainda tem que
//        // estar lá
//        assertTrue(getManager().isTokenInCache(token));
//        assertEquals(getManager().getCacheSize(), 1);
//
//        sleepSeconds(2);
//        // Agora não deve estar mais em cache
//        assertFalse(getManager().isTokenInCache(token));
//        assertEquals(getManager().getCacheSize(), 0);
    }

//    private AuthenticationManager getManager() {
//        return AuthenticationManager.getInstance();
//    }

    private void sleepSeconds(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
    }
}