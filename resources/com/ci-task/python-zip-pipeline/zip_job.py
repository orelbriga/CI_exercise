import os
import sys
from zipfile import ZipFile


def main():
    verENV = os.environ.get('VERSION')
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
        except FileNotFoundError:
            sys.exit(f"{newZip.filename} wasn't created, couldn't find {file}.txt to zip - aborting the script")

        if not os.path.isfile(f"./{newZip.filename}"):
            sys.exit(f"{newZip.filename} not found - aborting the script")
        print(f"{newZip.filename} created")

    print('All zip files were created successfully.')


if __name__ == "__main__":
    main()
