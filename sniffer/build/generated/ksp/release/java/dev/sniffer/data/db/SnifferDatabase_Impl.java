package dev.sniffer.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import dev.sniffer.data.dao.LogDao;
import dev.sniffer.data.dao.LogDao_Impl;
import dev.sniffer.data.dao.MockDao;
import dev.sniffer.data.dao.MockDao_Impl;
import dev.sniffer.data.dao.NetworkCallDao;
import dev.sniffer.data.dao.NetworkCallDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SnifferDatabase_Impl extends SnifferDatabase {
  private volatile NetworkCallDao _networkCallDao;

  private volatile LogDao _logDao;

  private volatile MockDao _mockDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `network_calls` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `requestUrl` TEXT NOT NULL, `requestMethod` TEXT NOT NULL, `requestHeaders` TEXT NOT NULL, `requestBody` TEXT, `responseCode` INTEGER NOT NULL, `responseMessage` TEXT NOT NULL, `responseHeaders` TEXT NOT NULL, `responseBody` TEXT, `timestamp` INTEGER NOT NULL, `durationMs` INTEGER NOT NULL, `wasMocked` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `message` TEXT NOT NULL, `tag` TEXT, `timestamp` INTEGER NOT NULL, `level` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `mocks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `urlPattern` TEXT NOT NULL, `responseBody` TEXT NOT NULL, `statusCode` INTEGER NOT NULL, `enabled` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_mocks_urlPattern` ON `mocks` (`urlPattern`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8b495276c96df527c0aace5ec3d20ba3')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `network_calls`");
        db.execSQL("DROP TABLE IF EXISTS `logs`");
        db.execSQL("DROP TABLE IF EXISTS `mocks`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsNetworkCalls = new HashMap<String, TableInfo.Column>(12);
        _columnsNetworkCalls.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNetworkCalls.put("requestUrl", new TableInfo.Column("requestUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNetworkCalls.put("requestMethod", new TableInfo.Column("requestMethod", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNetworkCalls.put("requestHeaders", new TableInfo.Column("requestHeaders", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNetworkCalls.put("requestBody", new TableInfo.Column("requestBody", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNetworkCalls.put("responseCode", new TableInfo.Column("responseCode", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNetworkCalls.put("responseMessage", new TableInfo.Column("responseMessage", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNetworkCalls.put("responseHeaders", new TableInfo.Column("responseHeaders", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNetworkCalls.put("responseBody", new TableInfo.Column("responseBody", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNetworkCalls.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNetworkCalls.put("durationMs", new TableInfo.Column("durationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNetworkCalls.put("wasMocked", new TableInfo.Column("wasMocked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNetworkCalls = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNetworkCalls = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNetworkCalls = new TableInfo("network_calls", _columnsNetworkCalls, _foreignKeysNetworkCalls, _indicesNetworkCalls);
        final TableInfo _existingNetworkCalls = TableInfo.read(db, "network_calls");
        if (!_infoNetworkCalls.equals(_existingNetworkCalls)) {
          return new RoomOpenHelper.ValidationResult(false, "network_calls(dev.sniffer.data.entity.NetworkCallEntity).\n"
                  + " Expected:\n" + _infoNetworkCalls + "\n"
                  + " Found:\n" + _existingNetworkCalls);
        }
        final HashMap<String, TableInfo.Column> _columnsLogs = new HashMap<String, TableInfo.Column>(5);
        _columnsLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLogs.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLogs.put("tag", new TableInfo.Column("tag", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLogs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLogs.put("level", new TableInfo.Column("level", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLogs = new TableInfo("logs", _columnsLogs, _foreignKeysLogs, _indicesLogs);
        final TableInfo _existingLogs = TableInfo.read(db, "logs");
        if (!_infoLogs.equals(_existingLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "logs(dev.sniffer.data.entity.LogEntity).\n"
                  + " Expected:\n" + _infoLogs + "\n"
                  + " Found:\n" + _existingLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsMocks = new HashMap<String, TableInfo.Column>(6);
        _columnsMocks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMocks.put("urlPattern", new TableInfo.Column("urlPattern", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMocks.put("responseBody", new TableInfo.Column("responseBody", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMocks.put("statusCode", new TableInfo.Column("statusCode", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMocks.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMocks.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMocks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMocks = new HashSet<TableInfo.Index>(1);
        _indicesMocks.add(new TableInfo.Index("index_mocks_urlPattern", true, Arrays.asList("urlPattern"), Arrays.asList("ASC")));
        final TableInfo _infoMocks = new TableInfo("mocks", _columnsMocks, _foreignKeysMocks, _indicesMocks);
        final TableInfo _existingMocks = TableInfo.read(db, "mocks");
        if (!_infoMocks.equals(_existingMocks)) {
          return new RoomOpenHelper.ValidationResult(false, "mocks(dev.sniffer.data.entity.MockEntity).\n"
                  + " Expected:\n" + _infoMocks + "\n"
                  + " Found:\n" + _existingMocks);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "8b495276c96df527c0aace5ec3d20ba3", "254ad56816d9404bf40dc022c8d8d8a8");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "network_calls","logs","mocks");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `network_calls`");
      _db.execSQL("DELETE FROM `logs`");
      _db.execSQL("DELETE FROM `mocks`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(NetworkCallDao.class, NetworkCallDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LogDao.class, LogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MockDao.class, MockDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public NetworkCallDao networkCallDao() {
    if (_networkCallDao != null) {
      return _networkCallDao;
    } else {
      synchronized(this) {
        if(_networkCallDao == null) {
          _networkCallDao = new NetworkCallDao_Impl(this);
        }
        return _networkCallDao;
      }
    }
  }

  @Override
  public LogDao logDao() {
    if (_logDao != null) {
      return _logDao;
    } else {
      synchronized(this) {
        if(_logDao == null) {
          _logDao = new LogDao_Impl(this);
        }
        return _logDao;
      }
    }
  }

  @Override
  public MockDao mockDao() {
    if (_mockDao != null) {
      return _mockDao;
    } else {
      synchronized(this) {
        if(_mockDao == null) {
          _mockDao = new MockDao_Impl(this);
        }
        return _mockDao;
      }
    }
  }
}
