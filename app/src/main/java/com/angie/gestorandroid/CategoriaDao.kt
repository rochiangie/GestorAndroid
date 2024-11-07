package com.angie.gestorandroid

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoriaDao {
    @Insert
    suspend fun registrarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuario WHERE username = :username LIMIT 1")
    suspend fun obtenerUsuario(username: String): Usuario?
}
