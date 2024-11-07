package com.angie.gestorandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.angie.gestorandroid.ui.theme.GestorAndroidTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.room.*

@Entity
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val monto: Double,
    val categoria: String,
    val fecha: Long
)

@Dao
interface GastoDao {
    @Insert
    suspend fun agregarGasto(gasto: Gasto)

    @Query("SELECT * FROM Gasto")
    suspend fun obtenerTodosLosGastos(): List<Gasto>
}

@Database(entities = [Gasto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gastoDao(): GastoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_diaria.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Composable
fun GestorDeGastosScreen(db: AppDatabase) {
    var nombre by remember { mutableStateOf(TextFieldValue()) }
    var monto by remember { mutableStateOf(TextFieldValue()) }
    var categoria by remember { mutableStateOf(TextFieldValue()) }
    var mensaje by remember { mutableStateOf("") }

    // Definir explícitamente el tipo de listaGastos
    var listaGastos: List<Gasto> by remember { mutableStateOf(emptyList()) }

    // Obtener todos los gastos para mostrarlos
    CoroutineScope(Dispatchers.IO).launch {
        listaGastos = db.gastoDao().obtenerTodosLosGastos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Gestor de Gastos", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        // Nombre del gasto
        BasicTextField(
            value = nombre,
            onValueChange = { nombre = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            decorationBox = { innerTextField ->
                Row(modifier = Modifier.padding(8.dp)) {
                    Text("Nombre del gasto:")
                    innerTextField()
                }
            }
        )

        // Monto del gasto
        BasicTextField(
            value = monto,
            onValueChange = { monto = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            decorationBox = { innerTextField ->
                Row(modifier = Modifier.padding(8.dp)) {
                    Text("Monto del gasto:")
                    innerTextField()
                }
            }
        )

        // Categoría del gasto
        BasicTextField(
            value = categoria,
            onValueChange = { categoria = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            decorationBox = { innerTextField ->
                Row(modifier = Modifier.padding(8.dp)) {
                    Text("Categoría del gasto:")
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (nombre.text.isNotEmpty() && monto.text.isNotEmpty() && categoria.text.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val nuevoGasto = Gasto(
                            nombre = nombre.text,
                            monto = monto.text.toDouble(),
                            categoria = categoria.text,
                            fecha = System.currentTimeMillis()
                        )
                        db.gastoDao().agregarGasto(nuevoGasto)
                        withContext(Dispatchers.Main) {
                            mensaje = "Gasto registrado con éxito"
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            mensaje = "Error al registrar el gasto: ${e.message}"
                        }
                    }
                }
            } else {
                mensaje = "Por favor complete todos los campos."
            }
        }) {
            Text("Registrar Gasto")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(mensaje)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Lista de gastos:")
        listaGastos.forEach { gasto ->
            Text("Nombre: ${gasto.nombre} | Monto: ${gasto.monto} | Categoría: ${gasto.categoria}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GestorAndroidTheme {
        // Inicia la base de datos para pruebas
        val db = AppDatabase.getDatabase(LocalContext.current)
        GestorDeGastosScreen(db)
    }
}
