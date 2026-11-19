# Phân công code
- `ui/`: giao diện và điều hướng màn hình. Thành viên A phụ trách.
- `data/`: đọc JSON, SQLite, repository. Thành viên A phụ trách.
- `model/`: các lớp dữ liệu dùng chung. Cả nhóm thống nhất trước khi sửa.
- `QuizActivity`: luồng luyện tập, thành viên B phát triển tiếp.
- `StatisticsActivity`: thống kê, lịch sử, câu sai; Việt Anh phát triển tiếp.
- `assets/questions_sample.json`: thay bằng bộ 300 câu hỏi thật.

## Cách làm việc Git
Mỗi người tạo nhánh: `feature/member-a-ui`, `feature/member-b-quiz`, `feature/viet-anh-statistics`. Không sửa trực tiếp `main`; tạo Pull Request để nhóm trưởng duyệt.

