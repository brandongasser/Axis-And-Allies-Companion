package com.example.axisandalliescompanion

import android.app.Application
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.axisandalliescompanion.ui.theme.AxisAndAlliesCompanionTheme
import kotlinx.coroutines.runBlocking

class AxisAndAlliesCompanion : Application() {
    lateinit var appRepository: AppRepository

    override fun onCreate() {
        super.onCreate()
        appRepository = AppRepository(
            AppDataSource(
                this
            )
        )
        runBlocking { appRepository.initializeEconomies() }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AxisAndAlliesCompanionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Layout()
                }
            }
        }
    }
}

@Composable
fun Layout(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "all-economies") {
        composable("all-economies") { AllEconomies(navController = navController) }
        composable("edit/{nationId}") { EditEconomy(navController = navController, nation = Nation.values()[it.arguments!!.getString("nationId")!!.toInt()]) }
        composable("purchase/{nationId}") { Purchase(navController = navController, nation = Nation.values()[it.arguments!!.getString("nationId")!!.toInt()]) }
        composable("collect-income/{nationId}") { CollectIncome(navController = navController, nation = Nation.values()[it.arguments!!.getString("nationId")!!.toInt()]) }
        composable("steal/{nationId}") { Steal(navController = navController, nation = Nation.values()[it.arguments!!.getString("nationId")!!.toInt()]) }
    }
}

@Composable
fun AllEconomies(modifier: Modifier = Modifier, navController: NavController) {
    val economyViewModel: EconomyViewModel = viewModel(factory = EconomyViewModel.Factory)

    Column {
        Nation.values().forEach {
            NationEditor(navController = navController, nation = it)
        }

        Row(modifier = Modifier.align(CenterHorizontally)) {
            Button(onClick = { runBlocking { economyViewModel.resetEconomies() } }) {
                Text(text = "Reset Economies")
            }
        }
    }
}

@Composable
fun NationEditor(modifier: Modifier = Modifier, navController: NavController, nation: Nation) {
    val economyViewModel: EconomyViewModel = viewModel(factory = EconomyViewModel.Factory)
    val economyUIState = economyViewModel.economyUIState.collectAsState()

    Row(modifier=Modifier.fillMaxWidth()) {
        Text(modifier=Modifier.align(Alignment.CenterVertically), text = "$nation: ${ economyUIState.value.economies[nation] }")

        Spacer(modifier=Modifier.weight(weight=1f))

        Button(onClick = { navController.navigate("edit/${nation.ordinal}") }) {
            Text(text = "Edit")
        }
    }
}

@Composable
fun EditEconomy(modifier: Modifier = Modifier, navController: NavController, nation: Nation) {
    val economyViewModel: EconomyViewModel = viewModel(factory = EconomyViewModel.Factory)
    val economyUIState = economyViewModel.economyUIState.collectAsState()

    Column {
        Text(text = "$nation")

        TextField(value = TextFieldValue(text = economyUIState.value.economies[nation].toString(), selection = TextRange(economyUIState.value.economies[nation].toString().length)), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done), onValueChange = {
            runBlocking { economyViewModel.setEconomy(nation, it.text.filter { it.isDigit() }.ifEmpty { "0" }.toInt()) }
        })

        Row {
            Button(onClick = { navController.navigate("collect-income/${nation.ordinal}") }) {
                Text(text = "Collect Income")
            }

            Button(onClick = { navController.navigate("purchase/${nation.ordinal}") }) {
                Text(text = "Purchase Units")
            }

            Button(onClick = { navController.navigate("steal/${nation.ordinal}") }) {
                Text(text = "Steal")
            }
        }

        Button(onClick = { navController.navigate("all-economies") }) {
            Text(text = "Return")
        }
    }
}

@Composable
fun Purchase(modifier: Modifier = Modifier, navController: NavController, nation: Nation) {
    val economyViewModel: EconomyViewModel = viewModel(factory = EconomyViewModel.Factory)
    val economyUIState = economyViewModel.economyUIState.collectAsState()

    val purchaseViewModel: PurchaseViewModel = viewModel(factory = PurchaseViewModel.Factory)
    val purchaseUIState = purchaseViewModel.purchaseUIState.collectAsState()

    val scrollState = rememberScrollState()

    if (purchaseUIState.value.showSummary) {
        AlertDialog(onDismissRequest = { purchaseViewModel.closeSummary() }, buttons = {
            Row {
                Button(onClick = { navController.navigate("all-economies") }) {
                    Text(text = "Confirm")
                }

                Button(onClick = { purchaseViewModel.closeSummary() }) {
                    Text(text = "Close")
                }
            }
        }, title = { Text(text = "Summary") }, text = {
            Column {
                PurchaseType.values().forEach {
                    if (purchaseUIState.value.purchaseCounts[it]!! != 0) {
                        Text(text = "$it: ${purchaseUIState.value.purchaseCounts[it]}")
                    }
                }
            }
        })
    }

    Column(modifier = Modifier.verticalScroll(state = scrollState)) {
        Text(text = "$nation")

        Text(text = "Remaining IPCs: ${economyUIState.value.economies[nation]}")

        PurchaseType.values().forEach {
            UnitPurchase(purchaseType = it, nation = nation)
        }

        Row {
            Button(onClick = { purchaseViewModel.openSummary() }) {
                Text(text = "Summary")
            }

            Button(onClick = { runBlocking {
                PurchaseType.values().forEach {
                    val purchaseType = it
                    repeat (purchaseUIState.value.purchaseCounts[purchaseType]!!) {
                        economyViewModel.addToEconomy(nation, purchaseType.cost)
                    }
                }
                navController.navigate("all-economies")
            } }) {
                Text(text = "Return")
            }
        }
    }
}

@Composable
fun UnitPurchase(modifier: Modifier = Modifier, purchaseType: PurchaseType, nation: Nation) {
    val economyViewModel: EconomyViewModel = viewModel(factory = EconomyViewModel.Factory)

    val purchaseViewModel: PurchaseViewModel = viewModel(factory = PurchaseViewModel.Factory)
    val purchaseUIState = purchaseViewModel.purchaseUIState.collectAsState()

    Row {
        Text(modifier = Modifier.align(Alignment.CenterVertically), text = "$purchaseType: ${purchaseUIState.value.purchaseCounts[purchaseType]}")

        Spacer(modifier = Modifier.weight(weight = 1f))

        Button(onClick = { runBlocking { if (economyViewModel.removeFromEconomy(nation, purchaseType.cost)) purchaseViewModel.addUnit(purchaseType) } }) {
            Text(text = "+")
        }

        Button(onClick = { runBlocking { if (purchaseViewModel.removeUnit(purchaseType)) economyViewModel.addToEconomy(nation, purchaseType.cost) } }) {
            Text(text = "-")
        }
    }
}

@Composable
fun CollectIncome(modifier: Modifier = Modifier, navController: NavController, nation: Nation) {
    val economyViewModel: EconomyViewModel = viewModel(factory = EconomyViewModel.Factory)

    val collectIncomeViewModel: CollectIncomeViewModel = viewModel(factory = CollectIncomeViewModel.Factory)
    val collectIncomeUIState = collectIncomeViewModel.collectIncomeUIState.collectAsState()

    Column {
        Text(text = "$nation")

        TextField(
            label = { Text(text = "Base Income") },
            value = TextFieldValue(
                text = collectIncomeUIState.value.baseIncome.toString(),
                selection = TextRange(collectIncomeUIState.value.baseIncome.toString().length)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            onValueChange = {
                collectIncomeViewModel.setBaseIncome(it.text.filter { it.isDigit() }.ifEmpty { "0" }.toInt())
            }
        )

        TextField(
            label = { Text(text = "Convoy Disruptions") },
            value = TextFieldValue(
                text = collectIncomeUIState.value.convoyDisruptions.toString(),
                selection = TextRange(collectIncomeUIState.value.convoyDisruptions.toString().length)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            onValueChange = {
                collectIncomeViewModel.setConvoyDisruptions(it.text.filter { it.isDigit() }.ifEmpty { "0" }.toInt())
            }
        )

        TextField(
            label = { Text(text = "War Bonds") },
            value = TextFieldValue(
                text = collectIncomeUIState.value.warBonds.toString(),
                selection = TextRange(collectIncomeUIState.value.warBonds.toString().length)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            onValueChange = {
                collectIncomeViewModel.setWarBonds(it.text.filter { it.isDigit() }.ifEmpty { "0" }.toInt())
            }
        )

        TextField(
            label = { Text(text = "National Objectives") },
            value = TextFieldValue(
                text = collectIncomeUIState.value.nationalObjectives.toString(),
                selection = TextRange(collectIncomeUIState.value.nationalObjectives.toString().length)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            onValueChange = {
                collectIncomeViewModel.setNationalObjectives(it.text.filter { it.isDigit() }.ifEmpty { "0" }.toInt())
            }
        )

        Row {
            Button(onClick = {
                runBlocking {
                    economyViewModel.addToEconomy(nation, collectIncomeUIState.value.baseIncome + collectIncomeUIState.value.warBonds + collectIncomeUIState.value.nationalObjectives - collectIncomeUIState.value.convoyDisruptions)
                }
                navController.navigate("all-economies")
            }) {
                Text(text = "Confirm")
            }

            Button(onClick = { navController.navigate("all-economies") }) {
                Text(text = "Return")
            }
        }
    }
}

@Composable
fun Steal(modifier: Modifier = Modifier, navController: NavController, nation: Nation) {
    val economyViewModel: EconomyViewModel = viewModel(factory = EconomyViewModel.Factory)

    Column {
        Nation.values().filter { it != nation }.forEach {
            Button(onClick = { runBlocking {
                economyViewModel.steal(from = it, to = nation)
                navController.navigate("all-economies")
            } }) {
                Text(text = "$it")
            }
        }

        Button(onClick = { navController.navigate("all-economies") }) {
            Text(text = "Return")
        }
    }
}