import zipfile
import sys
import io

# Let's create a dummy zip file and try to parse it
def create_dummy_zip():
    mem_zip = io.BytesIO()
    with zipfile.ZipFile(mem_zip, mode='w', compression=zipfile.ZIP_DEFLATED) as zf:
        zf.writestr('StorageCard2/Labels/TEST.lbl', '<?xml version="1.0"?><label CILF="1"><name="TEST"></label>')
        zf.writestr('StorageCard2/Labels/Logs/test.log', 'Log content')
    
    with open('/tmp/zip_test/dummy.zip', 'wb') as f:
        f.write(mem_zip.getvalue())

create_dummy_zip()
