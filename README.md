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