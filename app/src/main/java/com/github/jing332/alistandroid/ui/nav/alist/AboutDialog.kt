package com.github.jing332.alistandroid.ui.nav.alist

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.github.jing332.alistandroid.BuildConfig
import com.github.jing332.alistandroid.R

@Composable
fun AboutDialog(onDismissRequest: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(onDismissRequest = onDismissRequest,
        title = {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.alist_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    stringResource(id = R.string.app_name),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                )
            }
        },
        text = {
            SelectionContainer {
                Column {
                    Text("APP版本：${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
                    Text("AList版本：${BuildConfig.ALIST_VERSION}")
                    Spacer(modifier = Modifier.height(8.dp))

                    val annotString = buildAnnotatedString {
                        append("在")
                        pushStringAnnotation("Github1", "https://github.com/jing332/AlistAndroid")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Github")
                        }
                        append("上查看AlistAndroid")
                        pop()

                        appendLine()
                        appendLine()

                        append("在")
                        pushStringAnnotation(
                            "Github2",
                            "https://github.com/alist-org/alist/releases/tag/${BuildConfig.ALIST_VERSION}"
                        )
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Github")
                        }
                        append("上查看Alist")
                        pop()
                    }
                    ClickableText(annotString) {
                        fun openUrl(uri: Uri) {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW).apply {
                                    data = uri
                                }
                            )
                        }
                        annotString
                            .getStringAnnotations("Github1", it, it)
                            .firstOrNull()?.let { stringAnnotation ->
                                openUrl(stringAnnotation.item.toUri())
                            }

                        annotString
                            .getStringAnnotations("Github2", it, it)
                            .firstOrNull()?.let { stringAnnotation ->
                                openUrl(stringAnnotation.item.toUri())
                            }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(stringResource(id = R.string.ok))
            }
        })
}