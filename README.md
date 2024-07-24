
# ScanInput Library

`ScanInput` 是一个用于在 Android 设备上生成二维码并通过 WebSocket 进行实时输入数据的库。用户可以通过扫描二维码在网页中输入数据，这些数据会实时回传到 Android 设备。

## 特性

- 生成二维码，包含 WebSocket URL。
- 启动本地 HTTP 和 WebSocket 服务器。
- 在二维码扫描后的网页中输入数据，实时回传到 Android 设备。
- 支持自定义 WebSocket 服务器和 HTTP 服务器端口。

## 安装

### 添加依赖

在你的 `build.gradle` 文件中添加以下依赖项：

```groovy
dependencies {
    implementation 'com.google.zxing:core:3.4.1'
    implementation 'fi.iki.elonen:nanohttpd:2.3.1'
    implementation 'fi.iki.elonen:nanowebsocket:2.3.1'
}
```

## 使用

### 初始化和启动

在你的 `Activity` 或 `Fragment` 中，首先初始化 `ScanInputLibrary` 并启动服务器：

```kotlin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cool.wuhao.scaninputlib.ScanInputLibrary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scanInputLibrary = ScanInputLibrary.getInstance()
        scanInputLibrary.startServer(
            context = this,
            webPort = 8080,
            wsPort = 8081,
            callback = object : ScanInputLibrary.InputCallback {
                override fun onInputReceived(input: String) {
                    // Handle received input here
                }
            }
        )
        
        val qrCodeBitmap = scanInputLibrary.generateQrCode("http://localhost:8080")
        // Display QR code in your UI
    }
}
```

### 生成二维码

使用 `ScanInputLibrary` 的 `generateQrCode` 方法生成二维码：

```kotlin
val qrCodeBitmap = scanInputLibrary.generateQrCode("http://localhost:8080")
```

### 停止服务器

当不再需要服务器时，可以调用 `stopServer` 方法：

```kotlin
scanInputLibrary.stopServer()
```

## 如何贡献

我们欢迎任何贡献！你可以通过以下方式参与：

1. **报告问题**：在 GitHub 仓库的 [Issues](https://github.com/haowu77/ScanInput/issues) 部分报告任何问题或错误。
2. **提交改进**：如果你有任何改进建议，请提交 [Pull Request](https://github.com/haowu77/ScanInput/pulls)。
3. **反馈**：提供你对项目的任何反馈，以帮助我们改进。

## 许可证

`ScanInput` 库采用 [MIT 许可证](LICENSE)。你可以自由地使用、修改和分发这个库，只要在你分发的版本中包含原始许可证和版权声明。

## 参考

- [ZXing](https://github.com/zxing/zxing) - 用于生成二维码的库。
- [NanoHTTPD](https://github.com/NanoHttpd/nanohttpd) - 用于实现轻量级 HTTP 服务器。
- [NanoWSD](https://github.com/NanoHttpd/nanowebsocket) - 用于实现 WebSocket 服务器。

## 联系

如果你有任何问题或需要进一步的帮助，请通过以下方式联系我：

- [GitHub Issues](https://github.com/haowu77/ScanInput/issues)
- [Email](mailto:hao.wu77@outlook.com)
