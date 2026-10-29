
package com.example.manutech.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Equipamento(
    val nome: String,
    val identificacao: String,
    val localizacao: String
)

@Composable
fun EquipamentosScreen(
    onVoltar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var identificacao by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }

    val equipamentos = remember {
        mutableStateListOf(
            Equipamento(
                "Servidor de Rede",
                "SRV-001",
                "Datacenter"
            ),
            Equipamento(
                "Switch de Rede",
                "SW-001",
                "Sala técnica"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onVoltar) {
            Text("← Voltar")
        }

        Text(
            text = "Equipamentos",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF164A8A)
        )

        Text(
            text = "Cadastre e consulte os equipamentos.",
            color = Color.DarkGray
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Cadastrar equipamento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do equipamento") },
                    placeholder = { Text("Ex.: Roteador") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = identificacao,
                    onValueChange = { identificacao = it },
                    label = { Text("Número de identificação") },
                    placeholder = { Text("Ex.: RT-001") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localizacao,
                    onValueChange = { localizacao = it },
                    label = { Text("Localização") },
                    placeholder = { Text("Ex.: Sala técnica") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (mensagem.isNotEmpty()) {
                    Text(
                        text = mensagem,
                        color = Color(0xFFB3261E),
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = {
                        when {
                            nome.isBlank() ||
                                    identificacao.isBlank() ||
                                    localizacao.isBlank() -> {
                                mensagem =
                                    "Preencha todos os campos."
                            }

                            equipamentos.any {
                                it.identificacao.equals(
                                    identificacao.trim(),
                                    ignoreCase = true
                                )
                            } -> {
                                mensagem =
                                    "Essa identificação já existe."
                            }

                            else -> {
                                equipamentos.add(
                                    Equipamento(
                                        nome.trim(),
                                        identificacao.trim(),
                                        localizacao.trim()
                                    )
                                )

                                nome = ""
                                identificacao = ""
                                localizacao = ""
                                mensagem = ""
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cadastrar equipamento")
                }
            }
        }

        Text(
            text = "Equipamentos cadastrados (${equipamentos.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        equipamentos.forEach { equipamento ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = equipamento.nome,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Text(
                            text = "ID: ${equipamento.identificacao}",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )

                        Text(
                            text = "Local: ${equipamento.localizacao}",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
