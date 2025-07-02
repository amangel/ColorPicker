package org.devgel.colorpicker

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.annotation.ColorLong
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.concurrent.Executors
import androidx.core.graphics.get

@Composable
fun CameraScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraController = remember { LifecycleCameraController(context) }
    var rgb by remember { mutableStateOf("RGB: ") }
    @ColorLong var rawRgb by remember { mutableStateOf(0)}

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                PreviewView(it).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setBackgroundColor(android.graphics.Color.BLACK)
                    scaleType = PreviewView.ScaleType.FILL_START
                }.also {
                    cameraController.bindToLifecycle(lifecycleOwner)
                    it.controller = cameraController
                }
            }
        )

        // Rectangle at the bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Text(
                text = rgb,
                color = Color.LightGray,
                fontSize = 20.sp,
                modifier = Modifier
                    .background(Color.DarkGray)
                    .padding(8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Make it stretch across the width
                    .height(32.dp) // Set its height
                    .background(Color(rawRgb)) // Set its background color (change as needed)
            )
        }
    }

    cameraController.setImageAnalysisAnalyzer(
        Executors.newSingleThreadExecutor()
    ) { imageProxy: ImageProxy ->
        val bitmap = imageProxy.toBitmap()
        val pixel = bitmap[bitmap.width / 2, bitmap.height / 2]
        rawRgb = pixel
        val r = android.graphics.Color.red(pixel)
        val g = android.graphics.Color.green(pixel)
        val b = android.graphics.Color.blue(pixel)
        rgb = "RGB: $r, $g, $b"
        imageProxy.close()
    }
}