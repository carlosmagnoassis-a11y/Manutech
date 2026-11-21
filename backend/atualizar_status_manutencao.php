
<?php
date_default_timezone_set("America/Sao_Paulo");

header("Content-Type: application/json; charset=UTF-8");

require_once "conexao.php";

if ($_SERVER["REQUEST_METHOD"] != "POST") {
    http_response_code(405);
    echo json_encode(array(
        "sucesso" => false,
        "erro" => "Metodo nao permitido. Utilize POST."
    ));
    exit;
}

$dados = json_decode(file_get_contents("php://input"), true);

if (!is_array($dados)) {
    http_response_code(400);
    echo json_encode(array(
        "sucesso" => false,
        "erro" => "JSON invalido."
    ));
    exit;
}

$id = isset($dados["id"]) ? (int) $dados["id"] : 0;
$status = isset($dados["status"])
    ? trim($dados["status"]) : "";

$statusPermitidos = array(
    "Aberta",
    "Em andamento",
    "Concluída"
);

if ($id <= 0 || !in_array($status, $statusPermitidos, true)) {
    http_response_code(400);
    echo json_encode(array(
        "sucesso" => false,
        "erro" => "ID ou status invalido."
    ));
    exit;
}

try {
    $consulta = $pdo->prepare(
        "SELECT id FROM manutencoes WHERE id = :id"
    );
    $consulta->execute(array(":id" => $id));

    if (!$consulta->fetch(PDO::FETCH_ASSOC)) {
        http_response_code(404);
        echo json_encode(array(
            "sucesso" => false,
            "erro" => "Manutencao nao encontrada."
        ));
        exit;
    }

    $sql = "UPDATE manutencoes
            SET status = :status
            WHERE id = :id";

    $stmt = $pdo->prepare($sql);

    $stmt->execute(array(
        ":status" => $status,
        ":id" => $id
    ));

    echo json_encode(array(
        "sucesso" => true,
        "mensagem" => "Status atualizado com sucesso.",
        "id" => $id,
        "status" => $status
    ));

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(array(
        "sucesso" => false,
        "erro" => "Erro ao atualizar status."
    ));
}
?>
