# MCP Tester Tools Overview

## 📋 Danh sách Công cụ MCP Tester

MCP Tester Server cung cấp 12 công cụ testing toàn diện cho AI assistants. Dưới đây là giới thiệu chi tiết từng công cụ:

---

## 🔧 1. generate_test_data
**Mục đích**: Tạo dữ liệu test giả lập cho nhiều loại đối tượng khác nhau

**Parameters**:
- `type` (string): Loại dữ liệu cần tạo
  - `user`: Tạo user data (id, name, email, active, role, createdAt)
  - `product`: Tạo product data (id, name, price, inStock, category, sku)
  - `string`: Tạo string data (value, length, hash)
  - `address`: Tạo address data (id, street, city, state, zipCode, country)
- `count` (integer): Số lượng items cần tạo (1-1000)

**Ví dụ sử dụng**:
```
generate_test_data(type="user", count=5)
generate_test_data(type="product", count=10)
generate_test_data(type="address", count=3)
```

**Output**: Danh sách objects với đầy đủ fields và metadata

---

## 🔍 2. validate_data
**Mục đích**: Validate dữ liệu input theo regex pattern

**Parameters**:
- `data` (string): Dữ liệu cần validate
- `pattern` (string): Regex pattern để kiểm tra

**Ví dụ sử dụng**:
```
validate_data(data="test@example.com", pattern="^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
validate_data(data="123-456-7890", pattern="^\\d{3}-\\d{3}-\\d{4}$")
```

**Output**: Kết quả validation với valid/invalid, message, và data length

---

## 📝 3. generate_test_patterns
**Mục đích**: Tạo các regex patterns phổ biến cho testing

**Parameters**:
- `type` (string): Loại pattern cần tạo
  - `email`: Email patterns (basic, strict, gmail)
  - `phone`: Phone patterns (US, international, general)
  - `url`: URL patterns (HTTP, HTTPS, domain)
  - `date`: Date patterns (ISO, US, EU format)

**Ví dụ sử dụng**:
```
generate_test_patterns(type="email")
generate_test_patterns(type="phone")
generate_test_patterns(type="url")
```

**Output**: Map các patterns theo loại với descriptions

---

## 📊 4. calculate_stats
**Mục đích**: Tính toán thống kê toàn diện cho danh sách số

**Parameters**:
- `numbers` (array): Danh sách số dưới dạng strings

**Ví dụ sử dụng**:
```
calculate_stats(numbers=["1", "2", "3", "4", "5"])
calculate_stats(numbers=["10.5", "20.3", "invalid", "30.1"])
```

**Output**: Thống kê chi tiết (count, sum, average, min, max, median, standard deviation, valid percentage)

---

## 🎭 5. generate_test_scenarios
**Mục đích**: Tạo kịch bản test cho các loại khác nhau

**Parameters**:
- `category` (string): Loại kịch bản
  - `api`: API response scenarios (success, not found, server error)
  - `form`: Form validation scenarios (valid, invalid)
  - `database`: Database query scenarios (success, no results)

**Ví dụ sử dụng**:
```
generate_test_scenarios(category="api")
generate_test_scenarios(category="form")
generate_test_scenarios(category="database")
```

**Output**: Danh sách scenarios với input, expected output, và error cases

---

## 💾 6. store_test_data
**Mục đích**: Lưu trữ dữ liệu test với metadata

**Parameters**:
- `key` (string): Key để định danh dữ liệu
- `value` (any): Giá trị cần lưu (có thể là object, array, string, etc.)

**Ví dụ sử dụng**:
```
store_test_data(key="user_test", value={"name": "Test User", "email": "test@example.com"})
store_test_data(key="config", value={"timeout": 30, "retries": 3})
```

**Output**: Storage key, test ID, và storage information

---

## 🔎 7. get_test_data
**Mục đích**: Truy xuất dữ liệu đã lưu trữ

**Parameters**:
- `key` (string): Key của dữ liệu cần lấy

**Ví dụ sử dụng**:
```
get_test_data(key="user_test_1")
get_test_data(key="config_5")
```

**Output**: Dữ liệu đã lưu hoặc thông báo không tìm thấy

---

## 📋 8. list_test_data
**Mục đích**: Liệt kê tất cả dữ liệu đã lưu trữ

**Parameters**: Không cần

**Ví dụ sử dụng**:
```
list_test_data()
```

**Output**: Tổng quan storage (total keys, storage percentage, preview của data)

---

## 🗑️ 9. clear_test_data
**Mục đích**: Xóa toàn bộ dữ liệu test đã lưu

**Parameters**: Không cần

**Ví dụ sử dụng**:
```
clear_test_data()
```

**Output**: Số lượng items đã xóa và confirmation

---

## 🎲 10. generate_random_strings
**Mục đích**: Tạo chuỗi ngẫu nhiên với tùy chọn nâng cao

**Parameters**:
- `count` (integer): Số lượng chuỗi cần tạo (1-1000)
- `length` (integer): Độ dài mỗi chuỗi (1-1000)
- `charset` (string, optional): Bộ ký tự sử dụng (mặc định: alphanumeric)

**Ví dụ sử dụng**:
```
generate_random_strings(count=5, length=10)
generate_random_strings(count=3, length=8, charset="ABC123")
generate_random_strings(count=2, length=15, charset="!@#$%^&*()")
```

**Output**: Danh sách chuỗi ngẫu nhiên với metadata (hash, length, index)

---

## ⚡ 11. performance_test
**Mục đích**: Benchmark performance các operations khác nhau

**Parameters**:
- `operation` (string): Loại operation cần test
  - `string_concat`: Nối chuỗi
  - `math_calculation`: Tính toán toán học phức tạp
  - `hash_calculation`: Tính hash strings
- `iterations` (integer): Số lần lặp (1-100000)

**Ví dụ sử dụng**:
```
performance_test(operation="string_concat", iterations=10000)
performance_test(operation="math_calculation", iterations=50000)
performance_test(operation="hash_calculation", iterations=25000)
```

**Output**: Performance metrics (duration, ops/second, average time)

---

## 📜 12. get_operation_history
**Mục đích**: Xem lịch sử operations đã thực hiện

**Parameters**: Không cần

**Ví dụ sử dụng**:
```
get_operation_history()
```

**Output**: Lịch sử 100 operations gần nhất với timestamp, success status, và parameters

---

## 🎯 Quy cách sử dụng chung

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
- Operation logging cho debugging

### Limits & Quotas
- Storage: 1000 items tối đa
- History: 100 operations gần nhất
- Data generation: 1000 items tối đa
- Performance test: 100000 iterations tối đa

### Best Practices
1. **Validation**: Luôn validate input trước khi xử lý
2. **Error Handling**: Kiểm tra `success` field trong response
3. **Storage**: Sử dụng descriptive keys cho dễ tìm kiếm
4. **Performance**: Sử dụng limits để tránh overload
5. **History**: Xem operation history để debug issues

---

## 🔗 Integration với Windsurf

1. Copy `windsurf-mcp-config.json` vào Windsurf settings
2. Restart Windsurf
3. Sử dụng tools qua MCP protocol
4. Tools sẽ xuất hiện trong AI assistant interface

---

**MCP Tester Tools** - Bộ công cụ testing toàn diện cho AI development! 🚀
