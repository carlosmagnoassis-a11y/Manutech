
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
import com.example.manutech.model.Equipamento
import com.example.manutech.model.Manutencao
import com.example.manutech.model.ManutencaoRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Azul = Color(0xFF164A8A)
private val Fundo = Color(0xFFF3F6FA)
private val Verde = Color(0xFF238653)
private val Vermelho = Color(0xFFB3261E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaManutencaoScreen(
    onVoltar: () -> Unit,
    onRegistrar: (Manutencao) -> Unit
) {
    val scope = rememberCoroutineScope()

    var equipamentos by remember {
        mutableStateOf<List<Equipamento>>(emptyList())
    }

    var equipamentoSelecionado by remember {
        mutableStateOf<Equipamento?>(null)
    }

    var problema by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    var data by remember {
        mutableStateOf(
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())
        )
    }

    var expandido by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(true) }
    var salvando by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }
    var registrado by remember { mutableStateOf(false) }
    var erroEquipamentos by remember { mutableStateOf(false) }
    var atualizarEquipamentos by remember { mutableIntStateOf(0) }

    // CONSULTA OS EQUIPAMENTOS NO MYSQL
    LaunchedEffect(atualizarEquipamentos) {
        carregando = true
        erroEquipamentos = false

        try {
            val resposta =
                RetrofitClient.apiService.listarEquipamentos()

            if (resposta.isSuccessful) {
                equipamentos = resposta.body().orEmpty()
            } else {
                erroEquipamentos = true
                mensagem = "Erro ao consultar equipamentos."
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                "MANUTECH_API",
                "Erro ao consultar equipamentos",
                e
            )
            erroEquipamentos = true
            mensagem = "Falha na comunicação com a API."
        } finally {
            carregando = false
        }
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
                enabled = !salvando,
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                        text = "＋",
                        color = Azul,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = "Nova Manutenção",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Text(
                        text = "Registre um problema ou serviço.",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }

            // FORMULÁRIO
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Dados da manutenção",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Text(
                        text = "Preencha os dados abaixo para registrar uma nova manutenção.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    // EQUIPAMENTO
                    if (carregando) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Azul,
                                strokeWidth = 2.dp
                            )
                            Text("Carregando equipamentos...")
                        }
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = expandido,
                            onExpandedChange = {
                                if (
                                    !salvando &&
                                    equipamentos.isNotEmpty() &&
                                    !erroEquipamentos
                                ) {
                                    expandido = !expandido
                                }
                            }
                        ) {
                            OutlinedTextField(
                                value = equipamentoSelecionado?.let {
                                    "${it.nome} - ${it.identificacao ?: ""}"
                                } ?: "",
                                onValueChange = {},
                                readOnly = true,
                                enabled = !salvando &&
                                        equipamentos.isNotEmpty() &&
                                        !erroEquipamentos,
                                label = {
                                    Text("Equipamento *")
                                },
                                placeholder = {
                                    Text("Selecione um equipamento")
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults
                                        .TrailingIcon(
                                            expanded = expandido
                                        )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = expandido,
                                onDismissRequest = {
                                    expandido = false
                                }
                            ) {
                                equipamentos.forEach { item ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "${item.nome} - " +
                                                        "${item.identificacao ?: ""}"
                                            )
                                        },
                                        onClick = {
                                            equipamentoSelecionado = item
                                            expandido = false
                                            mensagem = ""
                                        }
                                    )
                                }
                            }
                        }

                        if (
                            equipamentos.isEmpty() &&
                            !erroEquipamentos
                        ) {
                            Text(
                                text = "Nenhum equipamento disponível. Cadastre um equipamento primeiro.",
                                fontSize = 13.sp,
                                color = Vermelho
                            )
                        }

                        if (erroEquipamentos) {
                            OutlinedButton(
                                onClick = {
                                    mensagem = ""
                                    atualizarEquipamentos++
                                },
                                enabled = !salvando,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Tentar novamente")
                            }
                        }
                    }

                    // PROBLEMA
                    OutlinedTextField(
                        value = problema,
                        onValueChange = {
                            problema = it
                            mensagem = ""
                        },
                        label = {
                            Text("Problema identificado *")
                        },
                        placeholder = {
                            Text("Ex.: Falha de conectividade")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !salvando,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // DATA
                    OutlinedTextField(
                        value = data,
                        onValueChange = {
                            data = it
                            mensagem = ""
                        },
                        label = {
                            Text("Data (AAAA-MM-DD) *")
                        },
                        supportingText = {
                            Text("A data atual é preenchida automaticamente.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !salvando,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // DESCRIÇÃO
                    OutlinedTextField(
                        value = descricao,
                        onValueChange = {
                            descricao = it
                            mensagem = ""
                        },
                        label = {
                            Text("Descrição da ocorrência")
                        },
                        placeholder = {
                            Text("Descreva o que aconteceu...")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        enabled = !salvando,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text(
                        text = "* Campos obrigatórios",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    // STATUS INICIAL
                    Surface(
                        color = Color(0xFFFFF5E6),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "●",
                                color = Color(0xFFB7791F)
                            )

                            Text(
                                text = "Status inicial: Aberta",
                                color = Color(0xFF9A6700),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // MENSAGEM
                    if (mensagem.isNotEmpty()) {
                        Text(
                            text = mensagem,
                            color = if (registrado) {
                                Verde
                            } else {
                                Vermelho
                            },
                            fontSize = 13.sp
                        )
                    }

                    // BOTÃO REGISTRAR
                    Button(
                        onClick = {
                            val selecionado = equipamentoSelecionado
                            val problemaLimpo = problema.trim()
                            val descricaoLimpa = descricao.trim()
                            val dataLimpa = data.trim()

                            val formatoData = Regex(
                                "\\d{4}-\\d{2}-\\d{2}"
                            )

                            val dataValida = try {
                                val formato = SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    Locale.getDefault()
                                )
                                formato.isLenient = false

                                formato.parse(dataLimpa) != null &&
                                        formatoData.matches(dataLimpa)
                            } catch (e: Exception) {
                                false
                            }

                            when {
                                selecionado == null ||
                                        problemaLimpo.isBlank() ||
                                        dataLimpa.isBlank() -> {
                                    registrado = false
                                    mensagem =
                                        "Preencha todos os campos obrigatórios."
                                }

                                problemaLimpo.length > 200 -> {
                                    registrado = false
                                    mensagem =
                                        "O problema deve ter no máximo 200 caracteres."
                                }

                                !dataValida -> {
                                    registrado = false
                                    mensagem =
                                        "Informe uma data válida no formato AAAA-MM-DD."
                                }

                                else -> {
                                    salvando = true
                                    mensagem = ""
                                    registrado = false

                                    scope.launch {
                                        try {
                                            val requisicao =
                                                ManutencaoRequest(
                                                    id_equipamento =
                                                        selecionado.id,
                                                    problema =
                                                        problemaLimpo,
                                                    descricao =
                                                        descricaoLimpa,
                                                    data = dataLimpa
                                                )

                                            val resposta =
                                                RetrofitClient.apiService
                                                    .cadastrarManutencao(
                                                        requisicao
                                                    )

                                            if (
                                                resposta.isSuccessful &&
                                                resposta.body()?.sucesso == true
                                            ) {
                                                val idGerado =
                                                    resposta.body()?.id ?: 0

                                                registrado = true
                                                mensagem =
                                                    "Manutenção cadastrada com sucesso!"

                                                val novaManutencao =
                                                    Manutencao(
                                                        id = idGerado,
                                                        equipamento =
                                                            selecionado.nome,
                                                        problema =
                                                            problemaLimpo,
                                                        data = dataLimpa,
                                                        descricao =
                                                            descricaoLimpa,
                                                        status = "Aberta",
                                                        id_equipamento =
                                                            selecionado.id
                                                    )

                                                onRegistrar(novaManutencao)
                                            } else {
                                                registrado = false
                                                mensagem =
                                                    "Não foi possível cadastrar a manutenção. Código HTTP: ${resposta.code()}"
                                            }

                                        } catch (e: CancellationException) {
                                            throw e
                                        } catch (e: Exception) {
                                            Log.e(
                                                "MANUTECH_API",
                                                "Erro ao registrar manutenção",
                                                e
                                            )
                                            registrado = false
                                            mensagem =
                                                "Falha na comunicação com a API."
                                        } finally {
                                            salvando = false
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = !salvando &&
                                !carregando &&
                                equipamentos.isNotEmpty() &&
                                !erroEquipamentos,
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
                                text = "＋  Registrar manutenção",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
