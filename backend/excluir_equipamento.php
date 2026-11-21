
<?php
header("Content-Type: application/json; charset=UTF-8");

require_once "conexao.php";

// Permite somente requisições POST
if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    http_response_code(405);

    echo json_encode(array(
        "sucesso" => false,
        "mensagem" => "Método não permitido."
    ));
    exit;
}

// Recebe os dados enviados pelo aplicativo
$dados = json_decode(file_get_contents("php://input"), true);

$id = isset($dados["id"]) ? $dados["id"] : null;

// Valida o ID
if (filter_var($id, FILTER_VALIDATE_INT) === false ||
    (int)$id <= 0) {

    http_response_code(400);

    echo json_encode(array(
        "sucesso" => false,
        "mensagem" => "ID do equipamento inválido."
    ));
    exit;
}

$id = (int)$id;

try {
    // Verifica se o equipamento existe
    $sql = "SELECT id FROM equipamentos WHERE id = :id";
    $stmt = $pdo->prepare($sql);
    $stmt->execute(array(":id" => $id));

    if (!$stmt->fetch(PDO::FETCH_ASSOC)) {
        http_response_code(404);

        echo json_encode(array(
            "sucesso" => false,
            "mensagem" => "Equipamento não encontrado."
        ));
        exit;
    }

    // Impede exclusão se houver manutenções vinculadas
    $sql = "SELECT COUNT(*) FROM manutencoes
            WHERE id_equipamento = :id";

    $stmt = $pdo->prepare($sql);
    $stmt->execute(array(":id" => $id));

    $total = (int)$stmt->fetchColumn();

    if ($total > 0) {
        http_response_code(409);

        echo json_encode(array(
            "sucesso" => false,
            "mensagem" =>
                "Este equipamento possui manutenções vinculadas e não pode ser excluído."
        ));
        exit;
    }

    // Exclui o equipamento
    $sql = "DELETE FROM equipamentos WHERE id = :id";
    $stmt = $pdo->prepare($sql);
    $stmt->execute(array(":id" => $id));

    echo json_encode(array(
        "sucesso" => true,
        "mensagem" => "Equipamento excluído com sucesso."
    ));

} catch (PDOException $e) {
    // Evita excluir equipamentos com vínculos no banco
    if ($e->getCode() == "23000") {
        http_response_code(409);

        echo json_encode(array(
            "sucesso" => false,
            "mensagem" =>
                "Não é possível excluir este equipamento porque existem registros vinculados."
        ));
    } else {
        http_response_code(500);

        echo json_encode(array(
            "sucesso" => false,
            "mensagem" => "Erro ao excluir equipamento."
        ));
    }
}
?>
