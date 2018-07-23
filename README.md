# need4speed

This is a simple project showcasing the speed and size benefits of using [GRAAL VM](https://www.graalvm.org) in the containerised world.
The size and speed benefits are also applicable for the serverless application deployed in the cloud.

I have compared the following metrics running different tests
- JVM Startup time with [GRAAL JVM RC 4](https://github.com/oracle/graal/releases), Native compilation via GRAAL and [OpenJDK 10](http://jdk.java.net/10/).
- Bare minimum docker image sizes needed for running the uber jar and the native binary.


## Startup time
For this part of the test, the application have used the environment variable DIE=true. If this variable has some value, this application will terminate as soon as it starts completely.
I have used the linux [time](https://linux.die.net/man/1/time) utility to measure the startup time. 

I have used the native-image utility of GRAAL to create the native image.
```bash
 native-image -jar target/netty-mongo-native-full.jar -H:ReflectionConfigurationResources=netty_reflection_config.json -H:Name=netty-svm-http-server -H:+ReportUnsupportedElementsAtRuntime
```

I have also measured the startup time with the new [JVMCI](http://openjdk.java.net/jeps/317) options available from JDK 9. The measured time report here is recorded with -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler flags.
Note that I have not measured the metrics using the [AOTC](http://openjdk.java.net/jeps/295) and it would be interesting to see what changes the AOTC is bringing to the mix.


####Results
It is very encouraging to see that the native compiled image brings us an average startup time of ~17millisecond as compared to an average of 850millisecond with the jvm. 
This brilliant improvement warrant us to use the GRAAL native image for applications which needs fast startup times e.g. serverless and the containers. However, the native compilation have some specific restrictions, specially around usage of reflection and dynamic class loading, see [here](https://github.com/oracle/graal/blob/master/substratevm/REFLECTION.md). This makes it harder (atleast for now) to move all of the applications to move to native binaries.
The other noteable metrics is that the size of native image is almost double the size of the uber jar. Keep in mind that the uber jar needs the JRE runtime to be operational as you shall see in the Docker container section below, that with native image, the size of docker container is much smaller than that with the uber jar.



| Runtime                       |  JAR file size | Startup time    |
|:------------------------------|:---------------:|:--------------:|
| OpenJDK 10                    |      7.0M       |      940msec   |
| OpenJDK 10 - JVMCI            |      7.0M       |      990msec   |
| JDK 8                         |      7.0M       |      800msec   |
| GRAAL JVM RC 4  - Native      |      14M        |      17msec    |

![alt text](https://raw.githubusercontent.com/username/projectname/branch/path/to/img.png)

If someone is moving Spring related applications to SubstrateVM, have a look at [Spring Functions](https://github.com/spring-projects/spring-fu). This project is being designed for GRAAL native.


####Docker container 
As the name suggests, I have used the fedora-minimal base container to use as runtime for the JVM and native images. The size is the from SIZE column of the docker images command.
The docker image for the natively compiled artifact is less than half the size of the container with the JRE. Native compilation provides great savings in the docker size and this could be a big win for container environments like Kubernetes (none of the stuff matters without kubernetes these days).

####Results 
| Runtime                       |  Size           | 
|:------------------------------|:---------------:|
| Fedora Minimal Base           |      ~90M    |
| Fedora Minimal Base with JVM and User Jar           |      ~250M    |
| Fedora Minimal Base with native binary                         |      ~105M    |


## Application response time
[Vegeta](https://github.com/tsenart/vegeta), an HTTP load testing tool is used to put a load on the application for 60 seconds.
I have used following parameters
```bash
echo "GET http://localhost:8080/" | ./vegeta attack -duration=60s -rate=200 -keepalive=false | tee results.bin | ./vegeta report
```
Everything is running on my local laptop, core i7 and 16GB of RAM. MongoDB is also running locally and I have not tuned any specific parameter.
This exercise is not intended for tuning the application response time, instead, I am interested in verifying that if the native compilation introduces any overhead to the average response times. From the numbers below, it can be seen that native image is at-least as performant as the jar being hosted by full jvm.
Note that as the hotspot compilation comes into play, the JDK response times would starts to match the native one.

####Results
| Runtime                       |  Mean Latency   | 
|:------------------------------|:---------------:|
| OpenJDK 10                    |      ~14msec    |
| OpenJDK 10 - JVMCI            |      ~20msec    |
| JDK 8                         |      ~32msec    |
| GRAAL JVM RC 4  - Native      |      ~5msec     |






##Commands
####Using time utility to measure the startup time
````bash
time java -showversion -jar target/netty-mongo-native-full.jar 
````

##References
#####The netty related part of this repo has made available by Codrut Stancu via his [blog](https://medium.com/graalvm/instant-netty-startup-using-graalvm-native-image-generation-ed6f14ff7692)

This project is using following tools 
-   [Netty](http://netty.io/), 
-   [Mono Reactive Streams](http://mongodb.github.io/mongo-java-driver-reactivestreams/1.9/) and 
-   [Project Reactor](https://projectreactor.io)
-   [Vegeta](https://github.com/tsenart/vegeta)

