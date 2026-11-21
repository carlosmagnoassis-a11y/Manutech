
package com.example.manutech.model

data class ManutencaoRequest(
    val id_equipamento: Int,
    val problema: String,
    val descricao: String,
    val data: String
)


