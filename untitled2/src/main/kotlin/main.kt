import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.count

// Using coroutine
suspend fun method1() = coroutineScope{
    launch{
        for(i in 0..5){
            delay(400L)
            println(i)
        }
    }

    println("Calling print function after the launch")
}

// Launch 2 coroutines
suspend fun method2() = coroutineScope{

    launch{
        for(i in 0..5){
            delay(400L)
            println(i)
        }
    }
    launch{
        for(i in 6..10){
            delay(400L)
            println(i)
        }
    }

    println("Doing 2 coroutines on parallel")
}

// One coroutine in the other
suspend fun method3() = coroutineScope{

    launch{
        println("Outer coroutine")
        launch{
            println("Inner coroutine")
            delay(400L)
        }
    }

    println("End of Main")
}

// Waiting until the process ends in the coroutine
suspend fun method4() = coroutineScope{

    val job = launch{
        for(i in 1..5){
            println(i)
            delay(400L)
        }
    }

    println("Start")
    job.join() // ожидаем завершения корутины
    println("End")
}

// Helpful function
suspend fun getMessage() : String{
    delay(500L)  // имитация продолжительной работы
    return "Hello"
}

// Getting the result from coroutine
suspend fun method5() = coroutineScope{

    val message: Deferred<String> = async{ getMessage()}
    println("message: ${message.await()}")
    println("Program has finished")
}

// Getting data about threads
suspend fun method6() = coroutineScope{

    launch {
        println("Current thread (coroutine): ${Thread.currentThread().name}")
    }
    println("Current thread (main function): ${Thread.currentThread().name}")
}

// Launch given thread
suspend fun method7() = coroutineScope{

    launch(newSingleThreadContext("Custom Thread")) {
        println("Current thread (coroutine): ${Thread.currentThread().name}")
    }

    println("Current thread (main function): ${Thread.currentThread().name}")
}

// Canceling routines
suspend fun method8() = coroutineScope{

    val downloader: Job = launch{
        println("Starting loading files")
        for(i in 1..5){
            println("Downloaded file $i")
            delay(500L)
        }
    }
    delay(800L)
    println("Cancelling...")
    downloader.cancel()
    downloader.join()
    println("The process is canceled")
}

// CancellationExeption
suspend fun method9() = coroutineScope{

    val downloader: Job = launch{
        try {
            println("Starting loading files")
            for(i in 1..5){
                println("Downloaded file $i")
                delay(500L)
            }
        }
        catch (e: CancellationException ){
            println("Canceled")
        }
        finally{
            println("The process is canceled")
        }
    }
    delay(800L)
    println("Cancelling...")
    downloader.cancelAndJoin()    // отменяем корутину и ожидаем ее завершения
    println("The process is canceled")
}

// Using Channels
suspend fun method10() = coroutineScope{

    val channel = Channel<String>()
    launch {
        val users = listOf("Bob", "Alex", "Anna")
        for (user in users) {
            channel.send(user)  // Отправляем данные в канал
        }
        channel.close()  // Закрытие канала
    }

    for(user in channel) {  // Получаем данные из канала
        println(user)
    }
    println("End")
}

data class Person(val name: String, val age: Int)

suspend fun main(){
    println("1---------")
    method1()
    println("2---------")
    method2()
    println("3---------")
    method3()
    println("4---------")
    method4()
    println("5---------")
    method5()
    println("6---------")
    method6()
    println("7---------")
    method7()
    println("8---------")
    method8()
    println("9---------")
    method9()
    println("10---------")
    method10()
    println("THREADS---------")
    // Threads
    val userFlow = listOf("Tom", "Bob", "Kate", "Sam", "Alice").asFlow()
    val count = userFlow.count{ username -> username.length > 3 }
    println("Count: $count")       // Count: 2
    // Map
    print("MAP------")
    val peopleFlow = listOf(
        Person("Tom", 37),
        Person("Bill", 5),
        Person("Sam", 14),
        Person("Bob", 21),
    ).asFlow()

    peopleFlow.map{ person -> object{
        val name = person.name
        val isAdult = person.age > 17
    }}.collect { user -> println("name: ${user.name}   adult:  ${user.isAdult} ")}
    // Filter
    print("FILTER------")
    val peopleFlow1 = listOf(
        Person("Tom", 37),
        Person("Bill", 5),
        Person("Sam", 14),
        Person("Bob", 21),
    ).asFlow()

    peopleFlow1.filter{ person -> person.age > 17}
        .collect { person -> println("name: ${person.name}   age:  ${person.age} ")}
    // Joining 2 threads
    print("ZIP------")
    val names = listOf("Tom", "Bob", "Sam").asFlow()
    val ages = listOf(37, 41, 25).asFlow()
    names.zip(ages) { name, age -> Person(name, age) }
        .collect { person -> println("Name: ${person.name}   Age: ${person.age}") }
}