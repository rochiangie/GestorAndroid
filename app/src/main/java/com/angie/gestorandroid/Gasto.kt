package com.angie.gestorandroid

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val monto: Double,
    val categoria: String,
    val fecha: Long,
    val descripcion : String,
)
