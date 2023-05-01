Unicode True
!include "x64.nsh"
OutFile "ArchivaInstaller.exe"
Name "Archiva"

InstallDir $PROGRAMFILES\Archiva

RequestExecutionLevel admin

!include MUI2.nsh
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

!insertmacro MUI_LANGUAGE "Hungarian"

Icon "Archiva.ico"
UninstallIcon "Archiva.ico"

Section -Pre

  ;Ha 64 bites a rendszer, akkor kell ez mivel nem fogja kiolvasni a regisztert
  ${If} ${RunningX64}
    SetRegView 64
  ${EndIf}

  ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\JDK" "CurrentVersion"
  StrCmp $0 "" notfound found

  found:
    ${If} $0 < "19.0.2"
      Goto notfound
    ${Else}
      Goto done
    ${Endif}

  notfound:
    MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST|MB_DEFBUTTON2 \
    "OpenJDK 19.0.2 verzió vagy újabb szükséges az Archiva futtatásához. Szeretné telepíteni most?" \
    IDYES download IDNO cancel

    download:
      inetc::get /caption "OpenJDK letöltése..." /popup "" \
      /RESUME "A letöltés megszakadt. Újra szeretné próbálni?" \
      /CONNECTTIMEOUT=60000 \ 
      /RECEIVETIMEOUT=60000 \
      /NOPROXY \
      /WEAKSECURITY \
      /USERAGENT "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36" \
      "https://download.oracle.com/java/19/latest/jdk-19_windows-x64_bin.msi" "$TEMP\jdk-19_windows-x64_bin.msi"

      Pop $0
      StrCmp $0 "OK" install
      MessageBox MB_OK|MB_ICONSTOP|MB_TOPMOST \
      "Hiba történt az OpenJDK telepítése közben: $0"
      Abort

      install:
        ExecWait '"msiexec" /i "$TEMP\jdk-19_windows-x64_bin.msi"'
        Delete "$TEMP\jdk-19_windows-x64_bin.msi"
        Goto done

    cancel:
      MessageBox MB_OK|MB_ICONSTOP|MB_TOPMOST \
      "Nem folytathatja a telepítést az OpenJDK nélkül."
      Abort

  done:

SectionEnd

Section "Archiva" SecJavaProgram
    SetOutPath $INSTDIR
    
    ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\JDK" "CurrentVersion"
    ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\JDK\$0" "JavaHome"

    File "Archiva.exe"
    File "Archiva.ico"
    
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Archiva" \
        "DisplayName" "Archiva"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Archiva" \
        "UninstallString" '"$INSTDIR\Uninstall.exe"'
    
    #Programs
    CreateDirectory "$SMPROGRAMS\Archiva"
    CreateShortCut "$SMPROGRAMS\Archiva\Archiva.lnk" "$INSTDIR\Archiva.exe" \
        '' "$INSTDIR\Archiva.ico"
    CreateShortCut "$SMPROGRAMS\Archiva\Uninstall.lnk" "$INSTDIR\Uninstall.exe" "" \
        "$INSTDIR\Archiva.ico"

    #Desktop
    CreateShortCut "$DESKTOP\Archiva.lnk" "$INSTDIR\Archiva.exe" '' "$INSTDIR\Archiva.ico"
    
    #shell:sendto
    CreateShortCut "$SENDTO\Archiva.lnk" "$INSTDIR\Archiva.exe" '--compress' "$INSTDIR\Archiva.ico"
        

    #környezetérzékeny
    WriteRegStr HKCR ".arc\shell\Archiva fájl kibontása\command" "" '"$INSTDIR\Archiva.exe" --decompress "%1"'
    WriteRegStr HKCR ".arc\shell\open\command" "" '$INSTDIR\Archiva.exe --open "%1"'
    WriteRegStr HKCR ".arc\DefaultIcon" "Icon" "$INSTDIR\Archiva.ico"

    WriteUninstaller "$INSTDIR\Uninstall.exe"
SectionEnd

Section -AdditionalIcons
  CreateDirectory "$SMPROGRAMS\Archiva"
  CreateShortCut "$SMPROGRAMS\Archiva\Uninstall.lnk" "$INSTDIR\uninstall.exe"
SectionEnd

Section "Uninstall"
  Delete "$INSTDIR\Archiva.exe"
  Delete "$INSTDIR\uninstall.exe"
  Delete "$INSTDIR"

  Delete "$DESKTOP\Archiva.lnk"
  Delete "$SENDTO\Archiva.lnk"
  Delete "$SMPROGRAMS\Archiva\Uninstall.lnk"
  RMDir "$SMPROGRAMS\Archiva"

  RMDir "$INSTDIR"

  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Archiva"

SectionEnd