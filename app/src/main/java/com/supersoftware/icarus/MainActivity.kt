package com.supersoftware.icarus

import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.service.autofill.OnClickAction
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.supersoftware.icarus.ui.theme.IcarusTheme
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.logging.Logger


class MainActivity : ComponentActivity() {

    private var localIp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val androidModelName = android.os.Build.MODEL
        val androidVersion = android.os.Build.VERSION.RELEASE
        this.localIp = getIpv4HostAddress()

        if (localIp.isNullOrEmpty()) Logger.getLogger("MainActivity").severe("Unable to get local ip address")

        val systemInformation = SystemInformation(androidVersion, androidModelName, this.localIp ?: "-")

        setContent {
            IcarusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        if (localIp.isNullOrEmpty()) {
                            ErrorLocalIpAlertDialog(onClick = { exitApplication() })
                        }

                        SystemInformationCard(
                            systemInformation = systemInformation
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(color = Color.White, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Searching for a server...",
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { handlePingRequest() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)

                        ) {
                            Text(text = "Send ping request")
                        }
                    }
                }
            }
        }
    }

    private fun getIpv4HostAddress(): String? {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress }
        }
        return null
    }

    private fun exitApplication() {
        finishAndRemoveTask()
    }

    private fun handlePingRequest() {
        Logger.getLogger("MainActivity").info("Ping request sent")
    }

}

data class Message(val author: String, val body: String)
data class SystemInformation(val version: String, val model: String, val localIp: String)

@Composable
fun MessageCard(message: Message) {
    Column {
        Text(text = message.author, color = Color.White)
        Text(text = message.body, color = Color.White)
    }
}

@Composable
fun SystemInformationCard(systemInformation: SystemInformation) {
    Column {
        Text(
            text = "Information système",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(text = "Version Android: ${systemInformation.version}", color = Color.White)
        Text(text = "Modèle: ${systemInformation.model}", color = Color.White)
        Text(text = "Adresse ip locale: ${systemInformation.localIp}", color = Color.White)
    }
}

@Composable
fun ErrorLocalIpAlertDialog(onClick: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = onClick)
            { Text(text = "Close the application") }
        },
        title = { Text(text = "An error occurred") },
        text = { Text(text = "Sorry but we were not able to retrieve your local IP.") }
    )
}

@Preview
@Composable
fun PreviewMessageCard() {
    Column {
        SystemInformationCard(
            systemInformation = SystemInformation("13", "OnePlus 8", "192.168.0.166")
        )
        Spacer(modifier = Modifier.height(4.dp))
        Divider(color = Color.White, thickness = 1.dp)
        Spacer(modifier = Modifier.height(4.dp))
        MessageCard(Message("Mishaaa", "Hello"))
        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)

        ) {
            Text(text = "Send ping request")
        }
    }
}
