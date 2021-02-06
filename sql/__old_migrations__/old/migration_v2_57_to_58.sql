-- Essa migração deve ser executada quando o WS versão 58 for publicado.
BEGIN TRANSACTION;
-- ########################################################################################################
-- ########################################################################################################
-- ####################### CRIA NOVA COLUNA CODIGO BIGSERIAL NA TABELA PNEU ###############################
-- ########################################################################################################
-- ########################################################################################################
-- REMOVE O SCHEMA CS QUE NÃO ESTÁ MAIS SENDO UTILIZADO.
DROP SCHEMA CS CASCADE;

-- REMOVE FUNCTION NÃO NECESSÁRIA.
DROP FUNCTION FUNC_INSERT_AFERICAO_PENDENTE(BIGINT, TIMESTAMP, VARCHAR, BIGINT, BIGINT, BIGINT);

-- DEPRECIA FUNCTION QUE NÃO ESTÁ SENDO UTILIZADA.
ALTER FUNCTION FUNC_RELATORIO_PNEU_PROBLEMAS_PLACAS_EXTRATO(TEXT) RENAME TO DEPRECATED_FUNC_RELATORIO_PNEU_PROBLEMAS_PLACAS_EXTRATO;

-- REMOVE AS VIEWS.
DROP VIEW VIEW_ANALISE_PNEUS;
DROP VIEW VIEW_PNEU_KM_PERCORRIDO CASCADE;

-- REMOVE FKs ATUAIS QUE APONTAM PARA PNEU.
ALTER TABLE AFERICAO_VALORES DROP CONSTRAINT FK_AFERICAO_VALORES_PNEU;
ALTER TABLE AFERICAO_MANUTENCAO DROP CONSTRAINT FK_AFERICAO_MANUTENCAO_PNEU;
ALTER TABLE AFERICAO_MANUTENCAO DROP CONSTRAINT FK_AFERICAO_MANUTENCAO_PNEU_INSERIDO;
ALTER TABLE VEICULO_PNEU_INCONSISTENCIA DROP CONSTRAINT FK_VEICULO_PNEU_INCONSISTENCIA_PNEU_CORRETO;
ALTER TABLE VEICULO_PNEU_INCONSISTENCIA DROP CONSTRAINT FK_VEICULO_PNEU_INCONSISTENCIA_PNEU_INCORRETO;
ALTER TABLE MOVIMENTACAO DROP CONSTRAINT FK_MOVIMENTACAO_PNEU;
ALTER TABLE PNEU_FOTO_CADASTRO DROP CONSTRAINT FK_PNEU_FOTO_CADASTRO_PNEU;
ALTER TABLE VEICULO_PNEU DROP CONSTRAINT FK_VEICULO_PNEU_PNEU;
ALTER TABLE PNEU_VALOR_VIDA DROP CONSTRAINT FK_PNEU_VALOR_VIDA_PNEU;

-- PRECISAMOS DROPAR A CONSTRAINT DE PK DA PNEU_VALOR_VIDA TAMBÉM. POIS EXISTEM PNEUS QUE TEM O CÓDIGO DO CLIENTE
-- IGUAL AO CÓDIGO BIGSERIAL GERADO EM PNEU, AÍ NO UPDATE TEMOS PROBLEMA.
ALTER TABLE PNEU_VALOR_VIDA DROP CONSTRAINT PK_PNEU_VALOR_VIDA;

-- PRECISAMOS DROPAR ESSA CONSTRAINT DA VEICULO_PNEU TAMBÉM, QUE NÃO É MAIS NECESSÁRIA.
ALTER TABLE VEICULO_PNEU DROP CONSTRAINT VEICULO_PNEU_COD_PNEU_COD_UNIDADE_KEY;

-- ALTERA PK ATUAL DE PNEU.
ALTER TABLE PNEU DROP CONSTRAINT PK_PNEU;

-- RENOMEIA COLUNA CODIGO ATUAL DO PNEU.
ALTER TABLE PNEU RENAME CODIGO TO CODIGO_CLIENTE;

-- ADICIONA BIGSERIAL NA TABELA PNEU.
ALTER TABLE PNEU ADD COLUMN CODIGO BIGSERIAL;
ALTER TABLE PNEU ALTER COLUMN CODIGO SET NOT NULL;

-- CRIA NOVA PK DA TABELA PNEU.
ALTER TABLE PNEU ADD CONSTRAINT PK_PNEU PRIMARY KEY(CODIGO);

-- ATUALIZA TABELAS QUE REFERENCIAM PNEU PARA UTILIZAREM O NOVO CODIGO.
UPDATE AFERICAO_VALORES T SET COD_PNEU = (SELECT P.CODIGO
                                          FROM PNEU P
                                          WHERE P.CODIGO_CLIENTE = T.COD_PNEU AND P.COD_UNIDADE = T.COD_UNIDADE);

UPDATE AFERICAO_MANUTENCAO T SET COD_PNEU = (SELECT P.CODIGO
                                            FROM PNEU P
                                            WHERE P.CODIGO_CLIENTE = T.COD_PNEU AND P.COD_UNIDADE = T.COD_UNIDADE);

UPDATE AFERICAO_MANUTENCAO T SET COD_PNEU_INSERIDO = (SELECT P.CODIGO
                                                      FROM PNEU P
                                                      WHERE P.CODIGO_CLIENTE = T.COD_PNEU AND P.COD_UNIDADE = T.COD_UNIDADE)
WHERE T.COD_PNEU_INSERIDO IS NOT NULL;

UPDATE VEICULO_PNEU_INCONSISTENCIA T SET COD_PNEU_CORRETO = (SELECT P.CODIGO
                                                             FROM PNEU P
                                                             WHERE P.CODIGO_CLIENTE = T.COD_PNEU_CORRETO AND P.COD_UNIDADE = T.COD_UNIDADE)
WHERE T.COD_PNEU_CORRETO IS NOT NULL;

UPDATE VEICULO_PNEU_INCONSISTENCIA T SET COD_PNEU_INCORRETO = (SELECT P.CODIGO
                                                               FROM PNEU P
                                                               WHERE P.CODIGO_CLIENTE = T.COD_PNEU_INCORRETO AND P.COD_UNIDADE = T.COD_UNIDADE)
WHERE T.COD_PNEU_INCORRETO IS NOT NULL;

UPDATE MOVIMENTACAO T SET COD_PNEU = (SELECT P.CODIGO
                                      FROM PNEU P
                                      WHERE P.CODIGO_CLIENTE = T.COD_PNEU AND P.COD_UNIDADE = T.COD_UNIDADE);

UPDATE PNEU_FOTO_CADASTRO T SET COD_PNEU = (SELECT P.CODIGO
                                            FROM PNEU P
                                            WHERE P.CODIGO_CLIENTE = T.COD_PNEU AND P.COD_UNIDADE = T.COD_UNIDADE_PNEU);

UPDATE VEICULO_PNEU T SET COD_PNEU = (SELECT P.CODIGO
                                      FROM PNEU P
                                      WHERE P.CODIGO_CLIENTE = T.COD_PNEU AND P.COD_UNIDADE = T.COD_UNIDADE);

UPDATE PNEU_VALOR_VIDA T SET COD_PNEU = (SELECT P.CODIGO
                                          FROM PNEU P
                                          WHERE P.CODIGO_CLIENTE = T.COD_PNEU AND P.COD_UNIDADE = T.COD_UNIDADE);

-- ALTERA TIPO DAS COLUNAS COD_PNEU QUE APONTAM PARA PNEU.
ALTER TABLE AFERICAO_VALORES ALTER COLUMN COD_PNEU TYPE BIGINT USING (COD_PNEU::BIGINT);
ALTER TABLE AFERICAO_MANUTENCAO ALTER COLUMN COD_PNEU TYPE BIGINT USING (COD_PNEU::BIGINT);
ALTER TABLE AFERICAO_MANUTENCAO ALTER COLUMN COD_PNEU_INSERIDO TYPE BIGINT USING (COD_PNEU_INSERIDO::BIGINT);
ALTER TABLE VEICULO_PNEU_INCONSISTENCIA ALTER COLUMN COD_PNEU_CORRETO TYPE BIGINT USING (COD_PNEU_CORRETO::BIGINT);
ALTER TABLE VEICULO_PNEU_INCONSISTENCIA ALTER COLUMN COD_PNEU_INCORRETO TYPE BIGINT USING (COD_PNEU_INCORRETO::BIGINT);
ALTER TABLE MOVIMENTACAO ALTER COLUMN COD_PNEU TYPE BIGINT USING (COD_PNEU::BIGINT);
ALTER TABLE PNEU_FOTO_CADASTRO ALTER COLUMN COD_PNEU TYPE BIGINT USING (COD_PNEU::BIGINT);
ALTER TABLE VEICULO_PNEU ALTER COLUMN COD_PNEU TYPE BIGINT USING (COD_PNEU::BIGINT);
ALTER TABLE PNEU_VALOR_VIDA ALTER COLUMN COD_PNEU TYPE BIGINT USING (COD_PNEU::BIGINT);

-- ########################################################################################################
-- ########################################################################################################
-- #######################        ALTERA PNEUS COM CÓDIGO DUPLICADO         ###############################
-- ########################################################################################################
-- ########################################################################################################

-- QUERY PARA DETECTAR PNEUS DUPLICADOS EM UMA MESMA EMPRESA SÓ QUE EM UNIDADES DIFERENTES.
-- WITH P1 AS (
--     select p1.codigo, P1.cod_unidade, U1.cod_empresa
-- from pneu p1
--   join unidade u1 on p1.cod_unidade = u1.codigo
-- ),
--
-- P2 AS (
--     select P2.codigo, P2.cod_unidade, U2.cod_empresa
-- from pneu P2
--   join unidade U2 on P2.cod_unidade = U2.codigo
-- )
--
--
-- SELECT P1.codigo, P2.COD_UNIDADE, P1.cod_empresa
-- FROM P1 JOIN P2 ON P1.cod_empresa = P2.cod_empresa
-- WHERE P1.CODIGO = P2.CODIGO AND P1.cod_unidade != P2.cod_unidade

-- Cria updates de todos os pneus do sistema, mas nós iremos atualizar apenas um deles para seu código concatenado
-- com _DUP.
UPDATE PNEU SET CODIGO_CLIENTE = '1900557' || '_DUP' WHERE CODIGO_CLIENTE = '1900557' AND COD_UNIDADE = 1;
-- UPDATE PNEU SET CODIGO_CLIENTE = '1900557' || '_DUP' WHERE CODIGO_CLIENTE = '1900557' AND COD_UNIDADE = 19;
UPDATE PNEU SET CODIGO_CLIENTE = '3092' || '_DUP' WHERE CODIGO_CLIENTE = '3092' AND COD_UNIDADE = 1;
-- UPDATE PNEU SET CODIGO_CLIENTE = '3092' || '_DUP' WHERE CODIGO_CLIENTE = '3092' AND COD_UNIDADE = 19;
UPDATE PNEU SET CODIGO_CLIENTE = '3161' || '_DUP' WHERE CODIGO_CLIENTE = '3161' AND COD_UNIDADE = 1;
-- UPDATE PNEU SET CODIGO_CLIENTE = '3161' || '_DUP' WHERE CODIGO_CLIENTE = '3161' AND COD_UNIDADE = 19;
UPDATE PNEU SET CODIGO_CLIENTE = '31737' || '_DUP' WHERE CODIGO_CLIENTE = '31737' AND COD_UNIDADE = 24;
-- UPDATE PNEU SET CODIGO_CLIENTE = '31737' || '_DUP' WHERE CODIGO_CLIENTE = '31737' AND COD_UNIDADE = 36;
UPDATE PNEU SET CODIGO_CLIENTE = '33713' || '_DUP' WHERE CODIGO_CLIENTE = '33713' AND COD_UNIDADE = 20;
-- UPDATE PNEU SET CODIGO_CLIENTE = '33713' || '_DUP' WHERE CODIGO_CLIENTE = '33713' AND COD_UNIDADE = 32;
UPDATE PNEU SET CODIGO_CLIENTE = '50302' || '_DUP' WHERE CODIGO_CLIENTE = '50302' AND COD_UNIDADE = 20;
-- UPDATE PNEU SET CODIGO_CLIENTE = '50302' || '_DUP' WHERE CODIGO_CLIENTE = '50302' AND COD_UNIDADE = 24;
UPDATE PNEU SET CODIGO_CLIENTE = '52873' || '_DUP' WHERE CODIGO_CLIENTE = '52873' AND COD_UNIDADE = 24;
-- UPDATE PNEU SET CODIGO_CLIENTE = '52873' || '_DUP' WHERE CODIGO_CLIENTE = '52873' AND COD_UNIDADE = 32;
UPDATE PNEU SET CODIGO_CLIENTE = '53722' || '_DUP' WHERE CODIGO_CLIENTE = '53722' AND COD_UNIDADE = 32;
-- UPDATE PNEU SET CODIGO_CLIENTE = '53722' || '_DUP' WHERE CODIGO_CLIENTE = '53722' AND COD_UNIDADE = 37;
UPDATE PNEU SET CODIGO_CLIENTE = '54509' || '_DUP' WHERE CODIGO_CLIENTE = '54509' AND COD_UNIDADE = 32;
-- UPDATE PNEU SET CODIGO_CLIENTE = '54509' || '_DUP' WHERE CODIGO_CLIENTE = '54509' AND COD_UNIDADE = 37;
UPDATE PNEU SET CODIGO_CLIENTE = '55154' || '_DUP' WHERE CODIGO_CLIENTE = '55154' AND COD_UNIDADE = 30;
-- UPDATE PNEU SET CODIGO_CLIENTE = '55154' || '_DUP' WHERE CODIGO_CLIENTE = '55154' AND COD_UNIDADE = 37;
UPDATE PNEU SET CODIGO_CLIENTE = '55277' || '_DUP' WHERE CODIGO_CLIENTE = '55277' AND COD_UNIDADE = 24;
-- UPDATE PNEU SET CODIGO_CLIENTE = '55277' || '_DUP' WHERE CODIGO_CLIENTE = '55277' AND COD_UNIDADE = 32;
UPDATE PNEU SET CODIGO_CLIENTE = '55278' || '_DUP' WHERE CODIGO_CLIENTE = '55278' AND COD_UNIDADE = 24;
-- UPDATE PNEU SET CODIGO_CLIENTE = '55278' || '_DUP' WHERE CODIGO_CLIENTE = '55278' AND COD_UNIDADE = 32;
UPDATE PNEU SET CODIGO_CLIENTE = '56567' || '_DUP' WHERE CODIGO_CLIENTE = '56567' AND COD_UNIDADE = 23;
-- UPDATE PNEU SET CODIGO_CLIENTE = '56567' || '_DUP' WHERE CODIGO_CLIENTE = '56567' AND COD_UNIDADE = 38;
UPDATE PNEU SET CODIGO_CLIENTE = '56677' || '_DUP' WHERE CODIGO_CLIENTE = '56677' AND COD_UNIDADE = 24;
-- UPDATE PNEU SET CODIGO_CLIENTE = '56677' || '_DUP' WHERE CODIGO_CLIENTE = '56677' AND COD_UNIDADE = 38;
UPDATE PNEU SET CODIGO_CLIENTE = '57334' || '_DUP' WHERE CODIGO_CLIENTE = '57334' AND COD_UNIDADE = 24;
-- UPDATE PNEU SET CODIGO_CLIENTE = '57334' || '_DUP' WHERE CODIGO_CLIENTE = '57334' AND COD_UNIDADE = 36;
UPDATE PNEU SET CODIGO_CLIENTE = '57692' || '_DUP' WHERE CODIGO_CLIENTE = '57692' AND COD_UNIDADE = 30;
-- UPDATE PNEU SET CODIGO_CLIENTE = '57692' || '_DUP' WHERE CODIGO_CLIENTE = '57692' AND COD_UNIDADE = 37;
UPDATE PNEU SET CODIGO_CLIENTE = '58252' || '_DUP' WHERE CODIGO_CLIENTE = '58252' AND COD_UNIDADE = 20;
-- UPDATE PNEU SET CODIGO_CLIENTE = '58252' || '_DUP' WHERE CODIGO_CLIENTE = '58252' AND COD_UNIDADE = 38;
UPDATE PNEU SET CODIGO_CLIENTE = '58695' || '_DUP' WHERE CODIGO_CLIENTE = '58695' AND COD_UNIDADE = 30;
-- UPDATE PNEU SET CODIGO_CLIENTE = '58695' || '_DUP' WHERE CODIGO_CLIENTE = '58695' AND COD_UNIDADE = 37;
UPDATE PNEU SET CODIGO_CLIENTE = '59661' || '_DUP' WHERE CODIGO_CLIENTE = '59661' AND COD_UNIDADE = 23;
-- UPDATE PNEU SET CODIGO_CLIENTE = '59661' || '_DUP' WHERE CODIGO_CLIENTE = '59661' AND COD_UNIDADE = 32;
UPDATE PNEU SET CODIGO_CLIENTE = '59888' || '_DUP' WHERE CODIGO_CLIENTE = '59888' AND COD_UNIDADE = 37;
-- UPDATE PNEU SET CODIGO_CLIENTE = '59888' || '_DUP' WHERE CODIGO_CLIENTE = '59888' AND COD_UNIDADE = 38;
UPDATE PNEU SET CODIGO_CLIENTE = '60371' || '_DUP' WHERE CODIGO_CLIENTE = '60371' AND COD_UNIDADE = 20;
-- UPDATE PNEU SET CODIGO_CLIENTE = '60371' || '_DUP' WHERE CODIGO_CLIENTE = '60371' AND COD_UNIDADE = 38;
UPDATE PNEU SET CODIGO_CLIENTE = '60462' || '_DUP' WHERE CODIGO_CLIENTE = '60462' AND COD_UNIDADE = 30;
-- UPDATE PNEU SET CODIGO_CLIENTE = '60462' || '_DUP' WHERE CODIGO_CLIENTE = '60462' AND COD_UNIDADE = 32;
UPDATE PNEU SET CODIGO_CLIENTE = '60463' || '_DUP' WHERE CODIGO_CLIENTE = '60463' AND COD_UNIDADE = 30;
-- UPDATE PNEU SET CODIGO_CLIENTE = '60463' || '_DUP' WHERE CODIGO_CLIENTE = '60463' AND COD_UNIDADE = 32;
UPDATE PNEU SET CODIGO_CLIENTE = '60464' || '_DUP' WHERE CODIGO_CLIENTE = '60464' AND COD_UNIDADE = 30;
-- UPDATE PNEU SET CODIGO_CLIENTE = '60464' || '_DUP' WHERE CODIGO_CLIENTE = '60464' AND COD_UNIDADE = 32;
UPDATE PNEU SET CODIGO_CLIENTE = '60465' || '_DUP' WHERE CODIGO_CLIENTE = '60465' AND COD_UNIDADE = 24;
-- UPDATE PNEU SET CODIGO_CLIENTE = '60465' || '_DUP' WHERE CODIGO_CLIENTE = '60465' AND COD_UNIDADE = 30;
UPDATE PNEU SET CODIGO_CLIENTE = '60466' || '_DUP' WHERE CODIGO_CLIENTE = '60466' AND COD_UNIDADE = 24;
-- UPDATE PNEU SET CODIGO_CLIENTE = '60466' || '_DUP' WHERE CODIGO_CLIENTE = '60466' AND COD_UNIDADE = 30;
UPDATE PNEU SET CODIGO_CLIENTE = '60467' || '_DUP' WHERE CODIGO_CLIENTE = '60467' AND COD_UNIDADE = 24;
-- UPDATE PNEU SET CODIGO_CLIENTE = '60467' || '_DUP' WHERE CODIGO_CLIENTE = '60467' AND COD_UNIDADE = 30;
UPDATE PNEU SET CODIGO_CLIENTE = '60468' || '_DUP' WHERE CODIGO_CLIENTE = '60468' AND COD_UNIDADE = 24;
-- UPDATE PNEU SET CODIGO_CLIENTE = '60468' || '_DUP' WHERE CODIGO_CLIENTE = '60468' AND COD_UNIDADE = 30;
UPDATE PNEU SET CODIGO_CLIENTE = '60469' || '_DUP' WHERE CODIGO_CLIENTE = '60469' AND COD_UNIDADE = 30;
-- UPDATE PNEU SET CODIGO_CLIENTE = '60469' || '_DUP' WHERE CODIGO_CLIENTE = '60469' AND COD_UNIDADE = 37;
UPDATE PNEU SET CODIGO_CLIENTE = '65164' || '_DUP' WHERE CODIGO_CLIENTE = '65164' AND COD_UNIDADE = 19;
-- UPDATE PNEU SET CODIGO_CLIENTE = '65164' || '_DUP' WHERE CODIGO_CLIENTE = '65164' AND COD_UNIDADE = 35;
UPDATE PNEU SET CODIGO_CLIENTE = '69304' || '_DUP' WHERE CODIGO_CLIENTE = '69304' AND COD_UNIDADE = 19;
-- UPDATE PNEU SET CODIGO_CLIENTE = '69304' || '_DUP' WHERE CODIGO_CLIENTE = '69304' AND COD_UNIDADE = 35;
UPDATE PNEU SET CODIGO_CLIENTE = '69378' || '_DUP' WHERE CODIGO_CLIENTE = '69378' AND COD_UNIDADE = 19;
-- UPDATE PNEU SET CODIGO_CLIENTE = '69378' || '_DUP' WHERE CODIGO_CLIENTE = '69378' AND COD_UNIDADE = 35;
UPDATE PNEU SET CODIGO_CLIENTE = '69756' || '_DUP' WHERE CODIGO_CLIENTE = '69756' AND COD_UNIDADE = 19;
-- UPDATE PNEU SET CODIGO_CLIENTE = '69756' || '_DUP' WHERE CODIGO_CLIENTE = '69756' AND COD_UNIDADE = 35;
UPDATE PNEU SET CODIGO_CLIENTE = '70153' || '_DUP' WHERE CODIGO_CLIENTE = '70153' AND COD_UNIDADE = 19;
-- UPDATE PNEU SET CODIGO_CLIENTE = '70153' || '_DUP' WHERE CODIGO_CLIENTE = '70153' AND COD_UNIDADE = 35;
UPDATE PNEU SET CODIGO_CLIENTE = '9600250' || '_DUP' WHERE CODIGO_CLIENTE = '9600250' AND COD_UNIDADE = 19;
-- UPDATE PNEU SET CODIGO_CLIENTE = '9600250' || '_DUP' WHERE CODIGO_CLIENTE = '9600250' AND COD_UNIDADE = 40;
UPDATE PNEU SET CODIGO_CLIENTE = '9600252' || '_DUP' WHERE CODIGO_CLIENTE = '9600252' AND COD_UNIDADE = 19;
-- UPDATE PNEU SET CODIGO_CLIENTE = '9600252' || '_DUP' WHERE CODIGO_CLIENTE = '9600252' AND COD_UNIDADE = 40;
UPDATE PNEU SET CODIGO_CLIENTE = '960269' || '_DUP' WHERE CODIGO_CLIENTE = '960269' AND COD_UNIDADE = 19;
-- UPDATE PNEU SET CODIGO_CLIENTE = '960269' || '_DUP' WHERE CODIGO_CLIENTE = '960269' AND COD_UNIDADE = 40;
UPDATE PNEU SET CODIGO_CLIENTE = '960271' || '_DUP' WHERE CODIGO_CLIENTE = '960271' AND COD_UNIDADE = 19;
-- UPDATE PNEU SET CODIGO_CLIENTE = '960271' || '_DUP' WHERE CODIGO_CLIENTE = '960271' AND COD_UNIDADE = 40;
UPDATE PNEU SET CODIGO_CLIENTE = '960273' || '_DUP' WHERE CODIGO_CLIENTE = '960273' AND COD_UNIDADE = 19;
-- UPDATE PNEU SET CODIGO_CLIENTE = '960273' || '_DUP' WHERE CODIGO_CLIENTE = '960273' AND COD_UNIDADE = 40;
UPDATE PNEU SET CODIGO_CLIENTE = '960274' || '_DUP' WHERE CODIGO_CLIENTE = '960274' AND COD_UNIDADE = 19;
-- UPDATE PNEU SET CODIGO_CLIENTE = '960274' || '_DUP' WHERE CODIGO_CLIENTE = '960274' AND COD_UNIDADE = 40;


-- CRIA NOVAS FKS DAS TABELAS QUE APONTAM PARA PNEU.
ALTER TABLE AFERICAO_VALORES ADD CONSTRAINT FK_AFERICAO_VALORES_PNEU
  FOREIGN KEY (COD_PNEU) REFERENCES PNEU(CODIGO);
ALTER TABLE AFERICAO_MANUTENCAO ADD CONSTRAINT FK_AFERICAO_MANUTENCAO_PNEU
  FOREIGN KEY (COD_PNEU) REFERENCES PNEU(CODIGO);
ALTER TABLE AFERICAO_MANUTENCAO ADD CONSTRAINT FK_AFERICAO_MANUTENCAO_PNEU_INSERIDO
  FOREIGN KEY (COD_PNEU_INSERIDO) REFERENCES PNEU(CODIGO);
ALTER TABLE VEICULO_PNEU_INCONSISTENCIA ADD CONSTRAINT FK_VEICULO_PNEU_INCONSISTENCIA_PNEU_CORRETO
  FOREIGN KEY (COD_PNEU_CORRETO) REFERENCES PNEU(CODIGO);
ALTER TABLE VEICULO_PNEU_INCONSISTENCIA ADD CONSTRAINT FK_VEICULO_PNEU_INCONSISTENCIA_PNEU_INCORRETO
  FOREIGN KEY (COD_PNEU_INCORRETO) REFERENCES PNEU(CODIGO);
ALTER TABLE MOVIMENTACAO ADD CONSTRAINT FK_MOVIMENTACAO_PNEU
  FOREIGN KEY (COD_PNEU) REFERENCES PNEU(CODIGO);
ALTER TABLE PNEU_FOTO_CADASTRO ADD CONSTRAINT FK_PNEU_FOTO_CADASTRO_PNEU
  FOREIGN KEY (COD_PNEU) REFERENCES PNEU(CODIGO);
ALTER TABLE VEICULO_PNEU ADD CONSTRAINT FK_VEICULO_PNEU_PNEU
  FOREIGN KEY (COD_PNEU) REFERENCES PNEU(CODIGO);
ALTER TABLE PNEU_VALOR_VIDA ADD CONSTRAINT FK_PNEU_VALOR_VIDA_PNEU
  FOREIGN KEY (COD_PNEU) REFERENCES PNEU(CODIGO);

-- RECRIA A PK DE PNEU_VALOR_VIDA.
ALTER TABLE PNEU_VALOR_VIDA ADD CONSTRAINT PK_PNEU_VALOR_VIDA PRIMARY KEY (COD_PNEU, VIDA);

-- ADICIONA COLUNA COD_EMPRESA EM PNEU PARA IMPEDIRMOS QUE CÓDIGOS SEJAM DUPLICADOS DENTRO DE UMA EMPRESA.
ALTER TABLE PNEU ADD COLUMN COD_EMPRESA BIGINT;
ALTER TABLE PNEU ADD CONSTRAINT FK_PNEU_EMPRESA FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA(CODIGO);
UPDATE PNEU P SET COD_EMPRESA = (SELECT U.COD_EMPRESA FROM UNIDADE U WHERE U.CODIGO = P.COD_UNIDADE);
ALTER TABLE PNEU ALTER COLUMN COD_EMPRESA SET NOT NULL;
ALTER TABLE PNEU ADD CONSTRAINT UNIQUE_PNEU_EMPRESA UNIQUE (CODIGO_CLIENTE, COD_EMPRESA);

-- RECRIA AS VIEWS.
CREATE VIEW VIEW_ANALISE_PNEUS AS
  SELECT
    p.codigo AS "COD PNEU",
    p.CODIGO_CLIENTE as "COD PNEU CLIENTE",
    p.status AS "STATUS PNEU",
    p.cod_unidade,
    map.nome AS "MARCA",
    mp.nome AS "MODELO",
    ((((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro) AS "MEDIDAS",
    dados.qt_afericoes AS "QTD DE AFERIÇÕES",
    to_char((dados.primeira_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA 1a AFERIÇÃO",
    to_char((dados.ultima_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA ÚLTIMA AFERIÇÃO",
    dados.total_dias AS "DIAS ATIVO",
    round(
        CASE
            WHEN (dados.total_dias > 0) THEN (dados.total_km / (dados.total_dias)::numeric)
            ELSE NULL::numeric
        END) AS "MÉDIA KM POR DIA",
    p.altura_sulco_interno,
    p.altura_sulco_central_interno,
    p.altura_sulco_central_externo,
    p.altura_sulco_externo,
    round((dados.maior_sulco)::numeric, 2) AS "MAIOR MEDIÇÃO VIDA",
    round((dados.menor_sulco)::numeric, 2) AS "MENOR SULCO ATUAL",
    round((dados.sulco_gasto)::numeric, 2) AS "MILIMETROS GASTOS",
    round((dados.km_por_mm)::numeric, 2) AS "KMS POR MILIMETRO",
    round(((dados.km_por_mm * dados.sulco_restante))::numeric) AS "KMS A PERCORRER",
    trunc(
        CASE
            WHEN (((dados.total_km > (0)::numeric) AND (dados.total_dias > 0)) AND ((dados.total_km / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km / (dados.total_dias)::numeric))::double precision)
            ELSE (0)::double precision
        END) AS "DIAS RESTANTES",
        CASE
            WHEN (((dados.total_km > (0)::numeric) AND (dados.total_dias > 0)) AND ((dados.total_km / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km / (dados.total_dias)::numeric))::double precision))::integer + ('now'::text)::date)
            ELSE NULL::date
        END AS "PREVISÃO DE TROCA"
   FROM (((((pneu p
     JOIN ( SELECT av.cod_pneu,
            av.cod_unidade,
            count(av.altura_sulco_central_interno) AS qt_afericoes,
            (min(timezone(( SELECT func_get_time_zone_unidade.timezone
                   FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date AS primeira_afericao,
            (max(timezone(( SELECT func_get_time_zone_unidade.timezone
                   FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date AS ultima_afericao,
            ((max(timezone(( SELECT func_get_time_zone_unidade.timezone
                   FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date - (min(timezone(( SELECT func_get_time_zone_unidade.timezone
                   FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date) AS total_dias,
            max(total_km.total_km) AS total_km,
            max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) AS maior_sulco,
            min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) AS menor_sulco,
            (max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno))) AS sulco_gasto,
                CASE
                    WHEN (
                    CASE
                        WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_descarte)
                        WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_recapagem)
                        ELSE NULL::real
                    END < (0)::double precision) THEN (0)::real
                    ELSE
                    CASE
                        WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_descarte)
                        WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_recapagem)
                        ELSE NULL::real
                    END
                END AS sulco_restante,
                CASE
                    WHEN (((max(timezone(( SELECT func_get_time_zone_unidade.timezone
                       FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date - (min(timezone(( SELECT func_get_time_zone_unidade.timezone
                       FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date) > 0) THEN (((max(total_km.total_km))::double precision / max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno))) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)))
                    ELSE (0)::double precision
                END AS km_por_mm
           FROM ((((afericao_valores av
             JOIN afericao a ON ((a.codigo = av.cod_afericao)))
             JOIN pneu p_1 ON (((((p_1.codigo)::text = (av.cod_pneu)::text) AND (p_1.cod_unidade = av.cod_unidade)) AND ((p_1.status)::text = 'EM_USO'::text))))
             JOIN pneu_restricao_unidade erp ON ((erp.cod_unidade = av.cod_unidade)))
             JOIN ( SELECT total_km_rodado.cod_pneu,
                    total_km_rodado.cod_unidade,
                    sum(total_km_rodado.km_rodado) AS total_km
                   FROM ( SELECT av_1.cod_pneu,
                            av_1.cod_unidade,
                            a_1.placa_veiculo,
                            (max(a_1.km_veiculo) - min(a_1.km_veiculo)) AS km_rodado
                           FROM (afericao_valores av_1
                             JOIN afericao a_1 ON ((a_1.codigo = av_1.cod_afericao)))
                          GROUP BY av_1.cod_pneu, av_1.cod_unidade, a_1.placa_veiculo) total_km_rodado
                  GROUP BY total_km_rodado.cod_pneu, total_km_rodado.cod_unidade) total_km ON ((((total_km.cod_pneu)::text = (av.cod_pneu)::text) AND (total_km.cod_unidade = av.cod_unidade))))
          GROUP BY av.cod_pneu, av.cod_unidade, p_1.vida_atual, p_1.vida_total, erp.sulco_minimo_descarte, erp.sulco_minimo_recapagem) dados ON ((((dados.cod_pneu)::text = (p.codigo)::text) AND (dados.cod_unidade = p.cod_unidade))))
     JOIN dimensao_pneu dp ON ((dp.codigo = p.cod_dimensao)))
     JOIN unidade u ON ((u.codigo = p.cod_unidade)))
     JOIN modelo_pneu mp ON (((mp.codigo = p.cod_modelo) AND (mp.cod_empresa = u.cod_empresa))))
     JOIN marca_pneu map ON ((map.codigo = mp.cod_marca)));

COMMENT ON VIEW view_analise_pneus IS 'View utilizada para gerar dados de uso sobre os pneus, esses dados são usados para gerar relatórios';

CREATE VIEW view_pneu_km_percorrido AS
  SELECT total_km_rodado.cod_pneu,
    total_km_rodado.vida_momento_afericao AS vida,
    total_km_rodado.cod_unidade,
    sum(total_km_rodado.km_rodado) AS total_km
   FROM ( SELECT av_1.cod_pneu,
            av_1.vida_momento_afericao,
            av_1.cod_unidade,
            a_1.placa_veiculo,
            (max(a_1.km_veiculo) - min(a_1.km_veiculo)) AS km_rodado
           FROM (afericao_valores av_1
             JOIN afericao a_1 ON ((a_1.codigo = av_1.cod_afericao)))
          GROUP BY av_1.cod_pneu, av_1.cod_unidade, a_1.placa_veiculo, av_1.vida_momento_afericao) total_km_rodado
  GROUP BY total_km_rodado.cod_pneu, total_km_rodado.cod_unidade, total_km_rodado.vida_momento_afericao;

CREATE VIEW view_pneu_analise_vidas AS
  SELECT av.cod_pneu,
    av.vida_momento_afericao AS vida,
    p_1.status,
    p_1.valor AS valor_pneu,
    COALESCE(pvv.valor, (0)::real) AS valor_banda,
    av.cod_unidade,
    count(av.altura_sulco_central_interno) AS qt_afericoes,
    (min(timezone(( SELECT func_get_time_zone_unidade.timezone
           FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date AS primeira_afericao,
    (max(timezone(( SELECT func_get_time_zone_unidade.timezone
           FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date AS ultima_afericao,
    ((max(timezone(( SELECT func_get_time_zone_unidade.timezone
           FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date - (min(timezone(( SELECT func_get_time_zone_unidade.timezone
           FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date) AS total_dias,
    max(total_km.total_km) AS total_km_percorrido_vida,
    max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) AS maior_sulco,
    min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) AS menor_sulco,
    (max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno))) AS sulco_gasto,
        CASE
            WHEN (
            CASE
                WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_descarte)
                WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_recapagem)
                ELSE NULL::real
            END < (0)::double precision) THEN (0)::real
            ELSE
            CASE
                WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_descarte)
                WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_recapagem)
                ELSE NULL::real
            END
        END AS sulco_restante,
        CASE
            WHEN ((((max(timezone(( SELECT func_get_time_zone_unidade.timezone
               FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date - (min(timezone(( SELECT func_get_time_zone_unidade.timezone
               FROM func_get_time_zone_unidade(av.cod_unidade) func_get_time_zone_unidade(timezone)), a.data_hora)))::date) > 0) AND ((max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno))) > (0)::double precision)) THEN ((max(total_km.total_km))::double precision / (max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno))))
            ELSE (0)::double precision
        END AS km_por_mm,
        CASE
            WHEN (max(total_km.total_km) <= (0)::numeric) THEN (0)::double precision
            ELSE
            CASE
                WHEN (av.vida_momento_afericao = 1) THEN (p_1.valor / (max(total_km.total_km))::double precision)
                ELSE (COALESCE(pvv.valor, (0)::real) / (max(total_km.total_km))::double precision)
            END
        END AS valor_por_km_vida_atual
   FROM (((((afericao_valores av
     JOIN afericao a ON ((a.codigo = av.cod_afericao)))
     JOIN pneu p_1 ON ((((p_1.codigo)::text = (av.cod_pneu)::text) AND (p_1.cod_unidade = av.cod_unidade))))
     JOIN pneu_restricao_unidade erp ON ((erp.cod_unidade = av.cod_unidade)))
     LEFT JOIN pneu_valor_vida pvv ON ((((pvv.cod_unidade = p_1.cod_unidade) AND ((pvv.cod_pneu)::text = (p_1.codigo)::text)) AND (pvv.vida = av.vida_momento_afericao))))
     JOIN ( SELECT view_pneu_km_percorrido.cod_pneu,
            view_pneu_km_percorrido.vida,
            view_pneu_km_percorrido.cod_unidade,
            view_pneu_km_percorrido.total_km
           FROM view_pneu_km_percorrido) total_km ON (((((total_km.cod_pneu)::text = (av.cod_pneu)::text) AND (total_km.cod_unidade = av.cod_unidade)) AND (total_km.vida = av.vida_momento_afericao))))
  GROUP BY av.cod_pneu, av.cod_unidade, p_1.vida_atual, p_1.vida_total, erp.sulco_minimo_descarte, erp.sulco_minimo_recapagem, av.vida_momento_afericao, pvv.valor, p_1.valor, p_1.status
  ORDER BY av.cod_pneu, av.vida_momento_afericao;

CREATE VIEW view_pneu_analise_vida_atual AS
  SELECT
    p.codigo AS "COD PNEU",
    p.codigo_CLIENTE AS "COD PNEU CLIENTE",
    (p.valor + sum(acumulado.valor_banda)) AS valor_acumulado,
    sum(acumulado.total_km_percorrido_vida) AS km_acumulado,
    p.vida_atual AS "VIDA ATUAL",
    p.status AS "STATUS PNEU",
    p.cod_unidade,
    p.valor AS valor_pneu,
        CASE
            WHEN (dados.vida = 1) THEN dados.valor_pneu
            ELSE dados.valor_banda
        END AS valor_vida_atual,
    map.nome AS "MARCA",
    mp.nome AS "MODELO",
    ((((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro) AS "MEDIDAS",
    dados.qt_afericoes AS "QTD DE AFERIÇÕES",
    to_char((dados.primeira_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA 1a AFERIÇÃO",
    to_char((dados.ultima_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA ÚLTIMA AFERIÇÃO",
    dados.total_dias AS "DIAS ATIVO",
    round(
        CASE
            WHEN (dados.total_dias > 0) THEN (dados.total_km_percorrido_vida / (dados.total_dias)::numeric)
            ELSE NULL::numeric
        END) AS "MÉDIA KM POR DIA",
    round((dados.maior_sulco)::numeric, 2) AS "MAIOR MEDIÇÃO VIDA",
    round((dados.menor_sulco)::numeric, 2) AS "MENOR SULCO ATUAL",
    round((dados.sulco_gasto)::numeric, 2) AS "MILIMETROS GASTOS",
    round((dados.km_por_mm)::numeric, 2) AS "KMS POR MILIMETRO",
    round((dados.valor_por_km_vida_atual)::numeric, 2) AS "VALOR POR KM",
    round((
        CASE
            WHEN (sum(acumulado.total_km_percorrido_vida) > (0)::numeric) THEN ((p.valor + sum(acumulado.valor_banda)) / (sum(acumulado.total_km_percorrido_vida))::double precision)
            ELSE (0)::double precision
        END)::numeric, 2) AS "VALOR POR KM ACUMULADO",
    round(((dados.km_por_mm * dados.sulco_restante))::numeric) AS "KMS A PERCORRER",
    trunc(
        CASE
            WHEN (((dados.total_km_percorrido_vida > (0)::numeric) AND (dados.total_dias > 0)) AND ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric))::double precision)
            ELSE (0)::double precision
        END) AS "DIAS RESTANTES",
        CASE
            WHEN (((dados.total_km_percorrido_vida > (0)::numeric) AND (dados.total_dias > 0)) AND ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric))::double precision))::integer + ('now'::text)::date)
            ELSE NULL::date
        END AS "PREVISÃO DE TROCA",
        CASE
            WHEN (p.vida_atual = p.vida_total) THEN 'DESCARTE'::text
            ELSE 'ANÁLISE'::text
        END AS "DESTINO"
   FROM ((((((pneu p
     JOIN ( SELECT view_pneu_analise_vidas.cod_pneu,
            view_pneu_analise_vidas.vida,
            view_pneu_analise_vidas.status,
            view_pneu_analise_vidas.valor_pneu,
            view_pneu_analise_vidas.valor_banda,
            view_pneu_analise_vidas.cod_unidade,
            view_pneu_analise_vidas.qt_afericoes,
            view_pneu_analise_vidas.primeira_afericao,
            view_pneu_analise_vidas.ultima_afericao,
            view_pneu_analise_vidas.total_dias,
            view_pneu_analise_vidas.total_km_percorrido_vida,
            view_pneu_analise_vidas.maior_sulco,
            view_pneu_analise_vidas.menor_sulco,
            view_pneu_analise_vidas.sulco_gasto,
            view_pneu_analise_vidas.sulco_restante,
            view_pneu_analise_vidas.km_por_mm,
            view_pneu_analise_vidas.valor_por_km_vida_atual
           FROM view_pneu_analise_vidas) dados ON (((((dados.cod_pneu)::text = (p.codigo)::text) AND (dados.cod_unidade = p.cod_unidade)) AND (dados.vida = p.vida_atual))))
     JOIN dimensao_pneu dp ON ((dp.codigo = p.cod_dimensao)))
     JOIN unidade u ON ((u.codigo = p.cod_unidade)))
     JOIN modelo_pneu mp ON (((mp.codigo = p.cod_modelo) AND (mp.cod_empresa = u.cod_empresa))))
     JOIN marca_pneu map ON ((map.codigo = mp.cod_marca)))
     JOIN view_pneu_analise_vidas acumulado ON ((((acumulado.cod_pneu)::text = (p.codigo)::text) AND (acumulado.cod_unidade = p.cod_unidade))))
  GROUP BY p.codigo, p.cod_unidade, dados.valor_banda, dados.valor_pneu, map.nome, mp.nome, dp.largura, dp.altura, dp.aro, dados.qt_afericoes, dados.primeira_afericao, dados.ultima_afericao, dados.total_dias, dados.total_km_percorrido_vida, dados.maior_sulco, dados.menor_sulco, dados.sulco_gasto, dados.km_por_mm, dados.valor_por_km_vida_atual, dados.sulco_restante, dados.vida
  ORDER BY
        CASE
            WHEN (((dados.total_km_percorrido_vida > (0)::numeric) AND (dados.total_dias > 0)) AND ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric))::double precision))::integer + ('now'::text)::date)
            ELSE NULL::date
        END;


-- CORRIGE AS FUNCTIONS.
CREATE OR REPLACE FUNCTION func_relatorio_pneu_extrato_servicos_abertos(f_cod_unidade bigint, f_data_inicial date, f_data_final date, f_data_atual date, f_time_zone text)
  RETURNS TABLE("CÓDIGO DO SERVIÇO" bigint, "TIPO DO SERVIÇO" text, "QTD APONTAMENTOS" integer, "DATA HORA ABERTURA" text, "QTD DIAS EM ABERTO" text, "NOME DO COLABORADOR" text, "PLACA" text, "AFERIÇÃO" bigint, "PNEU" text, "SULCO INTERNO" real, "SULCO CENTRAL INTERNO" real, "SULCO CENTRAL EXTERNO" real, "SULCO EXTERNO" real, "PRESSÃO (PSI)" real, "PRESSÃO RECOMENDADA (PSI)" real, "POSIÇÃO DO PNEU" text, "ESTADO ATUAL" text, "MÁXIMO DE RECAPAGENS" text)
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
  P.CODIGO_CLIENTE AS COD_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_EXTERNO AS SULCO_EXTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_CENTRAL_EXTERNO AS SULCO_CENTRAL_EXTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_CENTRAL_INTERNO AS SULCO_CENTRAL_INTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_INTERNO AS SULCO_INTERNO_PNEU_PROBLEMA,
  AV.PSI AS PRESSAO_PNEU_PROBLEMA,
  P.PRESSAO_RECOMENDADA,
  CASE WHEN PONU.NOMENCLATURA IS NOT NULL THEN PONU.NOMENCLATURA ELSE AV.POSICAO::TEXT END AS POSICAO_PNEU_PROBLEMA,
  PVN.NOME AS VIDA_PNEU_PROBLEMA,
  PRN.NOME AS TOTAL_RECAPAGENS
FROM AFERICAO_MANUTENCAO AM
  JOIN PNEU P ON AM.COD_PNEU = P.CODIGO
  JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO
  JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
  JOIN AFERICAO_VALORES AV ON AV.COD_AFERICAO = AM.COD_AFERICAO AND AV.COD_PNEU = AM.COD_PNEU
  JOIN UNIDADE U ON U.CODIGO = AM.COD_UNIDADE
  JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO
  JOIN PNEU_RECAPAGEM_NOMENCLATURA PRN ON PRN.COD_TOTAL_VIDA = P.VIDA_TOTAL
  JOIN VEICULO V ON A.PLACA_VEICULO = V.PLACA AND V.COD_UNIDADE = A.COD_UNIDADE
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
    ON AM.COD_UNIDADE = PONU.COD_UNIDADE
       AND AV.POSICAO = PONU.POSICAO_PROLOG
       AND V.COD_TIPO = PONU.COD_TIPO_VEICULO
WHERE AM.COD_UNIDADE = F_COD_UNIDADE
      AND (A.DATA_HORA AT TIME ZONE F_TIME_ZONE)::DATE >= F_DATA_INICIAL
      AND (A.DATA_HORA AT TIME ZONE F_TIME_ZONE)::DATE <= F_DATA_FINAL
      AND AM.DATA_HORA_RESOLUCAO IS NULL
      AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
ORDER BY A.DATA_HORA;
$$;

CREATE OR REPLACE FUNCTION func_relatorio_pneu_extrato_servicos_fechados(f_cod_unidade bigint, f_data_inicial date, f_data_final date)
  RETURNS TABLE("DATA AFERIÇÃO" text, "DATA RESOLUÇÃO" text, "HORAS PARA RESOLVER" double precision, "MINUTOS PARA RESOLVER" double precision, "PLACA" text, "KM AFERIÇÃO" bigint, "KM CONSERTO" bigint, "KM PERCORRIDO" bigint, "COD PNEU" character varying, "PRESSÃO RECOMENDADA" real, "PRESSÃO AFERIÇÃO" text, "DISPERSÃO RECOMENDADA x AFERIÇÃO" text, "PRESSÃO INSERIDA" text, "DISPERSÃO RECOMENDADA x INSERIDA" text, "POSIÇÃO" text, "SERVIÇO" text, "MECÂNICO" text, "PROBLEMA APONTADO(INSPEÇÃO)" text)
LANGUAGE SQL
AS $$
SELECT
  to_char((A.data_hora AT TIME ZONE (SELECT TIMEZONE
                                     FROM func_get_time_zone_unidade(f_cod_unidade))),
          'DD/MM/YYYY HH24:MM:SS') AS data_hora_afericao,
  to_char((AM.data_hora_resolucao AT TIME ZONE (SELECT TIMEZONE
                                                FROM func_get_time_zone_unidade(f_cod_unidade))),
          'DD/MM/YYYY HH24:MM:SS') AS data_hora_resolucao,
  trunc(extract(EPOCH FROM ((am.data_hora_resolucao AT TIME ZONE (SELECT TIMEZONE
                                                                  FROM func_get_time_zone_unidade(f_cod_unidade)))
                            - (a.data_hora AT TIME ZONE (SELECT TIMEZONE
                                                         FROM func_get_time_zone_unidade(f_cod_unidade))))) /
        3600)                      AS horas_resolucao,
  trunc(extract(EPOCH FROM ((am.data_hora_resolucao AT TIME ZONE (SELECT TIMEZONE
                                                                  FROM func_get_time_zone_unidade(f_cod_unidade)))
                            - (a.data_hora AT TIME ZONE (SELECT TIMEZONE
                                                         FROM func_get_time_zone_unidade(f_cod_unidade))))) /
        60)                        AS minutos_resolucao,
  A.placa_veiculo,
  A.km_veiculo                     AS KM_AFERICAO,
  AM.km_momento_conserto,
  am.km_momento_conserto -
  a.km_veiculo                     AS km_percorrido,
  P.CODIGO_CLIENTE,
  P.pressao_recomendada,
  replace(round(AV.psi :: NUMERIC, 2) :: TEXT, '.',
          ',')                     AS PSI_AFERICAO,
  replace(round((((av.psi / p.pressao_recomendada) - 1) * 100) :: NUMERIC, 2) || '%', '.',
          ',')                     AS dispersao_pressao_antes,
  replace(round(AM.psi_apos_conserto :: NUMERIC, 2) :: TEXT, '.',
          ',')                     AS psi_pos_conserto,
  replace(round((((am.psi_apos_conserto / p.pressao_recomendada) - 1) * 100) :: NUMERIC, 2) || '%', '.',
          ',')                     AS dispersao_pressao_depois,
  pon.nomenclatura                 AS posicao,
  AM.tipo_servico,
  initcap(
      c.nome)                      AS nome_mecanico,
  coalesce(aa.alternativa,
           '-')                    AS problema_apontado
FROM
  AFERICAO_MANUTENCAO AM
  JOIN AFERICAO_VALORES AV ON AM.cod_unidade = AV.cod_unidade AND AM.cod_afericao = AV.cod_afericao
                              AND AM.cod_pneu = AV.cod_pneu
  JOIN AFERICAO A ON A.codigo = AV.cod_afericao
  JOIN COLABORADOR C ON am.cpf_mecanico = c.cpf
  JOIN PNEU P ON P.CODIGO = AV.COD_PNEU
  JOIN VEICULO_PNEU VP ON vp.cod_pneu = p.codigo AND vp.cod_unidade = p.cod_unidade
  LEFT JOIN afericao_alternativa_manutencao_inspecao aa ON aa.codigo = am.cod_alternativa
  JOIN VEICULO V ON V.PLACA = VP.placa
  JOIN pneu_ordem_nomenclatura_unidade pon on pon.cod_unidade = p.cod_unidade and pon.cod_tipo_veiculo = v.cod_tipo
  and pon.posicao_prolog = av.posicao
WHERE AV.cod_unidade = f_cod_unidade and am.cpf_mecanico is not null
      and (a.data_hora AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::date
          >= (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      and (a.data_hora AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::date
          <= (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
ORDER BY a.data_hora desc
$$;

CREATE OR REPLACE FUNCTION func_relatorio_pneus_descartados(f_cod_unidade bigint, f_data_inicial date, f_data_final date)
  RETURNS TABLE("RESPONSÁVEL PELO DESCARTE" text, "DATA/HORA DO DESCARTE" text, "CÓDIGO DO PNEU" text, "MARCA DO PNEU" text, "MODELO DO PNEU" text, "MARCA DA BANDA" text, "MODELO DA BANDA" text, "DIMENSÃO DO PNEU" text, "ÚLTIMA PRESSÃO" numeric, "TOTAL DE VIDAS" integer, "ALTURA SULCO INTERNO" numeric, "ALTURA SULCO CENTRAL INTERNO" numeric, "ALTURA SULCO CENTRAL EXTERNO" numeric, "ALTURA SULCO EXTERNO" numeric, "DOT" text, "MOTIVO DO DESCARTE" text, "FOTO 1" text, "FOTO 2" text, "FOTO 3" text)
LANGUAGE SQL
AS $$
SELECT
  C.NOME                        AS "RESPONSÁVEL PELO DESCARTE",
  TO_CHAR(MP.DATA_HORA AT TIME ZONE (SELECT TIMEZONE
                                     FROM func_get_time_zone_unidade(f_cod_unidade)),
          'DD/MM/YYYY HH24:MI') AS "DATA/HORA DESCARTE",
  P.CODIGO_CLIENTE              AS "CÓDIGO DO PNEU",
  MAP.NOME                      AS "MARCA DO PNEU",
  MOP.NOME                      AS "MODELO DO PNEU",
  MAB.NOME                      AS "MARCA DA BANDA",
  MOB.NOME                      AS "MODELO DA BANDA",
  'Altura: ' || DP.ALTURA || ' - Largura: ' || DP.LARGURA || ' - Aro: ' ||
  DP.ARO                        AS "DIMENSÃO DO PNEU",
  ROUND(P.PRESSAO_ATUAL :: NUMERIC,
        2)                      AS "ÚLTIMA PRESSÃO",
  P.VIDA_ATUAL                  AS "TOTAL DE VIDAS",
  ROUND(P.ALTURA_SULCO_INTERNO :: NUMERIC,
        2)                      AS "ALTURA SULCO INTERNO",
  ROUND(P.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC,
        2)                      AS "ALTURA SULCO CENTRAL INTERNO",
  ROUND(P.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC,
        2)                      AS "ALTURA SULCO CENTRAL EXTERNO",
  ROUND(P.ALTURA_SULCO_EXTERNO :: NUMERIC,
        2)                      AS "ALTURA SULCO EXTERNO",
  P.DOT                         AS "DOT",
  MMDE.MOTIVO                   AS "MOTIVO DO DESCARTE",
  MD.URL_IMAGEM_DESCARTE_1      AS "FOTO 1",
  MD.URL_IMAGEM_DESCARTE_2      AS "FOTO 2",
  MD.URL_IMAGEM_DESCARTE_3      AS "FOTO 3"
FROM PNEU P
  JOIN MODELO_PNEU MOP ON P.COD_MODELO = MOP.CODIGO
  JOIN MARCA_PNEU MAP ON MOP.COD_MARCA = MAP.CODIGO
  JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
  LEFT JOIN MODELO_BANDA MOB ON P.COD_MODELO_BANDA = MOB.CODIGO
  LEFT JOIN MARCA_BANDA MAB ON MOB.COD_MARCA = MAB.CODIGO
  LEFT JOIN MOVIMENTACAO_PROCESSO MP ON P.COD_UNIDADE = MP.COD_UNIDADE
  LEFT JOIN MOVIMENTACAO M ON MP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO
  LEFT JOIN MOVIMENTACAO_DESTINO MD ON M.CODIGO = MD.COD_MOVIMENTACAO
  LEFT JOIN COLABORADOR C ON MP.CPF_RESPONSAVEL = C.CPF
  LEFT JOIN MOVIMENTACAO_MOTIVO_DESCARTE_EMPRESA MMDE
    ON MD.COD_MOTIVO_DESCARTE = MMDE.CODIGO AND C.COD_EMPRESA = MMDE.COD_EMPRESA
WHERE P.COD_UNIDADE = F_COD_UNIDADE
      AND P.STATUS = 'DESCARTE'
      AND M.COD_PNEU = P.CODIGO
      AND MD.TIPO_DESTINO = 'DESCARTE'
      AND (MP.DATA_HORA AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::DATE
          >= (F_DATA_INICIAL AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND (MP.DATA_HORA AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::DATE
          <= (F_DATA_FINAL AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)));
$$;

CREATE OR REPLACE FUNCTION func_relatorio_previsao_troca(f_data_inicial date, f_data_final date, f_cod_unidade bigint, f_status_pneu character varying)
  RETURNS TABLE("COD PNEU" text, "STATUS" text, "VIDA ATUAL" integer, "MARCA" text, "MODELO" text, "MEDIDAS" text, "QTD DE AFERIÇÕES" bigint, "DTA 1a AFERIÇÃO" text, "DTA ÚLTIMA AFERIÇÃO" text, "DIAS ATIVO" integer, "MÉDIA KM POR DIA" numeric, "MAIOR MEDIÇÃO VIDA" numeric, "MENOR SULCO ATUAL" numeric, "MILIMETROS GASTOS" numeric, "KMS POR MILIMETRO" numeric, "VALOR VIDA" real, "VALOR ACUMULADO" real, "VALOR POR KM VIDA ATUAL" numeric, "VALOR POR KM ACUMULADO" numeric, "KMS A PERCORRER" numeric, "DIAS RESTANTES" double precision, "PREVISÃO DE TROCA" text, "DESTINO" text)
LANGUAGE SQL
AS $$
SELECT VAP."COD PNEU CLIENTE", VAP."STATUS PNEU", VAP."VIDA ATUAL", VAP."MARCA", VAP."MODELO", VAP."MEDIDAS", VAP."QTD DE AFERIÇÕES", VAP."DTA 1a AFERIÇÃO",
VAP."DTA ÚLTIMA AFERIÇÃO", VAP."DIAS ATIVO", VAP."MÉDIA KM POR DIA", VAP."MAIOR MEDIÇÃO VIDA", VAP."MENOR SULCO ATUAL",
VAP."MILIMETROS GASTOS", VAP."KMS POR MILIMETRO", VAP.valor_vida_atual, vap.valor_acumulado,VAP."VALOR POR KM", VAP."VALOR POR KM ACUMULADO" ,
VAP."KMS A PERCORRER", VAP."DIAS RESTANTES", TO_CHAR(VAP."PREVISÃO DE TROCA", 'DD/MM/YYYY'), VAP."DESTINO"
FROM VIEW_PNEU_ANALISE_VIDA_ATUAL VAP
WHERE VAP.cod_unidade = f_cod_unidade
      AND VAP."PREVISÃO DE TROCA" BETWEEN (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND VAP."STATUS PNEU" LIKE f_status_pneu
$$;

CREATE or replace FUNCTION func_relatorio_dados_ultima_afericao_pneu(f_cod_unidade bigint, f_time_zone_unidade text)
  RETURNS TABLE("PNEU" text, "MARCA" text, "MODELO" text, "MEDIDAS" text, "PLACA" text, "TIPO" text, "POSIÇÃO" text, "SULCO INTERNO" text, "SULCO CENTRAL INTERNO" text, "SULCO CENTRAL EXTERNO" text, "SULCO EXTERNO" text, "PRESSÃO (PSI)" text, "VIDA" text, "DOT" text, "ÚLTIMA AFERIÇÃO" text)
LANGUAGE SQL
AS $$
SELECT
  P.codigo_cliente                                         AS COD_PNEU,
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
ORDER BY P.CODIGO_CLIENTE;
$$;

-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ############################## RELATÓRIO COM INFORMAÇÕES GERAIS DOS PNEUS ##############################
-- ########################################################################################################
-- ########################################################################################################
CREATE or replace FUNCTION func_relatorio_pneu_resumo_geral_pneus(f_cod_unidade bigint, f_status_pneu text, f_time_zone_unidade text)
  RETURNS TABLE("PNEU" text, "STATUS" text, "MARCA" text, "MODELO" text, "BANDA APLICADA" TEXT, "MEDIDAS" text, "PLACA" text, "TIPO" text, "POSIÇÃO" text, "SULCO INTERNO" text, "SULCO CENTRAL INTERNO" text, "SULCO CENTRAL EXTERNO" text, "SULCO EXTERNO" text, "PRESSÃO (PSI)" text, "VIDA ATUAL" text, "DOT" text, "ÚLTIMA AFERIÇÃO" text)
LANGUAGE SQL
AS $$
SELECT
  P.codigo_cliente                                         AS COD_PNEU,
  P.STATUS AS STATUS,
  map.nome                                         AS NOME_MARCA_PNEU,
  mp.nome                                          AS NOME_MODELO_PNEU,
  CASE WHEN MARB.CODIGO IS NULL
    THEN 'Nunca Recapado'
    ELSE MARB.NOME || ' - ' || MODB.NOME
  END AS BANDA_APLICADA,
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
  PVN.nome :: TEXT                             AS VIDA_ATUAL,
  COALESCE(P.DOT, '-')                             AS DOT,
  coalesce(to_char(DATA_ULTIMA_AFERICAO.ULTIMA_AFERICAO AT TIME ZONE F_TIME_ZONE_UNIDADE, 'DD/MM/YYYY HH24:MI'),
           'Nunca Aferido')                          AS ULTIMA_AFERICAO
FROM PNEU P
  JOIN dimensao_pneu dp ON dp.codigo = p.cod_dimensao
  JOIN unidade u ON u.codigo = p.cod_unidade
  JOIN modelo_pneu mp ON mp.codigo = p.cod_modelo AND mp.cod_empresa = u.cod_empresa
  JOIN marca_pneu map ON map.codigo = mp.cod_marca
  JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = p.vida_atual
  LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.cod_modelo_banda
  LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.cod_marca
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
WHERE P.cod_unidade = F_COD_UNIDADE AND CASE WHEN F_STATUS_PNEU IS NULL THEN TRUE ELSE P.STATUS = F_STATUS_PNEU END
ORDER BY P.CODIGO_CLIENTE;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ############################## FUNÇÃO DE CONFIGURAÇÃO DE TIPO DE AFERIÇÃO ##############################
-- ########################################################################################################
-- ########################################################################################################
CREATE TABLE IF NOT EXISTS AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO(
  CODIGO BIGSERIAL NOT NULL,
  COD_UNIDADE BIGINT NOT NULL,
  COD_TIPO_VEICULO BIGINT NOT NULL,
  PODE_AFERIR_SULCO BOOLEAN NOT NULL,
  PODE_AFERIR_PRESSAO BOOLEAN NOT NULL,
  PODE_AFERIR_SULCO_PRESSAO BOOLEAN NOT NULL,
  PODE_AFERIR_ESTEPE BOOLEAN NOT NULL,
  CONSTRAINT PK_AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO PRIMARY KEY (CODIGO),
  CONSTRAINT FK_AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO_TIPO_VEICULO FOREIGN KEY (COD_TIPO_VEICULO, COD_UNIDADE)
  REFERENCES VEICULO_TIPO(CODIGO, COD_UNIDADE),
  CONSTRAINT FK_AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO_UNIDADE FOREIGN KEY (COD_UNIDADE) REFERENCES UNIDADE(CODIGO),
  CONSTRAINT UNIQUE_TIPO_VEICULO_UNIDADE UNIQUE(COD_TIPO_VEICULO, COD_UNIDADE)
);
COMMENT ON TABLE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
IS 'Tipos de veículos que possuem alguma configuração de aferição setada';

CREATE OR REPLACE VIEW view_afericao_configuracao_tipo_afericao AS
SELECT CONFIG.CODIGO,
  VT.CODIGO AS COD_TIPO_VEICULO,
  VT.NOME NOME_TIPO_VEICULO,
  VT.COD_UNIDADE,
  VT.STATUS_ATIVO,
  (CASE WHEN CONFIG.PODE_AFERIR_SULCO IS NULL
             AND CONFIG.PODE_AFERIR_PRESSAO IS NULL
             AND CONFIG.PODE_AFERIR_SULCO_PRESSAO IS NULL
    THEN TRUE
   ELSE CONFIG.PODE_AFERIR_SULCO END) AS PODE_AFERIR_SULCO,
  (CASE WHEN CONFIG.PODE_AFERIR_SULCO IS NULL
             AND CONFIG.PODE_AFERIR_PRESSAO IS NULL
             AND CONFIG.PODE_AFERIR_SULCO_PRESSAO IS NULL
    THEN TRUE
   ELSE CONFIG.PODE_AFERIR_PRESSAO END) AS PODE_AFERIR_PRESSAO,
  (CASE WHEN CONFIG.PODE_AFERIR_SULCO IS NULL
             AND CONFIG.PODE_AFERIR_PRESSAO IS NULL
             AND CONFIG.PODE_AFERIR_SULCO_PRESSAO IS NULL
    THEN TRUE
   ELSE CONFIG.PODE_AFERIR_SULCO_PRESSAO END) AS PODE_AFERIR_SULCO_PRESSAO,
  (CASE WHEN CONFIG.PODE_AFERIR_SULCO IS NULL
             AND CONFIG.PODE_AFERIR_PRESSAO IS NULL
             AND CONFIG.PODE_AFERIR_SULCO_PRESSAO IS NULL
             AND CONFIG.PODE_AFERIR_ESTEPE IS NULL
    THEN TRUE
   ELSE CONFIG.PODE_AFERIR_ESTEPE END) AS PODE_AFERIR_ESTEPE
FROM VEICULO_TIPO AS VT
  LEFT JOIN AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO AS CONFIG
    ON VT.CODIGO = CONFIG.COD_TIPO_VEICULO AND VT.COD_UNIDADE = CONFIG.COD_UNIDADE;

-- Cria permissão para realizar as configurações
INSERT INTO FUNCAO_PROLOG_V11 VALUES (100, 'Aferição - Configurar', 1);

-- Remove tabela antiga para controle dos estepes aferíveis
DROP TABLE AFERICAO_ESTEPES_TIPOS_VEICULOS_BLOQUEADOS;

-- Concede permissão de configuração aos cargos que possuem (permissão de cadastro de pneu E visualização relatório de
-- pneu) OU (permissão de alteração de pneu E visualização de relatório de pneu)
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (1, 1, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (1, 46, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (1, 48, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (1, 53, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (1, 56, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (1, 61, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (1, 63, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (1, 69, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (3, 1, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (4, 1, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (4, 17, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (4, 24, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 45, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 158, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (5, 159, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (6, 1, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (6, 46, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (6, 47, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (7, 77, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (7, 147, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (9, 74, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (11, 103, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (14, 105, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (14, 106, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (14, 149, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (16, 122, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (16, 129, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (16, 132, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (16, 137, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (19, 157, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (20, 187, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (20, 191, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (21, 185, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (21, 186, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (21, 189, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (21, 190, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (21, 194, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (21, 195, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (23, 188, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (24, 187, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (25, 188, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (26, 188, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (30, 188, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (32, 187, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (32, 207, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (33, 48, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (36, 188, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (36, 195, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (36, 226, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (44, 242, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (48, 257, 100, 1);
INSERT INTO cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
VALUES (48, 259, 100, 1);
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ######################## ADICIONA FOREIGN KEY DE CÓDIGO DA UNIDADE NA TABELA TRACKING ##################
-- ########################################################################################################
-- ########################################################################################################
ALTER TABLE TRACKING ADD CONSTRAINT FK_TRACKING_UNIDADE FOREIGN KEY ("código_transportadora") REFERENCES UNIDADE(CODIGO);
-- ########################################################################################################
-- ########################################################################################################

END TRANSACTION;