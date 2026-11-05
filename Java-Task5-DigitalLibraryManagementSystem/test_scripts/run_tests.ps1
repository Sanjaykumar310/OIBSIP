Param()
$base = 'E:\\OIBSIP\\Library_Test_Cases'
if (-not (Test-Path $base)) { New-Item -ItemType Directory -Path $base | Out-Null }
$orig = 'E:\\OIBSIP\\Java-Task5-DigitalLibraryManagementSystem\\library-data.ser'
if (-not (Test-Path $orig)) { Write-Error 'Original data file missing'; exit 1 }
# Define cases as hashtable array
$cases = @(
    @{ id='case1'; desc='Admin valid login & logout'; input=@('1','admin','admin123','10','4') },
    @{ id='case2'; desc='Admin invalid login'; input=@('1','admin','wrong','4') },
    @{ id='case3'; desc='User valid login & logout'; input=@('2','user1','1234','8','4') },
    @{ id='case4'; desc='User invalid login'; input=@('2','userX','0000','4') },
    @{ id='case5'; desc='Admin list books'; input=@('1','admin','admin123','1','10','4') },
    @{ id='case6'; desc='Admin search books'; input=@('1','admin','admin123','2','Clean','10','4') },
    @{ id='case7'; desc='Admin add book'; input=@('1','admin','admin123','3','200','New Book','New Author','New Category','10','4') },
    @{ id='case8'; desc='Admin update book'; input=@('1','admin','admin123','4','101','Updated Title','Updated Author','Updated Category','10','4') },
    @{ id='case9'; desc='Admin delete book (available)'; input=@('1','admin','admin123','5','200','10','4') },
    @{ id='case10'; desc='Admin add duplicate ID'; input=@('1','admin','admin123','3','101','Dup Title','Dup Author','Dup Cat','10','4') },
    @{ id='case11'; desc='User borrow book'; input=@('2','user1','1234','3','101','10','4') },
    @{ id='case12'; desc='User return book'; input=@('2','user1','1234','4','101','10','4') },
    @{ id='case13'; desc='User borrow unavailable book then another user borrow'; input=@('2','user1','1234','3','101','10','4','2','user2','1234','3','101','10','4','2','user1','1234','4','101','10','4') },
    @{ id='case14'; desc='Reservation queue FIFO and fulfillment'; input=@('2','user1','1234','3','101','10','4','2','user2','1234','5','101','10','4','2','user3','1234','5','101','10','4','2','user1','1234','4','101','10','4') },
    @{ id='case15'; desc='Invalid menu input'; input=@('x','4') }
)
$javaCp = 'E:\\OIBSIP\\Java-Task5-DigitalLibraryManagementSystem\\out'
foreach ($c in $cases) {
    $dir = Join-Path $base $c.id
    if (Test-Path $dir) { Remove-Item -Recurse -Force $dir }
    New-Item -ItemType Directory -Path $dir | Out-Null
    Copy-Item -Path $orig -Destination $dir
    $inputPath = Join-Path $dir 'input.txt'
    $c.input | Set-Content -Path $inputPath -NoNewline
    $logPath = Join-Path $dir 'run.log'
    # Run java application
    $psi = New-Object System.Diagnostics.ProcessStartInfo
    $psi.FileName = 'java'
    $psi.Arguments = "-cp \"$javaCp\" Main"
    $psi.WorkingDirectory = $dir
    $psi.RedirectStandardInput = $true
    $psi.RedirectStandardOutput = $true
    $psi.RedirectStandardError = $true
    $psi.UseShellExecute = $false
    $process = [System.Diagnostics.Process]::Start($psi)
    $input = Get-Content -Path $inputPath -Raw
    $process.StandardInput.WriteLine($input)
    $process.StandardInput.Close()
    $stdout = $process.StandardOutput.ReadToEnd()
    $stderr = $process.StandardError.ReadToEnd()
    $process.WaitForExit()
    $exitCode = $process.ExitCode
    $stdout | Set-Content -Path $logPath
    if ($stderr) { $stderr | Add-Content -Path $logPath }
    "EXITCODE=$exitCode" | Add-Content -Path $logPath
    Write-Host "Completed $($c.id) with exit $exitCode"
}
