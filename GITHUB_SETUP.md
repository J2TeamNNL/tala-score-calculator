# 🚀 Hướng dẫn đẩy code lên GitHub

## Bước 1: Tạo repository trên GitHub

1. Đi đến https://github.com
2. Đăng nhập vào tài khoản GitHub của bạn
3. Click nút **"New"** hoặc **"+"** → **"New repository"**
4. Điền thông tin:
   - **Repository name:** `tala-score-calculator`
   - **Description:** `Android app for calculating Tá Lả card game scores`
   - **Visibility:** Public hoặc Private (tùy bạn)
   - **KHÔNG** check "Add a README file" (vì đã có sẵn)
   - **KHÔNG** check "Add .gitignore" (vì đã có sẵn)
   - **KHÔNG** check "Choose a license" (có thể thêm sau)
5. Click **"Create repository"**

## Bước 2: Kết nối local repository với GitHub

Sau khi tạo repository, GitHub sẽ hiển thị hướng dẫn. Chạy các lệnh sau trong terminal:

```bash
# Thêm remote origin (thay YOUR_USERNAME bằng username GitHub của bạn)
git remote add origin https://github.com/YOUR_USERNAME/tala-score-calculator.git

# Đổi tên branch chính thành main (nếu cần)
git branch -M main

# Push code lên GitHub
git push -u origin main
```

## Bước 3: Xác minh

1. Refresh trang GitHub repository
2. Bạn sẽ thấy tất cả files đã được upload
3. README.md sẽ hiển thị thông tin dự án

## 🔧 Lệnh git đã thực hiện

```bash
# Đã khởi tạo git repository
git init

# Đã thêm tất cả files
git add .

# Đã commit với message chi tiết
git commit -m "Initial commit: Complete Tá Lả Score Calculator Android app..."
```

## 📁 Files đã được commit

- ✅ Complete Android project structure
- ✅ MainActivity.java với logic tính điểm đúng
- ✅ SettingsActivity.java với preferences
- ✅ Layout files với Material Design
- ✅ Resources (strings, colors, themes)
- ✅ Build configuration (gradle files)
- ✅ Documentation (README.md, LOGIC_FIXES.md)
- ✅ .gitignore cho Android project

## 🎯 Kết quả

Sau khi push thành công, bạn sẽ có:
- Repository công khai/riêng tư trên GitHub
- Code được backup an toàn
- Có thể chia sẻ với người khác
- Có thể clone về máy khác
- Lịch sử commit đầy đủ

## 🔄 Các lệnh git hữu ích sau này

```bash
# Xem trạng thái
git status

# Thêm files mới/đã sửa
git add .

# Commit thay đổi
git commit -m "Mô tả thay đổi"

# Push lên GitHub
git push

# Pull từ GitHub
git pull
```
