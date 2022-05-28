import os
from zipfile import ZipFile

verENV = os.environ.get('VERSION')
print(verENV)


def createZip(txtFile):
    zipObj = ZipFile(f'{txtFile}_{verENV}.zip','w')
    zipObj.write(f'{txtFile}.txt')
    zipObj.close()
    return f'{txtFile}_{verENV}.zip'

files = ['a','b','c','d']
for file in files:
    with open(f"{file}.txt","w") as f:
        if not os.path.isfile(f"{file}.txt"):
            exit(print(f'py script aborted, failed to create {file}.txt'))
    zippedFile = createZip(file)
    os.remove(f'{file}.txt')
    if not os.path.isfile((zippedFile)):
        exit(print(f'py script aborted, failed to create {zippedFile}'))

print('All files were created successfully.')








