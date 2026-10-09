#!/usr/bin/env python3
"""Create a deterministic classes.dex-only ZIP/JAR for app_process."""
import pathlib
import sys
import zipfile

if len(sys.argv) != 3:
    raise SystemExit("Usage: package.py input-classes.dex output.jar")
source = pathlib.Path(sys.argv[1])
target = pathlib.Path(sys.argv[2])
# app_process accepts a JAR/ZIP containing classes.dex; there is no Java manifest.
info = zipfile.ZipInfo("classes.dex", (1980, 1, 1, 0, 0, 0))
info.compress_type = zipfile.ZIP_DEFLATED
info.external_attr = 0o644 << 16
with zipfile.ZipFile(target, "w") as archive:
    archive.writestr(info, source.read_bytes(), compresslevel=9)
