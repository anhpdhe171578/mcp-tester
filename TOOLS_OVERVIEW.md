# MCP Tester Tools Overview

## 📋 Danh sách Công cụ MCP Tester

MCP Tester Server cung cấp 6 công cụ chuyên biệt cho việc đọc file, sinh test và quản lý chất lượng test. Dưới đây là giới thiệu chi tiết từng công cụ:

---

## 📁 1. read_file
**Mục đích**: Đọc nội dung file để phân tích và tạo test

**Parameters**:
- `filePath` (string): Đường dẫn đến file cần đọc

**Ví dụ sử dụng**:
```
read_file(filePath="src/main/java/com/example/MyClass.java")
read_file(filePath="utils/helper.py")
read_file(filePath="components/Button.js")
```

**Output**: File content với metadata (file size, line count, character count, last modified)

**Features**:
- Đọc file với encoding UTF-8
- Metadata đầy đủ (size, lines, characters)
- Security validation (path traversal prevention)
- File size limit (5MB max)
- Error handling chi tiết

---

## 🧪 2. generate_tests_from_file
**Mục đích**: Tự động sinh test cases từ file source code

**Parameters**:
- `filePath` (string): Đường dẫn đến file source code
- `testType` (string): Loại test cần sinh
  - `unit`: Unit tests cho functions/methods
  - `integration`: Integration tests cho file-level
  - `api`: API tests cho endpoints
  - `validation`: Validation tests cho data
  - `performance`: Performance tests
  - `generic`: Generic tests cho tất cả file types

**Ví dụ sử dụng**:
```
generate_tests_from_file(filePath="src/main/java/Service.java", testType="unit")
generate_tests_from_file(filePath="utils/calculator.py", testType="unit")
generate_tests_from_file(filePath="api/user.js", testType="api")
```

**Output**: Test cases được sinh tự động với:
- Phân tích functions/methods trong file
- Parameters và return types detection
- Test code theo đúng ngôn ngữ (Java JUnit, Python unittest, JavaScript Mocha)
- Specific test logic cho từng scenario (valid inputs, null handling, exceptions)

**Features nâng cao**:
- **Smart Function Detection**: Tự động nhận diện Java methods, Python functions, JavaScript functions
- **Parameter Analysis**: Trích xuất parameters và return types
- **Comprehensive Coverage**: Tạo test cho normal cases, edge cases, error cases
- **Language-specific**: Sinh test code theo chuẩn từng ngôn ngữ
- **Structured Output**: Test cases được group theo method target

**Supported Languages**:
- **Java**: JUnit 5 với Mockito
- **Python**: unittest với pytest compatibility
- **JavaScript**: Mocha/Chai với Sinon
- **Others**: Generic test format

---

## 📊 3. analyze_code_coverage
**Mục đích**: Phân tích code coverage và xác định các khu vực chưa được test

**Parameters**:
- `projectPath` (string): Đường dẫn đến thư mục project
- `coverageType` (string): Loại phân tích coverage (line, branch, method)

**Ví dụ sử dụng**:
```bash
analyze_code_coverage(projectPath="src/main/java", coverageType="line")
analyze_code_coverage(projectPath="src", coverageType="branch")
```

**Output**: Báo cáo coverage chi tiết với:
- Tổng số dòng code
- Số dòng được cover
- Tỷ lệ coverage (%)
- Danh sách các khu vực chưa được cover
- Metrics chi tiết (line, branch, method coverage)

---

## 🎲 4. generate_test_data_from_schema
**Mục đích**: Sinh test data thực tế từ JSON schema hoặc database schema

**Parameters**:
- `schema` (string): JSON schema hoặc database schema
- `count` (integer): Số lượng bản ghi test cần sinh (1-1000)
- `dataProfile` (string): Profile dữ liệu (realistic, edge_case, boundary)

**Ví dụ sử dụng**:
```python
generate_test_data_from_schema(
  schema='{"name": "string", "age": "number", "email": "string"}', 
  count=10, 
  dataProfile="realistic"
)
generate_test_data_from_file(
  schema='{"user": {"type": "object", "properties": {...}}}', 
  count=5, 
  dataProfile="edge_case"
)
```

**Output**: Test data được sinh tự động với:
- Dữ liệu theo schema đã cho
- Profile phù hợp (realistic/edge_case/boundary)
- Metadata cho từng bản ghi
- Thời gian sinh dữ liệu

---

## 📋 5. create_test_plan
**Mục đích**: Tạo kế hoạch test toàn diện từ requirements hoặc user stories

**Parameters**:
- `requirements` (string): Requirements text hoặc user stories
- `testLevels` (array): Các mức độ test (unit, integration, system, acceptance)
- `complexity` (string): Độ phức tạp của project (simple, medium, complex)

**Ví dụ sử dụng**:
```bash
create_test_plan(
  requirements="User should be able to register with email and password",
  testLevels=["unit", "integration", "system"],
  complexity="medium"
)
```

**Output**: Kế hoạch test chi tiết với:
- Test cases được phân tích từ requirements
- Test suites theo từng level
- Ước tính thời gian thực hiện
- Priority và description cho từng test case

---

## 🔍 6. validate_test_quality
**Mục đích**: Validate chất lượng test code và cung cấp gợi ý cải thiện

**Parameters**:
- `testCode` (string): Test code cần validate
- `qualityCriteria` (array): Tiêu chí chất lượng (readability, maintainability, coverage)

**Ví dụ sử dụng**:
```java
validate_test_quality(
  testCode="@Test void testUserLogin() { ... }",
  qualityCriteria=["readability", "maintainability", "coverage"]
)
```

**Output**: Báo cáo chất lượng test với:
- Điểm số theo từng tiêu chí
- Các vấn đề được phát hiện
- Gợi ý cải thiện chi tiết
- Tổng điểm chất lượng overall

---

## 🎯 Quy cách sử dụng chung

### Workflow đề xuất
1. **File Analysis**: Sử dụng `read_file` để preview nội dung file
2. **Test Generation**: Sử dụng `generate_tests_from_file` với testType phù hợp
3. **Code Review**: Review generated test code trước khi sử dụng
4. **Customization**: Tùy chỉnh test logic theo nhu cầu cụ thể

### Response Format
Tất cả tools trả về response theo format chuẩn:
```json
{
  "success": true|false,
  "operation": "tool_name",
  "timestamp": "2024-01-08 12:00:00",
  "data": {...},
  "error": "Error message (nếu có)"
}
```

### Error Handling
- Input validation cho tất cả parameters
- Graceful degradation cho invalid inputs
- Detailed error messages với context
- Security validation cho file paths

### Limits & Quotas
- File reading: 5MB tối đa per file
- Test generation: 100 functions tối đa per file
- Path traversal protection enabled
- UTF-8 encoding validation

### Best Practices
1. **File Analysis**: Luôn dùng `read_file` trước để preview content
2. **Test Type Selection**: Chọn đúng `testType` phù hợp:
   - `unit` cho functions/methods
   - `api` cho endpoints
   - `integration` cho file-level tests
3. **Code Review**: Luôn review generated test code
4. **Security**: Không sử dụng với sensitive files
5. **Performance**: Split large files nếu cần

---

## 🔗 Integration với Windsurf

1. Copy `windsurf-mcp-config.json` vào Windsurf settings
2. Restart Windsurf
3. Sử dụng tools qua MCP protocol
4. Tools sẽ xuất hiện trong AI assistant interface

---

## 🚀 Use Cases

### **Java Development**
```bash
# Read Java file
read_file(filePath="src/main/java/com/example/Service.java")

# Generate unit tests
generate_tests_from_file(filePath="src/main/java/com/example/Service.java", testType="unit")
```

### **Python Development**
```bash
# Read Python file
read_file(filePath="utils/calculator.py")

# Generate unit tests
generate_tests_from_file(filePath="utils/calculator.py", testType="unit")
```

### **JavaScript Development**
```bash
# Read JavaScript file
read_file(filePath="components/Button.js")

# Generate component tests
generate_tests_from_file(filePath="components/Button.js", testType="integration")
```

---

**MCP Tester** - Công cụ chuyên biệt cho đọc file, sinh test và quản lý chất lượng! 🧪✨
