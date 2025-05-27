package TASKLIST

import desafio3.isNullOrNegative
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

data class Task(
    val id: Int,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        private val idCounter = AtomicInteger(0)
        private val availableIds = mutableSetOf<Int>()

        fun create(title: String, description: String?, isCompleted: Boolean): Task {
            val id = availableIds.minOrNull() ?: idCounter.incrementAndGet()
            availableIds.remove(id)
            return Task(id, title, description, isCompleted)

        }

        fun recycleId(id: Int) {
            availableIds.add(id)
        }
    }
}

sealed class TaskResult {
    abstract fun handle()

    data class Success(val msg: String) : TaskResult() {
        override fun handle() = println("💕 - SUCESSO: $msg")
    }

    data class Error(val msg: String) : TaskResult() {
        override fun handle() = println("🗣️ - ERROR: $msg")
    }
}

interface Tasks<T> {
    fun addTask(item: T): TaskResult
    fun listAll(): String
    fun listTask(id: Int): T?
    fun update(item: T): TaskResult
    fun filterTask(isCompleted: Boolean): List<T>
    fun delete(id: Int): TaskResult
}

class TaskManager : Tasks<Task> {
    private val ListTask = mutableListOf<Task>()
    override fun addTask(item: Task):TaskResult {
        require(item.title.isNotEmpty()) {"Titulo não pode ser vazio"}
        ListTask.add(item)
        return TaskResult.Success("Tarefa '${item.title}' adicionada!")
    }

    override fun listAll(): String {
        return if (ListTask.isEmpty()) {
            "Não tem nenhuma tarefa."
        } else {
            ListTask.joinToString("\n") {it.toFormattedString()}
        }
    }

    override fun listTask(id: Int): Task? {
        return ListTask.find { it.id == id }
    }


    override fun filterTask(isCompleted: Boolean): List<Task> {
        return ListTask.filter { it.isCompleted == isCompleted }
    }

    override fun delete(id: Int): TaskResult {
        return if (ListTask.removeIf { it.id == id }) {
            Task.recycleId(id)
            TaskResult.Success("Tarefa $id removida!")
        } else {
            TaskResult.Error("ID: $id não encontrado")
        }
    }

    override fun update(item: Task): TaskResult {
        return if (ListTask.removeIf { it.id == item.id }) {
            ListTask.add(item)
            TaskResult.Success("Tarefa ${item.id} atualizada!")

        } else {
            TaskResult.Error("Tarefa não encontrada!")

        }
    }
}
    fun requireTask() : Task {
        var title: String? = null
        println("Insira o nome da tarefa: ")
        while (title.isNullOrEmpty()) {
            println("-> ")
            title = readlnOrNull()
            if(title.isNullOrEmpty()){
                println("O Nome inserido está invalido! Tente novamente.")
            }
        }
        var description: String? = null
        println("Insira a descrição da tarefa: ")
        while (description.isNullOrEmpty()) {
            println("-> ")
            description = readlnOrNull()?.ifEmpty { "" }
        }

        return Task.create(title = title, description = description, isCompleted = false)
    }

    fun isCompletedTask(task: Task): Task {
        println("Você gostaria de finalizar a tarefa? True/False")
        val isCompleted = readlnOrNull()?.toBoolean()?: false

        return task.copy(isCompleted = isCompleted)
    }

fun Task.toFormattedString(): String {
    return """
        ID: ${this.id}
        Titulo: ${this.title}
        Descrição: ${this.description ?: "N/A"}
        Status: ${if (this.isCompleted) "✅" else "❌"}
        Criada em: ${this.createdAt}
        -           x             -
    """.trimIndent()
}

fun main() {
    val task = TaskManager()

    var acao: Int? = null
    while (acao != 0) {
        println(
            """
        +---------------------------------+
        |  Criador de tarefas  - TaskList |
        +---------------------------------+
        |  1 - Adicionar                  |
        |  2 - Atualizar tarefa           |
        |  3 - Deletar                    |
        |  4 - Buscar                     |
        |  5 - Sair                       |
        +---------------------------------+
        """
        )
        println("Lista de tarefas:")
        println(task.listAll())

        println("\n Insira a açao desejada: ")
        acao = readlnOrNull()?.toIntOrNull()

        when(acao) {
            1 -> {
                val tasks = requireTask()
                task.addTask(tasks)
            }
            2 -> {
                var id: Int? = null
                println("Insira o ID da tarefa: ")
                while (id == null || id < 0) {
                    id = readlnOrNull()?.toIntOrNull()
                    if (id.isNullOrNegative() || task.listTask(id = id!!) == null) {
                        TaskResult.Error("O ID inserido não existe na lista de tarefas!")
                    }
                }

                val taskUpdate = task.listTask(id = id!!)
                taskUpdate?.let {
                    val taskup = isCompletedTask(task = it)
                    task.update(taskup)
                }
            }
            3 -> {
                println("Insira o ID da tarefa: ")
                val id = readlnOrNull()?.toIntOrNull()

                if (id != null) {
                    task.delete(id).handle()
                } else {
                    TaskResult.Error("ID Invalido!").handle()
                }


            }
            4 -> {
                println("Qual ID da tarefa? ")
                val id = readlnOrNull()?.toIntOrNull()

                if (id != null) {
                    val taskBusca = task.listTask(id)
                    if (taskBusca != null) {
                        println(taskBusca.toFormattedString())
                        TaskResult.Success("Tarefa encontrada!").handle()
                    } else {
                        TaskResult.Error("ID $id não encontrado!").handle()
                    }
                } else {
                    TaskResult.Error("ID INVALIDO!")
                }
            }

            5 -> {
                println("Obrigado. Volte sempre!")
            }
            else -> {
                TaskResult.Error("Opção invalida! Tente novamente.")
            }
        }

    }
}