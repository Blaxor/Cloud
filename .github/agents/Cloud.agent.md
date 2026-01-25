# Cloud Java Utility Library — Agent Documentation

## Summary
This document describes the **Cloud** Java utility library (version 1.3.2.5). Cloud is a comprehensive utility library designed to provide reusable components for various Java application development needs. It contains database connectivity (MySQL with HikariCP, Redis), caching, networking (NIO-based client/server), HTTP request utilities, file operations, threading utilities, YAML configuration, data structures, reflection utilities, and more. This library is built with Java 17 and Maven, with dependencies including Jedis (Redis), HikariCP (connection pooling), SnakeYAML, Zip4j, Log4j2, and optional Spigot API support for Minecraft plugins.

---

## Last Updated
- 2026-01-25 - Initial agent documentation created covering all major components, architecture flows, key services, and troubleshooting guidance.

---

## Table of Contents
1. [Summary](#summary)
2. [Last Updated](#last-updated)
3. [Table of Contents](#table-of-contents)
4. [Project at-a-glance](#project-at-a-glance)
5. [Architecture and Data Flow](#architecture-and-data-flow)
6. [Key Source Locations](#key-source-locations)
7. [Quick Start (Developer)](#quick-start-developer)
8. [Configuration and Secrets](#configuration-and-secrets)
9. [Database Components](#database-components)
10. [Caching System](#caching-system)
11. [Network Communication](#network-communication)
12. [Threading Utilities](#threading-utilities)
13. [File Operations](#file-operations)
14. [HTTP Request System](#http-request-system)
15. [Name-UUID Management](#name-uuid-management)
16. [Data Structures and Algorithms](#data-structures-and-algorithms)
17. [Testing](#testing)
18. [Developer Notes and Extension Points](#developer-notes-and-extension-points)
19. [Common Patterns and Idioms](#common-patterns-and-idioms)
20. [Troubleshooting](#troubleshooting)
21. [Where to Look for Logs](#where-to-look-for-logs)
22. [Next Steps and Suggestions](#next-steps-and-suggestions)
23. [Contact / Maintainers](#contact--maintainers)

---

## Project at-a-glance
- **Framework**: Pure Java library (no application framework)
- **Language**: Java 17
- **Build Tool**: Maven
- **Version**: 1.3.2.5
- **Package**: `ro.deiutzblaxo.cloud`
- **Responsibilities**: 
  - Database connectivity and management (MySQL with classic/HikariCP, Redis)
  - In-memory caching with TTL support
  - Non-blocking network communication (NIO-based client/server)
  - HTTP request utilities
  - File operations (batch reading, zip/unzip, YAML config)
  - Threading utilities (retry executor, custom thread abstractions)
  - Name-UUID storage with priority-based retrieval
  - Data structure utilities (QuickSort, BinarySearch with reflection)
  - Math utilities and geometry operations
  - Reflection utilities
- **External Dependencies**:
  - `redis.clients:jedis:3.6.0` - Redis client
  - `com.zaxxer:HikariCP:2.5.1` - JDBC connection pooling
  - `mysql:mysql-connector-java:8.0.28` - MySQL driver
  - `org.yaml:snakeyaml:2.0` - YAML parsing
  - `net.lingala.zip4j:zip4j:2.11.5` - ZIP operations
  - `org.apache.logging.log4j:log4j-api:2.12.4` & `log4j-core:2.12.4` - Logging
  - `commons-io:commons-io:2.14.0` - File utilities (optional)
  - `org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT` - Minecraft plugin support (optional/provided)
  - `org.projectlombok:lombok:1.18.38` - Code generation (optional/provided)

---

## Architecture and Data Flow

### General Pattern
This library provides pluggable components for common Java development tasks. Each module operates independently but can be composed. Key architectural patterns:
- **Interface-based design**: Core functionality exposed through interfaces (`MySQLConnection`, `MySQLManager`, `CacheManager`, `YAMLFile`)
- **Dual implementations**: MySQL offers both classic JDBC and HikariCP connection pooling
- **Asynchronous processing**: Threading utilities support callbacks and retry logic
- **NIO-based networking**: Non-blocking channel I/O for scalable client/server communication

### MySQL Database Flow
1. **Connection Initialization** (Two paths):
   - **Classic**: `MySQLConnectionNormal.connect()` → establishes JDBC connection → stores in `connection` field
   - **HikariCP**: `MySQLConnectionHikari.connect()` → creates `HikariDataSource` → manages connection pool
2. **Manager Setup**: `MySQLManagerNormal(MySQLConnection, nthreads)` → creates `ExecutorService` pool → wraps connection
3. **Query Execution**: 
   - → Client calls `MySQLManager.get(table, valueColumn, keyColumn, key, type)`
   - → Manager calls `getConnection().getConnection()` → retrieves JDBC `Connection`
   - → Creates `PreparedStatement` with parameterized query
   - → Executes query via `executeQuery()` or `executeUpdate()`
   - → Returns result (auto-closes connection via try-with-resources)
4. **Async Operations**: `MySQLManager.getPool()` provides executor for async database tasks

### Redis Caching Flow
1. **Connection Setup**: `RedisConnection(hostname, port, user, password)` → creates `JedisPool` with config
2. **Operations**: 
   - `set(key, value)` → acquires Jedis from pool → `jedis.set()` → releases back to pool
   - `get(key)` → checks `exist(key)` → acquires Jedis → `jedis.get()` → returns value or null
   - `delete(key)` → acquires Jedis → `jedis.del()` → releases
3. **Connection Management**: Pool managed by HikariCP-like pooling config (max 8 connections default)

### In-Memory Cache Flow
1. **Template Setup**: Subclass `CacheManagerTemplate<V>(cacheRetentionSeconds)` → initializes `ConcurrentHashMap<String, Value<V>>`
2. **Cache Put**: `putCache(key, value)` → wraps value with expiration epoch → stores in map
3. **Cache Get**: `getCache(key)` → retrieves `Value` wrapper → returns inner value (null if not found)
4. **Periodic Eviction**: `evictPeriodically()` → iterates map → checks `value.getExpirationEpoch()` vs current time → removes expired entries
   - (Caller must schedule this method, not automatic)

### Network Communication Flow (NIO-based)

#### Server-Side:
1. `CloudServer(port)` → constructor stores port
2. `start()` → opens `Selector` → creates `ServerSocketChannel` → binds to port → configures non-blocking → registers for `OP_ACCEPT`
3. → Starts `ConnectionGateway(selector, serverSocketChannel, 500)` thread (500ms selector timeout)
4. **ConnectionGateway.run()** loop:
   - → Waits for selector events
   - → On `OP_ACCEPT`: accepts client `SocketChannel` → registers for `OP_READ` → starts `ClientHandler` thread
   - → On `OP_READ`: `ClientHandler` reads `PacketData` → looks up registered `Handler` by packet ID → calls `Handler.process()`
   - → Handler returns response `PacketData` → writes back to client channel

#### Client-Side:
1. `CloudClient(hostname, port)` → constructor stores connection details
2. `start()` → opens `SocketChannel` → connects → configures blocking mode → creates `ConnectionGateway(socketChannel)`
3. → Starts `ConnectionGateway` thread
4. **Making Requests**:
   - `makeRequest(PacketData)` → serializes data → writes to server channel → no callback
   - `makeRequestWithCallBack(PacketData, CallBack<PacketData>)` → generates callback ID → stores in map → sends packet → waits for response
5. **ConnectionGateway.run()** loop:
   - → Reads incoming `PacketData` from server
   - → If packet has callback ID: retrieves callback from map → executes `callback.onFinish(packetData)`
   - → Else: processes as normal response

### Retry Executor Flow
1. `RetryExecutor(maxRetries, delayBetweenRetriesMillis)` → constructor sets retry config (defaults: 3 retries, 500ms delay)
2. `executeWithRetry(Supplier<T>)` → attempts `operation.get()`
3. → On exception: increments attempt counter → checks if > MAX_RETRIES → throws `RetryLimitExceededException`
4. → Else: logs warning → sleeps for delay → retries operation

### Name-UUID Management Flow
1. **Setup**: `NameUUIDManager(NameUUIDStorage...)` → stores storages → sorts by priority (descending) using `QuickSortReflectByMethodReturn`
2. → Starts background thread monitoring `ConcurrentLinkedQueue<Pair<UUID, String>>`
3. **Lookup**:
   - `getUUIDByName(name)` → iterates storages (priority order) → calls `storage.getUUIDByName()` → returns first non-null
   - → If found: enqueues `add(name, uuid)` to background thread for cross-storage sync
4. **Background Sync**: Thread polls queue → for each pair → calls `storage.add(name, uuid)` on all storages

### YAML Configuration Flow
1. `YAMLFile.load(path)` → reads file using SnakeYAML → parses into internal map
2. `put(key, value)` → updates internal map (in-memory only)
3. `save()` → serializes internal map via SnakeYAML → writes to last known path
4. `get(key)` → retrieves from internal map

---

## Key Source Locations

### Entry Points & Interfaces
- `src/main/java/ro/deiutzblaxo/cloud/interfaces/AppInterface.java` - Generic application interface template

### Database Layer
- `src/main/java/ro/deiutzblaxo/cloud/data/mysql/MySQLConnection.java` - MySQL connection interface
- `src/main/java/ro/deiutzblaxo/cloud/data/mysql/MySQLManager.java` - Database operations interface with default methods
- `src/main/java/ro/deiutzblaxo/cloud/data/mysql/classic/MySQLConnectionNormal.java` - Classic JDBC connection implementation
- `src/main/java/ro/deiutzblaxo/cloud/data/mysql/classic/MySQLManagerNormal.java` - Classic manager with thread pool
- `src/main/java/ro/deiutzblaxo/cloud/data/mysql/hikari/MySQLConnectionHikari.java` - HikariCP connection pool implementation
- `src/main/java/ro/deiutzblaxo/cloud/data/redis/RedisConnection.java` - Redis client wrapper using Jedis

### Caching
- `src/main/java/ro/deiutzblaxo/cloud/data/cache/interfaces/CacheManager.java` - Cache manager interface
- `src/main/java/ro/deiutzblaxo/cloud/data/cache/template/CacheManagerTemplate.java` - Abstract cache implementation with TTL
- `src/main/java/ro/deiutzblaxo/cloud/data/cache/objects/Value.java` - Cache value wrapper with expiration

### Network Communication
- `src/main/java/ro/deiutzblaxo/cloud/net/channel/network/server/CloudServer.java` - NIO-based server
- `src/main/java/ro/deiutzblaxo/cloud/net/channel/network/server/ConnectionGateway.java` - Server connection handler
- `src/main/java/ro/deiutzblaxo/cloud/net/channel/network/client/CloudClient.java` - NIO-based client
- `src/main/java/ro/deiutzblaxo/cloud/net/channel/network/client/ConnectionGateway.java` - Client connection handler
- `src/main/java/ro/deiutzblaxo/cloud/net/channel/data/objects/PacketData.java` - Network packet data structure
- `src/main/java/ro/deiutzblaxo/cloud/net/channel/processing/PacketDataHandlers.java` - Handler registration
- `src/main/java/ro/deiutzblaxo/cloud/net/channel/processing/handler/Handler.java` - Base packet handler
- `src/main/java/ro/deiutzblaxo/cloud/net/channel/processing/handler/StringHandler.java` - String-based handler

### HTTP Utilities
- `src/main/java/ro/deiutzblaxo/cloud/http/request/Request.java` - HTTP request interface
- `src/main/java/ro/deiutzblaxo/cloud/http/request/RequestMethod.java` - HTTP methods enum
- `src/main/java/ro/deiutzblaxo/cloud/http/request/RequestPrefab.java` - Request builder implementation
- `src/main/java/ro/deiutzblaxo/cloud/http/request/RequestSender.java` - Request execution

### Threading
- `src/main/java/ro/deiutzblaxo/cloud/threads/RetryExecutor.java` - Retry logic executor with exponential backoff
- `src/main/java/ro/deiutzblaxo/cloud/threads/interfaces/CloudThread.java` - Abstract thread with callback support
- `src/main/java/ro/deiutzblaxo/cloud/threads/interfaces/CallBack.java` - Callback interface
- `src/main/java/ro/deiutzblaxo/cloud/threads/CircleThread.java` - Circular pattern thread
- `src/main/java/ro/deiutzblaxo/cloud/threads/SphereThread.java` - Spherical pattern thread

### File Operations
- `src/main/java/ro/deiutzblaxo/cloud/fileutils/batch/ReadFileInBatch.java` - Batch file reading utility
- `src/main/java/ro/deiutzblaxo/cloud/fileutils/batch/Callable.java` - Callback for batch processing
- `src/main/java/ro/deiutzblaxo/cloud/fileutils/zip/ArchiveHandler.java` - ZIP/UNZIP operations using Zip4j
- `src/main/java/ro/deiutzblaxo/cloud/fileutils/zip/FileUtils.java` - File utility helpers
- `src/main/java/ro/deiutzblaxo/cloud/fileutils/communication/Files.java` - File communication utilities
- `src/main/java/ro/deiutzblaxo/cloud/fileutils/ProgramDirectoryUtilities.java` - Program directory helpers

### YAML Configuration
- `src/main/java/ro/deiutzblaxo/cloud/yaml/YAMLFile.java` - YAML file interface
- `src/main/java/ro/deiutzblaxo/cloud/yaml/YAMLFileImpl.java` - YAML implementation using SnakeYAML

### Name-UUID Storage
- `src/main/java/ro/deiutzblaxo/cloud/nus/NameUUIDManager.java` - Priority-based name-UUID manager
- `src/main/java/ro/deiutzblaxo/cloud/nus/NameUUIDStorage.java` - Storage interface
- `src/main/java/ro/deiutzblaxo/cloud/nus/PriorityNUS.java` - Priority wrapper
- `src/main/java/ro/deiutzblaxo/cloud/nus/NusType.java` - Storage type enum
- `src/main/java/ro/deiutzblaxo/cloud/nus/prefab/NameUUIDStorageMySQL.java` - MySQL-backed storage
- `src/main/java/ro/deiutzblaxo/cloud/nus/prefab/NameUUIDStorageRedis.java` - Redis-backed storage
- `src/main/java/ro/deiutzblaxo/cloud/nus/prefab/NameUUIDStorageYaml.java` - YAML-backed storage

### Data Structures & Algorithms
- `src/main/java/ro/deiutzblaxo/cloud/datastructure/QuickSortReflectByMethodReturn.java` - Reflection-based QuickSort by method return value
- `src/main/java/ro/deiutzblaxo/cloud/datastructure/QuickSortReflectByVariable.java` - Reflection-based QuickSort by field value
- `src/main/java/ro/deiutzblaxo/cloud/datastructure/BinarySearchReflect.java` - Reflection-based binary search
- `src/main/java/ro/deiutzblaxo/cloud/datastructure/OrderType.java` - Sort order enum (ASCENDING/DESCENDING)

### Utilities
- `src/main/java/ro/deiutzblaxo/cloud/utils/Reflection.java` - Reflection helper methods
- `src/main/java/ro/deiutzblaxo/cloud/utils/TimeUtils.java` - Time/epoch utilities
- `src/main/java/ro/deiutzblaxo/cloud/utils/CloudLogger.java` - Logging utilities
- `src/main/java/ro/deiutzblaxo/cloud/utils/objects/` - Common data objects (Pair, etc.)

### Math & Geometry
- `src/main/java/ro/deiutzblaxo/cloud/math/Math.java` - Math utilities
- `src/main/java/ro/deiutzblaxo/cloud/math/geometry/twod/` - 2D geometry utilities
- `src/main/java/ro/deiutzblaxo/cloud/math/geometry/threed/` - 3D geometry utilities

### Images
- `src/main/java/ro/deiutzblaxo/cloud/images/ImagesToPoints.java` - Image to point cloud converter
- `src/main/java/ro/deiutzblaxo/cloud/images/Point2DColor.java` - 2D colored point

### Exceptions
- `src/main/java/ro/deiutzblaxo/cloud/expcetions/NoFoundException.java` - Data not found exception
- `src/main/java/ro/deiutzblaxo/cloud/expcetions/PathNotFoundException.java` - File path not found
- `src/main/java/ro/deiutzblaxo/cloud/expcetions/RetryLimitExceededException.java` - Retry exhausted exception
- `src/main/java/ro/deiutzblaxo/cloud/expcetions/TooManyArgs.java` - Argument count mismatch

### Bukkit/Spigot Converters (Optional)
- `src/main/java/ro/deiutzblaxo/cloud/convertos/bukkit/PointsConvertor.java` - Minecraft point conversion utilities

### Configuration
- `src/main/resources/log4j2.xml` - Log4j2 configuration (ERROR level default, INFO for network package)

### Tests
- `src/test/java/example/net/channel/Server.java` - Example server with packet handler
- `src/test/java/example/net/channel/Client.java` - Example client making requests

---

## Quick Start (Developer)
1. **Build**: Use Maven to build the library (`mvn clean install`)
2. **Dependency**: Add as Maven dependency to your project (groupId: `ro.deiutzblaxo`, artifactId: `Cloud`, version: `1.3.2.5`)
3. **Usage**: Import desired packages and use utility classes/interfaces
4. **Database**: Create `MySQLConnection` (classic or HikariCP) → wrap with `MySQLManager` → execute queries
5. **Network**: Create `CloudServer` for server-side or `CloudClient` for client-side → register handlers → start
6. **Caching**: Extend `CacheManagerTemplate` → override if needed → call `putCache/getCache` → schedule `evictPeriodically()`
7. **YAML**: Create `YAMLFile` instance → `load(path)` → `get/put` → `save()`

---

## Configuration and Secrets

### Logging Configuration
📄 **File**: `src/main/resources/log4j2.xml`

**Log Levels**:
- **Root**: ERROR level (default: minimal logging)
- **Network Package** (`ro.deiutzblaxo.cloud.net.channel`): INFO level (explicit: shows connection events)

**Pattern**: `%d{yyyy-MM-dd HH:mm:ss}-[%t]-%level-[%logger{1}.class:%line] - %msg%n`

**Appenders**: Console only (SYSTEM_OUT)

### MySQL Configuration
No configuration files - configured programmatically via constructor/builder:

**MySQLConnectionNormal** (Classic JDBC):
- `host` - Database hostname (required, no default)
- `port` - Database port (required, no default, typically 3306)
- `database` - Database name (required, no default)
- `username` - Database username (required, no default) 🔒
- `password` - Database password (required, no default) 🔒
- `params` - JDBC connection parameters (optional, default: empty string)

**MySQLConnectionHikari** (Connection Pooling):
- Same as above, plus:
- `poolSize` - Connection pool size (optional, verify in `HikariDataSource` defaults)
- `idleMin` - Minimum idle connections (optional, verify in `HikariDataSource` defaults)
- `prefixPoolName` - Pool name prefix (optional, default: "MySQLConnectionHikari")

**MySQLManager**:
- `nthreads` - Thread pool size for async operations (required, no default)

#### Security Notes
🔒 **Database Credentials**: MySQL username/password are passed via constructor
  - Risk: High - if hardcoded in source code
  - Recommendation: Load from environment variables or secure config files outside version control
  - Example: `System.getenv("DB_PASSWORD")`

### Redis Configuration
Configured programmatically via constructor:
- `hostname` - Redis server hostname (required, no default)
- `port` - Redis server port (required, no default, typically 6379)
- `user` - Redis username (optional, can be null for Redis < 6.0)
- `password` - Redis password (optional, can be null for no auth) 🔒

**Jedis Pool Config** (built internally):
- `maxTotal` - Max connections (default: 8, verify in `buildPoolConfig()`)
- `maxIdle` - Max idle connections (default: 8, verify in `buildPoolConfig()`)
- `minIdle` - Min idle connections (default: 0, verify in `buildPoolConfig()`)

#### Security Notes
🔒 **Redis Credentials**: Password passed via constructor
  - Risk: High - if hardcoded
  - Recommendation: Load from environment variables

### Cache Configuration
**CacheManagerTemplate**:
- `cacheRetentionSeconds` - TTL for cache entries (required, passed to constructor)
- Cache uses UTC timezone for expiration calculations (hardcoded in `TimeUtils` calls)

### Network Configuration
**CloudServer**:
- `port` - Server listening port (required, no default)
- Selector timeout: 500ms (hardcoded in `ConnectionGateway` constructor)

**CloudClient**:
- `hostname` - Server hostname/IP (required, no default)
- `port` - Server port (required, no default)

### Retry Executor Configuration
**RetryExecutor**:
- `MAX_RETRIES` - Number of retry attempts (default: 3)
- `DELAY_BETWEEN_RETRIES_MILLIS` - Delay between retries (default: 500ms)

---

## Database Components

### MySQL Connection Types

| Implementation | Location | Purpose | Key Features |
|----------------|----------|---------|--------------|
| `MySQLConnection` | `data/mysql/MySQLConnection.java` | Interface | Defines connection contract, DEFAULT_PREFIX: `"jdbc:mysql://"` |
| `MySQLConnectionNormal` | `data/mysql/classic/MySQLConnectionNormal.java` | Classic JDBC | Single connection, manual reconnection, suitable for low-traffic apps |
| `MySQLConnectionHikari` | `data/mysql/hikari/MySQLConnectionHikari.java` | Connection Pooling | Uses HikariCP, supports builder pattern via Lombok `@Builder`, production-ready |

### MySQL Manager Operations

**MySQLManager Interface**: `data/mysql/MySQLManager.java`

| Method | Parameters | Returns | Description |
|--------|------------|---------|-------------|
| `insert()` | `table, columns[], values[]` | void | Insert row with parameterized statement |
| `exists()` | `table, column, value` | boolean | Check if row exists (returns `ResultSet.next()`) |
| `update()` | `table, keyColumn, key, valueColumns[], values[]` | void | Update row by key |
| `get()` | `table, valueColumn, keyColumn, key, type` | `<T>` | Retrieve single value with type casting |
| `gets()` | `table, valueColumns[], keyColumn, key, types[]` | `HashMap<String, Object>` | Retrieve multiple columns |
| `getString()` | `table, valueColumn, keyColumn, key` | String | Convenience method for `get()` with String type |
| `createTable()` | `table, columns...` | void | Create table with IF NOT EXISTS |
| `createDataBase()` | `database` | void | Create database with IF NOT EXISTS |
| `deleteRow()` | `table, byField, field` | void | Delete row by field match |
| `delete()` | `table, column, value` | void | Delete row using PreparedStatement |
| `setNull()` | `table, byField, search, fieldToSet` | void | Set field to NULL by search criteria |
| `getPreparedStatement()` | `sql` | ResultSet | Execute parameterized query |
| `getPrepareStatement()` | `sql` | PreparedStatement | Get PreparedStatement for custom queries |
| `getConnection()` | - | MySQLConnection | Get underlying connection |
| `getPool()` | - | ExecutorService | Get thread pool for async operations |
| `close()` | - | void | Shutdown pool and close connection |

**Key Behaviors**:
- All queries use try-with-resources for auto-closing connections
- PreparedStatements used for SQL injection protection
- Exception handling: prints stack trace, returns null/false on error (not thrown)
- ⚠️ **Security**: Some methods like `exists()` use string concatenation (e.g., `WHERE column='value'`), should use parameterized queries

### Redis Operations

**RedisConnection Class**: `data/redis/RedisConnection.java`

| Method | Parameters | Returns | Description |
|--------|------------|---------|-------------|
| `connect()` | `hostname, port, user, password` | void | Initialize Jedis pool (called by constructor) |
| `set()` | `key, value` | void | Set string value (try-with-resources for Jedis) |
| `get()` | `key` | String | Get string value (returns null if not found or "nil") |
| `delete()` | `key` | void | Delete key |
| `exist()` | `key` | boolean | Check if key exists |
| `getJedisPool()` | - | JedisPool | Get underlying pool for advanced operations |

**Connection Logic**:
- If password is null or empty → no-auth pool
- If user is null or empty → password-only auth (Redis < 6.0 style)
- Else → username+password auth (Redis 6.0+)

---

## Caching System

### Cache Components

| Component | Location | Purpose |
|-----------|----------|---------|
| `CacheManager<K, V>` | `data/cache/interfaces/CacheManager.java` | Interface defining cache operations |
| `CacheManagerTemplate<V>` | `data/cache/template/CacheManagerTemplate.java` | Abstract implementation with TTL support using `ConcurrentHashMap` |
| `Value<V>` | `data/cache/objects/Value.java` | Wrapper holding cached value + expiration epoch |

### Cache Operations

| Operation | Method | Behavior |
|-----------|--------|----------|
| Put | `putCache(key, value)` | Stores value with expiration = `currentTime + cacheRetentionSeconds` (UTC) |
| Get | `getCache(key)` | Returns value (does NOT check expiration on retrieval) |
| Evict | `evictCache(key)` | Removes single entry |
| Reset | `resetCache()` | Clears entire cache |
| Periodic Eviction | `evictPeriodically()` | Iterates map, removes expired entries (must be scheduled manually) |
| Size | `size()` | Returns current cache entry count |

**Important**: 
- `getCache()` does NOT auto-evict expired entries; stale data may be returned
- Caller MUST schedule `evictPeriodically()` (e.g., via timer thread) for cleanup
- Uses UTC timezone for all time calculations

---

## Network Communication

### Server Architecture

**CloudServer** (`net/channel/network/server/CloudServer.java`):
- Opens `ServerSocketChannel` → non-blocking mode → registers `OP_ACCEPT` with `Selector`
- Delegates to `ConnectionGateway` thread for event loop
- Port binding: `InetAddress.getLocalHost()` (binds to local hostname, not 0.0.0.0)

**Server ConnectionGateway** (`net/channel/network/server/ConnectionGateway.java`):
- Runs selector loop with 500ms timeout
- On `OP_ACCEPT`: accepts client → registers `OP_READ` → spawns `ClientHandler` thread per client
- `ClientHandler`: reads `PacketData` → looks up handler via `PacketDataHandlers.getHandler(packetId)` → calls `handler.process()` → writes response

### Client Architecture

**CloudClient** (`net/channel/network/client/CloudClient.java`):
- Opens `SocketChannel` → blocking mode → connects to server
- Starts `ConnectionGateway` thread

**Client ConnectionGateway** (`net/channel/network/client/ConnectionGateway.java`):

| Method | Parameters | Returns | Description |
|--------|------------|---------|-------------|
| `makeRequest()` | `PacketData` | void | Send request without waiting for response |
| `makeRequestWithCallBack()` | `PacketData, CallBack<PacketData>` | void | Send request, register callback, wait for response with matching ID |

**Callback Mechanism**:
1. Client generates unique callback ID
2. Stores callback in `ConcurrentHashMap<Integer, CallBack<PacketData>>`
3. Sends packet with callback ID
4. Server processes and returns packet with same callback ID
5. Client receives response → retrieves callback by ID → executes `callback.onFinish(packetData)`

### Packet Data Structure

**PacketData** (`net/channel/data/objects/PacketData.java`):
- `id` (int) - Packet type identifier
- `data` (byte[]) - Payload
- `callbackId` (Integer, optional) - For callback-based requests

### Handler Registration

**PacketDataHandlers** (`net/channel/processing/PacketDataHandlers.java`):
- `registerHandler(int id, Class<? extends Handler> handlerClass)` - Register handler for packet ID
- Handlers must extend `Handler` or `StringHandler` (for string payloads)
- Example: `PacketDataHandlers.registerHandler(1, MyHandler.class)`

**Handler Interface** (`net/channel/processing/handler/Handler.java`):
- `process(PacketData, SocketChannel)` → returns `PacketData` response

---

## Threading Utilities

### Thread Components

| Component | Location | Purpose | Key Methods |
|-----------|----------|---------|-------------|
| `RetryExecutor` | `threads/RetryExecutor.java` | Retry logic with exponential backoff | `executeWithRetry(Supplier<T>)` |
| `CloudThread<T>` | `threads/interfaces/CloudThread.java` | Abstract thread with callback | `finish(CallBack<T>, T)`, `run()` |
| `CallBack<T>` | `threads/interfaces/CallBack.java` | Callback interface | `onFinish(T)` |
| `CircleThread` | `threads/CircleThread.java` | Circular pattern generator | (domain-specific) |
| `SphereThread` | `threads/SphereThread.java` | Spherical pattern generator | (domain-specific) |

### Retry Executor Details

**Configuration**:
- Default: 3 retries, 500ms delay
- Custom: `new RetryExecutor(maxRetries, delayMillis)`

**Flow**:
1. Attempts operation via `Supplier<T>`
2. On exception: logs attempt number + exception details (WARN level)
3. Sleeps for `DELAY_BETWEEN_RETRIES_MILLIS`
4. Retries up to `MAX_RETRIES` times
5. If all fail: logs ERROR → throws `RetryLimitExceededException` (wraps original exception)

**Usage Example**:
```java
RetryExecutor executor = new RetryExecutor(5, 1000); // 5 retries, 1s delay
String result = executor.executeWithRetry(() -> riskyOperation());
```

---

## File Operations

### File Components

| Component | Location | Purpose |
|-----------|----------|---------|
| `ReadFileInBatch` | `fileutils/batch/ReadFileInBatch.java` | Read large files in chunks |
| `Callable` | `fileutils/batch/Callable.java` | Callback interface for batch processing (`processBytes(byte[])`) |
| `ArchiveHandler` | `fileutils/zip/ArchiveHandler.java` | ZIP/UNZIP operations using Zip4j |
| `FileUtils` | `fileutils/zip/FileUtils.java` | File utilities |
| `Files` | `fileutils/communication/Files.java` | File communication utilities |
| `ProgramDirectoryUtilities` | `fileutils/ProgramDirectoryUtilities.java` | Program directory helpers |

### Archive Operations

| Method | Parameters | Throws | Description |
|--------|------------|--------|-------------|
| `zip()` | `src, dst` | `ZipException, FileNotFoundException` | Zips all files/folders in `src` directory to `dst` ZIP file |
| `unzip()` | `zipFilePath, dst` | `ZipException` | Extracts ZIP to `dst` directory |

**Zip Behavior**:
- Iterates `srcFile.listFiles()`
- Folders → `zipFile.addFolder()`
- Files → `zipFile.addFile()`
- Throws `FileNotFoundException` if `src` doesn't exist

### Batch File Reading

**ReadFileInBatch.ReadFileBatch()**:
- Parameters: `File file, int batchSize, Callable callable`
- Reads file in `batchSize` byte chunks
- Calls `callable.processBytes(buffer)` for each chunk
- Handles partial last buffer (adjusts buffer size if `rc < batchSize`)
- Use case: Processing large files without loading entire file into memory

---

## HTTP Request System

### HTTP Components

| Component | Location | Purpose |
|-----------|----------|---------|
| `Request` | `http/request/Request.java` | Request interface (headers, body, URL, method, auth) |
| `RequestMethod` | `http/request/RequestMethod.java` | Enum for HTTP methods (GET, POST, PUT, DELETE, etc.) |
| `RequestPrefab` | `http/request/RequestPrefab.java` | Request builder implementation |
| `RequestSender` | `http/request/RequestSender.java` | Executes HTTP requests |

### Request Interface

| Method | Returns | Description |
|--------|---------|-------------|
| `getHeader()` | `HashMap<String, String>` | Get headers |
| `setHeader(headers)` | `Request` | Set headers (builder pattern) |
| `getBody()` | String | Get request body |
| `setBody(body)` | `Request` | Set request body |
| `getURL()` | URL | Get target URL |
| `setURL(url)` | `Request` | Set target URL |
| `getRequestMethod()` | RequestMethod | Get HTTP method |
| `setRequestMethod(method)` | `Request` | Set HTTP method |
| `setAuthenticator(auth)` | `Request` | Set authenticator (for Basic/Digest auth) |
| `getAuthenticator()` | Authenticator | Get authenticator |
| `hasAuthenticator()` | boolean | Check if authenticator is set |

**Usage Pattern**: Builder-style chaining (e.g., `request.setURL(url).setMethod(GET).setHeader(headers)`)

---

## Name-UUID Management

### NUS Components

| Component | Location | Purpose |
|-----------|----------|---------|
| `NameUUIDManager` | `nus/NameUUIDManager.java` | Priority-based multi-storage manager |
| `NameUUIDStorage` | `nus/NameUUIDStorage.java` | Storage interface |
| `PriorityNUS` | `nus/PriorityNUS.java` | Priority wrapper |
| `NusType` | `nus/NusType.java` | Storage type enum |
| `NameUUIDStorageMySQL` | `nus/prefab/NameUUIDStorageMySQL.java` | MySQL-backed implementation |
| `NameUUIDStorageRedis` | `nus/prefab/NameUUIDStorageRedis.java` | Redis-backed implementation |
| `NameUUIDStorageYaml` | `nus/prefab/NameUUIDStorageYaml.java` | YAML-backed implementation |

### Manager Operations

**NameUUIDManager**:
- Constructor: `NameUUIDManager(NameUUIDStorage...)` → sorts storages by priority (descending)
- Starts background thread for async write queue

| Method | Parameters | Returns | Throws | Description |
|--------|------------|---------|--------|-------------|
| `getNameByUUID()` | `UUID` | String | `NoFoundException` | Queries storages in priority order, returns first match, enqueues add for sync |
| `getUUIDByName()` | `String` | UUID | `NoFoundException` | Queries storages in priority order, returns first match, enqueues add for sync |
| `getRealName()` | `String` | String | - | Returns UUID string by name (no exception if not found) |
| `addStorage()` | `NameUUIDStorage` | void | - | Add storage and re-sort by priority |
| `add()` | `name, uuid` | void | - | Enqueue to write queue (async) |

**Priority System**:
- Higher priority = queried first
- Typical setup: Redis (high priority, fast cache) → MySQL (medium, persistent) → YAML (low, fallback)
- Background thread ensures all storages eventually have same data

**NameUUIDStorage Interface**:
- `getNameByUUID(UUID)` → String
- `getUUIDByName(String)` → String (UUID as string)
- `add(name, uuid)` → void
- `getPriority()` → int (for sorting)

---

## Data Structures and Algorithms

### Sorting & Search Components

| Component | Location | Purpose |
|-----------|----------|---------|
| `QuickSortReflectByMethodReturn` | `datastructure/QuickSortReflectByMethodReturn.java` | QuickSort using method return value as key |
| `QuickSortReflectByVariable` | `datastructure/QuickSortReflectByVariable.java` | QuickSort using field value as key |
| `BinarySearchReflect` | `datastructure/BinarySearchReflect.java` | Binary search using reflection |
| `OrderType` | `datastructure/OrderType.java` | Enum: ASCENDING, DESCENDING |

### QuickSort By Method Return

**Signature**: `QuickSortReflectByMethodReturn.sort(ArrayList<T> list, int low, int high, String methodName, OrderType order)`

**How it works**:
1. Uses reflection to call `methodName` on each object (must return `Comparable`)
2. Compares return values for pivot selection
3. Sorts in-place
4. Throws: `NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException`

**Example**: Sorting `NameUUIDStorage` list by `getPriority()` method in descending order

### QuickSort By Variable

**Signature**: `QuickSortReflectByVariable.sort(ArrayList<T> list, int low, int high, String fieldName, OrderType order)`

**How it works**:
1. Uses reflection to access field value (must be `Comparable`)
2. Sorts by field value
3. Same exception handling as method-based sort

---

## Testing

### Test Locations
- `src/test/java/example/net/channel/Server.java` - Example NIO server with custom handler
- `src/test/java/example/net/channel/Client.java` - Example NIO client making requests

### Server Test
**Features**:
- Starts `CloudServer` on port 1234
- Registers handler for packet ID 1 (`Handler1.class`)
- Handler extends `StringHandler` → processes string data → returns JSON-style response

**Handler Implementation**:
```java
public class Handler1 extends StringHandler {
    public PacketData process(String data, SocketChannel clientChannel) {
        logger.error("We got the following data: " + data);
        return new PacketData(0, "This is a imaginary json response :D");
    }
}
```

### Client Test
**Features**:
- Connects to localhost:1234
- Makes normal request (no callback)
- Makes request with callback (logs when callback executes)

---

## Developer Notes and Extension Points

### Key Services and Interfaces

| Service/Interface | Location | Purpose | Key Methods/Extension Points |
|-------------------|----------|---------|------------------------------|
| `MySQLManager` | `data/mysql/MySQLManager.java` | Database operations | All default methods; implement for custom queries; `getConnection()`, `getPool()` for direct access |
| `CacheManager<K,V>` | `data/cache/interfaces/CacheManager.java` | Cache operations | Implement for custom cache backends (Redis, Memcached) |
| `CacheManagerTemplate<V>` | `data/cache/template/CacheManagerTemplate.java` | TTL cache | Extend and override methods; default uses `ConcurrentHashMap` |
| `Request` | `http/request/Request.java` | HTTP request | Implement for custom HTTP client (e.g., OkHttp wrapper) |
| `YAMLFile` | `yaml/YAMLFile.java` | Config files | Implement for custom config formats (JSON, TOML) |
| `NameUUIDStorage` | `nus/NameUUIDStorage.java` | Name-UUID storage | Implement for custom backends; must provide `getPriority()` |
| `Handler` | `net/channel/processing/handler/Handler.java` | Network packet handler | Extend for custom packet processing; `process(PacketData, SocketChannel)` |
| `CloudThread<T>` | `threads/interfaces/CloudThread.java` | Custom threading | Extend for callback-based async operations; implement `run()` and call `finish()` |

### Architecture Patterns

**Interface-First Design**:
- Core abstractions defined as interfaces (`MySQLConnection`, `MySQLManager`, `CacheManager`)
- Multiple implementations provided (classic JDBC vs HikariCP)
- Extension point: Implement interfaces for alternative backends

**Builder Pattern**:
- `MySQLConnectionHikari` uses Lombok `@Builder`
- `Request` uses fluent setter methods
- Pattern: Chain method calls for configuration

**Template Method Pattern**:
- `CacheManagerTemplate` provides base implementation
- Subclasses override specific behaviors
- Example: Custom eviction strategies, different time sources

**Repository Pattern**:
- `MySQLManager` acts as repository for database access
- Encapsulates SQL details
- Extension: Add domain-specific repositories wrapping `MySQLManager`

**Handler Pattern** (Network):
- `PacketDataHandlers` registry maps packet IDs to handler classes
- Handlers process packets and return responses
- Extension: Register custom handlers for new packet types

**Priority Queue Pattern** (NUS):
- `NameUUIDManager` queries storages by priority
- Background thread syncs across all storages
- Extension: Implement priority-based fallback for other resources

### Extension Points

1. **Database Layer**:
   - Implement `MySQLConnection` for other JDBC drivers (PostgreSQL, Oracle)
   - Extend `MySQLManager` for domain-specific query builders
   - Add NoSQL implementations (MongoDB, Cassandra)

2. **Caching Layer**:
   - Extend `CacheManagerTemplate` for distributed caches (Hazelcast, Redis)
   - Implement `CacheManager` for write-through/write-behind strategies
   - Add cache warming/preloading logic

3. **Network Layer**:
   - Implement custom `Handler` classes for new packet types
   - Add encryption/compression to `PacketData` serialization
   - Extend for WebSocket or HTTP protocol support

4. **Name-UUID Storage**:
   - Implement `NameUUIDStorage` for custom backends (MongoDB, Cassandra, API)
   - Add caching layer between manager and storages
   - Implement distributed locking for concurrent writes

5. **File Operations**:
   - Extend `ReadFileInBatch` for parallel processing
   - Implement custom `Callable` for specific file formats (CSV, XML, binary)
   - Add streaming support for cloud storage (S3, GCS)

---

## Common Patterns and Idioms

### Resource Management
- **Try-with-resources**: All database operations use try-with-resources for auto-closing connections
  - Example: `try (Connection connection = getConnection().getConnection()) { ... }`
  - Pattern: Ensures connection returned to pool even on exception

### Thread Safety
- **ConcurrentHashMap**: Used for cache storage and callback registry
  - Thread-safe without explicit locking
  - Pattern: `ConcurrentHashMap<String, Value<V>>` in `CacheManagerTemplate`
- **ConcurrentLinkedQueue**: Used for async write queue in `NameUUIDManager`
  - Lock-free queue for producer-consumer pattern

### Error Handling
- **Silent Failures**: Most methods catch exceptions and log stack traces, return null/false
  - Pattern: `catch (SQLException e) { e.printStackTrace(); return null; }`
  - ⚠️ **Note**: Errors may be swallowed; check logs for issues
- **Explicit Exceptions**: Some methods throw custom exceptions (`NoFoundException`, `TooManyArgs`, `RetryLimitExceededException`)
  - Pattern: Use checked exceptions for expected error cases

### Reflection Usage
- **Generic Sorting**: `QuickSortReflectByMethodReturn` sorts objects by method return value
  - Pattern: `Method method = object.getClass().getMethod(methodName); method.invoke(object);`
  - Use case: Sorting heterogeneous lists without Comparable implementation
- **Field Access**: `Reflection.getVariableByName()` retrieves private fields
  - Pattern: `field.setAccessible(true); value = field.get(object); field.setAccessible(original);`
  - Security: Restores original accessibility state

### Builder Pattern
- **Lombok @Builder**: `MySQLConnectionHikari` uses Lombok for builder
  - Pattern: `MySQLConnectionHikari.builder().host("localhost").port(3306).build()`
- **Fluent Setters**: `Request` interface uses chainable setters
  - Pattern: `request.setURL(url).setMethod(GET).setHeader(headers)`

### Callback Pattern
- **Network Requests**: `CloudClient.makeRequestWithCallBack(packet, callback)`
  - Callback stored in map with unique ID
  - Pattern: `callback.onFinish(response)` executed when response arrives
- **Thread Completion**: `CloudThread.finish(callback, value)`
  - Pattern: Custom threads signal completion to caller

### Dependency Injection
- **Constructor Injection**: Most classes receive dependencies via constructor
  - Example: `MySQLManagerNormal(MySQLConnection connection, int nthreads)`
  - Pattern: Dependencies explicit, testable

---

## Troubleshooting

### Database Issues

**Issue**: `NullPointerException` when calling `MySQLManager.get()`
- **Cause**: Connection not established or connection pool exhausted
- **Solution**: 
  - Check `MySQLConnection.connect()` was called
  - Verify connection parameters (host, port, credentials)
  - Check HikariCP pool size vs concurrent requests
  - Look for "Communications link failure" in logs

**Issue**: `SQLException: No suitable driver found`
- **Cause**: MySQL JDBC driver not in classpath
- **Solution**: Verify `mysql:mysql-connector-java:8.0.28` dependency in pom.xml

**Issue**: Queries return null unexpectedly
- **Cause**: Exception caught and swallowed (prints stack trace, returns null)
- **Solution**: Check console/log for stack traces; exceptions not propagated

**Issue**: SQL injection vulnerability
- **Cause**: Some `MySQLManager` methods use string concatenation (e.g., `exists()`)
- **Solution**: Use methods with PreparedStatements; avoid `exists()` method for user input

### Caching Issues

**Issue**: Cache returns stale/expired data
- **Cause**: `evictPeriodically()` not being called; `getCache()` doesn't check expiration
- **Solution**: Schedule periodic eviction (e.g., `ScheduledExecutorService` every N seconds)

**Issue**: Cache grows unbounded
- **Cause**: No eviction scheduled or retention time too long
- **Solution**: Schedule `evictPeriodically()`, reduce `cacheRetentionSeconds`, or add max size limit

**Issue**: `ConcurrentModificationException` in cache
- **Cause**: Unlikely (uses `ConcurrentHashMap`), but possible if iterating during modification
- **Solution**: Already using thread-safe collection; check for external iteration

### Network Issues

**Issue**: `BindException: Address already in use`
- **Cause**: Server already running on port or port not released
- **Solution**: Check if server already started, change port, or wait for OS to release port

**Issue**: Client callback never executes
- **Cause**: Server not returning packet with callback ID, or callback ID mismatch
- **Solution**: Verify server handler returns `PacketData` with same callback ID; check logs for exceptions in `ConnectionGateway`

**Issue**: `ConnectionGateway` thread dies silently
- **Cause**: Unhandled exception in `run()` loop
- **Solution**: Check logs for exceptions; wrap loop body in try-catch for debugging

**Issue**: Server can't accept connections
- **Cause**: `ServerSocketChannel` registered for wrong operation or selector not looping
- **Solution**: Verify `OP_ACCEPT` registration; check `ConnectionGateway` thread started; look for "Cloud Server started" log

### Redis Issues

**Issue**: `JedisConnectionException: Could not get a resource from the pool`
- **Cause**: Redis server down, wrong host/port, or pool exhausted
- **Solution**: Verify Redis server running, check connection parameters, increase pool size in `buildPoolConfig()`

**Issue**: `NOAUTH Authentication required`
- **Cause**: Redis requires password but none provided, or wrong password
- **Solution**: Pass correct password to `RedisConnection` constructor; check Redis `requirepass` config

**Issue**: `RedisConnection.get()` returns "nil" as string
- **Cause**: Jedis returns "nil" for missing keys (not null)
- **Solution**: Code checks `value == "nil" ? null : value`, but verify behavior

### Threading Issues

**Issue**: `RetryLimitExceededException` thrown
- **Cause**: Operation failed after max retries
- **Solution**: Check wrapped exception in `RetryLimitExceededException.getCause()`; increase max retries or fix underlying issue

**Issue**: Thread interrupted during retry
- **Cause**: `Thread.sleep()` interrupted
- **Solution**: Code restores interrupt status but continues retries; check if interrupt intentional

**Issue**: `NameUUIDManager` background thread consumes CPU
- **Cause**: Infinite loop without sleep when queue empty
- **Solution**: ⚠️ **Bug**: Background thread has `while(true)` with no sleep when queue empty; should add `Thread.sleep()` or use blocking queue

### File Operation Issues

**Issue**: `FileNotFoundException` in `ArchiveHandler.zip()`
- **Cause**: Source path doesn't exist
- **Solution**: Verify source path exists before calling `zip()`

**Issue**: `OutOfMemoryError` when reading large files
- **Cause**: Loading entire file into memory instead of using batch reading
- **Solution**: Use `ReadFileInBatch.ReadFileBatch()` with appropriate batch size

**Issue**: ZIP file corrupted
- **Cause**: Zip4j version incompatibility or I/O error during write
- **Solution**: Check disk space, verify Zip4j 2.11.5 compatible, check logs for exceptions

### YAML Issues

**Issue**: `YAMLException: Unable to find property`
- **Cause**: Key doesn't exist or YAML syntax error
- **Solution**: Verify YAML syntax, check key path (use dot notation for nested keys)

**Issue**: Changes not persisted
- **Cause**: `save()` not called after `put()`
- **Solution**: Call `save()` or use `put(key, value, true)` for auto-save

---

## Where to Look for Logs

### Log Configuration
**Location**: `src/main/resources/log4j2.xml`

**Log Outputs**:
- **Console** (SYSTEM_OUT): All logs
- **Format**: `%d{yyyy-MM-dd HH:mm:ss}-[%t]-%level-[%logger{1}.class:%line] - %msg%n`
  - Example: `2026-01-25 14:30:45-[main]-ERROR-[CloudServer.class:42] - Connection failed`

### Log Levels by Package

| Package/Class | Level | What to Look For |
|---------------|-------|------------------|
| **Root** | ERROR | Critical failures only |
| **ro.deiutzblaxo.cloud.net.channel** | INFO | Connection events, request/response flow |
| `ConnectionGateway` (client/server) | INFO | "Making request", "Running the callback" |
| `CloudServer` | INFO | "The Cloud Server started on port: X" |
| `RetryExecutor` | INFO/WARN/ERROR | Retry attempts: "Attempt N to execute operation failed", "Operation failed after X attempts" |
| `CacheManagerTemplate` | DEBUG | Eviction events: "Working on evicting", "Evict cache key X" (requires level change) |

### Key Log Messages

**Network**:
- Server start: `"The Cloud Server started on port: {port}"` (INFO)
- Client request: `"sending normal request"` (INFO in test code)
- Callback execution: `"Running the callback."` (INFO in test code)

**Retry Executor**:
- Retry attempt: `"Attempt {N} to execute operation failed... waiting {delay} ms before retry"` (INFO)
- Exception details: `"Exception details: "` (WARN, followed by stack trace)
- Retry exhausted: `"Operation failed after {maxRetries} attempts, giving up"` (ERROR)
- Sleep interrupted: `"Sleep interrupted during retry, but continuing with retry attempts"` (WARN)

**Cache** (if DEBUG enabled):
- Eviction start: `"Working on evicting..." + cacheSize` (DEBUG)
- Entry evicted: `"Evict cache key {key}"` (DEBUG)

**Database**:
- No explicit log messages (exceptions print to stderr via `e.printStackTrace()`)
- Look for SQL exceptions in console: `SQLException`, `Communications link failure`

### Classes that Emit Important Logs

| Class | Log Events |
|-------|------------|
| `CloudServer` | Server startup |
| `ConnectionGateway` (client) | Request sending, callback execution |
| `ConnectionGateway` (server) | Connection acceptance, packet processing |
| `RetryExecutor` | Retry attempts, failures |
| `CacheManagerTemplate` | Cache eviction (DEBUG level) |
| `MySQLManager` implementations | Database exceptions (stack traces) |
| `RedisConnection` | Redis exceptions (stack traces) |

### Troubleshooting Logs

**To Debug Database Issues**:
- Search for: `SQLException`, `NoFoundException`, `Communications link failure`
- Enable DEBUG for SQL queries (requires code changes, not in log4j2.xml)

**To Debug Network Issues**:
- Increase log level for `ro.deiutzblaxo.cloud.net.channel` to DEBUG (if available)
- Look for: Connection establishment, packet serialization errors, handler exceptions

**To Debug Cache Issues**:
- Change `CacheManagerTemplate` logger level to DEBUG in log4j2.xml:
  ```xml
  <Logger name="ro.deiutzblaxo.cloud.data.cache.template.CacheManagerTemplate" level="DEBUG" additivity="false">
      <AppenderRef ref="Console"/>
  </Logger>
  ```
- Watch for eviction frequency and cache size

---

## Next Steps and Suggestions

### Pending Improvements

**High Priority**:
1. **Security Audit**: 
   - ⚠️ Fix SQL injection vulnerability in `MySQLManager.exists()` (uses string concatenation)
   - Add input validation for all user-facing methods
   - Document security best practices for credential management

2. **Error Handling**:
   - Replace `e.printStackTrace()` with proper logging via Log4j2
   - Propagate exceptions instead of returning null (or document null returns)
   - Add custom exceptions for specific error cases

3. **Bug Fixes**:
   - `NameUUIDManager` background thread: Add sleep or use `BlockingQueue` to prevent CPU spin
   - `RedisConnection.get()`: Clarify "nil" string handling (currently checks `== "nil"`)

**Medium Priority**:
4. **Testing**:
   - Add unit tests for all components (currently only manual tests)
   - Add integration tests for MySQL, Redis, network components
   - Add test coverage reports

5. **Documentation**:
   - Add JavaDoc comments to all public methods
   - Create user guide with examples for each component
   - Document thread safety guarantees

6. **Cache Improvements**:
   - Make `CacheManagerTemplate.getCache()` check expiration before returning value
   - Add max cache size limit with LRU eviction
   - Add cache statistics (hits, misses, evictions)

**Low Priority**:
7. **API Consistency**:
   - Standardize exception handling (checked vs unchecked)
   - Make builder pattern consistent across all components
   - Add validation for constructor parameters (null checks, range checks)

8. **Performance**:
   - Profile and optimize reflection-based sorting
   - Add connection pooling metrics for HikariCP
   - Optimize network packet serialization

9. **Features**:
   - Add SSL/TLS support for network layer
   - Add transaction support for `MySQLManager`
   - Add Redis pub/sub utilities
   - Add configuration file support (externalize hardcoded defaults)

### Refactoring Opportunities

**Code Quality**:
- Extract hardcoded values to constants (timeout values, pool sizes, etc.)
- Reduce code duplication in `MySQLManager` default methods
- Split large classes (e.g., `MySQLManager` has many methods, consider splitting by concern)

**Architecture**:
- Separate interface definitions into own modules
- Create separate artifacts for optional dependencies (redis, mysql, bukkit)
- Add dependency injection framework support (Spring, Guice)

---

## Contact / Maintainers

**Repository**: Cloud Java Utility Library
**Package**: `ro.deiutzblaxo.cloud`
**Version**: 1.3.2.5
**Author**: Deiutzblaxo

**Ownership**: Individual open-source project
**Maintenance**: Active development (tracked via WakaTime badge in README)

For issues, contributions, or questions, refer to repository documentation.

---

## Self-Validation Checklist Results

### Completeness:
- ✅ **Entry point**: Library has no single entry point (utility library); all public classes documented
- ✅ **Request tracing**: Network flow documented from `CloudClient/CloudServer` → `ConnectionGateway` → `Handler` → database/cache
- ✅ **Scheduled jobs**: No scheduled jobs (library doesn't include scheduler)
- ✅ **Configuration usage**: All configuration documented (programmatic, no config files except log4j2.xml)

### Accuracy:
- ✅ **Scheduling times**: N/A (no scheduling)
- ✅ **Framework versions**: Verified from pom.xml (Java 17, all dependencies listed with versions)
- ✅ **Unverified specifics**: Marked pool defaults as "verify in code" where not explicit
- ✅ **No operational commands**: Documentation is code-focused only

### Debugging Value:
- ✅ **Troubleshooting section**: Covers common failures for all major components
- ✅ **Service methods**: All key methods documented with call chains
- ✅ **Data invariants**: Documented (e.g., cache expiration behavior, callback ID matching)
- ✅ **Security issues**: Called out SQL injection risk, credential management

### Readability:
- ✅ **Tables render**: All tables validated
- ✅ **File paths**: All paths relative to repo root
- ✅ **Code elements**: Wrapped in backticks
- ✅ **No truncation**: Document complete

---

**End of Cloud Library Agent Documentation**
