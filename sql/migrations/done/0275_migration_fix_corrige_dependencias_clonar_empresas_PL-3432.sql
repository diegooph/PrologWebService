CREATE OR REPLACE FUNCTION INTERNO.FUNC_RESETA_EMPRESA_APRESENTACAO(F_COD_EMPRESA_BASE BIGINT,
                                                                    F_COD_EMPRESA_USUARIO BIGINT,
                                                                    OUT MENSAGEM_SUCESSO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_UNIDADES_BASE                     BIGINT[] := (SELECT ARRAY_AGG(U.CODIGO)
                                                         FROM UNIDADE U
                                                         WHERE U.COD_EMPRESA = F_COD_EMPRESA_BASE);
    V_COD_UNIDADE_BASE                      BIGINT;
    V_COD_UNIDADES_USUARIO                  BIGINT[] := (SELECT ARRAY_AGG(U.CODIGO)
                                                         FROM UNIDADE U
                                                         WHERE U.COD_EMPRESA = F_COD_EMPRESA_USUARIO);
    V_COD_UNIDADE_USUARIO_NOVA              BIGINT;
    V_COD_COLABORADORES_USUARIO             BIGINT[] := (SELECT ARRAY_AGG(CD.CODIGO)
                                                         FROM COLABORADOR_DATA CD
                                                         WHERE CD.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_AFERICOES                         BIGINT[] := (SELECT ARRAY_AGG(AD.CODIGO)
                                                         FROM AFERICAO_DATA AD
                                                         WHERE AD.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_CHECKLISTS                        BIGINT[] := (SELECT ARRAY_AGG(CD.CODIGO)
                                                         FROM CHECKLIST_DATA CD
                                                         WHERE CD.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_CHECKLISTS_MODELO                 BIGINT[] := (SELECT DISTINCT ARRAY_AGG(CMD.CODIGO)
                                                         FROM CHECKLIST_MODELO_DATA CMD
                                                         WHERE CMD.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_TOKENS_CHECKLISTS_OFF                 TEXT     := (SELECT ARRAY_AGG(CODU.TOKEN_SINCRONIZACAO_CHECKLIST)
                                                         FROM CHECKLIST_OFFLINE_DADOS_UNIDADE CODU
                                                         WHERE CODU.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_MOVIMENTACOES                     BIGINT[] := (SELECT ARRAY_AGG(MO.CODIGO)
                                                         FROM MOVIMENTACAO MO
                                                         WHERE MO.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_SOCORROS                          BIGINT[] := (SELECT ARRAY_AGG(SR.CODIGO)
                                                         FROM SOCORRO_ROTA SR
                                                         WHERE SR.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_VEICULOS_TRANSFERENCIAS_PROCESSOS BIGINT[] := (SELECT ARRAY_AGG(VTP.CODIGO)
                                                         FROM VEICULO_TRANSFERENCIA_PROCESSO VTP
                                                         WHERE (VTP.COD_UNIDADE_DESTINO = ANY (V_COD_UNIDADES_USUARIO))
                                                            OR (VTP.COD_UNIDADE_ORIGEM = ANY (V_COD_UNIDADES_USUARIO)));
    V_COD_PNEU_TRANSFERENCIAS_PROCESSOS     BIGINT[] := (SELECT ARRAY_AGG(PTP.CODIGO)
                                                         FROM PNEU_TRANSFERENCIA_PROCESSO PTP
                                                         WHERE (PTP.COD_UNIDADE_ORIGEM = ANY (V_COD_UNIDADES_USUARIO))
                                                            OR (PTP.COD_UNIDADE_DESTINO = ANY (V_COD_UNIDADES_USUARIO)));
    V_COD_COLABORADORES_NPS                 BIGINT[] := (SELECT ARRAY_AGG(COLABORADORES.COD_COLABORADOR_NPS)
                                                         FROM (SELECT NBPC.COD_COLABORADOR_BLOQUEIO AS COD_COLABORADOR_NPS
                                                               FROM CS.NPS_BLOQUEIO_PESQUISA_COLABORADOR NBPC
                                                               WHERE NBPC.COD_COLABORADOR_BLOQUEIO = ANY (V_COD_COLABORADORES_USUARIO)
                                                               UNION
                                                               SELECT NR.COD_COLABORADOR_RESPOSTAS AS COD_COLABORADOR_NPS
                                                               FROM CS.NPS_RESPOSTAS NR
                                                               WHERE NR.COD_COLABORADOR_RESPOSTAS = ANY (V_COD_COLABORADORES_USUARIO)) COLABORADORES);
    V_COD_TREINAMENTOS                      BIGINT[] := (SELECT ARRAY_AGG(T.CODIGO)
                                                         FROM TREINAMENTO T
                                                         WHERE T.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_SERVICOS_REALIZADOS               BIGINT[] := (SELECT ARRAY_AGG(PSR.CODIGO)
                                                         FROM PNEU_SERVICO_REALIZADO_DATA PSR
                                                         WHERE PSR.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_INTERVALO                         BIGINT[] := (SELECT ARRAY_AGG(IU.COD_UNIDADE)
                                                         FROM INTERVALO_UNIDADE IU
                                                         WHERE IU.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_MARCACOES                         BIGINT[] := (SELECT ARRAY_AGG(I.CODIGO)
                                                         FROM INTERVALO I
                                                         WHERE I.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_RELATOS                           BIGINT[] := (SELECT ARRAY_AGG(R.CODIGO)
                                                         FROM RELATO R
                                                         WHERE R.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_QUIZ                              BIGINT[] := (SELECT ARRAY_AGG(Q.CODIGO)
                                                         FROM QUIZ Q
                                                         WHERE Q.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_FALE_CONOSCO                      BIGINT[] := (SELECT ARRAY_AGG(FC.CODIGO)
                                                         FROM FALE_CONOSCO FC
                                                         WHERE FC.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_TESTES_AFERIDOR                   BIGINT[] := (SELECT ARRAY_AGG(PT.CODIGO)
                                                         FROM AFERIDOR.PROCEDIMENTO_TESTE PT
                                                         WHERE PT.COD_COLABORADOR_EXECUCAO = ANY (V_COD_COLABORADORES_USUARIO));
    V_COLABORADORES_CADASTRADOS             TEXT[] ;
BEGIN
    -- VERIFICA SE EMPRESAS EXISTEM.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA_BASE);
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA_USUARIO);

    -- BUSCA E DELETA VÍNCULOS QUE POSSAM EXISTIR DE COLABORADOR, VEÍCULOS E PNEUS.
    --- AFERIÇAO.
    IF (V_COD_AFERICOES IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_AFERICOES_DEPENDENCIAS(V_COD_UNIDADES_USUARIO, V_COD_AFERICOES);
    END IF;

    --- CHECKLIST.
    IF ((V_COD_CHECKLISTS IS NOT NULL) OR (V_COD_CHECKLISTS_MODELO IS NOT NULL))
    THEN
        PERFORM INTERNO.FUNC_DELETA_CHECKLISTS_DEPENDENCIAS(V_COD_UNIDADES_USUARIO,
                                                            V_COD_CHECKLISTS,
                                                            V_COD_CHECKLISTS_MODELO);
    END IF;

    --- DELETA TOKEN CKECKLIST OFFLINE
    -- (MESMO SEM TER CHECKLIST - PODE HAVER O TOKEN - POIS ELE É CRIADO ASSIM QUE UMA UNIDADE É CADASTRADA)
    IF (V_TOKENS_CHECKLISTS_OFF IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TOKENS_CHECKLISTS_OFFLINES(V_COD_UNIDADES_USUARIO);
    END IF;

    -- MOVIMENTAÇÃO.
    IF (V_COD_MOVIMENTACOES IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_MOVIMENTACOES_DEPENDENCIAS(V_COD_UNIDADES_USUARIO, V_COD_MOVIMENTACOES);
    END IF;

    --- SOCORRO EM ROTA.
    IF (V_COD_SOCORROS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_SOCORROS_DEPENDENCIAS(F_COD_EMPRESA_USUARIO, V_COD_SOCORROS);
    END IF;

    --- TRANSFERENCIA DE VEÍCULOS.
    IF (V_COD_VEICULOS_TRANSFERENCIAS_PROCESSOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TRANSFERENCIAS_VEICULOS_DEPENDENCIAS(V_COD_VEICULOS_TRANSFERENCIAS_PROCESSOS);
    END IF;

    -- TRANSFERENCIA DE PNEU
    IF (V_COD_PNEU_TRANSFERENCIAS_PROCESSOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TRANSFERENCIAS_PNEUS_DEPENDENCIAS(V_COD_PNEU_TRANSFERENCIAS_PROCESSOS,
                                                                      V_COD_UNIDADES_USUARIO);
    END IF;

    -- INTERVALO
    IF (V_COD_INTERVALO IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_INTERVALO_DEPENDENCIAS(V_COD_UNIDADES_USUARIO, V_COD_MARCACOES);
    END IF;

    -- NPS
    IF (V_COD_COLABORADORES_NPS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_NPS(V_COD_COLABORADORES_NPS);
    END IF;

    -- PRODUTIVIDADE
    IF EXISTS(SELECT AP.COD_UNIDADE FROM ACESSOS_PRODUTIVIDADE AP WHERE AP.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO))
    THEN
        PERFORM INTERNO.FUNC_DELETA_PRODUTIVIDADES_DEPENDENCIAS(V_COD_UNIDADES_USUARIO);
    END IF;

    -- RELATO
    IF (V_COD_RELATOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_RELATOS_DEPENDENCIAS(V_COD_UNIDADES_USUARIO);
    END IF;

    -- QUIZ
    IF (V_COD_QUIZ IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_QUIZ_DEPENDENCIAS(V_COD_UNIDADES_USUARIO, V_COD_QUIZ);
    END IF;

    -- TREINAMENTO
    IF (V_COD_TREINAMENTOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TREINAMENTOS_DEPENDENCIAS(V_COD_TREINAMENTOS);
    END IF;

    -- SERVICO PNEU
    IF (V_COD_SERVICOS_REALIZADOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_SERVICOS_PNEU_DEPENDENCIAS(F_COD_EMPRESA_USUARIO, V_COD_SERVICOS_REALIZADOS);
    END IF;

    -- FALE CONOSCO
    IF (V_COD_FALE_CONOSCO IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_FALE_CONOSCO(V_COD_FALE_CONOSCO);
    END IF;

    -- TESTES AFERIDOR
    IF (V_COD_TESTES_AFERIDOR IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TESTES_AFERIDOR(V_COD_TESTES_AFERIDOR);
    END IF;

    -- DELETA VEÍCULOS
    PERFORM INTERNO.FUNC_DELETA_VEICULOS(F_COD_EMPRESA_USUARIO, V_COD_UNIDADES_USUARIO);

    -- DELETA PNEUS
    PERFORM INTERNO.FUNC_DELETA_PNEUS(F_COD_EMPRESA_USUARIO, V_COD_UNIDADES_USUARIO);

    -- DELETA COLABORADORES
    PERFORM INTERNO.FUNC_DELETA_COLABORADORES(F_COD_EMPRESA_USUARIO, V_COD_UNIDADES_USUARIO);

    -- DELETA UNIDADES
    PERFORM INTERNO.FUNC_DELETA_UNIDADES(F_COD_EMPRESA_USUARIO, V_COD_UNIDADES_USUARIO);

    -- CLONAGENS
    --- CLONA UNIDADES
    PERFORM INTERNO.FUNC_CLONA_UNIDADES(F_COD_EMPRESA_BASE, F_COD_EMPRESA_USUARIO);

    --- CLONA NOMENCLATURAS
    PERFORM INTERNO.FUNC_CLONA_NOMENCLATURAS(F_COD_EMPRESA_BASE, F_COD_EMPRESA_USUARIO);

    FOREACH V_COD_UNIDADE_BASE IN ARRAY V_COD_UNIDADES_BASE
        LOOP
            V_COD_UNIDADE_USUARIO_NOVA := (SELECT UNOVA.CODIGO
                                           FROM UNIDADE UBASE
                                                    JOIN UNIDADE UNOVA ON UBASE.NOME = UNOVA.NOME
                                           WHERE UBASE.CODIGO = V_COD_UNIDADE_BASE
                                             AND UNOVA.COD_EMPRESA = F_COD_EMPRESA_USUARIO);

            --- CLONA VEÍCULOS
            IF EXISTS(SELECT VD.CODIGO FROM VEICULO_DATA VD WHERE VD.COD_UNIDADE = V_COD_UNIDADE_BASE)
            THEN
                PERFORM INTERNO.FUNC_CLONA_VEICULOS(F_COD_EMPRESA_BASE, V_COD_UNIDADE_BASE, F_COD_EMPRESA_USUARIO,
                                                    V_COD_UNIDADE_USUARIO_NOVA);

            END IF;

            --- CLONA PNEUS
            IF EXISTS(SELECT PD.CODIGO FROM PNEU_DATA PD WHERE PD.COD_UNIDADE = V_COD_UNIDADE_BASE)
            THEN
                PERFORM INTERNO.FUNC_CLONA_PNEUS(F_COD_EMPRESA_BASE, V_COD_UNIDADE_BASE, F_COD_EMPRESA_USUARIO,
                                                 V_COD_UNIDADE_USUARIO_NOVA);
            END IF;

            --- CLONA VINCULOS
            IF EXISTS(SELECT VP.PLACA FROM VEICULO_PNEU VP WHERE VP.COD_UNIDADE = V_COD_UNIDADE_BASE)
            THEN
                PERFORM INTERNO.FUNC_CLONA_VINCULO_VEICULOS_PNEUS(V_COD_UNIDADE_BASE, V_COD_UNIDADE_USUARIO_NOVA);
            END IF;

            --- CLONA COLABORADORES
            IF EXISTS(SELECT CD.CODIGO FROM COLABORADOR_DATA CD WHERE CD.COD_UNIDADE = V_COD_UNIDADE_BASE)
            THEN
                PERFORM INTERNO.FUNC_CLONA_COLABORADORES(F_COD_EMPRESA_BASE, V_COD_UNIDADE_BASE,
                                                         F_COD_EMPRESA_USUARIO,
                                                         V_COD_UNIDADE_USUARIO_NOVA);
            END IF;
        END LOOP;

    V_COLABORADORES_CADASTRADOS = (SELECT ARRAY_AGG(CONCAT('CPF: ', C.CPF,
                                                           ' | DATA NASCIMENTO: ', C.DATA_NASCIMENTO,
                                                           ' | NÍVEL DE PERMISSAO: ', C.COD_PERMISSAO,
                                                           ' | CARGO: ', F.NOME))
                                   FROM COLABORADOR C
                                            JOIN FUNCAO F ON F.COD_EMPRESA = C.COD_EMPRESA AND F.CODIGO = C.COD_FUNCAO
                                   WHERE C.COD_EMPRESA = F_COD_EMPRESA_USUARIO);

    SELECT 'A EMPRESA FOI RESETADA E OS DADOS FORAM CLONADOS COM SUCESSO. OS COLABORADORES CADASTRADOS SÃO: ' ||
           CONCAT(V_COLABORADORES_CADASTRADOS)
    INTO MENSAGEM_SUCESSO;
END ;
$$;

create or replace function interno.func_clona_veiculos(f_cod_empresa_base bigint,
                                                       f_cod_unidade_base bigint,
                                                       f_cod_empresa_usuario bigint,
                                                       f_cod_unidade_usuario bigint)
    returns void
    language plpgsql
as
$$
declare
    v_placa_prefixo_padrao          text   := 'ZXY';
    v_placa_sufixo_padrao           bigint := 0;
    v_placa_verificacao             text;
    v_placas_validas_cadastro       text[];
    v_tentativa_buscar_placa_valida bigint := 0;

begin
    -- VERIFICA SE EXISTEM MODELOS DE VEÍCULOS PARA COPIAR.
    if not EXISTS(select mv.codigo from modelo_veiculo mv where mv.cod_empresa = f_cod_empresa_base)
    then
        raise exception
            'Não existem modelos de veículos para serem copiados da empresa de código: %.' , f_cod_empresa_base;
    end if;

    -- VERIFICA SE EXISTEM TIPOS DE VEÍCULOS PARA COPIAR.
    if not EXISTS(select vt.codigo from veiculo_tipo vt where vt.cod_empresa = f_cod_empresa_base)
    then
        raise exception
            'Não existem tipos de veículos para serem copiados da empresa de código: %.' , f_cod_empresa_base;
    end if;

    -- VERIFICA SE EXISTEM VEÍCULOS PARA COPIAR.
    if not EXISTS(select vd.codigo from veiculo_data vd where vd.cod_unidade = f_cod_unidade_base)
    then
        raise exception
            'Não existem veículos para serem copiados da unidade de código: %.' , f_cod_unidade_base;
    end if;

    -- COPIA OS MODELOS DE VEÍCULOS.
    insert into modelo_veiculo (nome,
                                cod_marca,
                                cod_empresa)
    select mv.nome,
           mv.cod_marca,
           f_cod_empresa_usuario
    from modelo_veiculo mv
    where mv.cod_empresa = f_cod_empresa_base
    on conflict on constraint nomes_unicos_por_empresa_e_marca do nothing;

    -- COPIA OS TIPOS DE VEÍCULOS.
    insert into veiculo_tipo(nome,
                             status_ativo,
                             cod_diagrama,
                             cod_empresa)
    select vt.nome,
           vt.status_ativo,
           vt.cod_diagrama,
           f_cod_empresa_usuario
    from veiculo_tipo vt
    where vt.cod_empresa = f_cod_empresa_base;

    --SELECIONA PLACAS VÁLIDAS PARA CADASTRO.
    while ((ARRAY_LENGTH(v_placas_validas_cadastro, 1) < (select COUNT(vd.placa)
                                                          from veiculo_data vd
                                                          where vd.cod_unidade = f_cod_unidade_base)) or
           (ARRAY_LENGTH(v_placas_validas_cadastro, 1) is null))
        loop
        --EXISTEM 10000 PLACAS DISPONÍVEIS PARA CADASTRO (DE ZXY0000 ATÉ ZXY9999),
        --CASO EXCEDA O NÚMERO DE TENTATIVAS - UM ERRO É MOSTRADO.
            if (v_tentativa_buscar_placa_valida = 10000)
            then
                raise exception
                    'Não existem placas válidas para serem cadastradas';
            end if;
            v_placa_verificacao := CONCAT(v_placa_prefixo_padrao, LPAD(v_placa_sufixo_padrao::text, 4, '0'));
            if not EXISTS(select vd.placa from veiculo_data vd where vd.placa ilike v_placa_verificacao)
            then
                -- PLACAS VÁLIDAS PARA CADASTRO.
                v_placas_validas_cadastro := ARRAY_APPEND(v_placas_validas_cadastro, v_placa_verificacao);
            end if;
            v_placa_sufixo_padrao := v_placa_sufixo_padrao + 1;
            v_tentativa_buscar_placa_valida := v_tentativa_buscar_placa_valida + 1;
        end loop;

    with placas_validas_cadastro as (
        select ROW_NUMBER() over () as codigo,
               vdn                  as placa_cadastro
        from UNNEST(v_placas_validas_cadastro) vdn),
         veiculos_base as (
             select ROW_NUMBER() over () as codigo,
                    vd.placa             as placa_base,
                    vd.km                as km_base,
                    vd.cod_modelo        as modelo_base,
                    vd.cod_tipo          as tipo_base,
                    vd.cod_diagrama      as cod_diagrama_base,
                    vd.motorizado        as motorizado_base
             from veiculo_data vd
             where cod_unidade = f_cod_unidade_base
         ),
         dados_de_para as (
             select distinct on (pvc.placa_cadastro, vb.placa_base) pvc.placa_cadastro   as placa_cadastro,
                                                                    vb.placa_base        as placa_base,
                                                                    vb.km_base           as km_base,
                                                                    mva.codigo           as modelo_base,
                                                                    mvn.codigo           as modelo_novo,
                                                                    vta.codigo           as tipo_base,
                                                                    vtn.codigo           as tipo_novo,
                                                                    vb.cod_diagrama_base as cod_diagrama_base,
                                                                    vb.motorizado_base   as motorizado_base
             from veiculos_base vb
                      join modelo_veiculo mva on mva.codigo = vb.modelo_base
                      join modelo_veiculo mvn on mva.nome = mvn.nome and mva.cod_marca = mvn.cod_marca
                      join veiculo_tipo vta on vb.tipo_base = vta.codigo
                      join veiculo_tipo vtn on vta.nome = vtn.nome and vta.cod_diagrama = vtn.cod_diagrama
                      join placas_validas_cadastro pvc on pvc.codigo = vb.codigo
             where mva.cod_empresa = f_cod_empresa_base
               and mvn.cod_empresa = f_cod_empresa_usuario
               and vta.cod_empresa = f_cod_empresa_base
               and vtn.cod_empresa = f_cod_empresa_usuario)

         -- INSERE AS PLACAS DE->PARA.
    insert
    into veiculo_data(placa,
                      cod_unidade,
                      km,
                      status_ativo,
                      cod_tipo,
                      cod_modelo,
                      cod_eixos,
                      cod_unidade_cadastro,
                      deletado,
                      cod_empresa,
                      cod_diagrama,
                      motorizado)
    select ddp.placa_cadastro,
           f_cod_unidade_usuario,
           ddp.km_base,
           true,
           ddp.tipo_novo,
           ddp.modelo_novo,
           1,
           f_cod_unidade_usuario,
           false,
           f_cod_empresa_usuario,
           ddp.cod_diagrama_base,
           ddp.motorizado_base
    from dados_de_para ddp;
end ;
$$;

CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_VEICULOS(F_COD_EMPRESA BIGINT, F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- DELETA VINCULO VEÍCULO_PNEU
    DELETE FROM VEICULO_PNEU VP WHERE VP.COD_UNIDADE = ANY (F_COD_UNIDADES);

    -- DELETA TODOS OS VEÍCULOS DA EMPRESA DESTINO
    DELETE
    FROM VEICULO_DATA VD
    WHERE VD.COD_EMPRESA = F_COD_EMPRESA;

    -- REALIZA UPDATE DE VEICULOS QUE FORAM TRANSFERIDOS ENTRE EMPRESA PARA REMOVER O COD_UNIDADE_CADASTRO
    UPDATE VEICULO_DATA
    SET COD_UNIDADE_CADASTRO = COD_UNIDADE
    WHERE COD_UNIDADE_CADASTRO = ANY (F_COD_UNIDADES);

    -- DELETA OS MODELOS DE VEÍCULO DA EMPRESA DESTINO
    DELETE
    FROM MODELO_VEICULO MV
    WHERE MV.COD_EMPRESA = F_COD_EMPRESA;

    -- DELETA NOMENCLATURAS
    DELETE
    FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
    WHERE PPNE.COD_EMPRESA = F_COD_EMPRESA;

       -- DELETA OS TIPOS DE VEÍCULOS DA EMPRESA DESTINO
    DELETE
    FROM VEICULO_TIPO VT
    WHERE VT.COD_EMPRESA = F_COD_EMPRESA;
END;
$$;

create or replace function interno.func_clona_colaboradores(f_cod_empresa_base bigint,
                                                            f_cod_unidade_base bigint,
                                                            f_cod_empresa_usuario bigint,
                                                            f_cod_unidade_usuario bigint)
    returns void
    language plpgsql
as
$$
declare
    v_cpf_prefixo_padrao          text   := '0338328';
    v_cpf_sufixo_padrao           bigint := 0;
    v_cpf_verificacao             bigint;
    v_cpfs_validos_cadastro       bigint[];
    v_tentativa_buscar_cpf_valido bigint := 0;
begin
    -- VERIFICA SE EXISTEM EQUIPES DE VEÍCULOS PARA COPIAR
    if not EXISTS(select e.codigo from equipe e where e.cod_unidade = f_cod_unidade_base)
    then
        raise exception
            'Não existem equipes para serem copiadas da unidade de código: %.' , f_cod_unidade_base;
    end if;

    -- VERIFICA SE EXISTEM SETORES PARA COPIAR
    if not EXISTS(select se.codigo from setor se where se.cod_unidade = f_cod_unidade_base)
    then
        raise exception
            'Não existem setores para serem copiados da unidade de código: %.' , f_cod_unidade_base;
    end if;

    -- VERIFICA SE EXISTEM CARGOS PARA COPIAR
    if not EXISTS(select f.codigo from funcao f where f.cod_empresa = f_cod_empresa_base)
    then
        raise exception
            'Não existem cargos para serem copiados da empresa de código: %.' , f_cod_empresa_base;
    end if;

    -- VERIFICA SE EXISTEM COLABORADORES PARA COPIAR
    if not EXISTS(select cd.codigo from colaborador_data cd where cd.cod_unidade = f_cod_unidade_base)
    then
        raise exception
            'Não existem colaboradores para serem copiados da unidade de código: %.' , f_cod_unidade_base;
    end if;

    -- COPIA AS EQUIPES
    insert into equipe (nome,
                        cod_unidade)
    select e.nome,
           f_cod_unidade_usuario
    from equipe e
    where e.cod_unidade = f_cod_unidade_base;

    -- COPIA OS SETORES
    insert into setor(nome,
                      cod_unidade)
    select se.nome,
           f_cod_unidade_usuario
    from setor se
    where se.cod_unidade = f_cod_unidade_base;

    -- COPIA AS FUNÇÕES
    insert into funcao_data (nome,
                             cod_empresa)
    select f.nome,
           f_cod_empresa_usuario
    from funcao f
    where f.cod_empresa = f_cod_empresa_base
    on conflict do nothing;

    --SELECIONA CPFS VÁLIDOS PARA CADASTRO.
    while (((ARRAY_LENGTH(v_cpfs_validos_cadastro, 1)) < (select COUNT(cd.cpf)
                                                          from colaborador_data cd
                                                          where cd.cod_unidade = f_cod_unidade_base)) or
           ((ARRAY_LENGTH(v_cpfs_validos_cadastro, 1)) is null))
        loop
        --EXISTEM 10000 CPFS DISPONÍVEIS PARA CADASTRO (03383280000 ATÉ 03383289999),
        --CASO EXCEDA O NÚMERO DE TENTATIVAS - UM ERRO É MOSTRADO.
            if (v_tentativa_buscar_cpf_valido = 10000)
            then
                raise exception
                    'Não existem cpfs disponíveis para serem cadastrados';
            end if;
            v_cpf_verificacao := (CONCAT(v_cpf_prefixo_padrao, LPAD(v_cpf_sufixo_padrao::text, 4, '0')))::bigint;
            if not EXISTS(select cd.cpf from colaborador_data cd where cd.cpf = v_cpf_verificacao)
            then
                -- CPFS VÁLIDOS PARA CADASTRO
                v_cpfs_validos_cadastro := ARRAY_APPEND(v_cpfs_validos_cadastro, v_cpf_verificacao);
            end if;
            v_cpf_sufixo_padrao := v_cpf_sufixo_padrao + 1;
            v_tentativa_buscar_cpf_valido := v_tentativa_buscar_cpf_valido + 1;
        end loop;

    with cpfs_validos_cadastro as (
        select ROW_NUMBER() over () as codigo,
               cdn                  as cpf_novo_cadastro
        from UNNEST(v_cpfs_validos_cadastro) cdn),
         colaboradores_base as (
             select ROW_NUMBER() over () as codigo,
                    co.cpf               as cpf_base,
                    co.nome              as nome_base,
                    co.data_nascimento   as data_nascimento_base,
                    co.data_admissao     as data_admissao_base,
                    co.cod_equipe        as cod_equipe_base,
                    co.cod_setor         as cod_setor_base,
                    co.cod_funcao        as cod_funcao_base,
                    co.cod_permissao     as cod_permissao_base
             from colaborador co
             where cod_unidade = f_cod_unidade_base
         ),
         dados_de_para as (
             select cvc.cpf_novo_cadastro   as cpf_cadastro,
                    cb.cpf_base             as cpf_base,
                    cb.nome_base            as nome_base,
                    cb.data_nascimento_base as data_nascimento_base,
                    cb.data_admissao_base   as data_admissao_base,
                    cb.cod_permissao_base   as cod_permissao_base,
                    eb.codigo               as cod_equipe_base,
                    en.codigo               as cod_equipe_nova,
                    sb.codigo               as cod_setor_base,
                    sn.codigo               as cod_setor_novo,
                    fb.codigo               as cod_funcao_base,
                    fn.codigo               as cod_funcao_novo
             from colaboradores_base cb
                      join equipe eb on eb.codigo = cb.cod_equipe_base
                      join equipe en on eb.nome = en.nome
                      join setor sb on cb.cod_setor_base = sb.codigo
                      join setor sn on sb.nome = sn.nome
                      join funcao fb on cb.cod_funcao_base = fb.codigo
                      join funcao fn on fb.nome = fn.nome
                      join cpfs_validos_cadastro cvc on cvc.codigo = cb.codigo
             where eb.cod_unidade = f_cod_unidade_base
               and en.cod_unidade = f_cod_unidade_usuario
               and sb.cod_unidade = f_cod_unidade_base
               and sn.cod_unidade = f_cod_unidade_usuario
               and fb.cod_empresa = f_cod_empresa_base
               and fn.cod_empresa = f_cod_empresa_usuario)
         -- INSERE OS COLABORADORES DE->PARA.
    insert
    into colaborador_data(cpf,
                          data_nascimento,
                          data_admissao,
                          status_ativo,
                          nome,
                          cod_equipe,
                          cod_funcao,
                          cod_unidade,
                          cod_permissao,
                          cod_empresa,
                          cod_setor,
                          cod_unidade_cadastro,
                          deletado)
    select ddp.cpf_cadastro,
           ddp.data_nascimento_base,
           ddp.data_admissao_base,
           true,
           ddp.nome_base,
           ddp.cod_equipe_nova,
           ddp.cod_funcao_novo,
           f_cod_unidade_usuario,
           ddp.cod_permissao_base,
           f_cod_empresa_usuario,
           ddp.cod_setor_novo,
           f_cod_unidade_usuario,
           false
    from dados_de_para ddp;
end;
$$;

CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_AFERICOES_DEPENDENCIAS(F_COD_UNIDADES BIGINT[],
                                                                      F_COD_AFERICOES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM AFERICAO_MANUTENCAO_SERVICO_DELETADO_TRANSFERENCIA AMSDT
    WHERE AMSDT.COD_SERVICO IN (SELECT AMD.CODIGO
                                FROM AFERICAO_MANUTENCAO_DATA AMD
                                WHERE AMD.COD_AFERICAO = ANY
                                      (F_COD_AFERICOES));

    DELETE
    FROM AFERICAO_MANUTENCAO_DATA AMD
    WHERE AMD.COD_AFERICAO = ANY (F_COD_AFERICOES);

    DELETE
    FROM AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO ACTAV
    WHERE ACTAV.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM AFERICAO_VALORES_DATA AVD
    WHERE AVD.COD_AFERICAO = ANY (F_COD_AFERICOES);

    DELETE
    FROM AFERICAO_DATA AD
    WHERE AD.CODIGO = ANY (F_COD_AFERICOES);

    DELETE
    FROM PNEU_RESTRICAO_UNIDADE_HISTORICO PRUH
    WHERE PRUH.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM PNEU_RESTRICAO_UNIDADE PRU
    WHERE PRU.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM AFERICAO_CONFIGURACAO_ALERTA_SULCO ACAS
    WHERE ACAS.COD_UNIDADE = ANY (F_COD_UNIDADES);
END;
$$;

CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_CHECKLISTS_DEPENDENCIAS(F_COD_UNIDADES BIGINT[],
                                                                       F_COD_CHECKLISTS BIGINT[],
                                                                       F_COD_CHECKLISTS_MODELO BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_ITENS_OS     BIGINT[] := (SELECT ARRAY_AGG(COSID.CODIGO)
                                    FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSID
                                    WHERE COSID.COD_UNIDADE = ANY (F_COD_UNIDADES));
    V_COD_OS           BIGINT[] := (SELECT ARRAY_AGG(COSD.CODIGO)
                                    FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
                                    WHERE COSD.COD_UNIDADE = ANY (F_COD_UNIDADES));
    V_CODIGO_OS_PROLOG BIGINT[] := (SELECT ARRAY_AGG(COSD.CODIGO_PROLOG)
                                    FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
                                    WHERE COSD.COD_UNIDADE = ANY (F_COD_UNIDADES));
BEGIN
    -- Tornando a constraint deferível
    SET CONSTRAINTS FK_CHECKLIST_MODELO_CHECKLIST_MODELO_VERSAO DEFERRED;

    -- Deleção de checklists realizados.

    DELETE
    FROM CHECKLIST_RESPOSTAS_NOK CRN
    WHERE CRN.COD_CHECKLIST = ANY (F_COD_CHECKLISTS);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_ITEM_DELETADO_TRANSFERENCIA COSIDT
    WHERE COSIDT.COD_ITEM_OS_PROLOG = ANY (V_COD_ITENS_OS);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS COSIA
    WHERE COSIA.COD_CHECKLIST_REALIZADO = ANY (F_COD_CHECKLISTS);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSID
    WHERE COSID.CODIGO = ANY (V_COD_ITENS_OS)
      AND COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_DELETADA_TRANSFERENCIA COSDT
    WHERE COSDT.COD_OS_PROLOG = ANY (V_CODIGO_OS_PROLOG);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
    WHERE COSD.CODIGO = ANY (V_COD_OS)
      AND COSD.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM CHECKLIST_DATA CD
    WHERE CD.CODIGO = ANY (F_COD_CHECKLISTS);

    -- Deleção de modelos.

    -- DROPA REGRA QUE IMPEDE QUE ALTERNATIVA SEJA DELETADA.
    DROP RULE ALTERNATIVA_CHECK_DELETE_PROTECT ON CHECKLIST_ALTERNATIVA_PERGUNTA_DATA;
    -- DELETA ALTERNATIVA.
    DELETE
    FROM CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAPD
    WHERE CAPD.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND CAPD.COD_CHECKLIST_MODELO = ANY (F_COD_CHECKLISTS_MODELO);
    -- RECRIA REGRA QUE IMPEDE QUE ALTERNATIVA SEJA DELETADA.
    CREATE RULE ALTERNATIVA_CHECK_DELETE_PROTECT AS
        ON DELETE TO PUBLIC.CHECKLIST_ALTERNATIVA_PERGUNTA_DATA DO INSTEAD NOTHING;

    -- DROPA REGRA QUE IMPEDE QUE PERGUNTA SEJA DELETADA.
    DROP RULE PERGUNTA_CHECK_DELETE_PROTECT ON CHECKLIST_PERGUNTAS_DATA;
    -- DELETA PERGUNTA.
    DELETE
    FROM CHECKLIST_PERGUNTAS_DATA CP
    WHERE CP.COD_UNIDADE = ANY (F_COD_UNIDADES);
    -- RECRIA REGRA QUE IMPEDE QUE PERGUNTA SEJA DELETADA.
    CREATE RULE PERGUNTA_CHECK_DELETE_PROTECT AS
        ON DELETE TO PUBLIC.CHECKLIST_PERGUNTAS_DATA DO INSTEAD NOTHING;

    -- DELETA MODELO DE VERSÃO DE CHECK
    DELETE
    FROM CHECKLIST_MODELO_VERSAO CMV
    WHERE CMV.COD_CHECKLIST_MODELO = ANY (F_COD_CHECKLISTS_MODELO);

    DELETE
    FROM CHECKLIST_MODELO_VEICULO_TIPO CMVT
    WHERE CMVT.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM CHECKLIST_MODELO_FUNCAO CMF
    WHERE CMF.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM CHECKLIST_MODELO_DATA CMD
    WHERE CMD.CODIGO = ANY (F_COD_CHECKLISTS_MODELO);
END;
$$;

CREATE OR REPLACE FUNCTION INTERNO.FUNC_CLONA_PNEUS(F_COD_EMPRESA_BASE BIGINT,
                                                    F_COD_UNIDADE_BASE BIGINT,
                                                    F_COD_EMPRESA_USUARIO BIGINT,
                                                    F_COD_UNIDADE_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- VERIFICA SE EXISTEM MODELOS DE PNEUS PARA COPIAR.
    IF NOT EXISTS(SELECT MP.CODIGO FROM MODELO_PNEU MP WHERE MP.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM MODELOS DE PNEUS PARA SEREM COPIADOS DA EMPRESA DE CÓDIGO: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM MARCAS DE BANDA PARA COPIAR.
    IF NOT EXISTS(SELECT MAB.CODIGO FROM MARCA_BANDA MAB WHERE MAB.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM MARCAS DE BANDAS PARA SEREM COPIADOS DA EMPRESA DE CÓDIGO: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM MODELOS DE BANDA PARA COPIAR.
    IF NOT EXISTS(SELECT MOB.CODIGO FROM MODELO_BANDA MOB WHERE MOB.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM MODELOS DE BANDA PARA SEREM COPIADOS DA EMPRESA DE CÓDIGO: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM PNEUS PARA COPIAR.
    IF NOT EXISTS(SELECT PD.CODIGO FROM PNEU_DATA PD WHERE PD.COD_UNIDADE = F_COD_UNIDADE_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM PNEUS PARA SEREM COPIADOS DA UNIDADE DE CÓDIGO: %.' , F_COD_UNIDADE_BASE;
    END IF;

    -- COPIA OS MODELOS DE PNEUS.
    INSERT INTO MODELO_PNEU (NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
    SELECT MP.NOME,
           MP.COD_MARCA,
           F_COD_EMPRESA_USUARIO,
           MP.QT_SULCOS,
           MP.ALTURA_SULCOS
    FROM MODELO_PNEU MP
    WHERE MP.COD_EMPRESA = F_COD_EMPRESA_BASE;

    -- COPIA AS MARCAS DE BANDAS.
    INSERT INTO MARCA_BANDA(NOME, COD_EMPRESA)
    SELECT MAB.NOME,
           F_COD_EMPRESA_USUARIO
    FROM MARCA_BANDA MAB
    WHERE MAB.COD_EMPRESA = F_COD_EMPRESA_BASE
    ON CONFLICT ON CONSTRAINT UNIQUE_MARCA_BANDA
        DO NOTHING;

    -- REALIZA O DE -> PARA DOS CÓDIGOS DE MARCAS DE BANDA E INSERE OS MODELOS
    WITH DADOS_MARCA_BANDA_DE_PARA AS (
        SELECT MABB.CODIGO       AS COD_MARCA_BANDA_BASE,
               MABB.NOME         AS NOME_MARCA_BANDA_BASE,
               MABN.CODIGO       AS COD_MARCA_BANDA_NOVO,
               MABN.CODIGO       AS NOME_MARCA_BANDA_NOVO,
               MOB.NOME          AS NOME_MODELO_BANDA_BASE,
               MOB.ALTURA_SULCOS AS ALTURA_SULCOS_BANDA_BASE,
               MOB.QT_SULCOS     AS QT_SULCOS_BANDA_BASE
        FROM MARCA_BANDA MABB
                 JOIN MARCA_BANDA MABN ON MABB.NOME = MABN.NOME
                 JOIN MODELO_BANDA MOB ON MABB.CODIGO = MOB.COD_MARCA AND MABB.COD_EMPRESA = MOB.COD_EMPRESA
        WHERE MABB.COD_EMPRESA = F_COD_EMPRESA_BASE
          AND MABN.COD_EMPRESA = F_COD_EMPRESA_USUARIO)

         -- REALIZA A CLONAGEM DE MODELOS DE BANDA COM O CÓDIGO DAS MARCAS DE->PARA.
    INSERT
    INTO MODELO_BANDA(NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
    SELECT DMBDP.NOME_MODELO_BANDA_BASE,
           DMBDP.COD_MARCA_BANDA_NOVO,
           F_COD_EMPRESA_USUARIO,
           DMBDP.QT_SULCOS_BANDA_BASE,
           DMBDP.ALTURA_SULCOS_BANDA_BASE
    FROM DADOS_MARCA_BANDA_DE_PARA DMBDP
    ON CONFLICT ON CONSTRAINT UNIQUE_NOME_MODELO_BANDA_POR_MARCA
        DO NOTHING;

    PERFORM SETVAL('PNEU_DATA_CODIGO_SEQ', (SELECT MAX(P.CODIGO +1) FROM PNEU_DATA P));
    -- DADOS DE PARA
    WITH PNEUS_BASE AS (
        SELECT PD.CODIGO_CLIENTE               AS NUMERO_FOGO_BASE,
               PD.COD_MODELO                   AS COD_MODELO_PNEU_BASE,
               PD.COD_DIMENSAO                 AS COD_DIMENSAO_BASE,
               PD.PRESSAO_RECOMENDADA          AS PRESSAO_RECOMENDADA_BASE,
               PD.PRESSAO_ATUAL                AS PRESSAO_ATUAL_BASE,
               PD.ALTURA_SULCO_INTERNO         AS ALTURA_SULCO_INTERNO_BASE,
               PD.ALTURA_SULCO_CENTRAL_INTERNO AS ALTURA_SULCO_CENTRAL_INTERNO_BASE,
               PD.ALTURA_SULCO_EXTERNO         AS ALTURA_SULCO_EXTERNO_BASE,
               PD.STATUS                       AS STATUS_BASE,
               PD.VIDA_ATUAL                   AS VIDA_ATUAL_BASE,
               PD.VIDA_TOTAL                   AS VIDA_TOTAL_BASE,
               PD.COD_MODELO_BANDA             AS COD_MODELO_BANDA_BASE,
               PD.ALTURA_SULCO_CENTRAL_EXTERNO AS ALTURA_SULCO_CENTRAL_EXTERNO_BASE,
               PD.DOT                          AS DOT_BASE,
               PD.VALOR                        AS VALOR_BASE
        FROM PNEU PD
        WHERE COD_UNIDADE = F_COD_UNIDADE_BASE
    ),
         DADOS_DE_PARA AS (
             SELECT DISTINCT ON (PB.NUMERO_FOGO_BASE) PB.NUMERO_FOGO_BASE,
                                                      PB.COD_MODELO_PNEU_BASE,
                                                      MPB.CODIGO  AS COD_MODELO_PNEU_BASE,
                                                      MPN.CODIGO  AS COD_MODELO_PNEU_NOVO,
                                                      PB.COD_DIMENSAO_BASE,
                                                      PB.PRESSAO_RECOMENDADA_BASE,
                                                      PB.PRESSAO_ATUAL_BASE,
                                                      PB.ALTURA_SULCO_INTERNO_BASE,
                                                      PB.ALTURA_SULCO_CENTRAL_INTERNO_BASE,
                                                      PB.ALTURA_SULCO_EXTERNO_BASE,
                                                      PB.STATUS_BASE,
                                                      PB.VIDA_ATUAL_BASE,
                                                      PB.VIDA_TOTAL_BASE,
                                                      PB.COD_MODELO_BANDA_BASE,
                                                      MABB.NOME   AS NOME_MARCA_BANDA_BASE,
                                                      MABN.NOME   AS NOME_MARCA_BANSA_NOVA,
                                                      MABB.CODIGO AS COD_MARCA_BANDA_BASE,
                                                      MABN.CODIGO AS COD_MARCA_BANDA_NOVA,
                                                      MOBB.CODIGO AS COD_MODELO_BANDA_BASE,
                                                      MOBN.CODIGO AS COD_MODELO_BANDA_NOVO,
                                                      PB.ALTURA_SULCO_CENTRAL_EXTERNO_BASE,
                                                      PB.DOT_BASE,
                                                      PB.VALOR_BASE
             FROM PNEUS_BASE PB
                      JOIN MODELO_PNEU MPB
                           ON MPB.CODIGO = PB.COD_MODELO_PNEU_BASE AND MPB.COD_EMPRESA = F_COD_EMPRESA_BASE
                      JOIN MODELO_PNEU MPN
                           ON MPB.NOME = MPN.NOME AND MPB.COD_MARCA = MPN.COD_MARCA AND
                              MPN.COD_EMPRESA = F_COD_EMPRESA_USUARIO
                      LEFT JOIN MODELO_BANDA MOBB
                                ON PB.COD_MODELO_BANDA_BASE = MOBB.CODIGO AND MOBB.COD_EMPRESA = F_COD_EMPRESA_BASE
                      LEFT JOIN MODELO_BANDA MOBN ON MOBB.NOME = MOBN.NOME AND MOBN.COD_EMPRESA = F_COD_EMPRESA_USUARIO
                      LEFT JOIN MARCA_BANDA MABB ON MABB.CODIGO = MOBB.COD_MARCA
                      LEFT JOIN MARCA_BANDA MABN ON MABN.CODIGO = MOBN.COD_MARCA AND MABB.NOME = MABN.NOME)

         -- REALIZA A CLONAGEM DE PNEUS
    INSERT
    INTO PNEU_DATA (CODIGO_CLIENTE,
                    COD_MODELO,
                    COD_DIMENSAO,
                    PRESSAO_RECOMENDADA,
                    PRESSAO_ATUAL,
                    ALTURA_SULCO_INTERNO,
                    ALTURA_SULCO_CENTRAL_INTERNO,
                    ALTURA_SULCO_EXTERNO,
                    COD_UNIDADE,
                    STATUS,
                    VIDA_ATUAL,
                    VIDA_TOTAL,
                    COD_MODELO_BANDA,
                    ALTURA_SULCO_CENTRAL_EXTERNO,
                    DOT,
                    VALOR,
                    COD_EMPRESA,
                    COD_UNIDADE_CADASTRO)
    SELECT DDP.NUMERO_FOGO_BASE,
           DDP.COD_MODELO_PNEU_NOVO,
           DDP.COD_DIMENSAO_BASE,
           DDP.PRESSAO_RECOMENDADA_BASE,
           DDP.PRESSAO_ATUAL_BASE,
           DDP.ALTURA_SULCO_INTERNO_BASE,
           DDP.ALTURA_SULCO_CENTRAL_INTERNO_BASE,
           DDP.ALTURA_SULCO_EXTERNO_BASE,
           F_COD_UNIDADE_USUARIO,
           DDP.STATUS_BASE,
           DDP.VIDA_ATUAL_BASE,
           DDP.VIDA_TOTAL_BASE,
           DDP.COD_MODELO_BANDA_NOVO,
           DDP.ALTURA_SULCO_CENTRAL_EXTERNO_BASE,
           DDP.DOT_BASE,
           DDP.VALOR_BASE,
           F_COD_EMPRESA_USUARIO,
           F_COD_UNIDADE_USUARIO
    FROM DADOS_DE_PARA DDP;
END;
$$;

create or replace function interno.func_clona_colaboradores(f_cod_empresa_base bigint,
                                                            f_cod_unidade_base bigint,
                                                            f_cod_empresa_usuario bigint,
                                                            f_cod_unidade_usuario bigint)
    returns void
    language plpgsql
as
$$
declare
    v_cpf_prefixo_padrao          text   := '0338328';
    v_cpf_sufixo_padrao           bigint := 0;
    v_cpf_verificacao             bigint;
    v_cpfs_validos_cadastro       bigint[];
    v_tentativa_buscar_cpf_valido bigint := 0;
begin
    -- VERIFICA SE EXISTEM EQUIPES DE VEÍCULOS PARA COPIAR
    if not EXISTS(select e.codigo from equipe e where e.cod_unidade = f_cod_unidade_base)
    then
        raise exception
            'Não existem equipes para serem copiadas da unidade de código: %.' , f_cod_unidade_base;
    end if;

    -- VERIFICA SE EXISTEM SETORES PARA COPIAR
    if not EXISTS(select se.codigo from setor se where se.cod_unidade = f_cod_unidade_base)
    then
        raise exception
            'Não existem setores para serem copiados da unidade de código: %.' , f_cod_unidade_base;
    end if;

    -- VERIFICA SE EXISTEM CARGOS PARA COPIAR
    if not EXISTS(select f.codigo from funcao f where f.cod_empresa = f_cod_empresa_base)
    then
        raise exception
            'Não existem cargos para serem copiados da empresa de código: %.' , f_cod_empresa_base;
    end if;

    -- VERIFICA SE EXISTEM COLABORADORES PARA COPIAR
    if not EXISTS(select cd.codigo from colaborador_data cd where cd.cod_unidade = f_cod_unidade_base)
    then
        raise exception
            'Não existem colaboradores para serem copiados da unidade de código: %.' , f_cod_unidade_base;
    end if;

    -- COPIA AS EQUIPES
    insert into equipe (nome,
                        cod_unidade)
    select e.nome,
           f_cod_unidade_usuario
    from equipe e
    where e.cod_unidade = f_cod_unidade_base;

    -- COPIA OS SETORES
    insert into setor(nome,
                      cod_unidade)
    select se.nome,
           f_cod_unidade_usuario
    from setor se
    where se.cod_unidade = f_cod_unidade_base;

    -- COPIA AS FUNÇÕES
    insert into funcao_data (nome,
                             cod_empresa)
    select f.nome,
           f_cod_empresa_usuario
    from funcao f
    where f.cod_empresa = f_cod_empresa_base
    on conflict do nothing;

    --SELECIONA CPFS VÁLIDOS PARA CADASTRO.
    while (((ARRAY_LENGTH(v_cpfs_validos_cadastro, 1)) < (select COUNT(cd.cpf)
                                                          from colaborador_data cd
                                                          where cd.cod_unidade = f_cod_unidade_base)) or
           ((ARRAY_LENGTH(v_cpfs_validos_cadastro, 1)) is null))
        loop
        --EXISTEM 10000 CPFS DISPONÍVEIS PARA CADASTRO (03383280000 ATÉ 03383289999),
        --CASO EXCEDA O NÚMERO DE TENTATIVAS - UM ERRO É MOSTRADO.
            if (v_tentativa_buscar_cpf_valido = 10000)
            then
                raise exception
                    'Não existem cpfs disponíveis para serem cadastrados';
            end if;
            v_cpf_verificacao := (CONCAT(v_cpf_prefixo_padrao, LPAD(v_cpf_sufixo_padrao::text, 4, '0')))::bigint;
            if not EXISTS(select cd.cpf from colaborador_data cd where cd.cpf = v_cpf_verificacao)
            then
                -- CPFS VÁLIDOS PARA CADASTRO
                v_cpfs_validos_cadastro := ARRAY_APPEND(v_cpfs_validos_cadastro, v_cpf_verificacao);
            end if;
            v_cpf_sufixo_padrao := v_cpf_sufixo_padrao + 1;
            v_tentativa_buscar_cpf_valido := v_tentativa_buscar_cpf_valido + 1;
        end loop;

    perform setval('colaborador_data_codigo_seq', (select max(cd.codigo + 1) from colaborador_data cd));

    with cpfs_validos_cadastro as (
        select ROW_NUMBER() over () as codigo,
               cdn                  as cpf_novo_cadastro
        from UNNEST(v_cpfs_validos_cadastro) cdn),
         colaboradores_base as (
             select ROW_NUMBER() over () as codigo,
                    co.cpf               as cpf_base,
                    co.nome              as nome_base,
                    co.data_nascimento   as data_nascimento_base,
                    co.data_admissao     as data_admissao_base,
                    co.cod_equipe        as cod_equipe_base,
                    co.cod_setor         as cod_setor_base,
                    co.cod_funcao        as cod_funcao_base,
                    co.cod_permissao     as cod_permissao_base
             from colaborador co
             where cod_unidade = f_cod_unidade_base
         ),
         dados_de_para as (
             select cvc.cpf_novo_cadastro   as cpf_cadastro,
                    cb.cpf_base             as cpf_base,
                    cb.nome_base            as nome_base,
                    cb.data_nascimento_base as data_nascimento_base,
                    cb.data_admissao_base   as data_admissao_base,
                    cb.cod_permissao_base   as cod_permissao_base,
                    eb.codigo               as cod_equipe_base,
                    en.codigo               as cod_equipe_nova,
                    sb.codigo               as cod_setor_base,
                    sn.codigo               as cod_setor_novo,
                    fb.codigo               as cod_funcao_base,
                    fn.codigo               as cod_funcao_novo
             from colaboradores_base cb
                      join equipe eb on eb.codigo = cb.cod_equipe_base
                      join equipe en on eb.nome = en.nome
                      join setor sb on cb.cod_setor_base = sb.codigo
                      join setor sn on sb.nome = sn.nome
                      join funcao fb on cb.cod_funcao_base = fb.codigo
                      join funcao fn on fb.nome = fn.nome
                      join cpfs_validos_cadastro cvc on cvc.codigo = cb.codigo
             where eb.cod_unidade = f_cod_unidade_base
               and en.cod_unidade = f_cod_unidade_usuario
               and sb.cod_unidade = f_cod_unidade_base
               and sn.cod_unidade = f_cod_unidade_usuario
               and fb.cod_empresa = f_cod_empresa_base
               and fn.cod_empresa = f_cod_empresa_usuario)
         -- INSERE OS COLABORADORES DE->PARA.
    insert
    into colaborador_data(cpf,
                          data_nascimento,
                          data_admissao,
                          status_ativo,
                          nome,
                          cod_equipe,
                          cod_funcao,
                          cod_unidade,
                          cod_permissao,
                          cod_empresa,
                          cod_setor,
                          cod_unidade_cadastro,
                          deletado)
    select ddp.cpf_cadastro,
           ddp.data_nascimento_base,
           ddp.data_admissao_base,
           true,
           ddp.nome_base,
           ddp.cod_equipe_nova,
           ddp.cod_funcao_novo,
           f_cod_unidade_usuario,
           ddp.cod_permissao_base,
           f_cod_empresa_usuario,
           ddp.cod_setor_novo,
           f_cod_unidade_usuario,
           false
    from dados_de_para ddp;
end;
$$;

create or replace function interno.func_clona_veiculos(f_cod_empresa_base bigint,
                                                       f_cod_unidade_base bigint,
                                                       f_cod_empresa_usuario bigint,
                                                       f_cod_unidade_usuario bigint)
    returns void
    language plpgsql
as
$$
declare
    v_placa_prefixo_padrao          text   := 'ZXY';
    v_placa_sufixo_padrao           bigint := 0;
    v_placa_verificacao             text;
    v_placas_validas_cadastro       text[];
    v_tentativa_buscar_placa_valida bigint := 0;

begin
    -- VERIFICA SE EXISTEM MODELOS DE VEÍCULOS PARA COPIAR.
    if not EXISTS(select mv.codigo from modelo_veiculo mv where mv.cod_empresa = f_cod_empresa_base)
    then
        raise exception
            'Não existem modelos de veículos para serem copiados da empresa de código: %.' , f_cod_empresa_base;
    end if;

    -- VERIFICA SE EXISTEM TIPOS DE VEÍCULOS PARA COPIAR.
    if not EXISTS(select vt.codigo from veiculo_tipo vt where vt.cod_empresa = f_cod_empresa_base)
    then
        raise exception
            'Não existem tipos de veículos para serem copiados da empresa de código: %.' , f_cod_empresa_base;
    end if;

    -- VERIFICA SE EXISTEM VEÍCULOS PARA COPIAR.
    if not EXISTS(select vd.codigo from veiculo_data vd where vd.cod_unidade = f_cod_unidade_base)
    then
        raise exception
            'Não existem veículos para serem copiados da unidade de código: %.' , f_cod_unidade_base;
    end if;

    -- COPIA OS MODELOS DE VEÍCULOS.
    insert into modelo_veiculo (nome,
                                cod_marca,
                                cod_empresa)
    select mv.nome,
           mv.cod_marca,
           f_cod_empresa_usuario
    from modelo_veiculo mv
    where mv.cod_empresa = f_cod_empresa_base
    on conflict on constraint nomes_unicos_por_empresa_e_marca do nothing;

    -- COPIA OS TIPOS DE VEÍCULOS.
    insert into veiculo_tipo(nome,
                             status_ativo,
                             cod_diagrama,
                             cod_empresa)
    select vt.nome,
           vt.status_ativo,
           vt.cod_diagrama,
           f_cod_empresa_usuario
    from veiculo_tipo vt
    where vt.cod_empresa = f_cod_empresa_base;

    --SELECIONA PLACAS VÁLIDAS PARA CADASTRO.
    while ((ARRAY_LENGTH(v_placas_validas_cadastro, 1) < (select COUNT(vd.placa)
                                                          from veiculo_data vd
                                                          where vd.cod_unidade = f_cod_unidade_base)) or
           (ARRAY_LENGTH(v_placas_validas_cadastro, 1) is null))
        loop
        --EXISTEM 10000 PLACAS DISPONÍVEIS PARA CADASTRO (DE ZXY0000 ATÉ ZXY9999),
        --CASO EXCEDA O NÚMERO DE TENTATIVAS - UM ERRO É MOSTRADO.
            if (v_tentativa_buscar_placa_valida = 10000)
            then
                raise exception
                    'Não existem placas válidas para serem cadastradas';
            end if;
            v_placa_verificacao := CONCAT(v_placa_prefixo_padrao, LPAD(v_placa_sufixo_padrao::text, 4, '0'));
            if not EXISTS(select vd.placa from veiculo_data vd where vd.placa ilike v_placa_verificacao)
            then
                -- PLACAS VÁLIDAS PARA CADASTRO.
                v_placas_validas_cadastro := ARRAY_APPEND(v_placas_validas_cadastro, v_placa_verificacao);
            end if;
            v_placa_sufixo_padrao := v_placa_sufixo_padrao + 1;
            v_tentativa_buscar_placa_valida := v_tentativa_buscar_placa_valida + 1;
        end loop;

    perform setval('veiculo_data_codigo_seq', (select max(vd.codigo + 1) from veiculo_data vd));

    with placas_validas_cadastro as (
        select ROW_NUMBER() over () as codigo,
               vdn                  as placa_cadastro
        from UNNEST(v_placas_validas_cadastro) vdn),
         veiculos_base as (
             select ROW_NUMBER() over () as codigo,
                    vd.placa             as placa_base,
                    vd.km                as km_base,
                    vd.cod_modelo        as modelo_base,
                    vd.cod_tipo          as tipo_base,
                    vd.cod_diagrama      as cod_diagrama_base,
                    vd.motorizado        as motorizado_base
             from veiculo_data vd
             where cod_unidade = f_cod_unidade_base
         ),
         dados_de_para as (
             select distinct on (pvc.placa_cadastro, vb.placa_base) pvc.placa_cadastro   as placa_cadastro,
                                                                    vb.placa_base        as placa_base,
                                                                    vb.km_base           as km_base,
                                                                    mva.codigo           as modelo_base,
                                                                    mvn.codigo           as modelo_novo,
                                                                    vta.codigo           as tipo_base,
                                                                    vtn.codigo           as tipo_novo,
                                                                    vb.cod_diagrama_base as cod_diagrama_base,
                                                                    vb.motorizado_base   as motorizado_base
             from veiculos_base vb
                      join modelo_veiculo mva on mva.codigo = vb.modelo_base
                      join modelo_veiculo mvn on mva.nome = mvn.nome and mva.cod_marca = mvn.cod_marca
                      join veiculo_tipo vta on vb.tipo_base = vta.codigo
                      join veiculo_tipo vtn on vta.nome = vtn.nome and vta.cod_diagrama = vtn.cod_diagrama
                      join placas_validas_cadastro pvc on pvc.codigo = vb.codigo
             where mva.cod_empresa = f_cod_empresa_base
               and mvn.cod_empresa = f_cod_empresa_usuario
               and vta.cod_empresa = f_cod_empresa_base
               and vtn.cod_empresa = f_cod_empresa_usuario)

         -- INSERE AS PLACAS DE->PARA.
    insert
    into veiculo_data(placa,
                      cod_unidade,
                      km,
                      status_ativo,
                      cod_tipo,
                      cod_modelo,
                      cod_eixos,
                      cod_unidade_cadastro,
                      deletado,
                      cod_empresa,
                      cod_diagrama,
                      motorizado)
    select ddp.placa_cadastro,
           f_cod_unidade_usuario,
           ddp.km_base,
           true,
           ddp.tipo_novo,
           ddp.modelo_novo,
           1,
           f_cod_unidade_usuario,
           false,
           f_cod_empresa_usuario,
           ddp.cod_diagrama_base,
           ddp.motorizado_base
    from dados_de_para ddp;
end ;
$$;
