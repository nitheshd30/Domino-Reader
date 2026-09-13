import zipfile
import sys
import io

def create_zip():
    mem_zip = io.BytesIO()
    with zipfile.ZipFile(mem_zip, mode='w', compression=zipfile.ZIP_DEFLATED) as zf:
        zf.writestr('StorageCard2/Labels/TEST.lbl', '<?xml version="1.0"?><label CILF="1"><name="TEST"></label>')
        zf.writestr('StorageCard2/Labels/TEST2.LBL', 'hello world')
    with open('/tmp/zip_test/test3.zip', 'wb') as f:
        f.write(mem_zip.getvalue())

create_zip()
