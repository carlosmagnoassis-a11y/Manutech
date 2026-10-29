
package com.example.manutech

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.manutech.model.Manutencao
import com.example.manutech.screens.DetalhesManutencaoScreen
import com.example.manutech.screens.EquipamentosScreen
import com.example.manutech.screens.HistoricoScreen
import com.example.manutech.screens.NovaManutencaoScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val manutencoes = remember {
        mutableStateListOf(
            Manutencao(
                id = 1,
                equipamento = "Servidor de Rede",
                problema = "Verificar conectividade",
                data = "20/10/2026",
                descricao = "Verificar a conectividade do servidor.",
                status = "Em andamento"
            ),
            Manutencao(
                id = 2,
                equipamento = "Switch de Rede",
                problema = "Falha em uma porta",
                data = "18/10/2026",
                descricao = "Verificar a porta com falha.",
                status = "Aberta"
            )
        )
    }

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
                onRegistrar = { novaManutencao ->
                    val proximoId =
                        (manutencoes.maxOfOrNull { it.id } ?: 0) + 1

                    manutencoes.add(
                        novaManutencao.copy(id = proximoId)
                    )

                    navController.popBackStack()
                }
            )
        }

        composable("historico") {
            HistoricoScreen(
                manutencoes = manutencoes,
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

            val manutencao = manutencoes.find {
                it.id == id
            }

            if (manutencao != null) {
                DetalhesManutencaoScreen(
                    manutencao = manutencao,
                    onVoltar = {
                        navController.popBackStack()
                    },
                    onAlterarStatus = { novoStatus ->
                        val indice = manutencoes.indexOfFirst {
                            it.id == manutencao.id
                        }

                        if (indice != -1) {
                            manutencoes[indice] =
                                manutencoes[indice].copy(
                                    status = novoStatus
                                )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TelaSimples(
    titulo: String,
    descricao: String,
    onVoltar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = descricao)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onVoltar) {
            Text("Voltar")
        }
    }
}
