
<?php
date_default_timezone_set("America/Sao_Paulo");

header("Content-Type: application/json; charset=UTF-8");

require_once "conexao.php";

if ($_SERVER["REQUEST_METHOD"] != "GET") {
    http_response_code(405);

    echo json_encode(array(
        "sucesso" => false,
        "erro" => "Metodo nao permitido. Utilize GET."
    ));
    exit;
}

try {
    $sql = "SELECT
                m.id,
                m.id_equipamento,
                e.nome AS equipamento,
                m.problema,
                m.descricao,
                m.data,
                m.status
            FROM manutencoes m
            INNER JOIN equipamentos e
                ON m.id_equipamento = e.id
            ORDER BY m.id DESC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute();

    $manutencoes = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($manutencoes);

} catch (PDOException $e) {
    http_response_code(500);

    echo json_encode(array(
        "sucesso" => false,
        "erro" => "Erro ao consultar manutencoes."
    ));
}
?>
