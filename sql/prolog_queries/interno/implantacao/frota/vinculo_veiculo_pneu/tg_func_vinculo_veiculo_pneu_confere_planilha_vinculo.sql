-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica os dados que são inseridos na tabela de 'pré-vínculo', procurando os códigos correspondentes para efetuar
-- os vinculos entre pneus e veículos.
--
-- Pré-requisitos:
-- functions criadas:
-- REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS.
-- REMOVE_ALL_SPACES.
--
-- Histórico:
-- 2020-08-28 -> Function criada (thaisksf - PL-2771).
-- 2020-09-24 -> Adiciona cod_veiculo (thaisksf - PL-3170).
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
                                if exists(select vp.placa
                                          from veiculo_pneu vp
                                          where remove_all_spaces(vp.placa) = new.placa_formatada_vinculo
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
            insert into veiculo_pneu (placa,
                                      cod_pneu,
                                      cod_unidade,
                                      posicao,
                                      cod_diagrama,
                                      cod_veiculo)
            values (v_placa,
                    v_cod_pneu,
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