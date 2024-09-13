# CMakeLists.txt
cmake_minimum_required (VERSION 2.8)
project(RTMQuickStart)
set(TARGET_NAME rtm_demo)
set(SOURCES rtm_quick_start.cpp)
set(HEADERS)
set(TARGET_BUILD_TYPE "Debug")
set(CMAKE_CXX_FLAGS "-fPIC -O2 -g -std=c++11 -msse2")
include_directories(${CMAKE_SOURCE_DIR}/include)
link_directories(${CMAKE_SOURCE_DIR}/lib)
add_executable(${TARGET_NAME} ${SOURCES} ${HEADERS})
target_link_libraries(${TARGET_NAME} agora_rtm_sdk pthread)

