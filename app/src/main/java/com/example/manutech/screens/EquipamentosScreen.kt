
package com.example.manutech.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manutech.api.RetrofitClient
import com.example.manutech.model.Equipamento
import com.example.manutech.model.EquipamentoRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun EquipamentosScreen(
    onVoltar: () -> Unit
) {
    var equipamentos by remember {
        mutableStateOf<List<Equipamento>>(emptyList())
    }

    var carregando by remember {
        mutableStateOf(true)
    }

    var salvando by remember {
        mutableStateOf(false)
    }

    var erro by remember {
        mutableStateOf<String?>(null)
    }

    var mensagemCadastro by remember {
        mutableStateOf("")
    }

    var nome by remember {
        mutableStateOf("")
    }

    var identificacao by remember {
        mutableStateOf("")
    }

    var tipo by remember {
        mutableStateOf("")
    }

    var localizacao by remember {
        mutableStateOf("")
    }

    var atualizar by remember {
        mutableStateOf(0)
    }

    val scope = rememberCoroutineScope()

    // Consulta os equipamentos cadastrados no MySQL
    LaunchedEffect(atualizar) {
        carregando = true
        erro = null

        try {
            val resposta =
                RetrofitClient.apiService.listarEquipamentos()

            if (resposta.isSuccessful) {
                equipamentos = resposta.body() ?: emptyList()
            } else {
                erro = "Erro do servidor: ${resposta.code()}"
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                "MANUTECH_API",
                "Erro ao consultar equipamentos",
                e
            )

            erro = "Não foi possível carregar os equipamentos."
        } finally {
            carregando = false
        }
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
            text = "Cadastre e consulte os equipamentos do banco de dados.",
            color = Color.DarkGray
        )

        // Formulário de cadastro
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
                    placeholder = { Text("Ex.: Switch Cisco") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !salvando,
                    singleLine = true
                )

                OutlinedTextField(
                    value = identificacao,
                    onValueChange = { identificacao = it },
                    label = { Text("Número de identificação") },
                    placeholder = { Text("Ex.: SW-002") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !salvando,
                    singleLine = true
                )

                OutlinedTextField(
                    value = tipo,
                    onValueChange = { tipo = it },
                    label = { Text("Tipo do equipamento") },
                    placeholder = { Text("Ex.: Switch") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !salvando,
                    singleLine = true
                )

                OutlinedTextField(
                    value = localizacao,
                    onValueChange = { localizacao = it },
                    label = { Text("Localização") },
                    placeholder = { Text("Ex.: Sala de Telecom") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !salvando,
                    singleLine = true
                )

                if (mensagemCadastro.isNotEmpty()) {
                    Text(
                        text = mensagemCadastro,
                        color = if (
                            mensagemCadastro.startsWith("Sucesso")
                        ) {
                            Color(0xFF238653)
                        } else {
                            Color(0xFFB3261E)
                        },
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = {
                        val nomeLimpo = nome.trim()
                        val identificacaoLimpa =
                            identificacao.trim()
                        val tipoLimpo = tipo.trim()
                        val localizacaoLimpa =
                            localizacao.trim()

                        when {
                            nomeLimpo.isBlank() ||
                                    identificacaoLimpa.isBlank() ||
                                    localizacaoLimpa.isBlank() -> {
                                mensagemCadastro =
                                    "Preencha nome, identificação e localização."
                            }

                            nomeLimpo.length > 100 ||
                                    identificacaoLimpa.length > 50 ||
                                    tipoLimpo.length > 100 ||
                                    localizacaoLimpa.length > 150 -> {
                                mensagemCadastro =
                                    "Um ou mais campos ultrapassam o limite permitido."
                            }

                            else -> {
                                salvando = true
                                mensagemCadastro = ""

                                scope.launch {
                                    try {
                                        val novoEquipamento =
                                            EquipamentoRequest(
                                                nome = nomeLimpo,
                                                identificacao =
                                                    identificacaoLimpa,
                                                tipo = tipoLimpo,
                                                localizacao =
                                                    localizacaoLimpa
                                            )

                                        val resposta =
                                            RetrofitClient.apiService
                                                .cadastrarEquipamento(
                                                    novoEquipamento
                                                )

                                        if (resposta.isSuccessful &&
                                            resposta.body()?.sucesso == true
                                        ) {
                                            mensagemCadastro =
                                                "Sucesso! Equipamento cadastrado."

                                            nome = ""
                                            identificacao = ""
                                            tipo = ""
                                            localizacao = ""

                                            // Atualiza a lista após o cadastro
                                            atualizar++

                                        } else if (resposta.code() == 409) {
                                            mensagemCadastro =
                                                "Essa identificação já está cadastrada."

                                        } else {
                                            mensagemCadastro =
                                                "Erro ao cadastrar. Código HTTP: ${resposta.code()}"
                                        }

                                    } catch (e: CancellationException) {
                                        throw e
                                    } catch (e: Exception) {
                                        Log.e(
                                            "MANUTECH_API",
                                            "Erro ao cadastrar equipamento",
                                            e
                                        )

                                        mensagemCadastro =
                                            "Não foi possível conectar à API."

                                    } finally {
                                        salvando = false
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !salvando
                ) {
                    Text(
                        if (salvando) {
                            "Cadastrando..."
                        } else {
                            "Cadastrar equipamento"
                        }
                    )
                }
            }
        }

        Text(
            text = "Equipamentos cadastrados (${equipamentos.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = { atualizar++ },
            modifier = Modifier.fillMaxWidth(),
            enabled = !carregando
        ) {
            Text("Atualizar lista")
        }

        when {
            carregando -> {
                CircularProgressIndicator()
                Text("Buscando equipamentos no servidor...")
            }

            erro != null -> {
                Text(
                    text = erro ?: "",
                    color = Color(0xFFB3261E)
                )
            }

            equipamentos.isEmpty() -> {
                Text(
                    text = "Nenhum equipamento encontrado.",
                    color = Color.Gray
                )
            }

            else -> {
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
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = equipamento.nome,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Text(
                                text = "ID: ${equipamento.id}",
                                fontSize = 13.sp
                            )

                            Text(
                                text = "Identificação: ${equipamento.identificacao ?: "Não informada"}",
                                fontSize = 13.sp
                            )

                            Text(
                                text = "Tipo: ${equipamento.tipo ?: "Não informado"}",
                                fontSize = 13.sp
                            )

                            Text(
                                text = "Local: ${equipamento.localizacao ?: "Não informada"}",
                                fontSize = 13.sp
                            )

                            Text(
                                text = "Status: ${equipamento.status}",
                                fontSize = 13.sp,
                                color = Color(0xFF238653)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
