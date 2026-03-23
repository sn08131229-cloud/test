#include "GameState.h"

#include <sstream>

bool GameState::ApplyEvent(const ParsedEvent& event) {
    switch (event.type) {
    case ParsedEvent::Type::EnterBattlegrounds:
        Reset();
        inBattlegrounds_ = true;
        return true;
    case ParsedEvent::Type::LeaveBattlegrounds:
        Reset();
        return true;
    case ParsedEvent::Type::TurnChanged:
        if (turn_ != event.value) {
            turn_ = event.value;
            return true;
        }
        return false;
    case ParsedEvent::Type::HeroDetected:
        if (hero_ != event.text) {
            hero_ = event.text;
            return true;
        }
        return false;
    case ParsedEvent::Type::HealthChanged:
        if (health_ != event.value) {
            health_ = event.value;
            return true;
        }
        return false;
    case ParsedEvent::Type::TavernTierChanged:
        if (tavernTier_ != event.value) {
            tavernTier_ = event.value;
            return true;
        }
        return false;
    case ParsedEvent::Type::OpponentSeen: {
        OpponentInfo& opponent = opponents_[event.playerId];
        bool changed = false;
        if (opponent.id != event.playerId) {
            opponent.id = event.playerId;
            changed = true;
        }
        if (opponent.name != event.playerName) {
            opponent.name = event.playerName;
            changed = true;
        }
        if (opponent.health != event.value) {
            opponent.health = event.value;
            changed = true;
        }
        if (opponent.hero != event.text) {
            opponent.hero = event.text;
            changed = true;
        }
        return changed;
    }
    case ParsedEvent::Type::OpponentBoardUpdated: {
        OpponentInfo& opponent = opponents_[event.playerId];
        std::vector<std::string> board;
        std::stringstream ss(event.text);
        std::string minion;
        while (std::getline(ss, minion, ',')) {
            if (!minion.empty()) {
                board.push_back(minion);
            }
        }
        if (opponent.lastBoard != board) {
            opponent.id = event.playerId;
            opponent.lastBoard = board;
            return true;
        }
        return false;
    }
    }

    return false;
}

bool GameState::IsBattlegrounds() const {
    return inBattlegrounds_;
}

int GameState::GetTurn() const {
    return turn_;
}

const std::string& GameState::GetHero() const {
    return hero_;
}

int GameState::GetHealth() const {
    return health_;
}

int GameState::GetTavernTier() const {
    return tavernTier_;
}

const std::unordered_map<std::string, OpponentInfo>& GameState::GetOpponents() const {
    return opponents_;
}

void GameState::Reset() {
    inBattlegrounds_ = false;
    turn_ = 0;
    hero_ = "Unknown";
    health_ = 40;
    tavernTier_ = 1;
    opponents_.clear();
}
