-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Quando adicionamos uma unidade, é preciso criar um token para a funcionalidade de checklist unidade.
-- Para evitar possíveis erros, decidimos criar uma estrutura para fazer isso automaticamente, ou seja, todas as
-- unidades cadastradas devem conter um token.
--
-- A Trigger TG_UNIDADE_INSERE_TOKEN_CHECKLIST_OFFLINE é acionada ao inserir na tabela unidade e dispara a function
-- TG_FUNC_CHECKLIST_INSERE_TOKEN_UNIDADE_CHECKLIST_OFFLINE, que cria e registra o token.
--
-- Observação:
-- Apesar de todas as unidades terem o token para checklist offline, apenas as empresas que não constam na tabela
-- CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA terão acesso a essa funciondalide.
--
--
-- Histórico:
-- 2019-07-23 -> Function alterada (wvinim - PL-2189).
-- * Adicionado SECURITY DEFINER

-- CRIA FUNCTION PARA INSERIR UM TOKEN DE CHECKLIST OFFLINE ATRAVÉS DA TRIGGER AO INSERIR UMA UNIDADE
CREATE OR REPLACE FUNCTION TG_FUNC_CHECKLIST_INSERE_TOKEN_UNIDADE_CHECKLIST_OFFLINE()
  RETURNS TRIGGER AS $TG_UNIDADE_ADICIONA_TOKEN_CHECKLIST_OFFLINE$
BEGIN
  INSERT INTO CHECKLIST_OFFLINE_DADOS_UNIDADE (COD_UNIDADE, TOKEN_SINCRONIZACAO_CHECKLIST)
  VALUES (NEW.CODIGO, F_RANDOM_STRING(64));
  RETURN NEW;
END;
$TG_UNIDADE_ADICIONA_TOKEN_CHECKLIST_OFFLINE$
LANGUAGE plpgsql SECURITY DEFINER;

-- CRIA TRIGGER PARA INSERIR UM TOKEN DE CHECKLIST OFFLINE AO INSERIR UMA UNIDADE
CREATE CONSTRAINT TRIGGER TG_UNIDADE_INSERE_TOKEN_CHECKLIST_OFFLINE
  AFTER INSERT
  ON UNIDADE
  DEFERRABLE
  FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_CHECKLIST_INSERE_TOKEN_UNIDADE_CHECKLIST_OFFLINE();