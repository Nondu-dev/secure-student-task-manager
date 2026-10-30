# demo.ps1 - Live demo for the Secure Student Task Manager
# Start the app (run Main) first, then run this file.
# Restart the app before running the demo again: the login limit is kept in
# memory, so bob stays blocked for 5 minutes after Step 6.

$baseUrl = "http://localhost:7077"

# Sends one request and prints the status code and the reply.
# The reply is saved in $last, so a login token can be reused.
function Call($method, $path, $body = $null, $token = $null) {
    $params = @{ Method = $method; Uri = "$baseUrl$path"; UseBasicParsing = $true }

    # Clear the old reply, so a failed call never reuses a token from an earlier call.
    $global:last = $null

    if ($token) { $params.Headers = @{ Authorization = "Bearer $token" } }
    if ($body)  { $params.Body = $body; $params.ContentType = "application/json" }

    try {
        $response = Invoke-WebRequest @params
        Write-Host "  -> $([int]$response.StatusCode)  $($response.Content)" -ForegroundColor Green
        $global:last = $response.Content
    } catch {
        # 400, 401 and 403 come here. They are the answers we want to show.
        Write-Host "  -> $([int]$_.Exception.Response.StatusCode)  $($_.ErrorDetails.Message)" -ForegroundColor Yellow
    }
}

# Prints a step title and waits for Enter, so you can talk first.
function Step($title) {
    Write-Host ""
    Write-Host "=== $title ===" -ForegroundColor Cyan
    Read-Host "Press Enter to run"
}

Step "Step 1 - Sign up (the second alice must be refused)"
Call POST /register '{"username":"alice","password":"password123"}'

Call POST /register '{"username":"alice","password":"password123"}'

Call POST /register '{"username":"bob","password":"password123"}'

Step "Step 2 - Log in (right password gives a token, wrong password gives 401)"
Call POST /login '{"username":"alice","password":"password123"}'
$alice = $last
Call POST /login '{"username":"bob","password":"password123"}'
$bob = $last
Call POST /login '{"username":"alice","password":"wrongpass1"}'

Step "Step 3 - Create a task (empty title and no token must be refused)"
Call POST /tasks '{"title":"Study Java","description":"Security notes"}' $alice
# The reply is "Task created with ID: N". Keep N, so the next steps ask for this task.
$taskId = $last -replace '\D', ''
Call POST /tasks '{"title":"","description":"x"}' $alice
Call POST /tasks '{"title":"No token","description":"x"}'

Step "Step 4 - Task owners (Alice can read it, Bob gets 403)"
Call GET /tasks/$taskId $null $alice

Call GET /tasks/$taskId $null $bob

Step "Step 5 - SQL injection attempt (must fail with 401)"
Call POST /login '{"username":"'' OR ''1''=''1","password":"x"}'

Step "Step 6 - Login limit (5 wrong passwords, then the right one is refused)"
1..5 | ForEach-Object { Call POST /login '{"username":"bob","password":"wrongpass1"}' }
Call POST /login '{"username":"bob","password":"password123"}'
Write-Host ""
Write-Host "security.log:" -ForegroundColor Cyan
Get-Content security.log -Tail 10

Step "Step 7 - Logout (the old token must stop working)"
Call POST /logout $null $alice
Call GET /tasks/$taskId $null $alice

Write-Host ""
Write-Host "Demo finished." -ForegroundColor Cyan
