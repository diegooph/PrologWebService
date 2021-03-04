-- Essa migração deve ser executada quando o WS versão 50 for publicado.
BEGIN TRANSACTION;

-- ########################################################################################################
-- ########################################################################################################
-- ################# CRIAÇÃO DE TABELAS PARA NORMALIZAR A URL DOS CHECKLISTS ##############################
-- ########################################################################################################
-- ########################################################################################################

-- Cria tabela para guardar as URLs de imagens de cada empresa e as gerais
CREATE TABLE IF NOT EXISTS CHECKLIST_GALERIA_IMAGENS (
  COD_IMAGEM BIGSERIAL NOT NULL,
  URL_IMAGEM TEXT NOT NULL,
  COD_EMPRESA BIGINT,
  DATA_HORA_CADASTRO TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
  STATUS_ATIVO BOOLEAN DEFAULT TRUE NOT NULL,
  CONSTRAINT FK_CHECKLIST_GALERIA_IMAGENS_EMPRESA FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA(CODIGO),
  CONSTRAINT PK_CHECKLIST_GALERIA_IMAGENS PRIMARY KEY (COD_IMAGEM)
);

-- Migra url da tabela checklist_pergunta para a Galeria
INSERT INTO CHECKLIST_GALERIA_IMAGENS(URL_IMAGEM, COD_EMPRESA)
  (SELECT
     cp.url_imagem,
     u.cod_empresa
   FROM checklist_perguntas AS cp
     JOIN unidade AS u
       ON cp.cod_unidade = u.codigo
   WHERE CP.URL_IMAGEM IS NOT NULL AND CP.URL_IMAGEM <> ''
   GROUP BY cp.url_imagem, u.cod_empresa);

-- Cria coluna no Checklist Pergunta para conter o código da imagem
ALTER TABLE CHECKLIST_PERGUNTAS ADD COLUMN COD_IMAGEM BIGINT;

ALTER TABLE CHECKLIST_PERGUNTAS
  ADD CONSTRAINT fk_checklist_perguntas_checklist_galeria_imagens
FOREIGN KEY (COD_IMAGEM)
REFERENCES CHECKLIST_GALERIA_IMAGENS(COD_IMAGEM);

-- Linkando COD_IMAGEM com a tabela CHECKLIST_PERGUNTAS.
-- Nessa query não utilizamos o código da empresa, pois se uma imagem é utilizada em mais de uma empresa, então ela
-- é uma imagem pública. Desse modo queremos pegar a primeira referência dessa imagem e botar esse código na
-- CHECKLIST_PERGUNTAS. Assim as outras URLs (mesma imagem mas com COD_IMAGEM diferente na galeria) podem ser deletadas
-- sem problema nenhum (já que não terão nenhuma referência na CHECKLIST_PERGUNTAS).
UPDATE CHECKLIST_PERGUNTAS CP
SET COD_IMAGEM =
(SELECT COD_IMAGEM
 FROM CHECKLIST_GALERIA_IMAGENS CGP
   JOIN UNIDADE U ON U.CODIGO = CP.COD_UNIDADE
 WHERE CP.URL_IMAGEM = CGP.URL_IMAGEM
 LIMIT 1);

-- Após fazer o migration
ALTER TABLE checklist_perguntas DROP COLUMN url_imagem;

-- Deixa imagens que são do ProLog públicas.
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/8.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/11.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/14.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/7.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/15.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/23.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/10.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/cinta_sider.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/2.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/17.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/8+-+adesvio+ANTT.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/12.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/9.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/6.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/sider.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/27+-+Baias.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/22.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/plataforma_levadica.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/18.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/13.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/bau-compressed.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/7+-+carrinhos.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/17+-+5S+limpeza.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/5.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/16.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/20.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/9+-+cofre.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/24.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/3.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/18+-+tampa+buzina%2C+porta+luvas.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/21.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/2+-+Parabrisa+e+vidros.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/24+-+doc+veiculo+antt.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/carroceria-compressed.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/4.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/28+-+Luzes+painel.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/19.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/25+-+nivel+de+combustivel.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/23+-+Documento+condutor.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/estofamento-motorista.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/estofamento-ajudante.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/Seta.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/velocimetro.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/Parachoque.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/para-brisa-e-vidros.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/veltec.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/Buzina.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/Limpador-de-para-brisa.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/Faixa-refletiva.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/Retrovisores.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/Lacre-da-placa.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/Lanterna-e-farol.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/Pneus.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/sirene-de-re%CC%81-(Conflito-de-codificac%CC%A7a%CC%83o-Unicode).png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/ANTT%2C-CRLV-e-cronotacografo.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/Pisca-Alerta.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/vazamento.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Avilan/checklist-rota/puxador-baia.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Avilan/checklist-rota/macaco-triangulo.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Translecchi/motor_partida.JPG';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Athivalog/Cuiaba/paleteira.png';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/Athivalog/Cuiaba/vazamento_ar.jpeg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/25+-+nivel+de+combustivel-ponteiro-a-esquerda.jpg';
UPDATE CHECKLIST_GALERIA_IMAGENS SET COD_EMPRESA = NULL WHERE URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/27+-+Baias.jpg';

-- Deleta da galeria todas as imagens que não estão sendo referenciadas por nenhuma empresa.
DELETE FROM CHECKLIST_GALERIA_IMAGENS
WHERE COD_IMAGEM IN
      (SELECT cod_imagem
       FROM checklist_galeria_imagens
       EXCEPT ALL
       SELECT cod_imagem
       FROM checklist_perguntas);

-- Temos duas imagens da galeria pública do ProLog que estão duplicadas no S3 (totalizando 4 imagens).
-- As seguintes imagens:
-- https://s3-sa-east-1.amazonaws.com/checklist-imagens/27+-+Baias.jpg
-- e https://s3-sa-east-1.amazonaws.com/checklist-imagens/25+-+nivel+de+combustivel-ponteiro-a-esquerda.jpg
-- são cópias de outras duas. Para podermos remover qualquer referencia às duas URLs acima, atualizamos qualquer
-- pergunta que esteja apontando para elas para apontarem para a imagem original.
UPDATE CHECKLIST_PERGUNTAS SET COD_IMAGEM =
  (SELECT COD_IMAGEM
   FROM CHECKLIST_GALERIA_IMAGENS G
   WHERE G.URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/24.jpg' LIMIT 1)
WHERE COD_IMAGEM =
      (SELECT COD_IMAGEM
       FROM CHECKLIST_GALERIA_IMAGENS G
       WHERE G.URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/27+-+Baias.jpg' LIMIT 1);

UPDATE CHECKLIST_PERGUNTAS SET COD_IMAGEM =
  (SELECT COD_IMAGEM
   FROM CHECKLIST_GALERIA_IMAGENS G
   WHERE G.URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/25+-+nivel+de+combustivel.jpg' LIMIT 1)
WHERE COD_IMAGEM =
      (SELECT COD_IMAGEM
       FROM CHECKLIST_GALERIA_IMAGENS G
       WHERE G.URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/25+-+nivel+de+combustivel-ponteiro-a-esquerda.jpg' LIMIT 1);

-- Após removermos as referências, podemos deletar da galeria as imagens duplicadas. É possível remover do S3 depois
-- também.
DELETE FROM CHECKLIST_GALERIA_IMAGENS G WHERE
  G.URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/27+-+Baias.jpg'
  OR G.URL_IMAGEM = 'https://s3-sa-east-1.amazonaws.com/checklist-imagens/25+-+nivel+de+combustivel-ponteiro-a-esquerda.jpg';

-- Adiciona imagens da Avilan que podem ser públicas também e foram removidas acima por não serem referenciadas por
-- nenhuma empresa.
INSERT INTO public.checklist_galeria_imagens (url_imagem, cod_empresa, status_ativo)
VALUES ('https://s3-sa-east-1.amazonaws.com/checklist-imagens/Avilan/checklist-rota/puxador-baia.jpg', null, true);
INSERT INTO public.checklist_galeria_imagens (url_imagem, cod_empresa, status_ativo)
VALUES ('https://s3-sa-east-1.amazonaws.com/checklist-imagens/Avilan/checklist-rota/macaco-triangulo.jpg', null, true);
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- Cria tabelas para disponibilizar modelos padrões de checklist do ProLog.
CREATE TABLE checklist_modelo_prolog
(
  codigo       SMALLSERIAL          NOT NULL
    CONSTRAINT pk_check_modelo_prolog
    PRIMARY KEY,
  nome         TEXT                 NOT NULL,
  status_ativo BOOLEAN DEFAULT TRUE NOT NULL
);

CREATE TABLE checklist_perguntas_prolog
(
  codigo                      BIGSERIAL            NOT NULL
    CONSTRAINT pk_check_perguntas_prolog
    PRIMARY KEY,
  cod_checklist_modelo_prolog SMALLINT             NOT NULL
    CONSTRAINT fk_check_perguntas_prolog_check_modelo_prolog
    REFERENCES checklist_modelo_prolog,
  ordem                       SMALLINT             NOT NULL,
  pergunta                    TEXT                 NOT NULL,
  prioridade                  VARCHAR(255)         NOT NULL
    CONSTRAINT prioridade
    CHECK ((prioridade) :: TEXT = ANY
           ((ARRAY ['BAIXA' :: CHARACTER VARYING, 'ALTA' :: CHARACTER VARYING, 'CRITICA' :: CHARACTER VARYING]) :: TEXT [])),
  single_choice               BOOLEAN              NOT NULL,
  status_ativo                BOOLEAN DEFAULT TRUE NOT NULL,
  cod_imagem                  BIGINT
    CONSTRAINT fk_check_perguntas_prolog_check_galeria_imagens
    REFERENCES checklist_galeria_imagens
);

CREATE TABLE checklist_alternativa_pergunta_prolog
(
  codigo                      BIGSERIAL            NOT NULL
    CONSTRAINT pk_check_alternativa_pergunta_prolog
    PRIMARY KEY,
  cod_checklist_modelo_prolog SMALLINT             NOT NULL
    CONSTRAINT fk_check_alternativa_prolog_check_modelo_prolog
    REFERENCES checklist_modelo_prolog,
  cod_pergunta_prolog         BIGINT               NOT NULL
    CONSTRAINT fk_check_alternativa_prolog_check_perguntas_prolog
    REFERENCES checklist_perguntas_prolog,
  ordem                       SMALLINT             NOT NULL,
  alternativa                 TEXT                 NOT NULL,
  status_ativo                BOOLEAN DEFAULT TRUE NOT NULL
);
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- Cria function de relatório contendo os dados da última aferição para os pneus.
CREATE FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADE BIGINT, F_TIME_ZONE_UNIDADE TEXT)
  RETURNS TABLE(
    "PNEU"                  TEXT,
    "MARCA"                 TEXT,
    "MODELO"                TEXT,
    "MEDIDAS"               TEXT,
    "PLACA"                 TEXT,
    "TIPO"                  TEXT,
    "POSIÇÃO"               TEXT,
    "SULCO INTERNO"         TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO"         TEXT,
    "PRESSÃO (PSI)"         TEXT,
    "VIDA"                  TEXT,
    "DOT"                   TEXT,
    "ÚLTIMA AFERIÇÃO"       TEXT)
LANGUAGE SQL
AS $$
SELECT
  P.codigo                                         AS COD_PNEU,
  map.nome                                         AS NOME_MARCA,
  mp.nome                                          AS NOME_MODELO,
  ((((dp.largura || '/' :: TEXT) || dp.altura) || ' R' :: TEXT) ||
   dp.aro)                                         AS MEDIDAS,
  coalesce(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU,
           '-')                                    AS PLACA,
  coalesce(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-') AS TIPO_VEICULO,
  coalesce(POSICAO_PNEU_VEICULO.POSICAO_PNEU,
           '-')                                    AS POSICAO_PNEU,
  coalesce(trunc(P.altura_sulco_interno :: NUMERIC, 2) :: TEXT,
           '-')                                    AS SULCO_INTERNO,
  coalesce(trunc(P.altura_sulco_central_interno :: NUMERIC, 2) :: TEXT,
           '-')                                    AS SULCO_CENTRAL_INTERNO,
  coalesce(trunc(P.altura_sulco_central_externo :: NUMERIC, 2) :: TEXT,
           '-')                                    AS SULCO_CENTRAL_EXTERNO,
  coalesce(trunc(P.altura_sulco_externo :: NUMERIC, 2) :: TEXT,
           '-')                                    AS SULCO_EXTERNO,
  coalesce(trunc(P.pressao_atual) :: TEXT,
           '-')                                    AS PRESSAO_ATUAL,
  P.vida_atual :: TEXT                             AS VIDA_ATUAL,
  COALESCE(P.DOT, '-')                             AS DOT,
  coalesce(to_char(DATA_ULTIMA_AFERICAO.ULTIMA_AFERICAO AT TIME ZONE F_TIME_ZONE_UNIDADE, 'DD/MM/YYYY HH24:MI'),
           'não aferido')                          AS ULTIMA_AFERICAO
FROM PNEU P
  JOIN dimensao_pneu dp ON dp.codigo = p.cod_dimensao
  JOIN unidade u ON u.codigo = p.cod_unidade
  JOIN modelo_pneu mp ON mp.codigo = p.cod_modelo AND mp.cod_empresa = u.cod_empresa
  JOIN marca_pneu map ON map.codigo = mp.cod_marca
  LEFT JOIN
  (SELECT
     PON.nomenclatura AS POSICAO_PNEU,
     VP.cod_pneu      AS CODIGO_PNEU,
     VP.placa         AS PLACA_VEICULO_PNEU,
     VP.cod_unidade   AS COD_UNIDADE_PNEU,
     VT.nome          AS VEICULO_TIPO
   FROM veiculo V
     JOIN veiculo_pneu VP ON VP.placa = V.placa AND VP.cod_unidade = V.cod_unidade
     JOIN veiculo_tipo vt ON v.cod_unidade = vt.cod_unidade AND v.cod_tipo = vt.codigo
     -- LEFT JOIN porque unidade pode não ter
     LEFT JOIN pneu_ordem_nomenclatura_unidade pon ON pon.cod_unidade = v.cod_unidade AND pon.cod_tipo_veiculo = v.cod_tipo
                                                 AND vp.posicao = pon.posicao_prolog
   WHERE V.cod_unidade = F_COD_UNIDADE
   ORDER BY VP.cod_pneu) AS POSICAO_PNEU_VEICULO
    ON P.codigo = POSICAO_PNEU_VEICULO.CODIGO_PNEU AND P.cod_unidade = POSICAO_PNEU_VEICULO.COD_UNIDADE_PNEU
  LEFT JOIN
  (SELECT
     AV.cod_pneu,
     A.cod_unidade                  AS COD_UNIDADE_DATA,
     MAX(A.data_hora AT TIME ZONE F_TIME_ZONE_UNIDADE) AS ULTIMA_AFERICAO
   FROM AFERICAO A
     JOIN afericao_valores AV ON A.codigo = AV.cod_afericao
   GROUP BY 1, 2) AS DATA_ULTIMA_AFERICAO
    ON DATA_ULTIMA_AFERICAO.COD_UNIDADE_DATA = P.cod_unidade AND DATA_ULTIMA_AFERICAO.cod_pneu = P.codigo
WHERE P.cod_unidade = F_COD_UNIDADE
ORDER BY COD_PNEU;
$$;
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- Adiciona coluna para sabermos se um pneu é completamente novo, nunca rodado.
ALTER TABLE PNEU ADD COLUMN PNEU_NOVO_NUNCA_RODADO BOOLEAN DEFAULT FALSE NOT NULL;
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ############## ALTERAÇÕES PARA PARAMETRIZAR SE O CÓDIGO DE UM PNEU É OU NÃO ALFANUMÉRICO ###############
-- ########################################################################################################
-- ########################################################################################################
-- Renomeia tabela.
ALTER TABLE EMPRESA_RESTRICAO_PNEU RENAME TO PNEU_RESTRICAO_UNIDADE;
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ################# RELATÓRIO DE INTERVALOS ESTILO FOLHA DE PONTO ########################################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_INTERVALO_FOLHA_DE_PONTO(F_COD_UNIDADE       BIGINT,
                                                                   F_CPF_COLABORADOR   BIGINT,
                                                                   F_DATA_INICIAL      DATE,
                                                                   F_DATA_FINAL        DATE,
                                                                   F_TIME_ZONE_UNIDADE TEXT)
  RETURNS TABLE(
    CPF_COLABORADOR                     BIGINT,
    NOME_COLABORADOR                    TEXT,
    COD_TIPO_INTERVALO                  BIGINT,
    DATA_HORA_INICIO                    TIMESTAMP,
    DATA_HORA_FIM                       TIMESTAMP)
LANGUAGE SQL
AS $$
SELECT
  CPF_COLABORADOR,
  C.NOME AS NOME_COLABORADOR,
  COD_TIPO_INTERVALO,
  DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE,
  DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE
FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, F_CPF_COLABORADOR, NULL) F
  JOIN COLABORADOR C
    ON F.CPF_COLABORADOR = C.CPF
WHERE
  (F.data_hora_inicio AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
  AND
  (F.data_hora_inicio AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL
ORDER BY F.CPF_COLABORADOR, F.DATA_HORA_INICIO ASC;
$$;
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ################# CRIAÇÃO DE TABELA PARA PARAMETRIZAR AFERICAO DE ESTEPE ###############################
-- ########################################################################################################
-- ########################################################################################################
CREATE TABLE IF NOT EXISTS AFERICAO_ESTEPES_TIPOS_VEICULOS_BLOQUEADOS (
  COD_UNIDADE BIGINT NOT NULL,
  COD_TIPO_VEICULO BIGINT NOT NULL,
  CONSTRAINT FK_AFERICAO_ESTEPES_TIPOS_VEICULOS_BLOQUEADOS_UNIDADE FOREIGN KEY (COD_UNIDADE) REFERENCES UNIDADE(CODIGO),
  CONSTRAINT FK_AFERICAO_ESTEPES_TIPOS_VEICULOS_BLOQUEADOS_VEICULO_TIPO FOREIGN KEY (COD_TIPO_VEICULO, COD_UNIDADE) REFERENCES VEICULO_TIPO(CODIGO, COD_UNIDADE),
  CONSTRAINT PK_AFERICAO_ESTEPES_TIPOS_VEICULOS_BLOQUEADOS PRIMARY KEY (COD_UNIDADE, COD_TIPO_VEICULO)
);
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ################# CRIA DUAS NOVAS POSIÇÕES PARA SUPORTAR LAYOUT DE EMPILHADEIRAS #######################
-- ########################################################################################################
-- ########################################################################################################
INSERT INTO public.pneu_posicao (posicao_pneu, descricao_posicao) VALUES (122, null);
INSERT INTO public.pneu_posicao (posicao_pneu, descricao_posicao) VALUES (112, null);

DELETE FROM PUBLIC.PNEU_ORDEM;
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (111, 1);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (112, 2);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (211, 3);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (212, 4);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (311, 5);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (312, 6);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (411, 7);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (412, 8);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (421, 9);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (422, 10);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (321, 11);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (322, 12);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (221, 13);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (222, 14);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (121, 15);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (122, 16);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (900, 90);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (901, 91);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (902, 92);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (903, 93);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (904, 94);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (905, 95);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (906, 96);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (907, 97);
INSERT INTO public.pneu_ordem (posicao_prolog, ordem_exibicao) VALUES (908, 98);
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- #################### ADICIONA COLUNA PARA SABERMOS SE UM EIXO É DIRECIONAL OU NÃO ######################
-- ########################################################################################################
-- ########################################################################################################
ALTER TABLE VEICULO_DIAGRAMA_EIXOS ADD COLUMN EIXO_DIRECIONAL BOOLEAN;

-- ATUALMENTE TODOS OS DIRECIONAIS SÃO OS D.
UPDATE VEICULO_DIAGRAMA_EIXOS V SET EIXO_DIRECIONAL = CASE WHEN V.TIPO_EIXO = 'D' THEN TRUE ELSE FALSE END;

ALTER TABLE VEICULO_DIAGRAMA_EIXOS ALTER COLUMN EIXO_DIRECIONAL SET NOT NULL;
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- #################### CRIAÇÃO DO RELATÓRIO DE ESTRATIFICAÇÃO DE SERVIÇOS ################################
-- ########################################################################################################
-- ########################################################################################################
-- Criação da tabela de nomenclatura para vidas do pneu
CREATE TABLE IF NOT EXISTS pneu_vida_nomenclatura (
  COD_VIDA BIGINT NOT NULL,
  NOME VARCHAR NOT NULL,
  CONSTRAINT PK_PNEU_VIDA_NOMENCLATURA PRIMARY KEY (COD_VIDA)
);

INSERT INTO pneu_vida_nomenclatura VALUES (1, 'Pneu Novo');
INSERT INTO pneu_vida_nomenclatura VALUES (2, '2ª Vida(1ª Recapagem)');
INSERT INTO pneu_vida_nomenclatura VALUES (3, '3ª Vida(2ª Recapagem)');
INSERT INTO pneu_vida_nomenclatura VALUES (4, '4ª Vida(3ª Recapagem)');
INSERT INTO pneu_vida_nomenclatura VALUES (5, '5ª Vida(4ª Recapagem)');
INSERT INTO pneu_vida_nomenclatura VALUES (6, '6ª Vida(5ª Recapagem)');

-- Criação da tabela de nomenclatura para recapes do pneu
CREATE TABLE IF NOT EXISTS pneu_recapagem_nomenclatura (
  COD_TOTAL_VIDA BIGINT NOT NULL,
  NOME VARCHAR NOT NULL,
  CONSTRAINT PK_PNEU_RECAPAGEM_NOMENCLATURA PRIMARY KEY (COD_TOTAL_VIDA)
);

INSERT INTO pneu_recapagem_nomenclatura VALUES (1, '1 Recapagem');
INSERT INTO pneu_recapagem_nomenclatura VALUES (2, '2 Recapagens');
INSERT INTO pneu_recapagem_nomenclatura VALUES (3, '3 Recapagens');
INSERT INTO pneu_recapagem_nomenclatura VALUES (4, '4 Recapagens');
INSERT INTO pneu_recapagem_nomenclatura VALUES (5, '5 Recapagens');

-- RELATORIO ESTRATIFICADO SERVIÇOS ABERTOS
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_ESTRATIFICADO_SERVICOS_PNEUS_EM_ABERTO(F_COD_UNIDADE BIGINT,F_DATA_INICIAL DATE, F_DATA_FINAL DATE, F_DATA_ATUAL DATE, F_TIME_ZONE TEXT)
  RETURNS TABLE(
    "CÓDIGO DO SERVIÇO"                       BIGINT,
    "TIPO DO SERVIÇO"                         TEXT,
    "QTD APONTAMENTOS"                        INTEGER,
    "DATA HORA ABERTURA"                      TEXT,
    "QTD DIAS EM ABERTO"                      TEXT,
    "NOME DO COLABORADOR"                     TEXT,
    "PLACA"                                   TEXT,
    "AFERIÇÃO"                                BIGINT,
    "PNEU"                                    TEXT,
    "SULCO INTERNO"                           REAL,
    "SULCO CENTRAL INTERNO"                   REAL,
    "SULCO CENTRAL EXTERNO"                   REAL,
    "SULCO EXTERNO"                           REAL,
    "PRESSÃO (PSI)"                           REAL,
    "PRESSÃO RECOMENDADA (PSI)"               REAL,
    "POSIÇÃO DO PNEU"                         INTEGER,
    "VIDA ATUAL"                              TEXT,
    "TOTAL DE VIDAS"                          TEXT)
LANGUAGE SQL
AS $$
SELECT
  AM.CODIGO AS CODIGO_SERVICO,
  AM.TIPO_SERVICO,
  AM.QT_APONTAMENTOS,
  to_char((A.DATA_HORA AT TIME ZONE F_TIME_ZONE), 'DD/MM/YYYY HH24:MI')::TEXT AS DATA_HORA_ABERTURA,
  (SELECT (EXTRACT(EPOCH FROM AGE(F_DATA_ATUAL,
                                  A.DATA_HORA AT TIME ZONE F_TIME_ZONE))
           / 86400)::INTEGER)::TEXT AS DIAS_EM_ABERTO,
  C.NOME AS NOME_COLABORADOR,
  A.PLACA_VEICULO AS PLACA_VEICULO,
  A.CODIGO AS COD_AFERICAO,
  AV.COD_PNEU AS COD_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_EXTERNO AS SULCO_EXTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_CENTRAL_EXTERNO AS SULCO_CENTRAL_EXTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_CENTRAL_INTERNO AS SULCO_CENTRAL_INTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_INTERNO AS SULCO_INTERNO_PNEU_PROBLEMA,
  AV.PSI AS PRESSAO_PNEU_PROBLEMA,
  P.PRESSAO_RECOMENDADA,
  AV.POSICAO AS POSICAO_PNEU_PROBLEMA,
  PVN.NOME AS VIDA_PNEU_PROBLEMA,
  PRN.NOME AS TOTAL_RECAPAGENS
FROM AFERICAO_MANUTENCAO AM
  JOIN PNEU P ON AM.COD_UNIDADE = P.COD_UNIDADE AND AM.COD_PNEU = P.CODIGO
  JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO
  JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
  JOIN AFERICAO_VALORES AV ON AV.COD_AFERICAO = AM.COD_AFERICAO AND AV.COD_PNEU = AM.COD_PNEU
  JOIN UNIDADE U ON U.CODIGO = AM.COD_UNIDADE
  JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO
  JOIN PNEU_RECAPAGEM_NOMENCLATURA PRN ON PRN.COD_TOTAL_VIDA = P.VIDA_TOTAL
WHERE AM.COD_UNIDADE = F_COD_UNIDADE
      AND (A.DATA_HORA AT TIME ZONE F_TIME_ZONE)::DATE >= F_DATA_INICIAL
      AND (A.DATA_HORA AT TIME ZONE F_TIME_ZONE)::DATE <= F_DATA_FINAL
      AND AM.DATA_HORA_RESOLUCAO IS NULL
      AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
ORDER BY A.DATA_HORA;
$$;
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
END TRANSACTION;