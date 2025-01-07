# Infrastructure

Run the `./up` script to start the infrastructure layer which comprises of the following services:

- Mongodb
- Postgresql
- Redis

The data is persisted in each directory's data directory.

Use the following commands for more information:

```bash
# This command shows running containers and their mapped ports.
docker ps
```

```bash
# This command shows the logs of a running container. (-f to follow the logs)
docker logs -f <container_name> # eg mongo, postgres or redis
```

```bash
# The lsof command lists open files and their associated processes, including network connections.
sudo lsof -i :<port>
```
