
package com.example.manutech.model

data class Manutencao(
    val id: Int,
    val equipamento: String,
    val problema: String,
    val data: String,
    val descricao: String,
    var status: String = "Aberta",
    val id_equipamento: Int = 0
)
