cmake_minimum_required(VERSION 3.14)
project(BattlegroundsTrackerDemo LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

add_executable(BattlegroundsTrackerDemo
    src/main.cpp
    src/LogTailer.cpp
    src/Parser.cpp
    src/GameState.cpp
    src/ConsoleUI.cpp
)

target_include_directories(BattlegroundsTrackerDemo PRIVATE include)


enable_testing()

add_executable(BattlegroundsTrackerDemoTests
    tests/test_main.cpp
    src/LogTailer.cpp
    src/Parser.cpp
    src/GameState.cpp
)

target_include_directories(BattlegroundsTrackerDemoTests PRIVATE include)
add_test(NAME BattlegroundsTrackerDemoTests COMMAND BattlegroundsTrackerDemoTests)
