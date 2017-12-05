package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

import java.util.List;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ServicosFechadosHolder {
   private List<QuantidadeServicosFechados> servicosFechados;

   public List<QuantidadeServicosFechados> getServicosFechados() {
      return servicosFechados;
   }

   public void setServicosFechados(List<QuantidadeServicosFechados> servicosFechados) {
      this.servicosFechados = servicosFechados;
   }
}