package br.com.zalf.prolog.webservice.v3;

import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.LocalDateType;

/**
 * Created on 2021-04-29
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class PrologDialect extends PostgreSQL10Dialect {

    public PrologDialect() {
        registerFunction("tz_date", new StandardSQLFunction("tz_date", LocalDateType.INSTANCE));
    }
}