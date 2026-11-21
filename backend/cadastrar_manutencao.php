
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

$id_equipamento = isset($dados["id_equipamento"])
    ? (int) $dados["id_equipamento"] : 0;

$problema = isset($dados["problema"])
    ? trim($dados["problema"]) : "";

$descricao = isset($dados["descricao"])
    ? trim($dados["descricao"]) : "";

$data = isset($dados["data"])
    ? trim($dados["data"]) : "";

if ($id_equipamento <= 0 || $problema == "" || $data == "") {
    http_response_code(400);
    echo json_encode(array(
        "sucesso" => false,
        "erro" => "Preencha os campos obrigatorios."
    ));
    exit;
}

$dataObj = DateTime::createFromFormat("!Y-m-d", $data);

if (!$dataObj || $dataObj->format("Y-m-d") != $data) {
    http_response_code(400);
    echo json_encode(array(
        "sucesso" => false,
        "erro" => "Data invalida. Utilize AAAA-MM-DD."
    ));
    exit;
}

if (strlen($problema) > 200) {
    http_response_code(400);
    echo json_encode(array(
        "sucesso" => false,
        "erro" => "O problema deve ter no maximo 200 caracteres."
    ));
    exit;
}

try {
    $consulta = $pdo->prepare(
        "SELECT id FROM equipamentos WHERE id = :id"
    );

    $consulta->execute(array(
        ":id" => $id_equipamento
    ));

    if (!$consulta->fetch(PDO::FETCH_ASSOC)) {
        http_response_code(400);
        echo json_encode(array(
            "sucesso" => false,
            "erro" => "Equipamento nao encontrado."
        ));
        exit;
    }

    $sql = "INSERT INTO manutencoes
            (id_equipamento, problema, descricao, data, status)
            VALUES
            (:id_equipamento, :problema, :descricao, :data, 'Aberta')";

    $stmt = $pdo->prepare($sql);

    $stmt->execute(array(
        ":id_equipamento" => $id_equipamento,
        ":problema" => $problema,
        ":descricao" => $descricao,
        ":data" => $data
    ));

    http_response_code(201);

    echo json_encode(array(
        "sucesso" => true,
        "mensagem" => "Manutencao cadastrada com sucesso.",
        "id" => (int) $pdo->lastInsertId()
    ));

} catch (PDOException $e) {
    http_response_code(500);

    echo json_encode(array(
        "sucesso" => false,
        "erro" => "Erro ao cadastrar manutencao."
    ));
}
?>
