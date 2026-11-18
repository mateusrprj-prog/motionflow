package app.motionflow.ui.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.motionflow.ui.kit.MfUi
import app.motionflow.ui.kit.UiLayer
import app.motionflow.ui.kit.UiLayerKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

/**
 * Composition preview. Position is stored as normalized coordinates so the
 * same project works with portrait, square and landscape artboards.
 */
@Composable
fun EditorCanvas(
    layers: List<UiLayer>,
    selectedLayerId: String?,
    aspectRatio: Float,
    modifier: Modifier = Modifier,
    onSelectLayer: (String) -> Unit = {},
    onTransformLayer: (String, Float, Float, Float, Float) -> Unit = { _, _, _, _, _ -> }
) {
    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = modifier.fillMaxSize().background(MfUi.PreviewBg),
        contentAlignment = Alignment.Center
    ) {
        val ratio = aspectRatio.coerceIn(0.25f, 4f)
        val artboardWidth = min(maxWidth, maxHeight * ratio)
        val artboardHeight = artboardWidth / ratio
        val widthPx = with(density) { artboardWidth.toPx() }.coerceAtLeast(1f)
        val heightPx = with(density) { artboardHeight.toPx() }.coerceAtLeast(1f)

        Box(
            Modifier.width(artboardWidth).height(artboardHeight)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFF0F0F2))
        ) {
            // Layers are drawn from bottom to top, in their timeline order.
            layers.filter { it.visible }.forEach { layer ->
                if (layer.kind == UiLayerKind.AUDIO || layer.kind == UiLayerKind.ADJUST ||
                    layer.kind == UiLayerKind.EMPTY
                ) return@forEach

                val itemWidth = when (layer.kind) {
                    UiLayerKind.TEXT -> artboardWidth * 0.58f
                    UiLayerKind.CAMERA_3D -> artboardWidth * 0.48f
                    else -> artboardWidth * 0.34f
                }
                val itemHeight = when (layer.kind) {
                    UiLayerKind.TEXT -> 62.dp
                    UiLayerKind.CAMERA_3D -> 74.dp
                    else -> itemWidth * 0.78f
                }
                val x = artboardWidth * layer.positionX - itemWidth / 2
                val y = artboardHeight * layer.positionY - itemHeight / 2
                val selected = layer.id == selectedLayerId

                Box(
                    Modifier.offset(x = x, y = y)
                        .width(itemWidth).height(itemHeight)
                        .graphicsLayer {
                            scaleX = layer.scale.coerceIn(0.15f, 6f)
                            scaleY = layer.scale.coerceIn(0.15f, 6f)
                            rotationZ = layer.rotation
                        }
                        .then(
                            if (selected) Modifier.border(2.dp, MfUi.Accent, RoundedCornerShape(4.dp))
                            else Modifier
                        )
                        .pointerInput(layer.id) {
                            detectTapGestures(onTap = { onSelectLayer(layer.id) })
                        }
                        .pointerInput(layer.id, widthPx, heightPx) {
                            detectTransformGestures { _, pan, zoomChange, rotationChange ->
                                onSelectLayer(layer.id)
                                onTransformLayer(
                                    layer.id,
                                    pan.x / widthPx,
                                    pan.y / heightPx,
                                    zoomChange,
                                    rotationChange
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when (layer.kind) {
                        UiLayerKind.MEDIA -> ImportedMedia(layer)
                        UiLayerKind.TEXT -> Text(
                            text = layer.textValue.ifBlank { layer.name },
                            color = Color(0xFF17171B),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        )
                        UiLayerKind.SHAPE -> Box(
                            Modifier.fillMaxSize(0.78f)
                                .background(
                                    Color(0xFF8B63E8),
                                    if (layer.scale < 0.8f) CircleShape else RoundedCornerShape(12.dp)
                                )
                        )
                        UiLayerKind.CAMERA_3D -> Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                                .background(Color(0x222D8299), RoundedCornerShape(10.dp))
                        ) {
                            Text("3D", color = Color(0xFF236D84), fontWeight = FontWeight.Black, fontSize = 25.sp)
                            Text("Câmera", color = Color(0xFF236D84), fontSize = 11.sp)
                        }
                        else -> Unit
                    }
                }
            }
            if (layers.none { it.visible && it.kind != UiLayerKind.AUDIO && it.kind != UiLayerKind.ADJUST && it.kind != UiLayerKind.EMPTY }) {
                Text(
                    "Canvas vazio",
                    color = Color(0xFF777B84),
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun ImportedMedia(layer: UiLayer) {
    val context = LocalContext.current
    val bitmap = produceState<Bitmap?>(initialValue = null, key1 = layer.sourceUri) {
        val source = layer.sourceUri ?: return@produceState
        value = withContext(Dispatchers.IO) {
            runCatching {
                val uri = Uri.parse(source)
                val mime = context.contentResolver.getType(uri).orEmpty()
                if (mime.startsWith("video/")) {
                    val retriever = MediaMetadataRetriever()
                    try {
                        retriever.setDataSource(context, uri)
                        retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    } finally {
                        retriever.release()
                    }
                } else {
                    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream, null, bounds)
                    }
                    val sample = maxOf(1, maxOf(bounds.outWidth, bounds.outHeight) / 1600)
                    val options = BitmapFactory.Options().apply { inSampleSize = sample }
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream, null, options)
                    }                }
            }.getOrNull()
        }
    }.value

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = layer.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(4.dp))
        )
    } else {
        Box(
            Modifier.fillMaxSize().background(Color(0xFF4B5361), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Carregando mídia…", color = Color.White, fontSize = 11.sp)
        }
    }
}
