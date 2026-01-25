# Cloud ☁️ 
<a href="https://wakatime.com/badge/user/f23a8559-7f6d-4531-99a1-da791607b099/project/2c3872bf-e918-4c9e-b60b-43994adc87a4"><img src="https://wakatime.com/badge/user/f23a8559-7f6d-4531-99a1-da791607b099/project/2c3872bf-e918-4c9e-b60b-43994adc87a4.svg" alt="wakatime"></a>

**A comprehensive Java utility library for modern application development.**

Cloud provides battle-tested, production-ready components for database connectivity, caching, networking, file operations, and more. Every feature has proven its value in real-world applications.

---

## 📋 Table of Contents
- [Features](#-features)
- [Quick Start](#-quick-start)
- [Installation](#-installation)
- [Usage Examples](#-usage-examples)
- [Components](#-components)
- [Requirements](#-requirements)
- [Documentation](#-documentation)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

### 🗄️ Database & Caching
- **MySQL** - Classic JDBC and HikariCP connection pooling
- **Redis** - Jedis-based client with connection pooling
- **In-Memory Cache** - TTL-based caching with automatic eviction

### 🌐 Networking
- **NIO Client/Server** - Non-blocking, scalable network communication
- **HTTP Utilities** - Simple HTTP request builder with authentication support
- **Packet Handlers** - Extensible packet processing system

### 🧵 Threading
- **Retry Executor** - Automatic retry logic with configurable delays
- **Callback Support** - Async operations with completion callbacks
- **Custom Thread Abstractions** - CloudThread base class for advanced patterns

### 📁 File Operations
- **Batch File Reading** - Process large files without memory issues
- **ZIP/UNZIP** - Archive handling using Zip4j
- **YAML Config** - Easy configuration file management

### 🔧 Utilities
- **Reflection Helpers** - Type-safe field and method access
- **Data Structures** - QuickSort & BinarySearch with reflection support
- **Name-UUID Manager** - Priority-based multi-storage system
- **Math & Geometry** - 2D/3D utilities
- **Time Utilities** - Epoch and timezone helpers

---

## 🚀 Quick Start

### Add Dependency
```xml
<dependency>
    <groupId>ro.deiutzblaxo</groupId>
    <artifactId>Cloud</artifactId>
    <version>1.3.2.5</version>
</dependency>
```

### Simple MySQL Example
```java
// Connect to database
MySQLConnection connection = new MySQLConnectionHikari.builder()
    .host("localhost")
    .port(3306)
    .database("mydb")
    .username("user")
    .password(System.getenv("DB_PASSWORD"))
    .poolSize(10)
    .build();

// Create manager
MySQLManager manager = new MySQLManagerNormal(connection, 5);

// Query data
String username = manager.get("users", "username", "id", 123, String.class);
```

### Simple Cache Example
```java
// Extend template
public class UserCache extends CacheManagerTemplate<User> {
    public UserCache() {
        super(300); // 5-minute TTL
    }
}

// Use cache
UserCache cache = new UserCache();
cache.putCache("user:123", user);
User cached = cache.getCache("user:123");

// Schedule periodic cleanup
scheduler.scheduleAtFixedRate(cache::evictPeriodically, 60, 60, TimeUnit.SECONDS);
```

### Simple Network Server
```java
// Register handler
PacketDataHandlers.registerHandler(1, MyHandler.class);

// Start server
CloudServer server = new CloudServer(8080);
server.start();

// Handler implementation
public class MyHandler extends StringHandler {
    @Override
    public PacketData process(String data, SocketChannel client) {
        // Process request
        return new PacketData(0, response.getBytes());
    }
}
```

---

## 📦 Installation

### Maven
```xml
<dependency>
    <groupId>ro.deiutzblaxo</groupId>
    <artifactId>Cloud</artifactId>
    <version>1.3.2.5</version>
</dependency>
```

### Build from Source
```bash
git clone https://github.com/yourusername/Cloud.git
cd Cloud
mvn clean install
```

---

## 💡 Usage Examples

### MySQL with HikariCP
```java
// Create connection pool
MySQLConnection connection = MySQLConnectionHikari.builder()
    .host("localhost")
    .port(3306)
    .database("myapp")
    .username("dbuser")
    .password(System.getenv("DB_PASSWORD"))
    .poolSize(20)
    .idleMin(5)
    .prefixPoolName("MyApp")
    .build();

MySQLManager db = new MySQLManagerNormal(connection, 10);

// Insert data
db.insert("users", 
    new String[]{"username", "email"}, 
    new Object[]{"john", "john@example.com"});

// Query data
String email = db.get("users", "email", "username", "john", String.class);

// Check existence
boolean exists = db.exists("users", "username", "john");

// Update data
db.update("users", "username", "john", 
    new String[]{"email"}, 
    new Object[]{"newemail@example.com"});

// Clean up
db.close();
```

### Redis Operations
```java
// Connect to Redis
RedisConnection redis = new RedisConnection("localhost", 6379, null, "password");

// Basic operations
redis.set("session:123", "user_data");
String data = redis.get("session:123");
boolean exists = redis.exist("session:123");
redis.delete("session:123");
```

### Network Client
```java
// Connect to server
CloudClient client = new CloudClient("localhost", 8080);
client.start();

// Send request without callback
client.getGateway().makeRequest(
    new PacketData(1, "Hello Server".getBytes())
);

// Send request with callback
client.getGateway().makeRequestWithCallBack(
    new PacketData(1, "Request data".getBytes()),
    response -> {
        System.out.println("Got response: " + new String(response.getData()));
    }
);
```

### Retry Logic
```java
// Create retry executor (5 attempts, 1 second delay)
RetryExecutor executor = new RetryExecutor(5, 1000);

// Execute with automatic retry
String result = executor.executeWithRetry(() -> {
    return callUnreliableApi(); // Retries on exception
});
```

### YAML Configuration
```java
// Load config
YAMLFile config = new YAMLFileImpl();
config.load("config.yaml");

// Read values
String apiKey = (String) config.get("api.key");
int timeout = (Integer) config.get("timeout");

// Update and save
config.put("last_updated", System.currentTimeMillis());
config.save();
```

### File Operations
```java
// Batch file reading (for large files)
ReadFileInBatch.ReadFileBatch(
    new File("large_file.dat"),
    8192, // 8KB chunks
    bytes -> {
        // Process each chunk
        processData(bytes);
    }
);

// ZIP operations
ArchiveHandler.zip("source_folder", "archive.zip");
ArchiveHandler.unzip("archive.zip", "destination_folder");
```

### Name-UUID Manager
```java
// Create storages with priority
NameUUIDStorage redis = new NameUUIDStorageRedis(redisConn, 100); // High priority cache
NameUUIDStorage mysql = new NameUUIDStorageMySQL(dbManager, 50);  // Medium priority DB
NameUUIDStorage yaml = new NameUUIDStorageYaml("cache.yml", 10);  // Low priority fallback

// Create manager
NameUUIDManager manager = new NameUUIDManager(redis, mysql, yaml);

// Lookup (queries by priority, syncs across storages)
UUID uuid = manager.getUUIDByName("PlayerName");
String name = manager.getNameByUUID(uuid);
```

---

## 🧩 Components

### Database Layer
| Component | Description |
|-----------|-------------|
| `MySQLConnection` | Interface for MySQL connections |
| `MySQLConnectionNormal` | Classic JDBC implementation |
| `MySQLConnectionHikari` | HikariCP connection pool (recommended) |
| `MySQLManager` | Database operations interface |
| `RedisConnection` | Redis client wrapper |

### Caching
| Component | Description |
|-----------|-------------|
| `CacheManager<K,V>` | Cache interface |
| `CacheManagerTemplate<V>` | TTL-based cache implementation |

### Networking
| Component | Description |
|-----------|-------------|
| `CloudServer` | NIO-based server |
| `CloudClient` | NIO-based client |
| `PacketData` | Network packet structure |
| `Handler` | Packet handler base class |
| `PacketDataHandlers` | Handler registry |

### Threading
| Component | Description |
|-----------|-------------|
| `RetryExecutor` | Automatic retry logic |
| `CloudThread<T>` | Thread with callback support |
| `CallBack<T>` | Callback interface |

### File Operations
| Component | Description |
|-----------|-------------|
| `ReadFileInBatch` | Batch file processing |
| `ArchiveHandler` | ZIP/UNZIP utilities |
| `YAMLFile` | YAML configuration |

### Utilities
| Component | Description |
|-----------|-------------|
| `Reflection` | Reflection helper methods |
| `TimeUtils` | Time and epoch utilities |
| `QuickSortReflectByMethodReturn` | Reflection-based sorting |
| `NameUUIDManager` | Name-UUID storage system |

---

## 📋 Requirements

- **Java**: 17 or higher
- **Build Tool**: Maven
- **Optional Dependencies** (based on features used):
  - MySQL JDBC Driver (for MySQL support)
  - Jedis (for Redis support)
  - SnakeYAML (for YAML support)
  - Zip4j (for archive support)
  - Log4j2 (for logging)

---

## 📚 Documentation

### For Developers
- See examples above for common use cases
- Check JavaDoc comments in source code
- Review test examples in `src/test/java/example/`

### For AI Agents
- Comprehensive agent documentation: `.github/agents/Cloud.agent.md`
- Includes architecture flows, troubleshooting, and extension points

### Logging
Configure Log4j2 via `src/main/resources/log4j2.xml`:
```xml
<!-- Example: Enable debug logging for network layer -->
<Logger name="ro.deiutzblaxo.cloud.net.channel" level="DEBUG" additivity="false">
    <AppenderRef ref="Console"/>
</Logger>
```

---

## ⚠️ Security Best Practices

1. **Never hardcode credentials** - Use environment variables:
   ```java
   .password(System.getenv("DB_PASSWORD"))
   ```

2. **Use PreparedStatements** - Already implemented in most methods, but avoid methods that concatenate SQL strings with user input

3. **Validate user input** - Always validate before passing to database/network operations

4. **Secure Redis** - Use password authentication in production

5. **Review handlers** - Network handlers should validate all incoming data

---

## 🛠️ Building and Testing

### Build
```bash
mvn clean install
```

### Run Tests
```bash
mvn test
```

### Package
```bash
mvn package
```

---

## 🤝 Contributing

Contributions are welcome! This library grows based on real-world use cases.

### Guidelines
1. Ensure code follows existing patterns
2. Add tests for new features
3. Update documentation
4. One feature per pull request

---

## 📝 Version History

### 1.3.2.5 (Current)
- Comprehensive utility library for Java applications
- Database: MySQL (classic + HikariCP), Redis
- Networking: NIO-based client/server
- Caching: TTL-based in-memory cache
- Threading: Retry executor, callbacks
- File ops: Batch reading, ZIP, YAML
- Utilities: Reflection, sorting, Name-UUID management

---

## 📄 License

See LICENSE file for details.

---

## 🙋 Support

- **Issues**: Report bugs or request features via GitHub Issues
- **Agent Documentation**: See `.github/agents/Cloud.agent.md` for detailed technical documentation

---

**Made with ☕ by Deiutzblaxo** | *Every feature has a proven use case*
