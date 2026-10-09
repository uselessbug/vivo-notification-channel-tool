#!/usr/bin/env python3
"""Static integrity/scope checks; not a substitute for testing on a real Vivo device."""
import hashlib
import pathlib
import struct
import sys
import zipfile
import zlib

if len(sys.argv) != 2:
    raise SystemExit("Usage: verify.py path/to/tool.jar")
file = pathlib.Path(sys.argv[1])
with zipfile.ZipFile(file) as archive:
    assert archive.namelist() == ["classes.dex"], "JAR must contain only the runtime DEX"
    dex = archive.read("classes.dex")
assert dex.startswith(b"dex\n"), "Not a DEX"
assert int.from_bytes(dex[32:36], "little") == len(dex), "Incorrect DEX file size"
assert dex[12:32] == hashlib.sha1(dex[32:]).digest(), "Incorrect DEX SHA-1"
assert struct.unpack_from("<I", dex, 8)[0] == zlib.adler32(dex[12:]) & 0xFFFFFFFF, "Incorrect DEX checksum"
for marker in (
    b"VivoNotificationChannelTool", b"com.vivo.daemonService", b"DEVELOPMENT_MODE",
    b"setBlockable", b"setImportance", b"updateNotificationChannelForPackage",
    b"inspect", b"disable", b"restore",
):
    assert marker in dex, f"Missing expected name/constant: {marker!r}"
print(f"PASS: {file.name} contains only classes.dex; DEX checksums and expected names verified")
print("SHA256:", hashlib.sha256(file.read_bytes()).hexdigest())
