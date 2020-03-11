package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

public class ColaboradorTest extends TestCase {
	private static final String TOKEN = "i35sck1knc0usuptgt6gjprnu9";
	private static final Long CPF = 12345678987L;
	private static final Long COD_UNIDADE = 1L;
	private ColaboradorService service = new ColaboradorService();

	@Test
	public void testGetAll() {
		List<Colaborador> colaboradores = null;
		try {
			colaboradores = service.getAllByUnidade(COD_UNIDADE, false);
		} catch (ProLogException e) {
			e.printStackTrace();
		}
		assertNotNull(colaboradores);
		// Valida se encontrou algo
		assertTrue(colaboradores.size() > 0);
//		// Valida se encontrou um checklist
//		Checklist check_1 = service.getByCpf(2L, "jsds");
//		Long cpf = 12345678987L;
//		assertEquals(cpf, check_1.getCpfColaborador());
//		assertEquals("MKE2501", check_1.getPlacaVeiculo());
//		assertEquals('s', check_1.getTipo());
	}

}
