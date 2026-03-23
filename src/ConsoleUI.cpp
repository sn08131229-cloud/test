#include "ConsoleUI.h"

#include <iostream>

void ConsoleUI::PrintState(const GameState& state) const {
    std::cout << "========================================" << std::endl;
    std::cout << "Battlegrounds active: " << (state.IsBattlegrounds() ? "yes" : "no") << std::endl;

    if (!state.IsBattlegrounds()) {
        std::cout << "Waiting for a Battlegrounds match..." << std::endl;
        return;
    }

    std::cout << "Turn: " << state.GetTurn() << std::endl;
    std::cout << "My hero: " << state.GetHero() << std::endl;
    std::cout << "My health: " << state.GetHealth() << std::endl;
    std::cout << "My tavern tier: " << state.GetTavernTier() << std::endl;
    std::cout << "Opponents:" << std::endl;

    if (state.GetOpponents().empty()) {
        std::cout << "  (none seen yet)" << std::endl;
        return;
    }

    for (const auto& [id, opponent] : state.GetOpponents()) {
        std::cout << "  - [" << id << "] " << opponent.name
                  << " | hero: " << (opponent.hero.empty() ? "Unknown" : opponent.hero)
                  << " | health: " << opponent.health << std::endl;

        if (!opponent.lastBoard.empty()) {
            std::cout << "    Last known board: ";
            for (std::size_t i = 0; i < opponent.lastBoard.size(); ++i) {
                std::cout << opponent.lastBoard[i];
                if (i + 1 < opponent.lastBoard.size()) {
                    std::cout << ", ";
                }
            }
            std::cout << std::endl;
        }
    }
}
