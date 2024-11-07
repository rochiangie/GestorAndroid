package com.angie.gestorandroid

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface GastoDao {

    @Insert
    suspend fun agregarGasto(gasto: Gasto)

    @Query("SELECT * FROM gasto")
    suspend fun obtenerTodosLosGastos(): List<Gasto>

    @Query("SELECT * FROM gasto WHERE id = :id")
    suspend fun obtenerGastoPorId(id: Int): Gasto?
}
