# Verify Pending User Login Flow (Expect 403)

$username = "pendingUser5"
$password = "password123"

# Login (Should fail with 403 and specific message)
$loginBody = @{
    username = $username
    password = $password
} | ConvertTo-Json

try {
    Write-Host "Attempting login for $username..."
    $response = Invoke-RestMethod -Uri "http://localhost:8080/auth/loginb" -Method Post -ContentType "application/json" -Body $loginBody
    Write-Host "Login Unexpected Success!"
} catch {
    $msg = $_.Exception.Message
    $details = ""
    $stream = $_.Exception.Response.GetResponseStream()
    if ($stream) {
        $reader = New-Object System.IO.StreamReader($stream)
        $details = $reader.ReadToEnd()
    }
    
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "Login Failed. Status: $statusCode"
    Write-Host "Details: $details"
    
    if ($statusCode -eq 403 -and $details -match "awaiting admin approval") {
        Write-Host "VERIFICATION PASSED: Backend returns 403 and correct message."
    } else {
        Write-Host "VERIFICATION FAILED: Status or Message mismatch."
    }
}
