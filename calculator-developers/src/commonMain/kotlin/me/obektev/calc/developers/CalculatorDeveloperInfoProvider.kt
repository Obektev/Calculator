package me.obektev.calc.developers

class CalculatorDeveloperInfoProvider : DeveloperInfoProvider {
    override fun developers(): List<String> {
        return listOf(
            "Команда Obektev/Calculator",
            "Лабораторные работы 1-8: Kotlin + Compose Multiplatform",
        )
    }

    override fun projectInfo(): String {
        return "Simple Calculator: приложение с пользовательскими библиотеками парсинга, вычисления, памяти и информации о разработчиках."
    }
}
