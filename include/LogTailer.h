#pragma once

#include <fstream>
#include <string>
#include <vector>

class LogTailer {
public:
    explicit LogTailer(const std::string& path);

    bool Initialize();
    std::vector<std::string> ReadNewLines();
    const std::string& GetPath() const;

private:
    std::string path_;
    std::ifstream file_;
    std::streampos offset_;
};
