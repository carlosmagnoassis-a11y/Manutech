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
import com.example.manutech.model.EquipamentoRequest
import com.example.manutech.model.ExcluirEquipamentoRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private val Azul = Color(0xFF164A8A)
private val Fundo = Color(0xFFF3F6FA)
private val Verde = Color(0xFF238653)
private val Vermelho = Color(0xFFB3261E)

@Composable
fun EquipamentosScreen(
    onVoltar: () -> Unit
) {
    var equipamentos by remember {
        mutableStateOf<List<Equipamento>>(emptyList())
    }

    var carregando by remember { mutableStateOf(true) }
    var salvando by remember { mutableStateOf(false) }
    var excluindo by remember { mutableStateOf(false) }

    var erro by remember { mutableStateOf<String?>(null) }
    var mensagemCadastro by remember { mutableStateOf("") }
    var cadastroSucesso by remember { mutableStateOf(false) }
    var mensagemExclusao by remember { mutableStateOf("") }
    var exclusaoSucesso by remember { mutableStateOf(false) }

    var equipamentoParaExcluir by remember {
        mutableStateOf<Equipamento?>(null)
    }

    var nome by remember { mutableStateOf("") }
    var identificacao by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf("") }

    var pesquisa by remember { mutableStateOf("") }

    val equipamentosFiltrados = equipamentos.filter { equipamento ->
        val termo = pesquisa.trim()
        termo.isEmpty() || listOf(
            equipamento.nome,
            equipamento.identificacao.orEmpty(),
            equipamento.tipo.orEmpty(),
            equipamento.localizacao.orEmpty()
        ).any { it.contains(termo, ignoreCase = true) }
    }

    var atualizar by remember { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()

    // CONSULTAR EQUIPAMENTOS NO MYSQL
    LaunchedEffect(atualizar) {
        carregando = true
        erro = null

        try {
            val resposta =
                RetrofitClient.apiService.listarEquipamentos()

            if (resposta.isSuccessful) {
                equipamentos = resposta.body().orEmpty()
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
                        text = "▣",
                        color = Azul,
                        fontSize = 27.sp
                    )
                }

                Column {
                    Text(
                        text = "Equipamentos",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    Text(
                        text = "Gerencie os ativos cadastrados.",
                        color = Color.DarkGray,
                        fontSize = 13.sp
                    )
                }
            }

            // FORMULÁRIO DE CADASTRO
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "＋  Cadastrar equipamento",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Azul
                    )

                    OutlinedTextField(
                        value = nome,
                        onValueChange = {
                            nome = it
                            mensagemCadastro = ""
                        },
                        label = { Text("Nome do equipamento") },
                        placeholder = { Text("Ex.: Switch Cisco") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !salvando,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = identificacao,
                        onValueChange = {
                            identificacao = it
                            mensagemCadastro = ""
                        },
                        label = { Text("Identificação") },
                        placeholder = { Text("Ex.: SW-002") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !salvando,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = tipo,
                        onValueChange = {
                            tipo = it
                            mensagemCadastro = ""
                        },
                        label = { Text("Tipo do equipamento") },
                        placeholder = { Text("Ex.: Switch") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !salvando,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = localizacao,
                        onValueChange = {
                            localizacao = it
                            mensagemCadastro = ""
                        },
                        label = { Text("Localização") },
                        placeholder = {
                            Text("Ex.: Sala de Telecom")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !salvando,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (mensagemCadastro.isNotEmpty()) {
                        Text(
                            text = mensagemCadastro,
                            color = if (cadastroSucesso) {
                                Verde
                            } else {
                                Vermelho
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
                                    cadastroSucesso = false
                                    mensagemCadastro =
                                        "Preencha nome, identificação e localização."
                                }

                                nomeLimpo.length > 100 ||
                                        identificacaoLimpa.length > 50 ||
                                        tipoLimpo.length > 100 ||
                                        localizacaoLimpa.length > 150 -> {
                                    cadastroSucesso = false
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

                                            if (
                                                resposta.isSuccessful &&
                                                resposta.body()?.sucesso == true
                                            ) {
                                                cadastroSucesso = true
                                                mensagemCadastro =
                                                    "Equipamento cadastrado com sucesso!"

                                                nome = ""
                                                identificacao = ""
                                                tipo = ""
                                                localizacao = ""
                                                atualizar++

                                            } else if (
                                                resposta.code() == 409
                                            ) {
                                                cadastroSucesso = false
                                                mensagemCadastro =
                                                    "Essa identificação já está cadastrada."

                                            } else {
                                                cadastroSucesso = false
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

                                            cadastroSucesso = false
                                            mensagemCadastro =
                                                "Não foi possível conectar à API."

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
                        enabled = !salvando,
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
                                text = "＋  Cadastrar equipamento",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // MENSAGEM DE EXCLUSÃO
            if (mensagemExclusao.isNotEmpty()) {
                Text(
                    text = mensagemExclusao,
                    color = if (exclusaoSucesso) Verde else Vermelho,
                    fontSize = 13.sp
                )
            }

            // LISTA DE EQUIPAMENTOS
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Equipamentos cadastrados",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = { atualizar++ },
                    enabled = !carregando
                ) {
                    Text("Atualizar")
                }
            }

            // PESQUISA DE EQUIPAMENTOS
            OutlinedTextField(
                value = pesquisa,
                onValueChange = { pesquisa = it },
                label = { Text("Pesquisar equipamento") },
                placeholder = { Text("Nome, identificação, tipo ou local") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    if (pesquisa.isNotEmpty()) {
                        TextButton(onClick = { pesquisa = "" }) {
                            Text("Limpar")
                        }
                    }
                }
            )

            if (!carregando && erro == null) {
                Text(
                    text = "${equipamentosFiltrados.size} de ${equipamentos.size} equipamentos encontrados",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            when {
                carregando -> {
                    CircularProgressIndicator(color = Azul)
                    Text("Buscando equipamentos no servidor...")
                }

                erro != null -> {
                    Text(
                        text = erro ?: "",
                        color = Vermelho
                    )
                }

                equipamentosFiltrados.isEmpty() -> {
                    Text(
                        text = if (equipamentos.isEmpty()) {
                            "Nenhum equipamento cadastrado."
                        } else {
                            "Nenhum equipamento corresponde à pesquisa."
                        },
                        color = Color.Gray
                    )
                }

                else -> {
                    equipamentosFiltrados.forEach { equipamento ->

                        val corStatus =
                            if (equipamento.status == "Ativo") {
                                Verde
                            } else {
                                Color(0xFFB7791F)
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
                                    Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = equipamento.nome,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = corStatus.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = equipamento.status,
                                            color = corStatus,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 5.dp
                                            )
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    color = Color(0xFFE9EDF2)
                                )

                                Text(
                                    text = "ID: ${equipamento.id}",
                                    fontSize = 13.sp,
                                    color = Color.DarkGray
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

                                HorizontalDivider(
                                    color = Color(0xFFE9EDF2)
                                )

                                OutlinedButton(
                                    onClick = {
                                        mensagemExclusao = ""
                                        equipamentoParaExcluir = equipamento
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !excluindo,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Vermelho),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Vermelho
                                    )
                                ) {
                                    Text(
                                        text = "Excluir equipamento",
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

    // JANELA DE CONFIRMAÇÃO
    val equipamentoSelecionado = equipamentoParaExcluir

    if (equipamentoSelecionado != null) {
        AlertDialog(
            onDismissRequest = {
                if (!excluindo) {
                    equipamentoParaExcluir = null
                }
            },
            title = {
                Text("Confirmar exclusão")
            },
            text = {
                Text(
                    "Deseja realmente excluir o equipamento " +
                            "\"${equipamentoSelecionado.nome}\"?\n\n" +
                            "Essa ação não poderá ser desfeita."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        excluindo = true

                        scope.launch {
                            try {
                                val resposta =
                                    RetrofitClient.apiService
                                        .excluirEquipamento(
                                            ExcluirEquipamentoRequest(
                                                id = equipamentoSelecionado.id
                                            )
                                        )

                                if (
                                    resposta.isSuccessful &&
                                    resposta.body()?.sucesso == true
                                ) {
                                    exclusaoSucesso = true
                                    mensagemExclusao =
                                        "Equipamento excluído com sucesso!"

                                    equipamentoParaExcluir = null
                                    atualizar++

                                } else if (resposta.code() == 409) {
                                    exclusaoSucesso = false
                                    mensagemExclusao =
                                        "Não é possível excluir: " +
                                                "o equipamento possui manutenções vinculadas."

                                    equipamentoParaExcluir = null

                                } else {
                                    exclusaoSucesso = false
                                    mensagemExclusao =
                                        "Erro ao excluir equipamento. " +
                                                "Código HTTP: ${resposta.code()}"

                                    equipamentoParaExcluir = null
                                }

                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                Log.e(
                                    "MANUTECH_API",
                                    "Erro ao excluir equipamento",
                                    e
                                )

                                exclusaoSucesso = false
                                mensagemExclusao =
                                    "Falha na comunicação com a API."

                                equipamentoParaExcluir = null

                            } finally {
                                excluindo = false
                            }
                        }
                    },
                    enabled = !excluindo,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Vermelho
                    )
                ) {
                    Text(
                        if (excluindo) "Excluindo..." else "Excluir"
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        equipamentoParaExcluir = null
                    },
                    enabled = !excluindo
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
