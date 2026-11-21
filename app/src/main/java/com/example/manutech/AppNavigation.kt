
package com.example.manutech

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.manutech.api.RetrofitClient
import com.example.manutech.model.Manutencao
import com.example.manutech.screens.DetalhesManutencaoScreen
import com.example.manutech.screens.EquipamentosScreen
import com.example.manutech.screens.HistoricoScreen
import com.example.manutech.screens.NovaManutencaoScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {

        composable("inicio") {
            TelaInicial(
                onEquipamentosClick = {
                    navController.navigate("equipamentos")
                },
                onNovaManutencaoClick = {
                    navController.navigate("nova_manutencao")
                },
                onHistoricoClick = {
                    navController.navigate("historico")
                }
            )
        }

        composable("equipamentos") {
            EquipamentosScreen(
                onVoltar = {
                    navController.popBackStack()
                }
            )
        }

        composable("nova_manutencao") {
            NovaManutencaoScreen(
                onVoltar = {
                    navController.popBackStack()
                },
                onRegistrar = {
                    // O cadastro ja foi realizado pela API POST.
                    navController.popBackStack()
                }
            )
        }

        composable("historico") {
            HistoricoScreen(
                manutencoes = emptyList(),
                onVoltar = {
                    navController.popBackStack()
                },
                onDetalhesClick = { id ->
                    navController.navigate("detalhes/$id")
                }
            )
        }

        composable("detalhes/{id}") { backStackEntry ->

            val id = backStackEntry.arguments
                ?.getString("id")
                ?.toIntOrNull()

            var manutencao by remember(id) {
                mutableStateOf<Manutencao?>(null)
            }

            var carregando by remember(id) {
                mutableStateOf(true)
            }

            var erro by remember(id) {
                mutableStateOf("")
            }

            LaunchedEffect(id) {
                carregando = true
                erro = ""

                if (id == null) {
                    erro = "Identificador da manutenção inválido."
                    carregando = false
                    return@LaunchedEffect
                }

                try {
                    val resposta =
                        RetrofitClient.apiService.listarManutencoes()

                    if (resposta.isSuccessful) {
                        manutencao = resposta.body()
                            ?.find { it.id == id }

                        if (manutencao == null) {
                            erro = "Manutenção não encontrada."
                        }
                    } else {
                        erro = "Erro ao consultar a manutenção."
                    }
                } catch (e: Exception) {
                    erro = "Falha na comunicação com a API."
                } finally {
                    carregando = false
                }
            }

            when {
                carregando -> {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Carregando manutenção...")
                    }
                }

                erro.isNotEmpty() -> {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(erro)

                        Button(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Text("Voltar")
                        }
                    }
                }

                manutencao != null -> {
                    DetalhesManutencaoScreen(
                        manutencao = manutencao!!,
                        onVoltar = {
                            navController.popBackStack()
                        },
                        onAlterarStatus = { novoStatus ->
                            // Atualizacao local temporaria.
                            manutencao = manutencao?.copy(
                                status = novoStatus
                            )
                        }
                    )
                }
            }
        }
    }
}
