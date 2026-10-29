package com.example.manutech.screens

import com.example.manutech.model.Manutencao
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaManutencaoScreen(
    onVoltar: () -> Unit,
    onRegistrar: (Manutencao) -> Unit
) {
    var equipamento by remember { mutableStateOf("") }
    var problema by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var data by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }
    var expandido by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }
    var registrado by remember { mutableStateOf(false) }

    val equipamentos = listOf(
        "Servidor de Rede - SRV-001",
        "Switch de Rede - SW-001",
        "Roteador - RT-001"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = onVoltar) {
            Text("← Voltar")
        }

        Text(
            text = "Nova Manutenção",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF164A8A)
        )

        Text(
            text = "Registre um problema ou serviço.",
            color = Color.DarkGray
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Dados da manutenção",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                ExposedDropdownMenuBox(
                    expanded = expandido,
                    onExpandedChange = {
                        expandido = !expandido
                    }
                ) {
                    OutlinedTextField(
                        value = equipamento,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Equipamento") },
                        placeholder = {
                            Text("Selecione um equipamento")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults
                                .TrailingIcon(expanded = expandido)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandido,
                        onDismissRequest = {
                            expandido = false
                        }
                    ) {
                        equipamentos.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    equipamento = item
                                    expandido = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = problema,
                    onValueChange = { problema = it },
                    label = { Text("Problema identificado") },
                    placeholder = {
                        Text("Ex.: Falha de conectividade")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = data,
                    onValueChange = { data = it },
                    label = { Text("Data (AAAA-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição da ocorrência") },
                    placeholder = {
                        Text("Descreva o que aconteceu...")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                if (mensagem.isNotEmpty()) {
                    Text(
                        text = mensagem,
                        color = if (registrado) {
                            Color(0xFF238653)
                        } else {
                            Color(0xFFB3261E)
                        },
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = {
                        if (equipamento.isBlank() || problema.isBlank()) {
                            mensagem = "Preencha os campos obrigatórios."
                        } else {
                            registrado = true
                            val novaManutencao = Manutencao(
                                id = 0,
                                equipamento = equipamento,
                                problema = problema,
                                data = data,
                                descricao = descricao,
                                status = "Aberta"
                            )

                            onRegistrar(novaManutencao)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar manutenção")
                }
            }
        }

        if (registrado) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Status inicial: Aberta",
                color = Color(0xFF238653),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
