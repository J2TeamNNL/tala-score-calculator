# Tá Lả Score Calculator

Ứng dụng Android tính điểm cho trò chơi bài Tá Lả với đầy đủ các luật chơi và tùy chỉnh.

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

### 🃏 Hệ thống cây ăn
- **Cây thứ 1:** +1 điểm cho người ăn, -1 điểm cho người bị ăn
- **Cây thứ 2:** +2 điểm cho người ăn, -2 điểm cho người bị ăn (tăng dần)
- **Cây chốt:** Penalty thêm theo settings (default: 4 điểm)
- **Móm:** Penalty cho người không ăn cây nào (default: 4 điểm)

### ⚙️ Tùy chỉnh đầy đủ
- Tất cả mức penalty có thể điều chỉnh trong Settings
- Lưu cài đặt tự động với SharedPreferences
- Giao diện Material Design thân thiện
- Hỗ trợ navigation qua lại giữa các bước

## 📱 Luồng sử dụng

1. **Nhập tên 4 người chơi**
2. **Chọn ai Ù (hoặc không ai Ù)**
3. **Nếu có Ù:** Chọn loại Ù (thường/khan/tròn)
4. **Nếu không Ù:** Xếp hạng từ Nhất đến Bét
5. **Nhập số cây đã ăn** (nếu cần)
6. **Xem kết quả tính điểm**

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

## 🏗️ Cấu trúc dự án

```
app/
├── src/main/
│   ├── java/com/example/talascore/
│   │   ├── MainActivity.java          # Activity chính với logic tính điểm
│   │   └── SettingsActivity.java      # Activity cài đặt
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml      # Layout chính với ViewFlipper
│   │   │   └── activity_settings.xml  # Layout cài đặt
│   │   ├── values/
│   │   │   ├── strings.xml            # Chuỗi văn bản
│   │   │   ├── colors.xml             # Màu sắc
│   │   │   └── themes.xml             # Theme Material Design
│   │   ├── xml/
│   │   │   └── preferences.xml        # Cấu hình settings
│   │   └── menu/
│   │       └── main_menu.xml          # Menu chính
│   └── AndroidManifest.xml
├── build.gradle                       # Cấu hình build
└── proguard-rules.pro                 # Cấu hình ProGuard
```

## 🎮 Luật chơi chi tiết

### Điểm cơ bản
Điểm được phân bổ theo thứ hạng đảm bảo tổng điểm mỗi ván = 0.

### Ù đền
Khi có người ăn 3 cây để làm người khác Ù, người đó phải trả toàn bộ penalty thay cho tất cả người khác.

### Cây ăn progressive
Cây thứ 2 được tính gấp đôi điểm so với cây thứ 1 để khuyến khích chơi tích cực.

## 🚀 Phiên bản

- **v1.0.0:** Phiên bản đầu tiên với đầy đủ tính năng
- Logic tính điểm chính xác theo luật Tá Lả
- Settings có thể tùy chỉnh mọi penalty
- Giao diện Material Design

## 📄 License

MIT License - xem file LICENSE để biết thêm chi tiết.

## 🤝 Đóng góp

Mọi đóng góp đều được chào đón! Hãy tạo issue hoặc pull request.

## 📞 Liên hệ

Nếu có vấn đề hoặc góp ý, hãy tạo issue trên GitHub.
