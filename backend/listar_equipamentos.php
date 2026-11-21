
<?php

header("Content-Type: application/json; charset=UTF-8");

require_once "conexao.php";

if ($_SERVER["REQUEST_METHOD"] !== "GET") {
    header("Allow: GET");
    http_response_code(405);

    echo json_encode([
        "erro" => "Método não permitido."
    ]);

    exit;
}

try {

    $sql = "SELECT id, nome, identificacao, tipo, localizacao, status
            FROM equipamentos
            ORDER BY id ASC";

    $stmt = $pdo->query($sql);

    $equipamentos = $stmt->fetchAll(
        PDO::FETCH_ASSOC
    );

    echo json_encode(
        $equipamentos,
        JSON_UNESCAPED_UNICODE
    );

} catch (PDOException $e) {

    error_log($e->getMessage());

    http_response_code(500);

    echo json_encode([
        "erro" => "Não foi possível consultar os equipamentos."
    ]);
}
