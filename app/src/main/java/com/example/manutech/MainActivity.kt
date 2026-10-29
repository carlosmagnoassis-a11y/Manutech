
package com.example.manutech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Fundo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Cabeçalho
            Text(
                text = "MANUTECH",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = AzulManutech
            )

            Text(
                text = "Controle de Manutenção",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Olá, seja bem-vindo!",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = TextoEscuro
            )

            Text(
                text = "Acompanhe suas manutenções.",
                fontSize = 15.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Resumo das manutenções
            Text(
                text = "Resumo das manutenções",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextoEscuro
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CartaoStatus(
                    titulo = "Abertas",
                    quantidade = "05",
                    cor = Color(0xFFE59A19),
                    modifier = Modifier.weight(1f)
                )

                CartaoStatus(
                    titulo = "Em andamento",
                    quantidade = "03",
                    cor = Color(0xFF2878D0),
                    modifier = Modifier.weight(1f)
                )

                CartaoStatus(
                    titulo = "Concluídas",
                    quantidade = "12",
                    cor = Color(0xFF238653),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Acesso rápido",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextoEscuro
            )

            Spacer(modifier = Modifier.height(12.dp))

            BotaoMenu(
                texto = "Consultar equipamentos",
                descricao = "Visualizar equipamentos cadastrados",
                onClick = onEquipamentosClick
            )

            BotaoMenu(
                texto = "Nova manutenção",
                descricao = "Registrar um problema ou serviço",
                onClick = onNovaManutencaoClick
            )

            BotaoMenu(
                texto = "Histórico de manutenções",
                descricao = "Consultar serviços anteriores",
                onClick = onHistoricoClick
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Manutenções recentes",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = TextoEscuro
            )

            Spacer(modifier = Modifier.height(12.dp))

            CartaoManutencao(
                equipamento = "Servidor de Rede",
                problema = "Verificar conectividade",
                status = "Em andamento",
                corStatus = Color(0xFF2878D0)
            )

            CartaoManutencao(
                equipamento = "Switch de Rede",
                problema = "Falha em uma porta",
                status = "Aberta",
                corStatus = Color(0xFFE59A19)
            )

            Spacer(modifier = Modifier.height(20.dp))

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
        shape = RoundedCornerShape(12.dp),
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
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = quantidade,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = cor
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = titulo,
                fontSize = 11.sp,
                color = TextoEscuro
            )
        }
    }
}

@Composable
fun BotaoMenu(
    texto: String,
    descricao: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = texto,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoEscuro
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = descricao,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulManutech
                )
            ) {
                Text(
                    text = "Abrir",
                    fontSize = 12.sp
                )
            }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = equipamento,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextoEscuro
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = problema,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Status: $status",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = corStatus
            )
        }
    }
}
