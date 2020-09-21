cmake_minimum_required(VERSION 3.4.1)

# cmake_minimum_required(VERSION 3.0.0)
# project(Project006 VERSION 0.1.0)

# include(CTest)
# enable_testing()

# add_executable(Project006 main.cpp)

# set(CPACK_PROJECT_NAME ${PROJECT_NAME})
# set(CPACK_PROJECT_VERSION ${PROJECT_VERSION})
# include(CPack)

project(demo)
message(${demo_SOURCE_DIR})
message(${demo_BINARY_DIR})

#${CMAKE_CURRENT_SOURCE_DIR}表示CMakeLists.txt文件的绝对路径
#${CMAKE_CURRENT_BINARY_DIR}
#${CMAKE_CURRENT_LIST_DIR}表示CMakeLists.txt文件的绝对路径
#${CMAKE_CURRENT_LIST_FILE}
#${CMAKE_INCLUDE_CURRENT_DIR}
#${PROJECT_SOURCE_DIR}

set(CMAKE_GENERATOR "MinGW Makefiles")
set(CMAKE_C_COMPILER "D:\\software_install\\TDM-GCC-64\\bin\\x86_64-w64-mingw32-gcc.exe")
set(CMAKE_CXX_COMPILER "D:\\software_install\\TDM-GCC-64\\bin\\x86_64-w64-mingw32-g++.exe")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

message(STATUS "C_COMPILER: ${CMAKE_C_COMPILER}")
message(STATUS "CXX_COMPILER: ${CMAKE_CXX_COMPILER}")
message(STATUS "C_FLAGS: ${CMAKE_C_FLAGS}")
message(STATUS "CXX_FLAGS: ${CMAKE_CXX_FLAGS}")

aux_source_directory(./src02 DIR_SRCS02)
aux_source_directory(./src DIR_SRCS)
include_directories(./include ./include02)
add_executable("aa" ${DIR_SRCS02} ${DIR_SRCS})




