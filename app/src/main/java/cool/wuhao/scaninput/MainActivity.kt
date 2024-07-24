package cool.wuhao.scaninput

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cool.wuhao.scaninput.ui.theme.ScanInputTheme
import cool.wuhao.scaninputlib.ScanInputLibrary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScanInputTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QRCodeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun QRCodeScreen(modifier: Modifier = Modifier) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var receivedText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scanInputLibrary = remember { ScanInputLibrary.getInstance() }

    LaunchedEffect(Unit) {
        // Define ports
        val webPort = 8080
        val wsPort = 8081

        // Start the server and generate the QR code
        scanInputLibrary.startServer(context, webPort, wsPort) { input ->
            receivedText = input
        }

        // Generate QR code URL
        val qrCodeUrl = "http://${scanInputLibrary.getLocalIpAddress()}:$webPort"
        val qrCode = scanInputLibrary.generateQrCode(context, qrCodeUrl)
        qrBitmap = qrCode
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .wrapContentHeight()
    ) {
        // Display QR Code
        if (qrBitmap != null) {
            Image(
                bitmap = qrBitmap!!.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            Text(text = "Generating QR Code...")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "获得输入： $receivedText")
    }
}

@Preview(showBackground = true)
@Composable
fun QRCodeScreenPreview() {
    ScanInputTheme {
        QRCodeScreen()
    }
}