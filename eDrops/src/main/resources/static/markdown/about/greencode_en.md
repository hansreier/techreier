## Energy efficient code

### General advice for energy efficient coding

- Evaluate the entire ICT architecture, including systems and interfaces in the value chain.
- Identify energy critical parts of the code (with bad performance) and optimize or rewrite.
- Optimize usage of containers (max memory, max CPU, scaling and down)
- Evaluate energy usage of AI. training and prompt engineering.
- Effective algorithms have lower energy usage.
- Code that is run quickly without waiting time, have lower energy usage.
- Simple code can have (but not always) lower energy usage. At least verify.
- Remove unused code and data
- Avoid calculations that is not required, conditional logic can be used.
- Spring Boot Virtual Threads shoul be used. Be aware of thread handing in the code.
- Reactive (asynchronous) object handling implies lower energy usage.
- Effective data packaging 
  - common techniques for initial load of web front-end.
  - Large uncompressed pictures? Consider usage of animations and video. 
- Effective packaging of data (e.g. common web-frontend techniques)
- Optimize data storage (RAM, cache, disk, database, noSQL, temporary, semi-permanent, permanent, backup)
- Minimize I/O operations.

The great thing about this is that it usually aligns well with common principles of good and clean code. 
So it’s a double win: Easier maintenance and lower energy consumption. 
The choice of hosting platform or cloud services affects this, but it is typically decided in advance.

### An example of optimizing large chunks of data

An old large Java monolith was to be migrated to the cloud.
We optimized the allocated memory and CPU in the deployment configuration of the pods.
There was old code for file transfer that was extremely inefficient (30MB maximum)
We discovered the problems after the entire system had been moved to the cloud.
Unfortunately, this meant that memory had to be increased significantly. 
Without this, users in areas with poor network connectivity experienced issues.
The solution was to rewrite critical parts of the code based on new file transfer APIs adapted to the new version of Spring.

How to identify critical parts of the code? 
The simplest is regular logging with, for example, Logback. See the example of using MXBean further down the page.
The logging level can be set to debug or trace later when it is no longer relevant.
A regular stopwatch can also be used for a GUI operation (or use a GUI testing tool).
If the response takes more than 2-3 seconds from the GUI, it’s not good.
Profiling tools for Java / JVM can also be used for this, but they are more expensive.

In a later project, I tested a new, better code for transferring large amounts of data.
Reactive code in Java was tested, which handled file sizes up to 2GB.
2GB is approximately the limit for what ordinary string handling can manage (Char Array). 
If it’s larger than that, it requires special handling. It’s also important to consider that the libraries we use must support this.
New versions of Spring Boot and REST interfaces can handle quite large amounts of data, up to that limit, 
if configured correctly, as it turned out.
Parameters such as how large chunks of the byte stream are read simultaneously are set for optimization.

Another requirement was intermediate storage in a database. 
After examining both a standard database API and a reactive API, as well as storage in BLOB-type objects; 
we concluded that a standard relational database was not suitable.
Instead, it was stored in a key/value store in the cloud. It turned out to be much more performant.

But was this 2GB requirement actually a specification error? Why should such large amounts of data be transferred in one chunk?
We can question the entire architecture, which included several systems. 
In practice, it was no more than 200-300MB. This solved much of the problem.

### Energieffektive miljøer med kontainer teknologi

Å bruke Kubernetes basert teknologi kan anbefales (men fikk i praksis mye hjelp av plattform team og overbygg).
Jeg jobbet også med en mer nettverksnær tjeneste i Azure (Azure App services), 
men denne var ikke nødvendigvis enklere  å sette opp med Ci/Cd og virtuelle nett (VNet).  

Optimaliser kontainere ved å minimere max minne, max CPU og parametre for opp og nedskalering av antall kontainere.  

Jeg prøvde i Openshift (Kubernetes) rett og slett å avbryte REST kallet før minnet gikk i taket for en pod,
og returnere en feilmelding til klienten om at lasten var for stor. I dette tilfellet var det snakk om en stor
engangs-last. Erfaringen derfra var at dette heller ikke var noen vits i. Det var bedre å la Kubernetes vanlig
kontainerhåndtering fikse det, dvs. drepe containeren automatisk og ta opp en ny.  

Ofte kan visuelle verktøy som Grafana være en stor hjelp her.  

### AI or no AI

For en del typer anvendelser så kan fuzzy tekstsøk være vel så bra som et søk i en AI modell med "Prompt engineering".
Da brukes f.eks. "Elastic Search" eller fuzzy søk i objekter i databaser beregnet for større tekstmengder
(Både Oracle og PostgreSQL har dette).
Det er ikke alltid like gunstig heller med en naturlig språk modell for presentasjon av et resultat.
Det passer best for mennesker og ikke for videre maskinell behandling.
Det må vurderes i hvert enkelt tilfelle. AI trening og bruk regnes generelt som det mest energikrevende.  

Men det er viktig å se på hele verdikjeden når AI vurderes.  


### To select interface type for efficient processing

There is no definitive answer. It depends on the use case.

| Method            | Energy efficiency        | Capacity | Pattern                       | Model   |
|-------------------|--------------------------|----------|-------------------------------|---------|
| Synchronous REST  | Low to average           | Low      | Request-response blocking     | Simple  |
| Asynchronous REST | Average to high          | Average  | Request-response non blocking | Average |
| Kafka             | High (with reservations) | High     | Publish-subscribe scalable    | Complex |

Virtual threads increases the performance of synchronous REST, most valid for large throughput.
Kafka can be beneficial for message intensive systems with strict requirements for performance and reliability.
I have to admit that I have not tried Kafka in practical programming yet. The other alternatives I have tested.


### Good coding practices

- Reuse objects, do not recreate.
- Use inline for tiny Kotlin functions.
- Velg datastruktur med omhu etter bruksområde. F.eks: Bruk Hashmap for raske oppslag, hvis rekkefølge er viktig LinkedHashMap
- Mye å spare på riktig SQL, konsistent datamodell og indeksering av tabeller
- Evaluate usage of relational databases relasjonsdatabase opp mot NO-SQL alternativer.
- Ordinær fillagring og filoverføring er tilsynelatende enkelt, men bør unngås, i hvertfall i skya.

- The choice of any ORM technology, such as Spring Data JPA, should be carefully evaluated.
  - Many alternatives offer simple APIs for CRUD (Create, Read, Update, Delete) and is still type safe.
  - JPA is excellent used for storage of complex structures.
  - JPA is not that well suited for large queries and reports.
  - Performance optimization of and ORM can be time-consuming, if you lack experience.
- Efficient free text search requires separate methods and data types in databases.


#### Virtual threads in JVM and Spring

Configure virtual threads in Spring Boot in the yaml file:
```
spring.threads.virtual.enabled: true
```
#### I/O with a lot of throughput

Use ByteArray and not text strings for I/O operations.
```
fun readFile(file: File): ByteArray {
    return file.inputStream().use { it.readBytes() }
}
```

#### Effective Collection operations and lambda expressions

The least efficient alternative:
```
val selectedCertifications = certifications.filter { s -> analyzeInactive || !s.disabled }.toMutableSet()

```
Do not filter if not required. Example:
```
  val selectedCertifications = if (analyzeInactive) 
    certifications 
else 
    certifications.filterNot { s -> s.disabled }.toMutableSet()
```

Even more efficient with Sequences:
```
val selectedCertifications = if (analyzeInactive) {
    certifications
} else {
    certifications.asSequence()
        .filterNot { it.disabled }
        .toMutableSet() // Evaluates lazily
}
```

Every step involving a Collection type creates a new temporary Collection.
The more chained collection operation, the less efficient.
Each element in a Sequence is executed seperately through the entire chain,
the calculation happens in the last step if possible.
This is an advantage for large data set, not quite like that for small ones
But do we really need a Mutable Collection here. It is another question.

#### Efficient loops in Kotlin

The most efficient variant with forEach:
```
fun processList(items: List<Int>) {
    items.forEach {
        if (it == 5) return@forEach // Skips to the next iteration
        println(it)
    }
}
```

The most efficient code is a traditional for loop:
```
fun processList(items: List<Int>) {
    for (i in items) {
         if (i == 3) break // Skips right out of the loop
            println(i)
    }
}
```

The extra lambda abstraction layer and functional code is a bit less efficient. 
If that means anything in practice. I would like to remind you that lambdas are translated into such structures regardless. 
There is a trend towards functional programming, which often results in more concise code. 
However, in this case, I don't think that’s true. 
But this code improvement is probably not what I would focus on for energy optimization.

#### Usage of MXBean in Kotlin (Java)

The purpose is to be able to log memory usage, the number of threads and possibly CPU.

```
fun mem(): MB {
    val memory: MemoryMXBean = ManagementFactory.getMemoryMXBean()
    val threads: ThreadMXBean = ManagementFactory.getThreadMXBean()
    // val cpu: OperatingSystemMXBean= ManagementFactory.getOperatingSystemMXBean()
    val mem = MB(
        memory.heapMemoryUsage.init,
        memory.heapMemoryUsage.used,
        memory.heapMemoryUsage.committed,
        memory.heapMemoryUsage.max,
        // cpu.systemCpuLoad * 100 //Percentage
        threads.threadCount,
        Thread.currentThread().isVirtual
    )
    return mem
}

data class MB(
    val init: Long, val used: Long, val committed: Long, val max: Long, val threads: Int,
    val virtual: Boolean
) {
    override fun toString(): String {
        return "init=${init / MBYTE}MB, used=${used / MBYTE}MB, committed=${committed / MBYTE}MB" +
                ", max=${max / MBYTE}MB, ${if (virtual) "vthreads=" else "threads="}$threads"
    }
}
```  

The parameters can be used, e.g. by logging a REST call in a filter.
``` 
logger.info("${req.method} ${req.servletPath} ${mem()}")
```
Result:
``` 
16:25:36.723 [tomcat-handler-0] INFO GET / init=254MB, used=72MB, committed=88MB, max=4040MB, vthreads=26 
```  
Observe in this way the change in memory usage and number of threads over time.
The same can be done for critical parts of the code in general.  

I recommend skipping CPU measurements in the above code, because the results were unstable. You can of cause try to calculate
an average over a period of time. The most critical checkpoint is to ensure that the memory usage remains within acceptable
limits and gradually decreases after high throughput. The same applies to the number of threads. 
If virtual threads are used, is detected in the code.
If the number of threads is growing steadily upwards, it is a bug in the code. I experienced that one occasion.  

### Usage of the Docker Stats command and a separate container with a service to measure energy usage

Instead of the JVM based MXBean method described below, the Dockes Stats command should be used for containers.
This provides more stable measurements.  

```
docker stats
```
Result:
```
CONTAINER ID   NAME               CPU %     MEM USAGE / LIMIT     MEM %     NET I/O       BLOCK I/O   PIDS
5b0957e198f2   awesome_mahavira   1.24%     332.6MiB / 7.648GiB   4.25%     1.39kB / 0B   0B / 0B     34
```
I am considering a setup in a cloud service with a container that runs an API calling this command.
The energy usage of the other containers in the system whill  then be calculated.
It should be a lot of fun to do this, but I have not had time or opportunity.
.  




