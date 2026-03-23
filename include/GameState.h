#pragma once

#include "Parser.h"

#include <string>
#include <unordered_map>
#include <vector>

struct OpponentInfo {
    std::string id;
    std::string name;
    int health = 0;
    std::string hero;
    std::vector<std::string> lastBoard;
};

class GameState {
public:
    bool ApplyEvent(const ParsedEvent& event);

    bool IsBattlegrounds() const;
    int GetTurn() const;
    const std::string& GetHero() const;
    int GetHealth() const;
    int GetTavernTier() const;
    const std::unordered_map<std::string, OpponentInfo>& GetOpponents() const;

private:
    void Reset();

    bool inBattlegrounds_ = false;
    int turn_ = 0;
    std::string hero_ = "Unknown";
    int health_ = 40;
    int tavernTier_ = 1;
    std::unordered_map<std::string, OpponentInfo> opponents_;
};
