try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/auth/loginb" -Method Post -ContentType "application/json" -Body '{"username": "pendingUser5", "password": "password123"}'
    Write-Host "200"
} catch {
    Write-Host $_.Exception.Response.StatusCode.value__
}
