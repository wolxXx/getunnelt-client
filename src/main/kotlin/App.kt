import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths

@Serializable
data class TunnelEntry(
    var localPort: String,
    var isRunning: Boolean = false
)

object TunnelStorage {
    private val configDir = Paths.get(System.getProperty("user.home"), ".getunnelt").toFile()
    private val configFile = File(configDir, "tunnels.json")
    private val json = Json { prettyPrint = true }

    fun load(): List<TunnelEntry> {
        if (!configFile.exists()) return emptyList()
        return try {
            json.decodeFromString(ListSerializer(TunnelEntry.serializer()), configFile.readText())
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(tunnels: List<TunnelEntry>) {
        if (!configDir.exists()) configDir.mkdirs()
        configFile.writeText(json.encodeToString(ListSerializer(TunnelEntry.serializer()), tunnels))
    }
}

@Composable
fun App(tunnels: MutableList<TunnelEntry>) {
    //val tunnels = remember { mutableStateListOf<TunnelEntry>() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tunnel Client", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            tunnels.add(TunnelEntry(localPort = "8080"))
        }) {
            Text("➕ Tunnel hinzufügen")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            itemsIndexed(tunnels) { index, tunnel ->
                TunnelRow(
                    tunnel = tunnel,
                    onRemove = { tunnels.removeAt(index) },
                    onToggle = {
                        tunnel.isRunning = !tunnel.isRunning
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun TunnelRow(tunnel: TunnelEntry, onRemove: () -> Unit, onToggle: () -> Unit) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        OutlinedTextField(
            value = tunnel.localPort,
            onValueChange = { tunnel.localPort = it },
            label = { Text("Port") },
            modifier = Modifier.width(120.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        val running = remember {
            mutableStateOf(tunnel.isRunning)
        }

        Button(onClick = onToggle) {
            Text(if (tunnel.isRunning) "Stoppen" else "Starten")
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(onClick = onRemove, colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error)) {
            Text("Entfernen")
        }

        Spacer(modifier = Modifier.width(16.dp))

        if (running.value) {
            Text("🔗 läuft", color = MaterialTheme.colors.primary)
        } else {
            Text("⏹️ gestoppt")
        }
    }
}

fun main() = application {
    val tunnels = mutableStateListOf<TunnelEntry>()
    tunnels.addAll(TunnelStorage.load())
    Window(onCloseRequest = {
        TunnelStorage.save(tunnels)
        exitApplication()
        //::exitApplication

    }, title = "Tunnel GUI") {
        MaterialTheme {
            App(tunnels)
        }
    }
}
