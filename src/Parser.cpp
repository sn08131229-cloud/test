#include "Parser.h"

#include <sstream>

std::optional<ParsedEvent> Parser::MatchSimpleValue(
    const std::string& line,
    const std::string& prefix,
    ParsedEvent::Type type,
    const std::string& playerId) const {
    if (line.rfind(prefix, 0) != 0) {
        return std::nullopt;
    }

    ParsedEvent event;
    event.type = type;
    event.playerId = playerId;
    event.value = std::stoi(line.substr(prefix.size()));
    return event;
}

std::vector<ParsedEvent> Parser::ParseLine(const std::string& line) const {
    std::vector<ParsedEvent> events;

    if (line.find("BATTLEGROUNDS_ENTER") != std::string::npos) {
        events.push_back({ParsedEvent::Type::EnterBattlegrounds, "", "", "", 0});
    }
    if (line.find("BATTLEGROUNDS_LEAVE") != std::string::npos) {
        events.push_back({ParsedEvent::Type::LeaveBattlegrounds, "", "", "", 0});
    }

    if (auto event = MatchSimpleValue(line, "TURN=", ParsedEvent::Type::TurnChanged)) {
        events.push_back(*event);
    }
    if (auto event = MatchSimpleValue(line, "PLAYER_HEALTH=", ParsedEvent::Type::HealthChanged, "me")) {
        events.push_back(*event);
    }
    if (auto event = MatchSimpleValue(line, "PLAYER_TAVERN_TIER=", ParsedEvent::Type::TavernTierChanged, "me")) {
        events.push_back(*event);
    }

    if (line.rfind("PLAYER_HERO=", 0) == 0) {
        ParsedEvent event;
        event.type = ParsedEvent::Type::HeroDetected;
        event.playerId = "me";
        event.text = line.substr(std::string("PLAYER_HERO=").size());
        events.push_back(event);
    }

    if (line.rfind("OPPONENT_SEEN|", 0) == 0) {
        std::stringstream ss(line);
        std::string token;
        ParsedEvent event;
        event.type = ParsedEvent::Type::OpponentSeen;

        std::getline(ss, token, '|'); // prefix
        std::getline(ss, event.playerId, '|');
        std::getline(ss, event.playerName, '|');
        std::getline(ss, token, '|');
        event.value = token.empty() ? 0 : std::stoi(token);
        std::getline(ss, event.text, '|');
        events.push_back(event);
    }

    if (line.rfind("OPPONENT_BOARD|", 0) == 0) {
        std::stringstream ss(line);
        std::string token;
        ParsedEvent event;
        event.type = ParsedEvent::Type::OpponentBoardUpdated;

        std::getline(ss, token, '|'); // prefix
        std::getline(ss, event.playerId, '|');
        std::getline(ss, event.text);
        events.push_back(event);
    }

    return events;
}
