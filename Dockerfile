FROM postgres:10-alpine as builder_extensions
#FROM postgres:10-alpine

RUN apk add --update --no-cache git build-base \
    && git clone https://github.com/eulerto/pg_similarity.git \
    && cd pg_similarity \
    && USE_PGXS=1 make \
    && USE_PGXS=1 make install \
    && apk del git build-base \
    && rm -rf /var/cache/apk/*

COPY ./db.sql /docker-entrypoint-initdb.d

# Main Image
FROM postgres:12.2

RUN apt-get update \
    && apt-get install wget -y \
    && apt-get install postgresql-12-postgis-3 -y \
    && apt-get install postgis -y

COPY --from=builder_extensions /var/lib/postgresql/10.16/lib /var/lib/postgresql/12/lib
COPY --from=builder_extensions /var/share/postgresql/10.16/extension /var/share/postgresql/12/extension

COPY ./db2.sql /docker-entrypoint-initdb.d