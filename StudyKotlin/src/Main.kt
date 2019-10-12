inline fun f1(i: Int, fn: (Int) -> Int) = fn(i)


fun main() {
    println(f1(1000){
        it * 2
    })
}