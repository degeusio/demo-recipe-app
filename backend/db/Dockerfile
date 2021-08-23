FROM postgres:11

# WdG 18-sep-20: we need to apply this hack to allow to persist changes across db restarts,
# otherwise, docker commit followed by a restart of that commit starts with a fresh database again
RUN mkdir -p /var/lib/postgresql-static/data
ENV PGDATA /var/lib/postgresql-static/data
