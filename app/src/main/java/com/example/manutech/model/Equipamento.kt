
package com.example.manutech.model

data class Equipamento(
    val id: Int,
    val nome: String,
    val identificacao: String?,
    val tipo: String?,
    val localizacao: String?,
    val status: String
)
