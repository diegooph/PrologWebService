create or replace function suporte.func_checklist_altera_tipo_checklist_realizado(f_cod_checklist bigint,
                                                                                  f_novo_tipo_checklist char)
    returns text
    language plpgsql
    security definer
as
$$
begin
    if f_cod_checklist is null or f_cod_checklist <= 0
    then
        raise exception 'Forneça um "f_cod_checklist" válido!';
    end if;

    if f_novo_tipo_checklist is null or f_novo_tipo_checklist not in ('S', 'R')
    then
        raise exception E'O parâmetro "f_novo_tipo_checklist" precisa ser \'S\' (para saída) ou \'R\' (para retorno).';
    end if;

    if not exists(select codigo from checklist c where c.codigo = f_cod_checklist)
    then
        raise exception
            'O checklist de código % não foi encontrado. Verifique se ele está deletado.
            Checklists deletados não podem ser alterados.',
            f_cod_checklist;
    end if;

    update checklist
    set tipo = f_novo_tipo_checklist
    where codigo = f_cod_checklist
      and tipo <> f_novo_tipo_checklist;

    return format('Tipo do checklist %s alterado com sucesso.', f_cod_checklist);
end
$$;