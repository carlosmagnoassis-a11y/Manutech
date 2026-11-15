
package com.example.manutech.api

data class CadastroResponse(
    val sucesso: Boolean = false,
    val mensagem: String? = null,
    val id: Int? = null,
    val erro: String? = null
)
