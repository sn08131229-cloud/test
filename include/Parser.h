#pragma once

#include <optional>
#include <string>
#include <vector>

struct ParsedEvent {
    enum class Type {
        EnterBattlegrounds,
        LeaveBattlegrounds,
        TurnChanged,
        HeroDetected,
        HealthChanged,
        TavernTierChanged,
        OpponentSeen,
        OpponentBoardUpdated
    };

    Type type;
    std::string playerId;
    std::string playerName;
    std::string text;
    int value = 0;
};

class Parser {
public:
    std::vector<ParsedEvent> ParseLine(const std::string& line) const;

private:
    std::optional<ParsedEvent> MatchSimpleValue(
        const std::string& line,
        const std::string& prefix,
        ParsedEvent::Type type,
        const std::string& playerId = "") const;
};
