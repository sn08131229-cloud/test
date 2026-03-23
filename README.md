# Battlegrounds Tracker Demo (C++)

这是一个使用标准 C++ 编写的最小可运行 demo，用于演示如何通过 **炉石传说公开日志文件** 跟踪“酒馆战棋”对局信息。

## 目标

- 只做酒馆战棋信息跟踪，不做传统记牌器。
- 只读取日志文件，不使用内存注入、Hook、DLL 注入等方式。
- 先实现控制台程序，后续可扩展为 Win32 小窗口或悬浮窗。
- 代码保持简单，适合初学者阅读和继续扩展。

## 项目结构

```text
.
├── CMakeLists.txt
├── include/
│   ├── ConsoleUI.h
│   ├── GameState.h
│   ├── LogTailer.h
│   └── Parser.h
├── sample_logs/
│   └── Power.log
├── src/
│   ├── ConsoleUI.cpp
│   ├── GameState.cpp
│   ├── LogTailer.cpp
│   ├── Parser.cpp
│   └── main.cpp
```

## 模块说明

- `LogTailer`：以类似 `tail -f` 的方式监听日志文件新增内容。
- `Parser`：先实现一个可扩展的解析骨架，使用简化日志行格式演示事件提取。
- `GameState`：维护当前战棋对局状态，包括回合、英雄、血量、酒馆等级、对手信息和最近一次见到的棋盘。
- `ConsoleUI`：当状态变化时，持续在控制台输出最新结果。
- `main.cpp`：负责启动、轮询读取日志和串联各模块。

## 如何编译

### 方式 1：Windows + g++ (MinGW)

```bash
mkdir build
cd build
g++ -std=c++17 -I../include ../src/main.cpp ../src/LogTailer.cpp ../src/Parser.cpp ../src/GameState.cpp ../src/ConsoleUI.cpp -o BattlegroundsTrackerDemo.exe
```

### 方式 2：Visual Studio / CMake

```bash
mkdir build
cd build
cmake ..
cmake --build . --config Release
```

## 如何运行

```bash
./BattlegroundsTrackerDemo sample_logs/Power.log
```

程序启动后，会从日志文件末尾开始监听。你可以手动向 `sample_logs/Power.log` 追加演示日志行，例如：

```text
BATTLEGROUNDS_ENTER
PLAYER_HERO=Millificent Manastorm
TURN=1
PLAYER_HEALTH=40
PLAYER_TAVERN_TIER=1
OPPONENT_SEEN|opp_1|Patchwerk|38|Patchwerk
OPPONENT_BOARD|opp_1|Scallywag,Harvest Golem
TURN=2
PLAYER_TAVERN_TIER=2
```

## 关键实现思路

### 1. 增量读取日志

`LogTailer` 在初始化时记录文件末尾偏移，后续只读取新增内容。这样就能模拟持续监听 `Power.log` 的效果。

### 2. 先用简化日志格式搭骨架

真实的 Hearthstone 日志比较长、字段复杂，而且不同日志文件的事件要组合起来看。为了先把 demo 跑起来，这一版使用了简化事件格式，比如：

- `BATTLEGROUNDS_ENTER`
- `TURN=3`
- `PLAYER_HERO=...`
- `OPPONENT_SEEN|id|name|health|hero`
- `OPPONENT_BOARD|id|minion1,minion2,...`

后续只要把 `Parser` 中的规则逐步替换成真实日志匹配逻辑即可。

### 3. 状态与解析解耦

- `Parser` 只负责“把一行文本变成事件”。
- `GameState` 只负责“根据事件更新当前状态”。
- `ConsoleUI` 只负责“把状态打印出来”。

这样后面想换成窗口 UI 或增加更多日志来源时，不需要推翻整个结构。

## 后续一步步升级建议

### 第一步：接入真实日志路径

你可以把程序参数换成 Hearthstone 的真实日志文件，例如 Windows 下常见的：

```text
C:\Users\<用户名>\AppData\Local\Blizzard\Hearthstone\Logs\Power.log
```

然后在 `Parser` 里增加针对真实日志关键词的匹配。

### 第二步：增加事件分类

可以把解析事件继续拆细，例如：

- 对战开始 / 结束
- 配对阶段 / 战斗阶段
- 招募阶段开始
- 英雄确认
- 酒馆升级
- 随从上场 / 死亡 / 亡语结算

这样更方便后面做“最近一次见到的对手阵容记录”。

### 第三步：保存更多公开信息

目前只保存最近一次看到的对手棋盘。后续可扩展：

- 每个对手最近 3 次交手时的棋盘快照
- 每回合首次见面时的对手血量
- 对手酒馆等级变化
- 已淘汰玩家列表

### 第四步：加入配置文件

可以增加一个简单的 `config.ini` 或 `config.json` 来保存：

- 日志路径
- 刷新间隔
- 是否输出调试日志
- 是否在状态变化时清屏重绘

### 第五步：扩展为 Win32 小窗口

因为 `Parser` 和 `GameState` 已经独立出来，所以后续只需要：

- 保留后台日志监听循环
- 把 `ConsoleUI` 替换成 `Win32UI`
- 用文本列表、静态控件或自绘方式展示当前回合、血量、酒馆等级和对手棋盘

这样就可以逐步升级成一个轻量的 Windows 桌面小工具。

## 说明

这只是第一版最小 demo，重点是：

- 结构清晰
- 能运行
- 能持续监听日志
- 能维护基础战棋状态
- 方便以后把“简化解析”替换成“真实日志解析”

如果你愿意，下一步我可以继续帮你把 `Parser` 升级成“更接近真实 Power.log 的版本”，先从识别 **回合、英雄、玩家实体、血量标签、酒馆等级标签** 开始。
