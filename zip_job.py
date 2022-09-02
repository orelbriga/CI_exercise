import os
import sys
from zipfile import ZipFile

verENV = os.environ.get('VERSION')
print(verENV)


def main():
    files = ['a', 'b', 'c', 'd']
    for file in files:

        try:
            with open(f"{file}.txt", "w+") as f:
                f.write(f"test-{file}")
        except FileNotFoundError:
            sys.exit(f"{file}.txt wasn't created - aborting the script")

        try:
            with ZipFile(f"{file}_{verENV}.zip", "w") as newZip:
                newZip.write(f"{file}.txt")
                # Delete the originial file (redundent file):
                os.remove(f"{file}.txt")
                print(f"{newZip.filename} created")

        except FileNotFoundError:
            sys.exit(f"{newZip.filename} wasn't created - aborting the script")

    print('All zip files were created successfully.')


if __name__ == "__main__":
    main()
