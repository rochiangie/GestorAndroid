package com.angie.gestorandroid

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gasto")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val monto: Double,
    val categoria: String,
    val descripcion: String,
    val fecha: Long  // Asegúrate de que sea Long
)


