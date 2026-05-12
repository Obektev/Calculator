package me.obektev.calc.developers

interface DeveloperInfoProvider {
    fun developers(): List<String>

    fun projectInfo(): String
}
