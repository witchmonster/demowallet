# Installation

You have 2 options for bootstrapping: 

a) all services in Docker with 1-button Docker Compose start

b) manual start of server and client using gradle, with only MySQL container running in Docker 

## a) Docker Compose

Fast bootstrap (but less control).

1. Clone this repository
2. `$ ./gradlew build`
3. `$ docker-compose up -d`

Done. 

Now you can connect to both containers and monitor logs.
- `$ docker ps | grep demowallet-server-app` to get server container name
- `$ docker ps | grep demowallet-client-app` to get client container name
- `$ docker logs -f <container_name>` to view logs 

#### Client run with custom number of users and threads 
You can use `$ WALLET_USERS=500 CONCURRENT_THREADS_PER_USER=2 ROUNDS_PER_THREAD=3 SHOW_PERF_INFO=1 docker-compose up -d` for configuring users, rounds and threads.

Or you can modify .env file in the root of the project.

## b) Gradle :bootRun

This option provides more control, but needs a few commands more.

You will still need docker for this one to start MySQL database.

1. Clone this repository
2. Install docker and docker-compose
3. In project root: 

- Start the MySQL: `$ docker-compose run -d walletdb`
- Build the project: `$ ./gradlew build`
- In separate terminals start server and client: 

 `$ ./gradlew :demowallet-server:bootRun`

 `$ ./gradlew :demowallet-client:bootRun`

#### Client run with custom number of users and threads 
You can use `$ ./gradlew :demowallet-client:bootRun -Pargs=--demowallet.client.users=100,--demowallet.client.concurrent_threads_per_user=2,--demowallet.client.rounds_per_thread=3`
 
# Performance testing

On my local machine, service is able to support ~5000 concurrent api calls per second.
Launch client with 

a) for Docker Compose setup run `$ SHOW_PERF_INFO=1 docker-compose up -d`

b) for Gradle :bootRun setup run with `--demowallet.client.perfinfo=true` parameter 

to see performance info and score (api calls per second).

#### How do I measure performance
I'm monitoring every call with AtomicLong using a wrapper for GrpcChannel.
Also, I'm measuring duration of task execution. Then I calculate qps by dividing number of calls executed by duration in milliseconds * 1000;