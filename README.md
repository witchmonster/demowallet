###Installation

1. Clone this repository
2. Install docker and docker-compose
3. In project root: 

- Start the MySQL: `$ docker-compose up -d`, it will also initialize the schema
- Build the project: `$ ./gradlew build`

- In separate terminals start server and client: 

 `$ ./gradlew :demowallet-server:bootRun`

 `$ ./gradlew :demowallet-client:bootRun`

####Client run with custom number of users and threads 
 `$ ./gradlew :demowallet-client:bootRun -Pargs=--demowallet.client.users=100,--demowallet.client.concurrent_threads_per_user=2,--demowallet.client.rounds_per_thread=3`
 
###Performance testing

On my local machine, service is able to support ~5000 concurrent api calls per second.
Launch client with --demowallet.client.perfinfo=true to see performance info and score (api calls per second).

####How do I measure performance
I'm monitoring every call with AtomicLong using a wrapper for GrpcChannel.
Also, I'm measuring duration of task execution. Then I calculate qps by dividing number of calls executed by duration in milliseconds * 1000;