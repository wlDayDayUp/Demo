import bean.Person

inline fun f1(i: Int, fn: (Int) -> Int) = fn(i)

class User(firstName: String, age: Int) {
    var firstName = firstName
    var age = age

    var userInfo
        get() = "姓名：$firstName, 年龄：$age"
        set(value) {
            if ("-" !in value) {
                "输入格式不正确"
            } else {
                val split = value.split("-")
                firstName = split[0]
                age = Integer.parseInt(split[1])
            }
        }
}

fun main() {
    val user = User("www", 29)
    println(user.userInfo)
}