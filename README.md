# Tá Lả Score Calculator

Ứng dụng Android tính điểm cho trò chơi bài Tá Lả với logic tính toán chính xác và giao diện thân thiện.

## 🎯 Tính năng chính

### Điểm cơ bản theo thứ hạng (tổng = 0):
- **Nhất:** +6 điểm
- **Nhì:** -1 điểm
- **Ba:** -2 điểm
- **Bét:** -3 điểm

### 🏆 Các loại Ù
- **Ù thường:** Mỗi người thua trả 5 điểm (default), người Ù nhận 15 điểm
- **Ù khan:** Mỗi người thua trả 7 điểm (default), người Ù nhận 21 điểm
- **Ù tròn:** Mỗi người thua trả 6 điểm (default), người Ù nhận 18 điểm
- **Ù đền:** Người ăn 3 cây trả thay cho tất cả

### 🃏 Hệ thống cây ăn progressive
- **Cây thứ 1:** +1 điểm cho người ăn, -1 điểm cho người bị ăn
- **Cây thứ 2:** +2 điểm cho người ăn, -2 điểm cho người bị ăn
- **Cây chốt:** Penalty thêm theo settings (default: 4 điểm)
- **Móm:** Penalty cho người không ăn cây nào (default: 4 điểm)

### ⚙️ Tùy chỉnh đầy đủ
- Tất cả mức penalty có thể điều chỉnh trong Settings
- Lưu cài đặt tự động với SharedPreferences
- Giao diện Material Design với ViewFlipper navigation
- Hỗ trợ back navigation giữa các bước

## 📱 Luồng sử dụng (Flow chuẩn)

### **Có người Ù:**
1. **Nhập tên 4 người chơi**
2. **Chọn ai Ù** → Chọn loại Ù (thường/khan/tròn)
3. **Nếu Ù khan:** Tính điểm ngay (21 điểm)
4. **Nếu Ù thường/tròn:**
   - **Bước 3:** Có Ù đền không? (Ai bị ăn 3 cây?)
   - **Nếu có Ù đền:** Tính điểm ngay
   - **Nếu không Ù đền:** Nhập số cây đã ăn (0-2 cây)
5. **Xem kết quả**

### **Không ai Ù:**
1. **Nhập tên 4 người chơi**
2. **Chọn "Không ai Ù"**
3. **Chọn người chơi đầu tiên**
4. **Xếp hạng từ Nhất đến Bét** (với cây ăn và móm)
5. **Xem kết quả**

## 🔧 Cài đặt

1. Clone repository:
```bash
git clone https://github.com/yourusername/tala-score-calculator.git
```

2. Mở trong Android Studio
3. Build và chạy trên thiết bị Android hoặc emulator

## 📋 Yêu cầu hệ thống

- Android 5.0 (API 21) trở lên
- Android Studio 2022.3.1 trở lên
- Gradle 8.0+
- Java 8+

## 🧪 Testing

### Pure Java Unit Tests
Dự án sử dụng pure Java testing để đảm bảo logic tính điểm chính xác 100%:

```bash
# Chạy standalone Java tests (không cần Android SDK)
cd tests
javac *.java
java ScoreCalculatorTest

# Hoặc chạy test đơn giản
java SimpleJavaTest
```

### Test Coverage
- **✅ Perfect Hand Scenarios:** Ù Khan, Ù Thường, Ù Tròn với eaten cards
- **✅ Ù Đền Logic:** Victim pays for everyone, multiple victims
- **✅ Ranking Scenarios:** Normal ranking với mom, last cards, penalties
- **✅ Edge Cases:** Maximum eaten cards, zero eaten cards, all mom
- **✅ Settings Variations:** High/low penalty configurations
- **✅ Flow Validation:** Complete game flows, UI navigation logic
- **✅ Mathematical Correctness:** Total score = 0 in all scenarios

### Test Files
- `tests/ScoreCalculator.java` - Pure logic (no Android dependencies)
- `tests/ScoreCalculatorTest.java` - Comprehensive test suite (50+ test cases)
- `tests/SimpleJavaTest.java` - Basic test examples
- `tests/README.md` - Detailed testing documentation

### Test Results
```
🧪 RUNNING TÁ LẢ SCORING TESTS
==================================================
✅ ALL TESTS PASSED!
🎯 Scoring logic is working correctly for all scenarios:
   • Ù Khan (no eaten cards)
   • Ù Thường/Ù Tròn (with eaten cards)
   • Ù Đền (someone gave 3 cards)
   • Normal ranking with various eaten cards
   • Edge cases and boundary conditions
   • Different settings configurations
```

## 🏗️ Cấu trúc dự án

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/talascore/
│   │   │   ├── MainActivity.java          # Activity chính (890+ lines)
│   │   │   └── SettingsActivity.java      # Activity cài đặt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml      # Layout với ViewFlipper (8 steps)
│   │   │   │   └── activity_settings.xml  # Layout cài đặt
│   │   │   ├── values/
│   │   │   │   ├── strings.xml            # Chuỗi văn bản tiếng Việt
│   │   │   │   ├── colors.xml             # Màu sắc Material Design
│   │   │   │   └── themes.xml             # Theme tùy chỉnh
│   │   │   ├── xml/
│   │   │   │   └── preferences.xml        # Cấu hình settings
│   │   │   └── menu/
│   │   │       └── main_menu.xml          # Menu chính
│   │   └── AndroidManifest.xml
├── tests/
│   ├── ScoreCalculator.java               # Pure Java logic (no Android deps)
│   ├── ScoreCalculatorTest.java           # Comprehensive test suite (50+ cases)
│   ├── SimpleJavaTest.java                # Basic test examples
│   └── README.md                          # Testing documentation
├── build.gradle                           # Dependencies & build config
└── proguard-rules.pro                     # ProGuard rules
```

## 🎮 Luật chơi chi tiết

### Điểm cơ bản
Điểm được phân bổ theo thứ hạng đảm bảo tổng điểm mỗi ván = 0:
- Nhất: +6, Nhì: -1, Ba: -2, Bét: -3 → Tổng = 0

### Ù đền (Logic đúng)
**Ù đền** = Người **BỊ ĂN** 3 cây phải trả thay cho tất cả người khác.

**Ví dụ:** A Ù thường, D bị ăn 3 cây
- A nhận: +15 điểm (5×3)
- D trả: -15 điểm (thay cho B, C)
- B, C: 0 điểm (không mất gì)

### Cây ăn progressive
- Cây thứ 1: +1 điểm cho người ăn, -1 điểm cho người bị ăn
- Cây thứ 2: +2 điểm cho người ăn, -2 điểm cho người bị ăn
- Logic này khuyến khích chơi tích cực và tạo sự cân bằng

### Settings linh hoạt
Tất cả penalty có thể tùy chỉnh:
- Ù thường: 5 điểm/người (default)
- Ù khan: 7 điểm/người (default)
- Ù tròn: 6 điểm/người (default)
- Móm penalty: 4 điểm (default)
- Cây chốt penalty: 4 điểm (default)

## 🚀 Phiên bản

- **v1.2.0:** Pure Java Testing & Logic Refinement
  - ✅ **Pure Java Tests:** Migrated from Python to standalone Java tests
  - ✅ **ScoreCalculator.java:** Extracted logic without Android dependencies
  - ✅ **50+ Test Cases:** Comprehensive coverage of all scenarios
  - ✅ **Mathematical Validation:** Total score = 0 guaranteed in all cases
  - ✅ **Zero-sum Eaten Cards:** Proper eater gains, victim loses logic
  - ✅ **Ù Đền Refinement:** Victim pays for everyone, winner gets all
  - ✅ **Penalty Redistribution:** Maintains total = 0 with penalties

- **v1.1.0:** Phiên bản cải tiến với Ù đền logic chuẩn
  - ✅ **Sửa logic Ù đền:** Người BỊ ăn 3 cây trả thay (không phải người ăn)
  - ✅ **UI step riêng cho Ù đền:** Checkbox + victim selection
  - ✅ **Flow chuẩn:** 8 steps với navigation rõ ràng
  - ✅ **Logic tính điểm chính xác 100%** theo luật Tá Lả thực tế
  - ✅ **Progressive eaten cards:** Cây 1=1đ, cây 2=2đ
  - ✅ **Comprehensive test suite** với edge cases
  - ✅ **Documentation chi tiết** với business analysis

## 📄 License

MIT License - xem file LICENSE để biết thêm chi tiết.

## 🤝 Đóng góp

Mọi đóng góp đều được chào đón! Hãy tạo issue hoặc pull request.

## 📞 Liên hệ

Nếu có vấn đề hoặc góp ý, hãy tạo issue trên GitHub.
