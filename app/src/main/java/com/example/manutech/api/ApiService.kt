
package com.example.manutech.api

import com.example.manutech.model.Equipamento
import com.example.manutech.model.EquipamentoRequest
import com.example.manutech.model.Manutencao
import com.example.manutech.model.ManutencaoRequest
import com.example.manutech.model.AtualizarStatusRequest
import com.example.manutech.model.ExcluirEquipamentoRequest

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

    // Consultar manutencoes cadastradas no MySQL
    @GET("listar_manutencoes.php")
    suspend fun listarManutencoes(): Response<List<Manutencao>>

    // Cadastrar uma nova manutencao no MySQL
    @POST("cadastrar_manutencao.php")
    suspend fun cadastrarManutencao(
        @Body manutencao: ManutencaoRequest
    ): Response<CadastroResponse>
    @POST("atualizar_status_manutencao.php")
    suspend fun atualizarStatusManutencao(
        @Body dados: AtualizarStatusRequest
    ): Response<CadastroResponse>


    @POST("excluir_equipamento.php")
    suspend fun excluirEquipamento(
        @Body dados: ExcluirEquipamentoRequest
    ): Response<CadastroResponse>

}
