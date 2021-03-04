-- Sobre:
--
-- Esta function foi criada para a integração de aferições com a nepomuceno, ela não afetará as tabelas de aferição
-- do prolog, apenas salvará os registros de valores de aferição integrados com sucesso para a api do cliente.
--
-- A lógica aplicada nessa function consiste em receber os dados das medições da aferição através do servidor, a
-- function realiza a inserção desses dados.
--
-- Histórico:
-- 2020-03-06 -> Function criada (wvinim - PL-2559).
-- 2020-03-12 -> Adicionado código auxiliar de pneu ao fluxo (wvinim - PL2563)
-- 2020-06-10 -> 'Yes we can!' altera function para smallcase (diogenesvanzella - PLI-164).
create or replace function
    integracao.func_pneu_afericao_insert_afericao_valores_integrada(f_cod_afericao_integrada bigint,
                                                                    f_cod_pneu text,
                                                                    f_cod_pneu_cliente text,
                                                                    f_vida_atual integer,
                                                                    f_psi real,
                                                                    f_altura_sulco_interno real,
                                                                    f_altura_sulco_central_interno real,
                                                                    f_altura_sulco_central_externo real,
                                                                    f_altura_sulco_externo real,
                                                                    f_posicao_prolog integer)
    returns void
    language plpgsql
as
$$
declare
    v_cod_afericao_valores_inserida bigint;
    v_nomenclatura_posicao          text := (select nomenclatura
                                             from pneu_posicao_nomenclatura_empresa ppne
                                             where ppne.cod_diagrama = (select cod_diagrama_prolog
                                                                        from integracao.afericao_integrada
                                                                        where codigo = f_cod_afericao_integrada)
                                               and ppne.cod_empresa = (select cod_empresa_prolog
                                                                       from integracao.afericao_integrada
                                                                       where codigo = f_cod_afericao_integrada)
                                               and ppne.posicao_prolog = f_posicao_prolog);
begin
    -- Realiza a inserção do registro de aferição integrada.
    insert into integracao.afericao_valores_integrada(cod_afericao_integrada,
                                                      cod_pneu,
                                                      cod_pneu_cliente,
                                                      vida_momento_afericao,
                                                      psi,
                                                      altura_sulco_interno,
                                                      altura_sulco_central_interno,
                                                      altura_sulco_central_externo,
                                                      altura_sulco_externo,
                                                      posicao_prolog,
                                                      nomenclatura_posicao)
    values (f_cod_afericao_integrada,
            f_cod_pneu,
            f_cod_pneu_cliente,
            f_vida_atual,
            f_psi,
            f_altura_sulco_interno,
            f_altura_sulco_central_interno,
            f_altura_sulco_central_externo,
            f_altura_sulco_externo,
            f_posicao_prolog,
            v_nomenclatura_posicao)
    returning codigo into v_cod_afericao_valores_inserida;

    if (v_cod_afericao_valores_inserida is null or v_cod_afericao_valores_inserida <= 0)
    then
        raise exception 'Não foi possível inserir os valores da aferição na tabela de integração, tente novamente';
    end if;
end
$$;