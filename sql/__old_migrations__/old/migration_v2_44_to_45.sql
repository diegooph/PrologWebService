-- Essa migração deve ser executada quando o WS versão 45 for publicado.
BEGIN TRANSACTION;
  -- Alterações necessárias para a refatoração de Descartes de Pneus.
  -- ########################################################################################################
  -- Cria tabela para conter os possíveis motivos de descarte de um pneu, para cada empresa
  CREATE TABLE movimentacao_motivo_descarte_empresa(
    cod_empresa BIGINT NOT NULL,
    codigo SERIAL NOT NULL,
    motivo TEXT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_hora_insercao TIMESTAMP,
    data_hora_ultima_alteracao TIMESTAMP,
    CONSTRAINT pk_movimentacao_motivo_descarte_empresa PRIMARY KEY (codigo),
    CONSTRAINT fk_cod_empresa FOREIGN KEY (cod_empresa) REFERENCES empresa(codigo)
  );

  -- Adiciona coluna código do motivo de descarte na tabela de movimentacao destino
  ALTER TABLE movimentacao_destino ADD COLUMN cod_motivo_descarte INT NULL;
  ALTER TABLE movimentacao_destino ADD CONSTRAINT fk_cod_motivo_descarte
    FOREIGN KEY (cod_motivo_descarte) REFERENCES movimentacao_motivo_descarte_empresa(codigo);

  -- Adiciona URLs das imagens de descarte captadas
  ALTER TABLE movimentacao_destino ADD COLUMN url_imagem_descarte_1 TEXT NULL;
  ALTER TABLE movimentacao_destino ADD COLUMN url_imagem_descarte_2 TEXT NULL;
  ALTER TABLE movimentacao_destino ADD COLUMN url_imagem_descarte_3 TEXT NULL;

  -- Cria novas permissões necessárias
  INSERT INTO public.funcao_prolog_v11 (codigo, funcao, cod_pilar) VALUES (125, 'Movimentar pneus da análise para o descarte', 1);
  INSERT INTO public.funcao_prolog_v11 (codigo, funcao, cod_pilar) VALUES (124, 'Edição dos motivos de descarte de uma empresa', 1);
  INSERT INTO public.funcao_prolog_v11 (codigo, funcao, cod_pilar) VALUES (123, 'Cadastrar novos motivos para descarte de pneus', 1);

  -- Insere motivos de descarte de Pneu para a unidade de Barueri
  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Multiplos picotamentos na banda de rodagem', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Trinca uniforme pontual/circunferencial próx. linha de centragem', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Quebra do talão devido a utilização / montagem', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Contaminação (Químico / Derivado de  Petróleo)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desgaste localizado', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Rodagem vazio ou com baixa pressão', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Dano próximo ao talão', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Cortes e/ou perfurações com distância inferior a 45º', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Corte e/ou perfuração com tamanho acima do limite p/ conserto', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Cortes e perfurações em quantidades acima do limite p/ reparação', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Quebra por impacto', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Corte lateral', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Deterioração por contato com peças fixas do veículo', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Arrancamento do ombro (Manobra)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Dano na coroa por perfuração / objeto cortante', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Dano excessivo lateral', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha de conserto em estrada', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desgaste excessivo da banda de rodagem', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desgaste irregular (mecânico)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Bolsa de ar externa', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Rachadura circunferencial próxima ao talão', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Raspagem / Desgaste circunferencial na lateral', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desagregação de lonas no ombro ou banda (Diagonal)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desagregação de cintas no ombro ou banda (Radial)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Talão deteriorado por aquecimento', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desgaste excessivo no centro da banda de rodagem', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desgaste excessivo nos ombros', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Saliência lateral (sem reparação)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desgaste natural', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Deterioração lateral por excesso de marcação a fogo', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Ruptura lateral tipo zípper', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Soltura do Liner', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Deterioração ozônio', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desagregação de lonas internas (Diagonal)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desistência do cliente', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desagregação das cintas (Radial)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desagregação das lonas (Diagonal)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desagregação do ombro', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desagregação da lateral', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desagregação do liner', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desagregação na área do talão', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Fadiga natural', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Idade da carcaça fora de Especificação', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Reparos fora de Especificação (pelo usuário)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Geminados diferentes (pares diferentes - toco/truck/eixo livre)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Venda da carcaça sucateada', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha por utilização de reparo inadequado', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha na aplicação do reparo', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha na preparação do conserto', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha por soltura', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha na preparação do interior do pneu (radial/diagonal)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha no processo de vulcanização', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha na reparação do talão (calcanhar/sola/unha)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha por falta do suporte metálico', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Reparo não homologado pela Bridgestone Bandag', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha por dimensionamento do reparo', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha por alinhamento incorreto', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha na preparação da escareação', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Sobreposição de reparos e/ou angulação menor que 45° (graus)', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Dano não detectado no exame', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Falha de vulcanização', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Abertura na emenda da banda', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Desenho impróprio', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Largura da banda imprópria', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Raspagem imprópria', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Aplicação inadequada da banda', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Dano não visível', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Escareação inadequada', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Ausência de reparo visivelmente necessário', TRUE, current_timestamp, NULL);

  INSERT INTO movimentacao_motivo_descarte_empresa (cod_empresa, motivo, ativo, data_hora_insercao, data_hora_ultima_alteracao)
  VALUES (1,'Excesso de pé de borracha', TRUE, current_timestamp, NULL);
  -- ########################################################################################################
END TRANSACTION;