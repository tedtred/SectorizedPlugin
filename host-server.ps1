param(
    [string]$ServerDir = (Join-Path $PSScriptRoot 'mindustry-server-v7'),
    [string]$ServerJar,
    [int]$RestartDelaySeconds = 2
)

$ErrorActionPreference = 'Stop'

function Resolve-ServerJar {
    param(
        [string]$ResolvedServerDir,
        [string]$ExplicitServerJar
    )

    if ($ExplicitServerJar) {
        if (-not (Test-Path $ExplicitServerJar)) {
            throw "Server jar not found: $ExplicitServerJar"
        }

        return (Resolve-Path $ExplicitServerJar).Path
    }

    $candidates = @(
        Get-ChildItem -Path $ResolvedServerDir -Filter 'server-release-*.jar' -File -ErrorAction SilentlyContinue | Sort-Object Name -Descending
        Get-ChildItem -Path $ResolvedServerDir -Filter 'server-release.jar' -File -ErrorAction SilentlyContinue | Sort-Object Name -Descending
    ) | Where-Object { $_ } | Select-Object -First 1

    if (-not $candidates) {
        throw "No Mindustry server jar found in $ResolvedServerDir"
    }

    return $candidates.FullName
}

$resolvedServerDir = (Resolve-Path $ServerDir).Path
$resolvedServerJar = Resolve-ServerJar -ResolvedServerDir $resolvedServerDir -ExplicitServerJar $ServerJar

Write-Host "Hosting Mindustry server from $resolvedServerDir"
Write-Host "Using server jar $resolvedServerJar"
Write-Host "Press Ctrl+C to stop the host loop."

Push-Location $resolvedServerDir
try {
    while ($true) {
        Write-Host "Starting server at $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
        & java -jar $resolvedServerJar
        $exitCode = $LASTEXITCODE

        if ($exitCode -eq 0) {
            Write-Host "Server exited cleanly. Restarting in $RestartDelaySeconds second(s)..."
        } else {
            Write-Warning "Server exited with code $exitCode. Restarting in $RestartDelaySeconds second(s)..."
        }

        Start-Sleep -Seconds $RestartDelaySeconds
    }
} finally {
    Pop-Location
}