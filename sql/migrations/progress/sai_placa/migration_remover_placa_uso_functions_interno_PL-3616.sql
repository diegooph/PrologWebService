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
            IF EXISTS(SELECT VP.COD_VEICULO FROM VEICULO_PNEU VP WHERE VP.COD_UNIDADE = V_COD_UNIDADE_BASE)
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

create or replace function
    interno.func_clona_vinculo_veiculos_pneus(f_cod_unidade_base bigint, f_cod_unidade_usuario bigint)
    returns void
    language plpgsql
as
$$
declare
    v_cod_veiculos_com_vinculo text := (select array_agg(vp.cod_veiculo)
                                        from veiculo_pneu vp
                                        where vp.cod_unidade = f_cod_unidade_base);
begin
    -- COPIA VÍNCULOS, CASO EXISTAM.
    if (v_cod_veiculos_com_vinculo is not null)
    then
        with veiculos_base as (
            select row_number() over () as codigo_comparacao,
                   v.codigo             as cod_veiculo,
                   v.placa,
                   vdpp.posicao_prolog
            from veiculo_data v
                     join veiculo_tipo vt on v.cod_tipo = vt.codigo and v.cod_empresa = vt.cod_empresa
                     join veiculo_diagrama_posicao_prolog vdpp
                          on vt.cod_diagrama = vdpp.cod_diagrama
            where v.cod_unidade = f_cod_unidade_base
        ),
             veiculos_novos as (
                 select row_number() over () as codigo_comparacao,
                        v.placa,
                        v.cod_diagrama,
                        v.codigo,
                        vdpp.posicao_prolog
                 from veiculo_data v
                          join veiculo_tipo vt on v.cod_tipo = vt.codigo and v.cod_empresa = vt.cod_empresa
                          join veiculo_diagrama_posicao_prolog vdpp on vt.cod_diagrama = vdpp.cod_diagrama
                 where v.cod_unidade = f_cod_unidade_usuario),
             dados_de_para as (
                 select vn.codigo         as cod_veiculo_novo,
                        vn.placa          as placa_nova,
                        vn.posicao_prolog as posicao_prolog_novo,
                        vn.cod_diagrama   as cod_diagrama_novo,
                        pdn.codigo        as cod_pneu_novo
                 from veiculos_base vb
                          join veiculos_novos vn
                               on vb.codigo_comparacao = vn.codigo_comparacao and vb.posicao_prolog = vn.posicao_prolog
                          join veiculo_pneu vp on vb.cod_veiculo = vp.cod_veiculo and vb.posicao_prolog = vp.posicao
                          join pneu_data pdb
                               on vp.status_pneu = pdb.status and vp.cod_unidade = pdb.cod_unidade and
                                  vp.cod_pneu = pdb.codigo
                          join pneu_data pdn
                               on pdb.codigo_cliente = pdn.codigo_cliente and
                                  pdn.cod_unidade = f_cod_unidade_usuario and
                                  pdn.status = 'EM_USO')
        insert
        into veiculo_pneu (cod_pneu, cod_unidade, posicao, cod_diagrama, cod_veiculo)
        select ddp.cod_pneu_novo,
               f_cod_unidade_usuario,
               ddp.posicao_prolog_novo,
               ddp.cod_diagrama_novo,
               ddp.cod_veiculo_novo
        from dados_de_para ddp;
    end if;
end;
$$;

create or replace function implantacao.tg_func_vinculo_veiculo_pneu_confere_planilha_vinculo()
    returns trigger
    language plpgsql
    security definer
as
$$
declare
    v_qtd_erros            smallint := 0;
    v_msgs_erros           text;
    v_quebra_linha         text     := chr(10);
    v_cod_pneu             bigint;
    v_status_pneu          varchar(255);
    v_cod_unidade_pneu     bigint;
    v_cod_veiculo          bigint;
    v_placa                varchar(7);
    v_cod_tipo_veiculo     bigint;
    v_cod_diagrama_veiculo bigint;
    v_cod_unidade_placa    bigint;
    v_cod_empresa_placa    bigint;
    v_posicao_prolog       integer;
begin
    if (tg_op = 'UPDATE' and old.status_vinculo_realizado is true)
    then
        return old;
    else
        if (tg_op = 'UPDATE')
        then
            new.cod_unidade = old.cod_unidade;
            new.cod_empresa = old.cod_empresa;
        end if;
        new.usuario_update := session_user;
        new.placa_formatada_vinculo := remove_espacos_e_caracteres_especiais(new.placa_editavel);
        new.numero_fogo_pneu_formatado_vinculo := remove_all_spaces(new.numero_fogo_pneu_editavel);
        new.nomenclatura_posicao_formatada_vinculo := remove_all_spaces(new.nomenclatura_posicao_editavel);

        -- Verifica se empresa existe.
        if not exists(select e.codigo from empresa e where e.codigo = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- Verifica se unidade existe.
        if not exists(select u.codigo from unidade u where u.codigo = new.cod_unidade)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- Verifica se unidade pertence a empresa.
        if not exists(
                select u.codigo from unidade u where u.codigo = new.cod_unidade and u.cod_empresa = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- A UNIDADE NÃO PERTENCE A EMPRESA', v_quebra_linha);
        end if;

        -- Verificações placas.
        -- Placa nula: Erro.
        -- Placa cadastrada em outra empresa: Erro.
        -- Placa cadastrada em outra unidade da mesma empresa: Erro.
        -- Posicao já ocupada por outro pneu: Erro.
        if ((new.placa_formatada_vinculo is not null) and
            (length(new.placa_formatada_vinculo) <> 0))
        then
            select v.codigo,
                   v.placa,
                   v.cod_tipo,
                   v.cod_diagrama,
                   v.cod_unidade,
                   v.cod_empresa
            into v_cod_veiculo,
                v_placa,
                v_cod_tipo_veiculo,
                v_cod_diagrama_veiculo,
                v_cod_unidade_placa,
                v_cod_empresa_placa
            from veiculo v
            where remove_all_spaces(v.placa) ilike
                  new.placa_formatada_vinculo;
            if (v_placa is null)
            then
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                      '- A PLACA NÃO FOI ENCONTRADA',
                                      v_quebra_linha);
                new.status_vinculo_realizado = false;
            else
                if (v_cod_empresa_placa != new.cod_empresa)
                then
                    v_qtd_erros = v_qtd_erros + 1;
                    v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                          '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS A PLACA PERTENCE A OUTRA EMPRESA',
                                          v_quebra_linha);
                    new.status_vinculo_realizado = false;
                else
                    if (v_cod_unidade_placa != new.cod_unidade)
                    then
                        v_qtd_erros = v_qtd_erros + 1;
                        v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                              '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS A PLACA PERTENCE A OUTRA
                                              UNIDADE',
                                              v_quebra_linha);
                        new.status_vinculo_realizado = false;
                    else
                        -- Verificar se a posição existe nesse veículo e se está disponível.
                        if ((new.nomenclatura_posicao_formatada_vinculo is not null) and
                            (length(new.nomenclatura_posicao_formatada_vinculo) <> 0))
                        then
                            select ppne.posicao_prolog
                            into v_posicao_prolog
                            from pneu_posicao_nomenclatura_empresa ppne
                            where ppne.cod_diagrama = v_cod_diagrama_veiculo
                              and remove_all_spaces(ppne.nomenclatura)
                                ilike new.nomenclatura_posicao_formatada_vinculo
                              and ppne.cod_empresa = new.cod_empresa;
                            if (v_posicao_prolog is not null)
                            then
                                if exists(select vp.cod_veiculo
                                          from veiculo_pneu vp
                                          where vp.cod_veiculo = v_cod_veiculo
                                            and vp.posicao = v_posicao_prolog
                                            and vp.cod_unidade = new.cod_unidade)
                                then
                                    v_qtd_erros = v_qtd_erros + 1;
                                    v_msgs_erros =
                                            concat(v_msgs_erros, v_qtd_erros,
                                                   '- JÁ EXISTE PNEU VINCULADO À POSIÇÃO (NOMENCLATURA) INFORMADA',
                                                   v_quebra_linha);
                                end if;
                            else
                                v_qtd_erros = v_qtd_erros + 1;
                                v_msgs_erros =
                                        concat(v_msgs_erros, v_qtd_erros,
                                               '- NOMENCLATURA NÃO ENCONTRADA',
                                               v_quebra_linha);
                                new.status_vinculo_realizado = false;
                            end if;
                        else
                            v_qtd_erros = v_qtd_erros + 1;
                            v_msgs_erros =
                                    concat(v_msgs_erros, v_qtd_erros,
                                           '- NOMENCLATURA NÃO PODE SER NULA',
                                           v_quebra_linha);
                        end if;
                    end if;
                end if;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- A PLACA DE FOGO NÃO PODE SER NULA',
                           v_quebra_linha);
        end if;


        -- Verificações número de fogo.
        -- Número de fogo nulo: Erro.
        -- Número de fogo cadastrado em outra unidade da mesma empresa: Erro.
        -- Código do pneu não encontrado: Erro.
        -- Status do pneu diferente de 'ESTOQUE': Erro.
        if ((new.numero_fogo_pneu_formatado_vinculo is not null) and
            (length(new.numero_fogo_pneu_formatado_vinculo) <> 0))
        then
            select p.codigo,
                   p.status,
                   p.cod_unidade
            into v_cod_pneu, v_status_pneu, v_cod_unidade_pneu
            from pneu p
            where remove_all_spaces(p.codigo_cliente) ilike
                  new.numero_fogo_pneu_formatado_vinculo
              and p.cod_empresa = new.cod_empresa;
            if (v_cod_pneu is null)
            then
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                      '- O PNEU NÃO FOI ENCONTRADO',
                                      v_quebra_linha);
                new.status_vinculo_realizado = false;
            else
                if (v_cod_unidade_pneu != new.cod_unidade)
                then
                    v_qtd_erros = v_qtd_erros + 1;
                    v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                          '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS O PNEU PERTENCE A OUTRA UNIDADE',
                                          v_quebra_linha);
                    new.status_vinculo_realizado = false;
                else
                    if (v_status_pneu != 'ESTOQUE')
                    then
                        v_qtd_erros = v_qtd_erros + 1;
                        v_msgs_erros =
                                concat(v_msgs_erros, v_qtd_erros,
                                       '- PARA REALIZAR O VÍNCULO O PNEU DEVE ESTAR EM ESTOQUE, O STATUS ATUAL DO PNEU
                                       É: ',
                                       v_status_pneu, v_quebra_linha);
                        new.status_vinculo_realizado = false;
                    end if;
                end if;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- O NÚMERO DE FOGO NÃO PODE SER NULO',
                           v_quebra_linha);
        end if;

        if (v_qtd_erros > 0)
        then
            new.erros_encontrados = v_msgs_erros;
        else
            update pneu_data set status = 'EM_USO' where codigo = v_cod_pneu;
            insert into veiculo_pneu (cod_pneu,
                                      cod_unidade,
                                      posicao,
                                      cod_diagrama,
                                      cod_veiculo)
            values (v_cod_pneu,
                    new.cod_unidade,
                    v_posicao_prolog,
                    v_cod_diagrama_veiculo,
                    v_cod_veiculo);

            new.status_vinculo_realizado = true;
            new.erros_encontrados = '-';
        end if;
    end if;
    return new;
end;
$$;