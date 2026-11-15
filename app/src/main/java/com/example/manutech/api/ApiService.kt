
package com.example.manutech.api

import com.example.manutech.model.Equipamento
import com.example.manutech.model.EquipamentoRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // Consultar equipamentos cadastrados
    @GET("listar_equipamentos.php")
    suspend fun listarEquipamentos(): Response<List<Equipamento>>

    // Cadastrar um novo equipamento
    @POST("cadastrar_equipamento.php")
    suspend fun cadastrarEquipamento(
        @Body equipamento: EquipamentoRequest
    ): Response<CadastroResponse>
}
