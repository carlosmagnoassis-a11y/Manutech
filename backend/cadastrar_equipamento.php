
<?php

header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    http_response_code(405);
    echo json_encode([
        "erro" => "Método não permitido. Utilize POST."
    ]);
    exit;
}

require_once "conexao.php";

try {
    $dados = json_decode(file_get_contents("php://input"), true);

    if (!is_array($dados)) {
        http_response_code(400);
        echo json_encode([
            "erro" => "JSON inválido."
        ]);
        exit;
    }

    
	$nome = isset($dados["nome"])
		? trim($dados["nome"]) : "";

	$identificacao = isset($dados["identificacao"])
		? trim($dados["identificacao"]) : "";

	$tipo = isset($dados["tipo"])
		? trim($dados["tipo"]) : "";

	$localizacao = isset($dados["localizacao"])
		? trim($dados["localizacao"]) : "";


    if (
        $nome === "" ||
        $identificacao === "" ||
        $localizacao === ""
    ) {
        http_response_code(400);
        echo json_encode([
            "erro" => "Preencha nome, identificação e localização."
        ]);
        exit;
    }

    if (
        strlen($nome) > 100 ||
        strlen($identificacao) > 50 ||
        strlen($tipo) > 100 ||
        strlen($localizacao) > 150
    ) {
        http_response_code(400);
        echo json_encode([
            "erro" => "Um ou mais campos ultrapassam o limite permitido."
        ]);
        exit;
    }

    $sql = "INSERT INTO equipamentos
            (nome, identificacao, tipo, localizacao, status)
            VALUES
            (:nome, :identificacao, :tipo, :localizacao, 'Ativo')";

    $stmt = $pdo->prepare($sql);

    $stmt->execute([
        ":nome" => $nome,
        ":identificacao" => $identificacao,
        ":tipo" => $tipo,
        ":localizacao" => $localizacao
    ]);

    http_response_code(201);

    echo json_encode([
        "sucesso" => true,
        "mensagem" => "Equipamento cadastrado com sucesso!",
        "id" => (int) $pdo->lastInsertId()
    ]);

} catch (PDOException $e) {

    if ($e->getCode() == "23000") {
        http_response_code(409);

        echo json_encode([
            "erro" => "Identificação já cadastrada."
        ]);
    } else {
        http_response_code(500);

        echo json_encode([
            "erro" => "Erro ao cadastrar equipamento."
        ]);
    }

} catch (Throwable $e) {
    http_response_code(500);

    echo json_encode([
        "erro" => "Erro interno do servidor."
    ]);
}
?>
