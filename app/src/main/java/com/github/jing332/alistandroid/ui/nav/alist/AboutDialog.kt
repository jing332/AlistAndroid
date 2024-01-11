package com.github.jing332.alistandroid.ui.nav.alist

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
            fun openUrl(uri: String) {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data = uri.toUri()
                    }
                )
            }

            Column {
                SelectionContainer {
                    Column {
                        Text("APP - ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
                        Text("AList - ${BuildConfig.ALIST_VERSION}")
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text(
                    "Github - AlistAndroid",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            openUrl("https://github.com/jing332/AlistAndroid")
                        }
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Github - AList (Release)",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            openUrl(
                                "https://github.com/alist-org/alist/releases/tag/${BuildConfig.ALIST_VERSION}"
                            )
                        }
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                )

            }
        },
        confirmButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(stringResource(id = R.string.ok))
            }
        })
}