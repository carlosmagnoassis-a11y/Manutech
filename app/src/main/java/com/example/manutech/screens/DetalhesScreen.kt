
package com.example.manutech.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.manutech.model.AtualizarStatusRequest
import com.example.manutech.model.Manutencao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private val Azul = Color(0xFF164A8A)
private val Fundo = Color(0xFFF3F6FA)
private val Verde = Color(0xFF238653)
private val Vermelho = Color(0xFFB3261E)
private val Laranja = Color(0xFFB7791F)
private val AzulStatus = Color(0xFF2878D0)

private fun corDoStatus(status: String): Color {
    return when (status.trim().lowercase()) {
        "aberta" -> Laranja
        "em andamento" -> AzulStatus
        "concluída", "concluida" -> Verde
        else -> Color.DarkGray
    }
}

@Composable
fun DetalhesManutencaoScreen(
    manutencao: Manutencao,
    onVoltar: () -> Unit,
    onAlterarStatus: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    var statusSelecionado by remember(manutencao.id) {
        mutableStateOf(manutencao.status)
    }

    var statusAtual by remember(manutencao.id) {
        mutableStateOf(manutencao.status)
    }

    var salvando by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }
    var sucesso by remember { mutableStateOf(false) }

    LaunchedEffect(manutencao.id, manutencao.status) {
        statusAtual = manutencao.status
        statusSelecionado = manutencao.status
    }

    val opcoes = listOf(
        Triple("Aberta", "◷", Laranja),
        Triple("Em andamento", "↻", AzulStatus),
        Triple("Concluída", "✓", Verde)
    )

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

            // ÚNICO BOTÃO VOLTAR
            OutlinedButton(
                onClick = onVoltar,
                enabled = !salvando,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Azul),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Azul
                )
            ) {
                Text(
                    text = "←  Voltar ao histórico",
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
                        text = "▤",
                        fontSize = 27.sp,
                        color = Azul,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = "Detalhes da manutenção",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Text(
                        text = "Consulte e atualize o registro.",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }

            // CARTÃO COM INFORMAÇÕES
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Informações da manutenção",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    HorizontalDivider(
                        color = Color(0xFFE9EDF2)
                    )

                    Text(
                        text = manutencao.equipamento,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Código da manutenção: #${manutencao.id}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    HorizontalDivider(
                        color = Color(0xFFE9EDF2)
                    )

                    Text(
                        text = "Problema identificado",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Text(
                        text = manutencao.problema,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Data do registro",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Text(
                        text = manutencao.data,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Descrição da ocorrência",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Text(
                        text = manutencao.descricao.ifBlank {
                            "Nenhuma descrição informada."
                        },
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )

                    HorizontalDivider(
                        color = Color(0xFFE9EDF2)
                    )

                    Text(
                        text = "Status atual",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Surface(
                        color = corDoStatus(statusAtual).copy(
                            alpha = 0.12f
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "●  $statusAtual",
                            color = corDoStatus(statusAtual),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 9.dp
                            )
                        )
                    }
                }
            }

            // CARTÃO PARA ALTERAÇÃO DE STATUS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Alterar status",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Text(
                        text = "Selecione o novo status da manutenção:",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    // OPÇÕES DE STATUS
                    opcoes.forEach { (status, icone, cor) ->

                        val selecionado =
                            statusSelecionado == status

                        OutlinedButton(
                            onClick = {
                                statusSelecionado = status
                                mensagem = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            enabled = !salvando,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                if (selecionado) 2.dp else 1.dp,
                                if (selecionado) cor
                                else Color(0xFFE0E5EC)
                            ),
                            colors =
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selecionado) {
                                        cor.copy(alpha = 0.10f)
                                    } else {
                                        Color.White
                                    },
                                    contentColor = cor
                                )
                        ) {
                            Text(
                                text = "$icone  $status",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (selecionado) {
                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text(
                                    text = "• Selecionado",
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = Color(0xFFE9EDF2)
                    )

                    // MENSAGEM DE RETORNO
                    if (mensagem.isNotEmpty()) {
                        Text(
                            text = mensagem,
                            color = if (sucesso) {
                                Verde
                            } else {
                                Vermelho
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // BOTÃO SALVAR ALTERAÇÃO
                    Button(
                        onClick = {
                            salvando = true
                            mensagem = ""

                            val novoStatus = statusSelecionado

                            scope.launch {
                                try {
                                    val resposta =
                                        RetrofitClient.apiService
                                            .atualizarStatusManutencao(
                                                AtualizarStatusRequest(
                                                    id = manutencao.id,
                                                    status = novoStatus
                                                )
                                            )

                                    if (
                                        resposta.isSuccessful &&
                                        resposta.body()?.sucesso == true
                                    ) {
                                        statusAtual = novoStatus
                                        sucesso = true
                                        mensagem =
                                            "Status atualizado com sucesso!"

                                        onAlterarStatus(novoStatus)

                                    } else {
                                        sucesso = false
                                        mensagem =
                                            "Não foi possível atualizar o status. Código HTTP: ${resposta.code()}"
                                    }

                                } catch (e: CancellationException) {
                                    throw e
                                } catch (e: Exception) {
                                    Log.e(
                                        "MANUTECH_API",
                                        "Erro ao atualizar status",
                                        e
                                    )

                                    sucesso = false
                                    mensagem =
                                        "Falha na comunicação com o servidor."

                                } finally {
                                    salvando = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = !salvando &&
                                statusSelecionado != statusAtual,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Azul
                        )
                    ) {
                        if (salvando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "✓  Salvar alteração",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
}
