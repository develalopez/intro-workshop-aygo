# Workshop

Este proyecto se basa en tres servicios diferentes, todos orquestados usando Docker y su feature Compose:

- Un servicio `logservice` que recibe mensajes sencillos en texto y retorna los 10 últimos mensajes recibidos.
- Un servicio web simple llamado `roundrobin` que contiene un HTML+JS sencillo y distribuye las peticiones en tres contenedores de `logservice` apoyándose en la estrategia de balanceo de Docker Compose.
- Una base de datos documental `mongodb` que almacena los mensajes.

## Ejecución

Para ejecutar la aplicación y todos sus componentes solo basta con correr el siguiente comando en la consola desde la carpeta raíz:

``` bash
$ docker compose up --scale logservice=3 -d
```
Este comando ejecutará todos y cada uno de los contenedores necesarios para el funcionamiento correcto de la aplicación. Además, el parámetro `--scale` permite que se creen múltiples réplicas del servicio indicado y que el tráfico dirigido a estas réplicas sea balanceado de manera automática usando Round Robin.

Al terminar la ejecución de Docker Compose, los contenedores en ejecución deberían verse así:

``` bash
$ docker ps

CONTAINER ID   IMAGE                       COMMAND                  CREATED          STATUS          PORTS                                   NAMES
CONTAINER ID   IMAGE                       COMMAND                  CREATED         STATUS         PORTS                                   NAMES
62411b1cd970   mongo:latest                "docker-entrypoint.s…"   4 seconds ago   Up 2 seconds   27017/tcp                               mongo
e5786d463e99   intro-workshop-logservice   "java -cp ./classes:…"   4 seconds ago   Up 2 seconds                                           intro-workshop-logservice-2
db02fb3e70c8   intro-workshop-logservice   "java -cp ./classes:…"   4 seconds ago   Up 3 seconds                                           intro-workshop-logservice-1
14feccf0087b   intro-workshop-roundrobin   "java -cp ./classes:…"   4 seconds ago   Up 3 seconds   0.0.0.0:80->6000/tcp, :::80->6000/tcp   intro-workshop-roundrobin-1
4cafeb0b5152   intro-workshop-logservice   "java -cp ./classes:…"   4 seconds ago   Up 2 seconds                                           intro-workshop-logservice-3
```

Ahora, puedes visitar http://localhost/ para acceder a la lista de los 10 últimos mensajes recibidos.

## Backend

Los servicios de `roundrobin` y `logservice` cuentan con un único path de API: `messages`.

### HTTP GET

Al realizar una petición HTTP GET a http://roundrobin/messages, el servicio realizará la correspondiente petición a uno de los tres contenedores del servicio `logservice` (http://logservice:6000/messages), el cual retornará los últimos 10 mensajes recibidos, para posteriormente ser entregados por `roundrobin` al frontend.

### HTTP POST

Al realizar una petición HTTP POST a http://roundrobin/messages, el servicio realizará la correspondiente petición a uno de los tres contenedores del servicio `logservice` (http://logservice:6000/messages), el cual almacenará el contenido del mensaje recibido junto con un epoch del momento en el que se recibió el mensaje.