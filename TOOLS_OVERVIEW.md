# MCP Tester Tools Overview

## 📋 Danh sách Công cụ MCP Tester

MCP Tester Server cung cấp 2 công cụ chuyên biệt cho việc đọc file và sinh test tự động. Dưới đây là giới thiệu chi tiết từng công cụ:

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

**MCP Tester** - Công cụ chuyên biệt cho đọc file và sinh test tự động! 🧪✨
