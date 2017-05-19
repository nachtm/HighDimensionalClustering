import argparse
import subprocess

parser = argparse.ArgumentParser()
parser.add_argument("clazz")
parser.add_argument("args", nargs=argparse.REMAINDER)
args = parser.parse_args()
subprocess.call([
    './gradlew',
    'run',
    '-PtheClass=' + args.clazz,
    '-PtheArgs=[{}]'.format(', '.join("'{}'".format(arg) for arg in args.args)),
    ])
