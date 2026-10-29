
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manutech.model.Manutencao

@Composable
fun HistoricoScreen(
    manutencoes: List<Manutencao>,
    onVoltar: () -> Unit,
    onDetalhesClick: (Int) -> Unit
) {
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
            text = "Histórico",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF164A8A)
        )

        Text(
            text = "Consulte as manutenções registradas.",
            color = Color.DarkGray
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Registros encontrados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${manutencoes.size} registros",
                color = Color(0xFF164A8A),
                fontSize = 13.sp
            )
        }

        if (manutencoes.isEmpty()) {
            Text(
                text = "Nenhuma manutenção cadastrada ainda.",
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        } else {
            manutencoes.forEach { manutencao ->

                val corStatus = when (manutencao.status) {
                    "Aberta" -> Color(0xFFB7791F)
                    "Em andamento" -> Color(0xFF2878D0)
                    "Concluída" -> Color(0xFF238653)
                    else -> Color.DarkGray
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = manutencao.equipamento,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
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

                        Text(
                            text = "Status: ${manutencao.status}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = corStatus
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = { onDetalhesClick(manutencao.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver detalhes")
                        }
                    }
                }
            }
        }
    }
}



