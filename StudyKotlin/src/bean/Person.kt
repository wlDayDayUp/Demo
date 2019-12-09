package bean


class Total(var nameList: ArrayList<String>) {

}

class Person(var name: String, val age: Int) {

    var nameList = arrayListOf<String>()

    infix fun add(p: Person): Total {
        nameList.add(this.name)
        nameList.add(p.name)
        return Total(nameList)
    }

    operator fun component1() = name
    operator fun component2() = age

    fun sayMsg() {
        println("Hello world")
    }
}