package com.angie.gestorandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.angie.gestorandroid.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app_diaria.db").build()

        setContent {
            AppDiariaScreen(db)
        }
    }
}

@Composable
fun AppDiariaScreen(db: AppDatabase) {
    var descripcion by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        TextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción del Gasto") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = monto,
            onValueChange = { monto = it },
            label = { Text("Monto") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { registrarGasto(descripcion, monto, categoria, fecha, db) { msg -> message = msg } }) {
                Text("Registrar Gasto")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(message)
    }
}

fun registrarGasto(descripcion: String, monto: String, categoria: String, fecha: String, db: AppDatabase, onResult: (String) -> Unit) {
    if (descripcion.isEmpty() || monto.isEmpty() || categoria.isEmpty() || fecha.isEmpty()) {
        onResult("Por favor, complete todos los campos.")
        return
    }
    val montoDouble = monto.toDoubleOrNull()
    if (montoDouble == null || montoDouble <= 0) {
        onResult("El monto debe ser un valor numérico positivo.")
        return
    }
    val gasto = Gasto(descripcion = descripcion, monto = montoDouble, categoria = categoria, fecha = fecha)

    CoroutineScope(Dispatchers.IO).launch {
        try {
            db.gastoDao().registrarGasto(gasto)
            withContext(Dispatchers.Main) {
                onResult("Gasto registrado con éxito")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult("Error al registrar gasto: ${e.message}")
            }
        }
    }
}
