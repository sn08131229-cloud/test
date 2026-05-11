#include "GameState.h"
#include "LogTailer.h"
#include "Parser.h"

#include <cstdio>
#include <fstream>
#include <iostream>
#include <string>

namespace {

int g_failed = 0;

void Expect(bool condition, const std::string& message) {
    if (!condition) {
        ++g_failed;
        std::cerr << "[FAIL] " << message << std::endl;
    }
}

void TestParserBasic() {
    Parser parser;

    auto events = parser.ParseLine("TURN=3");
    Expect(events.size() == 1, "TURN should produce one event");
    if (!events.empty()) {
        Expect(events[0].type == ParsedEvent::Type::TurnChanged, "TURN event type");
        Expect(events[0].value == 3, "TURN event value");
    }

    events = parser.ParseLine("OPPONENT_SEEN|opp_1|Patchwerk|38|Patchwerk");
    Expect(events.size() == 1, "OPPONENT_SEEN should produce one event");
    if (!events.empty()) {
        Expect(events[0].playerId == "opp_1", "Opponent id parsed");
        Expect(events[0].playerName == "Patchwerk", "Opponent name parsed");
        Expect(events[0].value == 38, "Opponent health parsed");
        Expect(events[0].text == "Patchwerk", "Opponent hero parsed");
    }

    events = parser.ParseLine("OPPONENT_BOARD|opp_1|Scallywag,Harvest Golem");
    Expect(events.size() == 1, "OPPONENT_BOARD should produce one event");
    if (!events.empty()) {
        Expect(events[0].type == ParsedEvent::Type::OpponentBoardUpdated, "Opponent board type");
        Expect(events[0].playerId == "opp_1", "Opponent board id");
    }
}

void TestGameStateFlow() {
    GameState state;

    Expect(!state.IsBattlegrounds(), "initially not in battlegrounds");

    ParsedEvent enter{ParsedEvent::Type::EnterBattlegrounds, "", "", "", 0};
    Expect(state.ApplyEvent(enter), "enter event should change state");
    Expect(state.IsBattlegrounds(), "after enter is battlegrounds");

    ParsedEvent turn{ParsedEvent::Type::TurnChanged, "", "", "", 2};
    Expect(state.ApplyEvent(turn), "turn change should apply");
    Expect(state.GetTurn() == 2, "turn should be 2");

    ParsedEvent seen{ParsedEvent::Type::OpponentSeen, "opp_1", "Patchwerk", "Patchwerk", 37};
    Expect(state.ApplyEvent(seen), "opponent seen should apply");
    Expect(state.GetOpponents().count("opp_1") == 1, "opponent should exist");

    ParsedEvent board{ParsedEvent::Type::OpponentBoardUpdated, "opp_1", "", "Scallywag,Harvest Golem", 0};
    Expect(state.ApplyEvent(board), "opponent board should apply");
    const auto& opp = state.GetOpponents().at("opp_1");
    Expect(opp.lastBoard.size() == 2, "board should have 2 minions");

    ParsedEvent leave{ParsedEvent::Type::LeaveBattlegrounds, "", "", "", 0};
    Expect(state.ApplyEvent(leave), "leave should reset");
    Expect(!state.IsBattlegrounds(), "after leave should not be battlegrounds");
    Expect(state.GetOpponents().empty(), "after leave opponents cleared");
}

void TestLogTailerReadNewLines() {
    const std::string filePath = "tmp_logtailer.log";
    {
        std::ofstream init(filePath);
        init << "initial line\n";
    }

    LogTailer tailer(filePath);
    Expect(tailer.Initialize(), "log tailer initialize");

    {
        std::ofstream append(filePath, std::ios::app);
        append << "TURN=1\n";
        append << "PLAYER_HEALTH=39\n";
    }

    const auto lines = tailer.ReadNewLines();
    Expect(lines.size() == 2, "should read exactly appended lines");
    if (lines.size() == 2) {
        Expect(lines[0] == "TURN=1", "first appended line matches");
        Expect(lines[1] == "PLAYER_HEALTH=39", "second appended line matches");
    }

    std::remove(filePath.c_str());
}

} // namespace

int main() {
    TestParserBasic();
    TestGameStateFlow();
    TestLogTailerReadNewLines();

    if (g_failed == 0) {
        std::cout << "All tests passed." << std::endl;
        return 0;
    }

    std::cerr << g_failed << " test(s) failed." << std::endl;
    return 1;
}
