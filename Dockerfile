FROM kartoza/postgis:12.4

RUN apt-get update\
    && apt-get install git -y


RUN apt-get -y --purge autoremove  \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN git clone https://github.com/eulerto/pg_similarity.git\
    && cd pg_similarity\
    && make\
    && make install\
    && cd /scripts
EXPOSE 5432