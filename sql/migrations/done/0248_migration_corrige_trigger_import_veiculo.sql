create or replace function implantacao.tg_func_veiculo_confere_planilha_importa_veiculo()
    returns trigger
    security definer
    language plpgsql
as
$$
declare
    v_valor_similaridade          constant real     := 0.4;
    v_valor_similaridade_diagrama constant real     := 0.5;
    v_sem_similaridade            constant real     := 0.0;
    v_qtd_erros                            smallint := 0;
    v_msgs_erros                           text;
    v_quebra_linha                         text     := CHR(10);
    v_cod_marca_banco                      bigint;
    v_similaridade_marca                   real;
    v_marca_modelo                         text;
    v_cod_modelo_banco                     bigint;
    v_similaridade_modelo                  real;
    v_cod_diagrama_banco                   bigint;
    v_nome_diagrama_banco                  text;
    v_similaridade_diagrama                real;
    v_diagrama_tipo                        text;
    v_eixos_diagrama                       text;
    v_diagrama_motorizado                  boolean;
    v_cod_tipo_banco                       bigint;
    v_similaridade_tipo                    real;
    v_possui_hubodometro_flag              boolean;
    v_similaridades_possui_hubodometro     real;
    v_possui_hubodometro                   text;
    v_possui_hubodometro_possibilidades    text[]   := ('{"SIM", "OK", "TRUE", "TEM"}');
begin
    if (tg_op = 'UPDATE' and old.status_import_realizado is true)
    then
        return old;
    else
        if (tg_op = 'UPDATE')
        then
            new.cod_unidade = old.cod_unidade;
            new.cod_empresa = old.cod_empresa;
        end if;
        new.placa_formatada_import := remove_espacos_e_caracteres_especiais(new.placa_editavel);
        new.marca_formatada_import := remove_espacos_e_caracteres_especiais(new.marca_editavel);
        new.modelo_formatado_import := remove_espacos_e_caracteres_especiais(new.modelo_editavel);
        new.tipo_formatado_import := remove_espacos_e_caracteres_especiais(new.tipo_editavel);
        new.identificador_frota_import := new.identificador_frota_editavel;
        new.usuario_update := SESSION_USER;

        -- VERIFICA SE EMPRESA EXISTE
        if not EXISTS(select e.codigo from empresa e where e.codigo = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- VERIFICA SE UNIDADE EXISTE
        if not EXISTS(select u.codigo from unidade u where u.codigo = new.cod_unidade)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- VERIFICA SE UNIDADE PERTENCE A EMPRESA
        if not EXISTS(
                select u.codigo from unidade u where u.codigo = new.cod_unidade and u.cod_empresa = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- A UNIDADE NÃO PERTENCE A EMPRESA', v_quebra_linha);
        end if;

        -- VERIFICAÇÕES PLACA.
        -- Placa sem 7 dígitos: Erro.
        -- Pĺaca cadastrada em outra empresa: Erro.
        -- Pĺaca cadastrada em outra unidade da mesma empresa: Erro.
        -- Pĺaca cadastrada na mesma unidade: Atualiza informações.
        if (new.placa_formatada_import is not null) then
            if LENGTH(new.placa_formatada_import) <> 7
            then
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros = concat(v_msgs_erros, v_qtd_erros, '- A PLACA NÃO POSSUI 7 CARACTERES', v_quebra_linha);
            else
                if EXISTS(select v.placa
                          from veiculo v
                          where v.placa = new.placa_formatada_import
                            and v.cod_empresa != new.cod_empresa)
                then
                    v_qtd_erros = v_qtd_erros + 1;
                    v_msgs_erros =
                            concat(v_msgs_erros, v_qtd_erros, '- A PLACA JÁ ESTÁ CADASTRADA E PERTENCE A OUTRA EMPRESA',
                                   v_quebra_linha);
                else
                    if EXISTS(select v.placa
                              from veiculo v
                              where v.placa = new.placa_formatada_import
                                and v.cod_empresa = new.cod_empresa
                                and cod_unidade != new.cod_unidade)
                    then
                        v_qtd_erros = v_qtd_erros + 1;
                        v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                              '- A PLACA JÁ ESTÁ CADASTRADA E PERTENCE A OUTRA UNIDADE',
                                              v_quebra_linha);
                    end if;
                end if;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros = concat(v_msgs_erros, v_qtd_erros, '- A PLACA NÃO PODE SER NULA', v_quebra_linha);
        end if;

        -- VERIFICAÇÕES MARCA: Procura marca similar no banco.
        select distinct on (new.marca_formatada_import) mav.codigo                                                        as cod_marca_banco,
                                                        MAX(func_gera_similaridade(new.marca_formatada_import, mav.nome)) as similariedade_marca
        into v_cod_marca_banco, v_similaridade_marca
        from marca_veiculo mav
        group by new.marca_formatada_import, new.marca_editavel, mav.nome, mav.codigo
        order by new.marca_formatada_import, similariedade_marca desc;

        v_marca_modelo := CONCAT(v_cod_marca_banco, new.modelo_formatado_import);
        -- Se a similaridade da marca for maior ou igual ao exigido: procura modelo.
        -- Se não for: Mostra erro de marca não encontrada.
        if (v_similaridade_marca >= v_valor_similaridade)
        then
            -- VERIFICAÇÕES DE MODELO: Procura modelo similar no banco.
            select distinct on (v_marca_modelo) mov.codigo as cod_modelo_veiculo,
                                                case
                                                    when v_cod_marca_banco = mov.cod_marca
                                                        then
                                                        MAX(func_gera_similaridade(v_marca_modelo,
                                                                                   CONCAT(mov.cod_marca, mov.nome)))
                                                    else v_sem_similaridade
                                                    end    as similariedade_modelo
            into v_cod_modelo_banco, v_similaridade_modelo
            from modelo_veiculo mov
            where mov.cod_empresa = new.cod_empresa
            group by v_marca_modelo, mov.nome, mov.codigo
            order by v_marca_modelo, similariedade_modelo desc;
            -- Se a similaridade do modelo for menor do que o exigido: cadastra novo modelo.

            if (v_similaridade_modelo < v_valor_similaridade or v_similaridade_modelo is null)
            then
                insert into modelo_veiculo (nome, cod_marca, cod_empresa)
                values (new.modelo_editavel, v_cod_marca_banco, new.cod_empresa)
                returning codigo into v_cod_modelo_banco;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros = concat(v_msgs_erros, v_qtd_erros, '- A MARCA NÃO FOI ENCONTRADA', v_quebra_linha);
        end if;

        -- VERIFICAÇÕES DE DIAGRAMA.
        -- O diagrama é obtido através do preenchimento do campo "tipo" da planilha de import.
        v_eixos_diagrama := CONCAT(new.qtd_eixos_editavel, new.tipo_formatado_import);
        -- Procura diagrama no banco:
        with info_diagramas as (
            select COUNT(vde.posicao) as qtd_eixos,
                   vde.cod_diagrama   as codigo,
                   vd.nome            as nome,
                   vd.motorizado      as diagrama_motorizado
            from veiculo_diagrama_eixos vde
                     join
                 veiculo_diagrama vd on vde.cod_diagrama = vd.codigo
            group by vde.cod_diagrama, vd.nome, vd.motorizado),

             diagramas as (
                 select vdup.cod_veiculo_diagrama as cod_diagrama,
                        vdup.nome                 as nome_diagrama,
                        vdup.qtd_eixos            as qtd_eixos,
                        vdup.motorizado           as diagrama_motorizado
                 from implantacao.veiculo_diagrama_usuario_prolog vdup
                 union all
                 select id.codigo as cod_diagrama, id.nome as nome_diagrama, id.qtd_eixos, id.diagrama_motorizado
                 from info_diagramas id)

             -- F_EIXOS_DIAGRAMA: Foi necessário concatenar a quantidade de eixos ao nome do diagrama para evitar
             -- similaridades ambiguas.
        select distinct on (v_eixos_diagrama) d.nome_diagrama as nome_diagrama,
                                              d.cod_diagrama  as diagrama_banco,
                                              case
                                                  when d.qtd_eixos ::text = new.qtd_eixos_editavel
                                                      then
                                                      MAX(func_gera_similaridade(v_eixos_diagrama,
                                                                                 CONCAT(d.qtd_eixos, d.nome_diagrama)))
                                                  else v_sem_similaridade
                                                  end         as similariedade_diagrama,
                                              d.diagrama_motorizado
        into v_nome_diagrama_banco, v_cod_diagrama_banco,
            v_similaridade_diagrama, v_diagrama_motorizado
        from diagramas d
        group by v_eixos_diagrama, d.nome_diagrama, d.cod_diagrama, d.qtd_eixos, d.diagrama_motorizado
        order by v_eixos_diagrama, similariedade_diagrama desc;

        -- Se o diagrama não for motorizado, perguntar se possui hubodômetro
        -- Verifica se possui hubodometro;
        case when (v_diagrama_motorizado is false and new.possui_hubodometro_editavel is not null)
            then
                foreach v_possui_hubodometro in array v_possui_hubodometro_possibilidades
                    loop
                        v_similaridades_possui_hubodometro :=
                                MAX(func_gera_similaridade(new.possui_hubodometro_editavel,
                                                           v_possui_hubodometro));
                        if (v_similaridades_possui_hubodometro >
                            v_valor_similaridade)
                        then
                            v_possui_hubodometro_flag = true;
                        else
                            v_possui_hubodometro_flag = false;
                        end if;
                    end loop;
            else v_possui_hubodometro_flag = false;
            end case;

        v_diagrama_tipo := CONCAT(v_nome_diagrama_banco, new.tipo_formatado_import);
        -- Se a similaridade do diagrama for maior ou igual ao exigido: procura tipo.
        -- Se não for: Mostra erro de diagrama não encontrado.
        case when (v_similaridade_diagrama >= v_valor_similaridade_diagrama)
            then
                select distinct on (v_diagrama_tipo) vt.codigo as cod_tipo_veiculo,
                                                     case
                                                         when v_cod_diagrama_banco = vt.cod_diagrama
                                                             then MAX(func_gera_similaridade(new.tipo_formatado_import, vt.nome))
                                                         else v_sem_similaridade
                                                         end   as similariedade_tipo_diagrama
                into v_cod_tipo_banco, v_similaridade_tipo
                from veiculo_tipo vt
                where vt.cod_empresa = new.cod_empresa
                group by v_diagrama_tipo,
                         vt.codigo
                order by v_diagrama_tipo, similariedade_tipo_diagrama desc;
                -- Se a similaridade do tipo for menor do que o exigido: cadastra novo modelo.
                if (v_similaridade_tipo < v_valor_similaridade or v_similaridade_tipo is null)
                then
                    insert into veiculo_tipo (nome, status_ativo, cod_diagrama, cod_empresa)
                    values (new.tipo_editavel, true, v_cod_diagrama_banco, new.cod_empresa)
                    returning codigo into v_cod_tipo_banco;
                end if;
            else
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros =
                        concat(v_msgs_erros, v_qtd_erros, '- O DIAGRAMA (TIPO) NÃO FOI ENCONTRADO', v_quebra_linha);
            end case;
        -- VERIFICA QTD DE ERROS
        if (v_qtd_erros > 0)
        then
            new.status_import_realizado = false;
            new.erros_encontrados = v_msgs_erros;
        else
            if (v_qtd_erros = 0 and EXISTS(select v.placa
                                           from veiculo v
                                           where v.placa = new.placa_formatada_import
                                             and v.cod_empresa = new.cod_empresa
                                             and cod_unidade = new.cod_unidade))
            then
                -- ATUALIZA INFORMAÇÕES DO VEÍCULO.
                update veiculo_data
                set cod_modelo          = v_cod_modelo_banco,
                    cod_tipo            = v_cod_tipo_banco,
                    km                  = new.km_editavel,
                    cod_diagrama        = v_cod_diagrama_banco,
                    identificador_frota = new.identificador_frota_import
                where placa = new.placa_formatada_import
                  and cod_empresa = new.cod_empresa
                  and cod_unidade = new.cod_unidade;
                new.status_import_realizado = null;
                new.erros_encontrados = 'A PLACA JÁ ESTAVA CADASTRADA - INFORMAÇÕES FORAM ATUALIZADAS.';
            else
                if (v_qtd_erros = 0 and not EXISTS(select v.placa
                                                   from veiculo v
                                                   where v.placa = new.placa_formatada_import))
                then
                    -- CADASTRA VEÍCULO.
                    insert into veiculo (placa,
                                         cod_unidade,
                                         km,
                                         status_ativo,
                                         cod_tipo,
                                         cod_modelo,
                                         cod_eixos,
                                         data_hora_cadastro,
                                         cod_unidade_cadastro,
                                         cod_empresa,
                                         cod_diagrama,
                                         identificador_frota,
                                         possui_hubodometro,
                                         motorizado)
                    values (new.placa_formatada_import,
                            new.cod_unidade,
                            new.km_editavel,
                            true,
                            v_cod_tipo_banco,
                            v_cod_modelo_banco,
                            1,
                            NOW(),
                            new.cod_unidade,
                            new.cod_empresa,
                            v_cod_diagrama_banco,
                            new.identificador_frota_import,
                            v_possui_hubodometro_flag,
                            v_diagrama_motorizado);
                    new.status_import_realizado = true;
                    new.erros_encontrados = '-';
                end if;
            end if;
        end if;
    end if;
    return new;
end;
$$;