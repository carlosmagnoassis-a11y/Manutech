
package com.example.manutech.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manutech.api.RetrofitClient
import com.example.manutech.model.Manutencao
import kotlinx.coroutines.CancellationException

private val Azul = Color(0xFF164A8A)
private val Fundo = Color(0xFFF3F6FA)
private val Verde = Color(0xFF238653)
private val Vermelho = Color(0xFFB3261E)
private val Laranja = Color(0xFFB7791F)

private fun normalizarStatus(status: String): String {
    return status.trim().lowercase()
        .replace("concluída", "concluida")
}

@Composable
fun HistoricoScreen(
    manutencoes: List<Manutencao>,
    onVoltar: () -> Unit,
    onDetalhesClick: (Int) -> Unit
) {
    var listaManutencoes by remember {
        mutableStateOf<List<Manutencao>>(manutencoes)
    }

    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf("") }
    var atualizar by remember { mutableIntStateOf(0) }

    // FILTRO SELECIONADO
    var filtroSelecionado by remember {
        mutableStateOf("Todas")
    }

    val filtros = listOf(
        "Todas",
        "Aberta",
        "Em andamento",
        "Concluída"
    )

    // CONSULTAR MANUTENÇÕES NO MYSQL
    LaunchedEffect(atualizar) {
        carregando = true
        erro = ""

        try {
            val resposta =
                RetrofitClient.apiService.listarManutencoes()

            if (resposta.isSuccessful) {
                listaManutencoes = resposta.body().orEmpty()
            } else {
                erro =
                    "Erro ao consultar manutenções. Código HTTP: ${resposta.code()}"
            }

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            Log.e(
                "MANUTECH_API",
                "Erro ao consultar histórico",
                e
            )

            erro = "Falha na comunicação com a API."

        } finally {
            carregando = false
        }
    }

    // APLICAR FILTRO NA LISTA
    val listaFiltrada = listaManutencoes.filter { manutencao ->
        filtroSelecionado == "Todas" ||
                normalizarStatus(manutencao.status) ==
                normalizarStatus(filtroSelecionado)
    }

    // CONTADORES
    val abertas = listaManutencoes.count {
        normalizarStatus(it.status) == "aberta"
    }

    val andamento = listaManutencoes.count {
        normalizarStatus(it.status) == "em andamento"
    }

    val concluidas = listaManutencoes.count {
        normalizarStatus(it.status) == "concluida"
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Fundo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // BOTÃO VOLTAR
            OutlinedButton(
                onClick = onVoltar,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Azul),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Azul
                )
            ) {
                Text(
                    text = "←  Voltar ao início",
                    fontWeight = FontWeight.SemiBold
                )
            }

            // CABEÇALHO
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            Color(0xFFEAF1F9),
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "≡",
                        color = Azul,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = "Histórico",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Text(
                        text = "Consulte as manutenções registradas.",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }

            // RESUMO DAS MANUTENÇÕES
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Resumo das manutenções",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Text(
                        text = when {
                            carregando ->
                                "Consultando registros..."

                            erro.isNotEmpty() ->
                                "Não foi possível atualizar o resumo."

                            else ->
                                "${listaManutencoes.size} manutenções cadastradas"
                        },
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )

                    if (!carregando && erro.isEmpty()) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            IndicadorHistorico(
                                titulo = "Abertas",
                                quantidade = abertas,
                                cor = Laranja,
                                modifier = Modifier.weight(1f)
                            )

                            IndicadorHistorico(
                                titulo = "Em andamento",
                                quantidade = andamento,
                                cor = Azul,
                                modifier = Modifier.weight(1f)
                            )

                            IndicadorHistorico(
                                titulo = "Concluídas",
                                quantidade = concluidas,
                                cor = Verde,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // FILTRO POR STATUS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Filtrar por status",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Text(
                        text = "Selecione quais manutenções deseja visualizar.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(
                                rememberScrollState()
                            ),
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        filtros.forEach { filtro ->

                            FilterChip(
                                selected =
                                    filtroSelecionado == filtro,
                                onClick = {
                                    filtroSelecionado = filtro
                                },
                                enabled = !carregando &&
                                        erro.isEmpty(),
                                label = {
                                    Text(
                                        text = filtro,
                                        fontSize = 12.sp
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors =
                                    FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Azul,
                                        selectedLabelColor = Color.White,
                                        labelColor = Azul
                                    )
                            )
                        }
                    }
                }
            }

            // TÍTULO DA LISTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Manutenções registradas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = { atualizar++ },
                    enabled = !carregando
                ) {
                    Text(
                        text = "↻ Atualizar",
                        color = Azul
                    )
                }
            }

            if (!carregando && erro.isEmpty()) {
                Text(
                    text = "${listaFiltrada.size} de " +
                            "${listaManutencoes.size} registros encontrados",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            // CARREGAMENTO, ERROS E RESULTADOS
            when {
                carregando -> {
                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Azul,
                            strokeWidth = 2.dp
                        )

                        Text(
                            text = "Carregando manutenções...",
                            fontSize = 14.sp
                        )
                    }
                }

                erro.isNotEmpty() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = erro,
                                color = Vermelho
                            )

                            OutlinedButton(
                                onClick = { atualizar++ }
                            ) {
                                Text("Tentar novamente")
                            }
                        }
                    }
                }

                listaFiltrada.isEmpty() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment =
                                Alignment.CenterHorizontally,
                            verticalArrangement =
                                Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (
                                    listaManutencoes.isEmpty()
                                ) {
                                    "Nenhuma manutenção cadastrada ainda."
                                } else {
                                    "Nenhuma manutenção encontrada com esse status."
                                },
                                color = Color.Gray,
                                fontSize = 14.sp
                            )

                            if (filtroSelecionado != "Todas") {
                                OutlinedButton(
                                    onClick = {
                                        filtroSelecionado = "Todas"
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Limpar filtro")
                                }
                            }
                        }
                    }
                }

                else -> {
                    listaFiltrada.forEach { manutencao ->

                        val corStatus = when (
                            normalizarStatus(manutencao.status)
                        ) {
                            "aberta" -> Laranja
                            "em andamento" -> Azul
                            "concluida" -> Verde
                            else -> Color.DarkGray
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(10.dp)
                            ) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment =
                                        Alignment.CenterVertically,
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = manutencao.equipamento,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = corStatus.copy(
                                            alpha = 0.12f
                                        )
                                    ) {
                                        Text(
                                            text = manutencao.status,
                                            color = corStatus,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 6.dp
                                            )
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    color = Color(0xFFE9EDF2)
                                )

                                Text(
                                    text = "Manutenção #${manutencao.id}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )

                                Text(
                                    text = "Problema: ${manutencao.problema}",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )

                                Text(
                                    text = "Data: ${manutencao.data}",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )

                                if (
                                    manutencao.descricao.isNotBlank()
                                ) {
                                    Text(
                                        text = "Descrição: ${manutencao.descricao}",
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                        maxLines = 3
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Button(
                                    onClick = {
                                        onDetalhesClick(manutencao.id)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = Azul
                                        )
                                ) {
                                    Text(
                                        text = "Ver detalhes",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// COMPONENTE DOS INDICADORES
@Composable
private fun IndicadorHistorico(
    titulo: String,
    quantidade: Int,
    cor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = cor.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 4.dp,
                    vertical = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = quantidade.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = cor
            )

            Text(
                text = titulo,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = cor,
                textAlign =
                    androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
