package com.kevinnzou.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevinnzou.sample.jsbridge.GreetJsMessageHandler
import com.kevinnzou.sample.res.HtmlRes
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.util.KLogSeverity
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData

/**
 * Created By Kevin Zou On 2023/9/8
 */
@Composable
internal fun BasicWebViewWithHTMLSample() {
    val html = HtmlRes.html
//    val webViewState = rememberWebViewStateWithHTMLFile(
//        fileName = "index.html",
//    )
    val webViewState = rememberWebViewStateWithHTMLData(html)
    val webViewNavigator = rememberWebViewNavigator()
    val jsBridge = rememberWebViewJsBridge(webViewNavigator)
    LaunchedEffect(Unit) {
        initWebView(webViewState)
        initJsBridge(jsBridge)
    }
    var jsRes by mutableStateOf("Evaluate JavaScript")
    MaterialTheme {
        Box(Modifier.fillMaxSize()) {
            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize(),
                captureBackPresses = false,
                navigator = webViewNavigator,
                webViewJsBridge = jsBridge,
            )
            Button(
                onClick = {
                    webViewNavigator.evaluateJavaScript(
                        """
                        document.getElementById("subtitle").innerText = "Hello from KMM!";
                        window.kmpJsBridge.callNative("Greet",JSON.stringify({message: "Hello"}),
                            function (data) {
                                document.getElementById("subtitle").innerText = data;
                                console.log("Greet from Native: " + data);
                            }
                        );
                        callJS();
                        """.trimIndent(),
                    ) {
                        jsRes = it
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp),
            ) {
                Text(jsRes)
            }
        }
    }
}

fun initWebView(webViewState: WebViewState) {
    webViewState.webSettings.apply {
        zoomLevel = 1.0
        isJavaScriptEnabled = true
        logSeverity = KLogSeverity.Debug
        allowFileAccessFromFileURLs = true
        allowUniversalAccessFromFileURLs = true
        androidWebSettings.apply {
            isAlgorithmicDarkeningAllowed = true
            safeBrowsingEnabled = true
            allowFileAccess = true
        }
    }
}

fun initJsBridge(webViewJsBridge: WebViewJsBridge) {
    webViewJsBridge.register(GreetJsMessageHandler())
}
