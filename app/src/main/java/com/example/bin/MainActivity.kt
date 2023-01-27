package com.example.bin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bin.network.models.CardInfo
import com.example.bin.ui.theme.BINTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BINTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {

    val viewModel: MainViewModel = viewModel()
    val text: String by remember {viewModel.input}
    val expanded: Boolean by remember {viewModel.expanded}
    val cardInfo: CardInfo by remember {viewModel.cardInfo}

    val phoneExists: Boolean by remember { viewModel.phoneExists }
    val urlExists: Boolean by remember { viewModel.urlExists }
    val geoExists: Boolean by remember { viewModel.geoExists }

    val history: List<String> by remember { viewModel.history }

    val ctx = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            stringResource(R.string.instructions),
            Modifier.padding(16.dp, 8.dp),
            style = MaterialTheme.typography.h5
        )
        InputField(
            history,
            expanded,
            { viewModel.onExpandedChanged(it) },
            text,
            { viewModel.onTextChanged(it) }
        ) { viewModel.sendRequest(it) }

        Text(
            stringResource(R.string.card_data),
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            style = MaterialTheme.typography.h5
        )

        CardInfoObject("SCHEME / NETWORK", cardInfo.scheme)
        CardInfoObject("BRAND", cardInfo.brand)

        Text(
            "CARD NUMBER",
            Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            CardInfoObject(
                "LENGTH",
                if (cardInfo.number.length == null) "?" else cardInfo.number.length.toString()
            )
            CardInfoObject(
                "LUHN",
                when (cardInfo.number.luhn) {
                    true -> "Yes"
                    false -> "No"
                    else -> "?"
                }
            )
        }

        CardInfoObject("TYPE", cardInfo.type)
        CardInfoObject(
            "PREPAID",
            when (cardInfo.prepaid) {
                true -> "Yes"
                false -> "No"
                else -> "?"
            }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = geoExists,
                    onClick = {
                        val u =
                            Uri.parse("geo:" + cardInfo.country.latitude + "," + cardInfo.country.longitude)
                        val i = Intent(Intent.ACTION_VIEW, u)
                        try {
                            ctx.startActivity(i)
                        } catch (s: SecurityException) {
                            Toast
                                .makeText(ctx, "An error occurred", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CardInfoObject("COUNTRY", cardInfo.country.name)
            CardInfoObject(
                "latitude:",
                if (cardInfo.country.latitude == null) "?" else cardInfo.country.latitude.toString()
            )
            CardInfoObject(
                "longitude:",
                if (cardInfo.country.longitude == null) "?" else cardInfo.country.longitude.toString()
            )
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                "BANK",
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                cardInfo.bank.name,
                style = MaterialTheme.typography.h6
            )
            Text(
                cardInfo.bank.url,
                Modifier.clickable (
                    enabled = urlExists,
                    onClick = {
                    val u = Uri.parse("http://" + cardInfo.bank.url)
                    val i = Intent(Intent.ACTION_VIEW, u)
                    try {
                        ctx.startActivity(i)
                    } catch (s: SecurityException) {
                        Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG)
                            .show()
                    } }
                ),
                style = MaterialTheme.typography.h6
            )
            Text(
                cardInfo.bank.phone,
                Modifier.clickable (
                    enabled = phoneExists,
                    onClick = {
                    val u = Uri.parse("tel:" + cardInfo.bank.phone)
                    val i = Intent(Intent.ACTION_DIAL, u)
                    try {
                        ctx.startActivity(i)
                    } catch (s: SecurityException) {
                        Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG)
                            .show()
                    } }
                ),
                style = MaterialTheme.typography.h6
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InputField(
    history: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    text: String,
    onTextChanged: (String) -> Unit,
    sendRequest: (String) -> Unit
) {

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp),
    ) {
        TextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = MaterialTheme.typography.h5,
            trailingIcon = {
                IconButton(
                    onClick = { sendRequest(text) }
                ) {
                    Icon (Icons.Filled.Done, contentDescription = "")
                }
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        if (history.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    onExpandedChange(false)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                history.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            onTextChanged(selectionOption)
                            onExpandedChange(false)
                        }
                    ) {
                        Text(
                            text = selectionOption,
                            style = MaterialTheme.typography.h5,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
    /*
    OutlinedTextField(
        value = text,
        onValueChange = onTextChanged,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp, 8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        textStyle = MaterialTheme.typography.h5,
    )
    */
}

@Composable
fun CardInfoObject(
    label: String,
    value: String = "?"
) {
    Column(
        Modifier
            .wrapContentSize()
            .padding(8.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            value,
            style = MaterialTheme.typography.h6
        )
    }
}