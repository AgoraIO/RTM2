# clean_build.sh
rm -fr build
mkdir -p build
cd build
cmake ..
make
cd ..

