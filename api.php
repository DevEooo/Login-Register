<?php
$url = "http://localhost/activity_main/regist.php";
$data = [
    "username" => "testuser",
    "email" => "test@mail.com",
    "password" => "123456"
];

$options = [
    "http" => [
        "header" => "Content-Type: application/x-www-form-urlencoded",
        "method" => "POST",
        "content" => http_build_query($data)
    ]
];

$context = stream_context_create($options);
$response = file_get_contents($url, false, $context);
echo $response;
