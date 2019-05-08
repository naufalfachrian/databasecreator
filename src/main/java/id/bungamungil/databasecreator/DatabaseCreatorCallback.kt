package id.bungamungil.databasecreator

interface DatabaseCreatorCallback {

    fun databaseHasBeenExisted()

    fun databaseHasBeenCreated()

    fun failedToCreateDatabase(reason: Throwable)

}