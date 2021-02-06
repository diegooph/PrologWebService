-- Sobre:
--
-- Function utilizada para buscar a configuração de abertura de serviços para uma unidade específica. A configuração é
-- realizada por empresa, assim, validamos de qual empresa é a unidade e retornamos a configuração para ela.
-- Assumimos que se a empresa não possui entrada nessa tabela, por default, ela não abre serviços, pois esse é o
-- comportamento comum no cenário integrado.
--
-- Histórico:
-- 2020-02-27 -> Function criada (diogenesvanzella - PLI-78).
create or replace function integracao.func_geral_busca_config_abertura_servico_pneu(f_cod_unidade bigint)
    returns boolean
    language plpgsql
as
$$
begin
    if (select exists(select ecasp.cod_empresa
                      from integracao.empresa_config_abertura_servico_pneu ecasp
                      where ecasp.cod_empresa =
                            (select u.cod_empresa from public.unidade u where u.codigo = f_cod_unidade)))
    then
        return (select ecasp.deve_abrir_servico_pneu
                from integracao.empresa_config_abertura_servico_pneu ecasp
                where ecasp.cod_empresa = (select u.cod_empresa from public.unidade u where u.codigo = f_cod_unidade));
    else
        return false;
    end if;
end;
$$;