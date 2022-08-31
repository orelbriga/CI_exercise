import os
from zipfile import ZipFile

verENV = os.environ.get('VERSION')
print(verENV)


def createZip(txtFile):
    zipObj = ZipFile(f'{txtFile}_{verENV}.zip', 'w')
    zipObj.write(f'{txtFile}.txt')
    zipObj.close()
    return f'{txtFile}_{verENV}.zip'


files = ['a', 'b', 'c', 'd']
for file in files:
    try:
        with open(f"{file}.txt", "w+") as f:
            f.write('')
    except FileNotFoundError:
        exit(f"{file}.txt doesn't exists")
    zippedFile = createZip(file)
    # Delete the originial file (redundent file):
    os.remove(f"{file}.txt")
    if not os.path.isfile(zippedFile):
        exit(f'py script aborted, failed to create {zippedFile}')

print('All zip files were created successfully.')
