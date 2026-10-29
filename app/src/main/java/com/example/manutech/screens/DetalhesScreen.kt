
package com.example.manutech.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manutech.model.Manutencao

@Composable
fun DetalhesManutencaoScreen(
    manutencao: Manutencao,
    onVoltar: () -> Unit,
    onAlterarStatus: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onVoltar) {
            Text("← Voltar")
        }

        Text(
            text = "Detalhes da manutenção",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF164A8A)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = manutencao.equipamento,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text("Código: ${manutencao.id}")
                Text("Problema: ${manutencao.problema}")
                Text("Data: ${manutencao.data}")

                Text(
                    text = "Descrição",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = manutencao.descricao.ifBlank {
                        "Nenhuma descrição informada."
                    }
                )

                Text(
                    text = "Status: ${manutencao.status}",
                    fontWeight = FontWeight.Bold,
                    color = when (manutencao.status) {
                        "Aberta" -> Color(0xFFB7791F)
                        "Em andamento" -> Color(0xFF2878D0)
                        "Concluída" -> Color(0xFF238653)
                        else -> Color.DarkGray
                    }
                )
            }
        }

        Text(
            text = "Alterar status",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = { onAlterarStatus("Aberta") },
            modifier = Modifier.fillMaxWidth(),
            enabled = manutencao.status != "Aberta"
        ) {
            Text("Aberta")
        }

        Button(
            onClick = { onAlterarStatus("Em andamento") },
            modifier = Modifier.fillMaxWidth(),
            enabled = manutencao.status != "Em andamento"
        ) {
            Text("Em andamento")
        }

        OutlinedButton(
            onClick = { onAlterarStatus("Concluída") },
            modifier = Modifier.fillMaxWidth(),
            enabled = manutencao.status != "Concluída"
        ) {
            Text("Concluída")
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}


