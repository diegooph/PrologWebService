package test;

import org.junit.Test;

import br.com.zalf.prolog.webservice.frota.checklist.ChecklistService;
import junit.framework.TestCase;

public class ChecklistTest extends TestCase {
	private ChecklistService service = new ChecklistService();
	
	@Test
	public void testListaChecklists() {
//		List<Checklist> checklists = service.getAll();
//		assertNotNull(checklists);
//		// Valida se encontrou algo
//		assertTrue(checklists.size() > 0);
//		// Valida se encontrou um checklist
//		Checklist check_1 = service.getByCpf(2L, "jsds");
//		Long cpf = 12345678987L;
//		assertEquals(cpf, check_1.getCpfColaborador());
//		assertEquals("MKE2501", check_1.getPlacaVeiculo());
//		assertEquals('s', check_1.getTipo());
	}
	
//	@Test
//	public void testSalvarDeletarCheckList() {
//		Checklist c = new ChecklistSaida();
//		c.setCpfColaborador(12345678987L);
//		Date date = new Date(System.currentTimeMillis());
//		c.setData(date);
//		c.setPlacaVeiculo("MKE2501");
//		c.setTipo('s');
//		Map<Pergunta, Resposta> map = new HashMap<>();
//		Pergunta pergunta = new Pergunta();
//		pergunta.setCodigo(1L);
//		Resposta resposta = new Resposta();
//		resposta.setResposta("SIM");
//		map.put(pergunta, resposta);
//		c.setPerguntaRespostaMap(map);
		
		// Salva
//		service.insert(c);
		// Verifica se salvou consultando o id
//		Long id = c.getCodigo();
//		assertNotNull(id);
		
		// Busca no bd pra confirmar que o checklist foi salvo
//		c = service.getByCpf(id);
//		Long cpf = new Long(12345678987L);
//		assertEquals(cpf, c.getCpfColaborador());
//		assertEquals(id, c.getCodigo());
//		assertEquals(date, c.getData());
//		assertEquals("MKE2501", c.getPlacaVeiculo());
//		assertEquals('s', c.getTipo());
//		for ( Map.Entry<Pergunta, Resposta> entry : c.getPerguntaRespostaMap().entrySet()) {
//		    Pergunta p = entry.getKey();
//		    Resposta r = entry.getValue();
//		    assertEquals(pergunta.getCodigo(), p.getCodigo());
//		    assertEquals(resposta.getResposta(), r.getResposta());
//		}
//
//		// Atualiza o carro
//		c.setTipo('r');
//		service.update(c);
//		
//		// Busca o checklist novamente (deve estar atualizado)
//		c = service.getByCpf(id);
//		assertEquals('r', c.getTipo());
//		
//		// Deleta o carro
//		service.delete(id);
//		// Busca o carro novamente
//		c = service.getByCpf(id);
//		// Agora deve ser null
//		assertNull(c);	
//	}
}
