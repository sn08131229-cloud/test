#include "ConsoleUI.h"
#include "GameState.h"
#include "LogTailer.h"
#include "Parser.h"

#include <chrono>
#include <iostream>
#include <thread>

int main(int argc, char* argv[]) {
    if (argc < 2) {
        std::cout << "Usage: BattlegroundsTrackerDemo <path-to-log-file>" << std::endl;
        std::cout << "Example: BattlegroundsTrackerDemo sample_logs/Power.log" << std::endl;
        return 1;
    }

    LogTailer tailer(argv[1]);
    if (!tailer.Initialize()) {
        return 1;
    }

    Parser parser;
    GameState state;
    ConsoleUI ui;

    std::cout << "Watching log file: " << tailer.GetPath() << std::endl;
    std::cout << "Append lines to the log file to simulate Hearthstone events." << std::endl;

    while (true) {
        const auto lines = tailer.ReadNewLines();
        bool anyChange = false;

        for (const std::string& line : lines) {
            const auto events = parser.ParseLine(line);
            for (const auto& event : events) {
                if (state.ApplyEvent(event)) {
                    anyChange = true;
                }
            }
        }

        if (anyChange) {
            ui.PrintState(state);
        }

        std::this_thread::sleep_for(std::chrono::milliseconds(500));
    }

    return 0;
}
