package navigation

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Класс для работы с базой данных приложения
 * @author Сергей Рейнн (bulkabuka)
 */
class Db {
    object Pinned : Table() {
        val id = integer("id").autoIncrement()
        val filePath = varchar("file_path", 255)
        val fileName = varchar("file_name", 255)
    }

    init {
        val conn = Database.connect("jdbc:sqlite:main.db", "org.sqlite.JDBC")

        transaction {
            SchemaUtils.create(Pinned)
        }
    }

    /**
     * Класс для работы с закрепленными файлами в базе данных
     * @author Сергей Рейнн (bulkabuka)
     */
    class PinnedFileDao {
        /**
         * Добавление нового закрепленного файла
         * @param fileName Имя файла
         * @param filePath Путь к файлу
         * @author Сергей Рейнн (bulkabuka)
         */
        fun insert(fileName: String, filePath: String) {
            transaction {
                Pinned.insert {
                    it[Pinned.fileName] = fileName
                    it[Pinned.filePath] = filePath
                }
            }
        }

        /** Обновление закрепленного файла по его id
         * @param id Идентификатор закрепленного файла
         * @param fileName Новое имя файла
         * @param filePath Новый путь к файлу
         * @author Сергей Рейнн (bulkabuka)
         */
        fun update(id: Int, fileName: String, filePath: String) {
            transaction {
                Pinned.update({ Pinned.id eq id }) {
                    it[Pinned.fileName] = fileName
                    it[Pinned.filePath] = filePath
                }
            }
        }

        /** Удаление информации о закрепленном файле по id
         * @param id Идентификатор закрепленного файла, который нужно удалить из базы данных
         * @author Сергей Рейнн (bulkabuka)
         */
        fun delete(id: Int) {
            transaction {
                Pinned.deleteWhere { Pinned.id eq id }
            }
        }
    }
}