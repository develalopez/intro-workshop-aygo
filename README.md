# Workshop

Este proyecto se basa en cuatro servicios diferentes, todos orquestados usando Docker y su feature Compose:

- Un frontend sencillo basado en HTML y JavaScript.
- Un servicio `logservice` que recibe mensajes sencillos en texto y retorna los 10 últimos mensajes recibidos.
- Un load balancer simple llamado `roundrobin` que distribuye las peticiones en tres contenedores de `logservice` apoyándose en la estrategia de balanceo de Docker Compose.
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

CONTAINER ID   IMAGE                       COMMAND                  CREATED          STATUS          PORTS                                           NAMES
407ded5d6851   intro-workshop-logservice   "java -cp ./classes:…"   16 seconds ago   Up 14 seconds   35001/tcp                                       intro-workshop-logservice-1
219dbb9a64ec   intro-workshop-logservice   "java -cp ./classes:…"   16 seconds ago   Up 15 seconds   35001/tcp                                       intro-workshop-logservice-2
a3b0fd828305   mongo:latest                "docker-entrypoint.s…"   16 seconds ago   Up 14 seconds   0.0.0.0:27017->27017/tcp, :::27017->27017/tcp   mongo
0361e55ff077   intro-workshop-front        "httpd-foreground"       16 seconds ago   Up 14 seconds   0.0.0.0:8080->80/tcp, :::8080->80/tcp           front
27cd7e5bb538   intro-workshop-roundrobin   "java -cp ./classes:…"   16 seconds ago   Up 14 seconds   35000/tcp                                       roundrobin
3a7b61e926d8   intro-workshop-logservice   "java -cp ./classes:…"   16 seconds ago   Up 15 seconds   35001/tcp                                       intro-workshop-logservice-3
```

Ahora, puedes visitar http://localhost:8080/index.html para acceder a la lista de los 10 últimos mensajes recibidos.

## Backend

Los servicios de `roundrobin` y `logservice` cuentan con un único path de API: `messages`.

### HTTP GET

Al realizar una petición HTTP GET a http://roundrobin:35000/messages, el servicio realizará la correspondiente petición a uno de los tres contenedores del servicio `logservice` (http://logservice:35001/messages), el cual retornará los últimos 10 mensajes recibidos, para posteriormente ser entregados por `roundrobin` al frontend.

### HTTP POST

Al realizar una petición HTTP POST a http://roundrobin:35000/messages, el servicio realizará la correspondiente petición a uno de los tres contenedores del servicio `logservice` (http://logservice:35001/messages), el cual almacenará el contenido del mensaje recibido junto con un epoch del momento en el que se recibió el mensaje.

## Anotaciones

Desafortunadamente, debido a problemas con la política de seguridad CORS, el frontend de la aplicación no logra comunicarse de manera correcta con el backend (`roundrobin` y `logservice`).