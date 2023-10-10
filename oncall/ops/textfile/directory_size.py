import os
import subprocess
import time

directory_to_measure = "/home/oncall/var/log"
output_file = "/tmp/textfile/directory_size.prom"

while True:
    directory_size = sum(os.path.getsize(os.path.join(dirpath, filename))
                         for dirpath, _, filenames in os.walk(directory_to_measure)
                         for filename in filenames)
    with open(output_file + '.$$', "w") as f:
        f.write(f'directory_size_bytes {directory_size}\n')
    subprocess.run(['mv', output_file + '.$$', output_file], check=True)
    time.sleep(10)
