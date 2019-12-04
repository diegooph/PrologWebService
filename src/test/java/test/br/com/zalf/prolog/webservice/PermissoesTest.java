package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import com.google.common.collect.Iterables;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created on 2/1/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class PermissoesTest {

    @Test
    public void testPermissoesCodigosRepetidos() throws Exception {
        testPilarFrota();
        testPilarSeguranca();
        testPilarGente();
        testPilarEntrega();
    }

    @Test
    public void testPilarFrota() throws Exception {
        testPermissoesPilar(getConstantValues(Pilares.Frota.class));
    }

    @Test
    public void testPilarSeguranca() throws Exception {
        testPermissoesPilar(getConstantValues(Pilares.Seguranca.class));
    }

    @Test
    public void testPilarGente() throws Exception {
        testPermissoesPilar(getConstantValues(Pilares.Gente.class));
    }

    @Test
    public void testPilarEntrega() throws Exception {
        testPermissoesPilar(getConstantValues(Pilares.Entrega.class));
    }

    private void testPermissoesPilar(List<Integer> permissoesPilar) throws Exception {
        assertNotNull(permissoesPilar);
        assertTrue(permissoesPilar.size() > 0);
        System.out.println(permissoesPilar);
        System.out.println("Size: " + permissoesPilar.size());
        for (final Integer value : permissoesPilar) {
            assertNotNull(permissoesPilar);
            final int frequency = Iterables.frequency(permissoesPilar, value);
            assertEquals("Permissão " + value + " declarada mais de uma vez", 1, frequency);
        }
    }

    private List<Integer> getConstantValues(Class<?> clazz) throws Exception {
        final List<Integer> constantValues = new ArrayList<>();
        addConstansOfClass(clazz, constantValues);
        return constantValues;
    }

    private void addConstansOfClass(Class<?> clazz, List<Integer> constantValues) throws Exception {
        for (Field field : clazz.getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                constantValues.add((Integer) field.get(null));
            }
        }
        final Class<?>[] classes = clazz.getClasses();
        if (classes != null && classes.length != 0) {
            for (Class<?> aClass : classes) {
                addConstansOfClass(aClass, constantValues);
            }
        }
    }
}