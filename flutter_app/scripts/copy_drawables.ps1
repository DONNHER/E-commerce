# Copy Android drawable image files into Flutter assets/images folder
# Run this from PowerShell. It will copy common image extensions from the Android drawable folder.

$repoRoot = Split-Path -Parent $PSScriptRoot
$androidDrawable = Join-Path $repoRoot "..\..\app\src\main\res\drawable"
$dest = Join-Path $repoRoot "assets\images"

# Resolve absolute paths
$androidDrawable = Resolve-Path $androidDrawable -ErrorAction SilentlyContinue
if (-not $androidDrawable) {
    Write-Error "Android drawable folder not found: $androidDrawable"
    exit 1
}
$androidDrawable = $androidDrawable.Path

if (-not (Test-Path $dest)) {
    New-Item -ItemType Directory -Path $dest | Out-Null
}

$patterns = '*.png','*.jpg','*.jpeg','*.webp','*.gif'
$copied = 0

foreach ($pat in $patterns) {
    Get-ChildItem -Path $androidDrawable -Filter $pat -File -ErrorAction SilentlyContinue | ForEach-Object {
        $src = $_.FullName
        $filename = $_.Name
        $dstFile = Join-Path $dest $filename
        Copy-Item -Path $src -Destination $dstFile -Force
        $copied++
    }
}

Write-Host "Copied $copied drawable images to $dest"
Write-Host "If you plan to use these assets in Flutter, run:`n  cd \"$repoRoot\"`n  flutter pub get"
