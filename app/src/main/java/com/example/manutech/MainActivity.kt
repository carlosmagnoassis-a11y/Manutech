
package com.example.manutech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.manutech.api.RetrofitClient
import com.example.manutech.model.Manutencao

private val AzulManutech = Color(0xFF164A8A)
private val Fundo = Color(0xFFF3F6FA)
private val TextoEscuro = Color(0xFF1D2939)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun TelaInicial(
    onEquipamentosClick: () -> Unit = {},
    onNovaManutencaoClick: () -> Unit = {},
    onHistoricoClick: () -> Unit = {}
) {
    var manutencoes by remember {
        mutableStateOf<List<Manutencao>>(emptyList())
    }

    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf("") }
    var atualizar by remember { mutableIntStateOf(0) }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                atualizar++
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(atualizar) {
        carregando = true
        erro = ""

        try {
            val resposta =
                RetrofitClient.apiService.listarManutencoes()

            if (resposta.isSuccessful) {
                manutencoes = resposta.body().orEmpty()
            } else {
                erro = "Não foi possível carregar os dados."
            }
        } catch (e: Exception) {
            erro = "Falha na conexão com o servidor."
        } finally {
            carregando = false
        }
    }

    val abertas = manutencoes.count {
        it.status == "Aberta"
    }

    val andamento = manutencoes.count {
        it.status == "Em andamento"
    }

    val concluidas = manutencoes.count {
        it.status == "Concluída"
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // CABEÇALHO

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AzulManutech
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⚙  MANUTECH",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Controle de Manutenção",
                        color = Color(0xFFDDEAF8),
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Olá, seja bem-vindo!",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Gerencie equipamentos e acompanhe seus serviços.",
                        color = Color(0xFFE5EDF7),
                        fontSize = 14.sp
                    )
                }
            }

            // RESUMO

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Resumo das manutenções",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoEscuro,
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = { atualizar++ },
                    enabled = !carregando
                ) {
                    Text("Atualizar")
                }
            }

            if (carregando) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (erro.isNotEmpty()) {
                Text(
                    text = erro,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CartaoStatus(
                    titulo = "Abertas",
                    quantidade = if (carregando || erro.isNotEmpty()) {
                        "—"
                    } else {
                        abertas.toString()
                    },
                    cor = Color(0xFFB7791F),
                    modifier = Modifier.weight(1f)
                )

                CartaoStatus(
                    titulo = "Em andamento",
                    quantidade = if (carregando || erro.isNotEmpty()) {
                        "—"
                    } else {
                        andamento.toString()
                    },
                    cor = Color(0xFF2878D0),
                    modifier = Modifier.weight(1f)
                )

                CartaoStatus(
                    titulo = "Concluídas",
                    quantidade = if (carregando || erro.isNotEmpty()) {
                        "—"
                    } else {
                        concluidas.toString()
                    },
                    cor = Color(0xFF238653),
                    modifier = Modifier.weight(1f)
                )
            }

            // ACESSO RÁPIDO

            Text(
                text = "Acesso rápido",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextoEscuro
            )

            BotaoMenu(
                texto = "Consultar equipamentos",
                descricao = "Visualizar equipamentos cadastrados",
                icone = "▣",
                onClick = onEquipamentosClick
            )

            BotaoMenu(
                texto = "Nova manutenção",
                descricao = "Registrar um problema ou serviço",
                icone = "+",
                onClick = onNovaManutencaoClick
            )

            BotaoMenu(
                texto = "Histórico de manutenções",
                descricao = "Consultar serviços registrados",
                icone = "☷",
                onClick = onHistoricoClick
            )

            // MANUTENÇÕES RECENTES

            Text(
                text = "Manutenções recentes",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextoEscuro
            )

            if (!carregando && erro.isEmpty()) {
                if (manutencoes.isEmpty()) {
                    Text(
                        text = "Nenhuma manutenção cadastrada.",
                        color = Color.Gray
                    )
                } else {
                    manutencoes
                        .sortedByDescending { it.id }
                        .take(3)
                        .forEach { manutencao ->
                            CartaoManutencao(
                                equipamento = manutencao.equipamento,
                                problema = manutencao.problema,
                                status = manutencao.status,
                                corStatus = when (manutencao.status) {
                                    "Aberta" -> Color(0xFFB7791F)
                                    "Em andamento" -> Color(0xFF2878D0)
                                    "Concluída" -> Color(0xFF238653)
                                    else -> Color.DarkGray
                                }
                            )
                        }
                }
            }

            Text(
                text = "Manutech • Controle de Manutenção",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CartaoStatus(
    titulo: String,
    quantidade: String,
    cor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(112.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = quantidade,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = cor
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = titulo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextoEscuro
            )
        }
    }
}

@Composable
fun BotaoMenu(
    texto: String,
    descricao: String,
    icone: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFFEAF1F9),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icone,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulManutech
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = texto,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoEscuro
                )

                Text(
                    text = descricao,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = "›",
                fontSize = 28.sp,
                color = AzulManutech
            )
        }
    }
}

@Composable
fun CartaoManutencao(
    equipamento: String,
    problema: String,
    status: String,
    corStatus: Color
) {
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
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(
                text = equipamento,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextoEscuro
            )

            Text(
                text = problema,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Text(
                text = "●  $status",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = corStatus
            )
        }
    }
}
