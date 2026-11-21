
<?php

$host = "127.0.0.1";
$porta = "3307";
$banco = "manutech_mobile";
$usuario = "SEU_USUARIO";
$senha = "SUA_SENHA";

try {
    $pdo = new PDO(
        "mysql:host=$host;port=$porta;dbname=$banco;charset=utf8",
        $usuario,
        $senha
    );

    $pdo->setAttribute(
        PDO::ATTR_ERRMODE,
        PDO::ERRMODE_EXCEPTION
    );

} catch (PDOException $e) {
    http_response_code(500);

    echo json_encode(array(
        "sucesso" => false,
        "mensagem" => "Erro na conexão com o banco de dados."
    ));

    exit;
}

?>
