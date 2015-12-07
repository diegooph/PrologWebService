package test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import br.com.empresa.oprojeto.models.Pergunta;
import br.com.empresa.oprojeto.models.Resposta;
import br.com.empresa.oprojeto.models.checklist.Checklist;
import br.com.empresa.oprojeto.models.checklist.ChecklistSaida;
import br.com.empresa.oprojeto.webservice.services.ChecklistService;
import junit.framework.TestCase;

public class ChecklistTest extends TestCase {
	private ChecklistService service = new ChecklistService();
	
	@Test
	public void testListaChecklists() {
		List<Checklist> checklists = service.getAll();
		assertNotNull(checklists);
		// Valida se encontrou algo
		assertTrue(checklists.size() > 0);
		// Valida se encontrou um checklist
		Checklist check_1 = service.getByCod(1L);
		Long cpf = 12345678987L;
		assertEquals(cpf, check_1.getCpfColaborador());
		assertEquals("MKE2501", check_1.getPlacaVeiculo());
		assertEquals('s', check_1.getTipo());
	}
	
	@Test
	public void testSalvarDeletarCheckList() {
		Checklist checklist = new ChecklistSaida();
		checklist.setCpfColaborador(12345678987L);
		checklist.setData(new Date(System.currentTimeMillis()));
		checklist.setPlacaVeiculo("MKE2501");
		checklist.setTipo('s');
		Map<Pergunta, Resposta> map = new HashMap<>();
		Pergunta pergunta = new Pergunta();
		pergunta.setCodigo(1L);
		Resposta resposta = new Resposta();
		resposta.setResposta("SIM");
		map.put(pergunta, resposta);
		checklist.setPerguntaRespostaMap(map);
		
		// Salva
		service.save(checklist);
		// Verifica se salvou consultando o id
		Long id = checklist.getCodigo();
		assertNotNull(id);
		
		//Busca no bd pra confirmar que o checklist foi salvo
		checklist = service.getByCod(id);
		
		// Deleta o carro
		service.delete(id);
		// Busca o carro novamente
		checklist = service.getByCod(id);
		// Agora deve ser null
		assertNull(checklist);	
	}
}
