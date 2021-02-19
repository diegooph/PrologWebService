FROM kartoza/postgis:12.4

RUN apt-get update\
    && apt-get install git -y

COPY ./db.sql /docker-entrypoint-initdb.d

RUN apt-get -y --purge autoremove  \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN git clone https://github.com/eulerto/pg_similarity.git\
    && cd pg_similarity\
    && make\
    && make install\
    && cd /scripts

COPY --from=builder_extensions /var/lib/postgresql/10.16/lib /var/lib/postgresql/12/lib
COPY --from=builder_extensions /var/share/postgresql/10.16/extension /var/share/postgresql/12/extension

COPY ./db2.sql /docker-entrypoint-initdb.d