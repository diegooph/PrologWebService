package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

import java.util.List;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ServicosFechadosHolder {
   private List<QuantidadeServicos> servicosFechados;

   public List<QuantidadeServicos> getServicosFechados() {
      return servicosFechados;
   }

   public void setServicosFechados(List<QuantidadeServicos> servicosFechados) {
      this.servicosFechados = servicosFechados;
   }
}