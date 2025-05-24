import kotlin.math.max
import kotlin.math.sqrt

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val a = 10
    val b = 5
    println(sqrt(16.0))

    println("Entre com seu nome: ")
    val name = readln()
    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
    // to see how IntelliJ IDEA suggests fixing it.
    println("Hello, $name!")

    println("Qual sua idade?")
    val age = readln().toInt()

    for (i in 1..age) {
        val ageText = if (i == age) " A sua idade é $i!" else "$i ..."
        println(ageText)
    }
}