#include "LogTailer.h"

#include <iostream>

LogTailer::LogTailer(const std::string& path)
    : path_(path), offset_(0) {
}

bool LogTailer::Initialize() {
    file_.open(path_);
    if (!file_.is_open()) {
        std::cerr << "Failed to open log file: " << path_ << std::endl;
        return false;
    }

    file_.seekg(0, std::ios::end);
    offset_ = file_.tellg();
    return true;
}

std::vector<std::string> LogTailer::ReadNewLines() {
    std::vector<std::string> lines;
    if (!file_.is_open()) {
        return lines;
    }

    file_.clear();
    file_.seekg(0, std::ios::end);
    const std::streampos endPos = file_.tellg();

    if (endPos < offset_) {
        // The log may have been recreated. Restart from the beginning.
        file_.close();
        file_.open(path_);
        offset_ = 0;
        if (!file_.is_open()) {
            return lines;
        }
    }

    file_.clear();
    file_.seekg(offset_);

    std::string line;
    while (std::getline(file_, line)) {
        if (!line.empty() && line.back() == '\r') {
            line.pop_back();
        }
        lines.push_back(line);
    }

    file_.clear();
    offset_ = file_.tellg();
    if (offset_ == std::streampos(-1)) {
        file_.clear();
        file_.seekg(0, std::ios::end);
        offset_ = file_.tellg();
    }

    return lines;
}

const std::string& LogTailer::GetPath() const {
    return path_;
}
