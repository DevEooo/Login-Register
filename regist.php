<?php
header("Content-Type: application/json"); 
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Cek apakah request menggunakan metode POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Invalid request method"]);
    exit;
}

// Konfigurasi Database
$host = "localhost";
$user = "root"; 
$pass = "";
$dbname = "android";

$conn = new mysqli($host, $user, $pass, $dbname);

// Cek koneksi database
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed: " . $conn->connect_error]);
    exit;
}

// Cek apakah data POST diterima
if (!isset($_POST['username']) || !isset($_POST['email']) || !isset($_POST['password'])) {
    echo json_encode(["status" => "error", "message" => "All fields are required."]);
    exit;
}

// Ambil data dari POST
$username = trim($_POST['username']);
$email = trim($_POST['email']);
$password = trim($_POST['password']); // Gunakan hash MD5 (sebaiknya pakai password_hash untuk lebih aman)

// Pastikan tidak ada data kosong
if (empty($username) || empty($email) || empty($password)) {
    echo json_encode(["status" => "error", "message" => "All fields are required."]);
    exit;
}

// Cek apakah email sudah terdaftar
$check_email = $conn->prepare("SELECT id_user FROM tb_regist WHERE email = ?");
$check_email->bind_param("s", $email);
$check_email->execute();
$check_email->store_result();
if ($check_email->num_rows > 0) {
    echo json_encode(["status" => "error", "message" => "Email already registered."]);
    exit;
}
$check_email->close();

// Simpan ke database menggunakan prepared statement
$stmt = $conn->prepare("INSERT INTO tb_regist (username, email, password) VALUES (?, ?, ?)");
$stmt->bind_param("sss", $username, $email, $password);

if ($stmt->execute()) {
    echo json_encode([
        "status" => "BERHASIL",
        "id" => $stmt->insert_id,
        "username" => $username
    ]);
} else {
    echo json_encode(["status" => "error", "message" => "Database error: " . $conn->error]);
}

// Tutup koneksi
$stmt->close();
$conn->close();
