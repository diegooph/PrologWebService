package test;

import java.util.List;

import org.junit.Test;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorService;
import junit.framework.TestCase;

public class ColaboradorTest extends TestCase {
	private static final String TOKEN = "i35sck1knc0usuptgt6gjprnu9";
	private static final Long CPF = 12345678987L;
	private static final Long COD_UNIDADE = 1L;
	private ColaboradorService service = new ColaboradorService();
	
	@Test
	public void testGetAll() {
		Request<?> request = new Request<>(TOKEN, CPF, COD_UNIDADE);
		List<Colaborador> colaboradores = service.getAll(request);
		assertNotNull(colaboradores);
		// Valida se encontrou algo
		assertTrue(colaboradores.size() > 0);
//		// Valida se encontrou um checklist
//		Checklist check_1 = service.getByCod(2L, "jsds");
//		Long cpf = 12345678987L;
//		assertEquals(cpf, check_1.getCpfColaborador());
//		assertEquals("MKE2501", check_1.getPlacaVeiculo());
//		assertEquals('s', check_1.getTipo());
	}

}
