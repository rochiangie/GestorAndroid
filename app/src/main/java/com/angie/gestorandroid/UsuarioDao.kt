package com.angie.gestorandroid

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {

    // Método para insertar un nuevo usuario
    @Insert
    suspend fun registrarUsuario(usuario: Usuario)

    // Método para obtener un usuario por nombre
    @Query("SELECT * FROM usuario WHERE username = :username LIMIT 1")
    suspend fun obtenerUsuario(username: String): Usuario?
}
