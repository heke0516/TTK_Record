# 三国杀战绩记录

一款用于记录三国杀（标准身份局）对战数据的安卓应用，适合宿舍、聚会等场景使用。

## 功能特性

- **对局记录** — 记录每局的玩家、武将、身份、胜负结果和持续时间
- **计时对局** — 开始对局后自动计时，手动结束计时后进入结果录入
- **身份保密** — 对局进行中不设置身份，结束后再统一录入，符合实际游戏流程
- **同阵营自动胜出** — 选择胜利方阵营后，同阵营玩家自动标记为胜利
- **玩家管理** — 添加、编辑、删除玩家，查看每位玩家的详细战绩统计
- **武将管理** — 预置 48 名常用武将，支持自行添加新武将
- **战绩统计** — 玩家胜率排行、武将胜率排行、阵营胜率排行
- **GPS 定位** — 新建对局时可自动获取地理位置，也可手动输入地点
- **对局备注** — 支持为每局对局添加备注信息
- **战报详情** — 点击对局记录查看详细的玩家身份、武将和胜负信息

## 技术栈

| 技术 | 用途 |
|------|------|
| Kotlin | 开发语言 |
| Jetpack Compose | 声明式 UI 框架 |
| Material 3 | 设计规范与组件 |
| Room | 本地数据库 |
| Navigation Compose | 页面导航 |
| ViewModel + StateFlow | 状态管理 |
| Coroutines | 异步处理 |
| FusedLocationProviderClient | GPS 定位 |

## 身份与阵营

| 身份 | 阵营 | 颜色 |
|------|------|------|
| 主公 | 主公阵营 | 红色 |
| 忠臣 | 主公阵营 | 黄色 |
| 反贼 | 反贼阵营 | 绿色 |
| 内奸 | 内奸阵营 | 蓝色 |

**胜利条件：**
- 主公阵营胜利：主公 + 所有忠臣获胜
- 反贼阵营胜利：所有反贼获胜
- 内奸胜利：内奸获胜

## 项目结构

```
app/src/main/java/com/sanguosha/record/
├── MainActivity.kt
├── SanguoshaApp.kt
├── data/
│   ├── db/AppDatabase.kt
│   ├── entity/          # Player, Hero, Game, GamePlayer
│   ├── dao/             # PlayerDao, HeroDao, GameDao, GamePlayerDao
│   └── repository/      # GameRepository, PlayerRepository, HeroRepository
├── model/Identity.kt
├── ui/
│   ├── theme/           # Color, Theme, Decorations
│   ├── navigation/NavGraph.kt
│   ├── home/            # 主页
│   ├── newgame/         # 新建对局（选玩家 → 选武将 → 对局中 → 录入结果）
│   ├── players/         # 玩家管理与详情
│   ├── heroes/          # 武将管理
│   ├── stats/           # 统计排行
│   └── gamedetail/      # 战报详情
└── util/DateUtils.kt
```

## 构建与运行

**环境要求：**
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 35

**步骤：**

```bash
# 克隆项目
git clone https://github.com/heke0516/TTK_Record
cd TTK_Record

# 用 Android Studio 打开项目，等待 Gradle 同步完成
# 连接手机或启动模拟器，点击 Run 即可
```

**命令行构建 APK：**

```bash
./gradlew assembleDebug
# APK 输出路径：app/build/outputs/apk/debug/app-debug.apk
```

## 权限说明

| 权限 | 用途 |
|------|------|
| `ACCESS_FINE_LOCATION` | 获取精确地理位置（用于自动填入对局地点） |
| `ACCESS_COARSE_LOCATION` | 获取粗略地理位置（辅助定位） |

## 截图

*待补充*

## License

MIT
