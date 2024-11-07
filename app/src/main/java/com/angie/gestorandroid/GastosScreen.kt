package com.angie.gestorandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.room.*
import com.angie.gestorandroid.ui.theme.GestorAndroidTheme
import androidx.compose.ui.platform.LocalContext



@Composable
fun GestorDeGastosScreen(db: AppDatabase) {
    var nombre by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var listaGastos by remember { mutableStateOf<List<Gasto>>(emptyList()) }
    var descripcion by remember { mutableStateOf("") }


    // Obtener todos los gastos para mostrarlos
    LaunchedEffect(true) {
        val gastos = withContext(Dispatchers.IO) {
            db.gastoDao().obtenerTodosLosGastos()
        }
        listaGastos = gastos
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Gestor de Gastos", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre del gasto
        BasicTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            decorationBox = { innerTextField ->
                Row(modifier = Modifier.padding(8.dp)) {
                    Text("Descripción del gasto:")
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
            if (nombre.isNotEmpty() && monto.isNotEmpty() && categoria.isNotEmpty() && descripcion.isNotEmpty()) {
                val nuevoGasto = Gasto(
                    id = 0,  // id autoincremental
                    nombre = nombre,
                    monto = monto.toDouble(),
                    categoria = categoria,
                    descripcion = descripcion,  // Asegúrate de tener el valor de descripción
                    fecha = System.currentTimeMillis()
                )
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        db.gastoDao().agregarGasto(nuevoGasto)
                        withContext(Dispatchers.Main) {
                            mensaje = "Gasto registrado con éxito"
                            // Actualizar lista de gastos
                            listaGastos = db.gastoDao().obtenerTodosLosGastos()
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
        val db = AppDatabase.getDatabase(LocalContext.current)
        GestorDeGastosScreen(db)
    }
}
