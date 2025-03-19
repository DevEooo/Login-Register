<?php
header("Content-Type: application/json");

// Database connection
$host = "localhost";
$user = "root";
$pass = "";
$dbname = "android";

$conn = new mysqli($host, $user, $pass, $dbname);

if ($conn->connect_error) {
    echo json_encode(["status" => "ERROR", "message" => "Database connection failed"]);
    exit();
}

// Get POST data
$username = isset($_POST['username']) ? trim($_POST['username']) : '';
$password = isset($_POST['password']) ? trim($_POST['password']) : '';

// Check if fields are empty
if (empty($username) || empty($password)) {
    echo json_encode(["status" => "GAGAL", "message" => "Username atau password kosong"]);
    exit();
}

// Query using username instead of id_user
$sql = "SELECT username, password FROM tb_regist WHERE username=?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    if ($password = $row["password"]) {
        echo json_encode(["status" => "BERHASIL", "message" => "Login sukses"]);
    } else {
        echo json_encode(["status" => "GAGAL", "message" => "Password salah"]);
    }
} else {
    echo json_encode(["status" => "GAGAL", "message" => "User tidak ditemukan"]);
}

// Cleanup
$stmt->close();
$conn->close();
exit();
