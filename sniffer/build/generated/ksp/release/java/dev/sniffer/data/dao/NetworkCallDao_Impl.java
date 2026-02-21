package dev.sniffer.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import dev.sniffer.data.entity.NetworkCallEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class NetworkCallDao_Impl implements NetworkCallDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NetworkCallEntity> __insertionAdapterOfNetworkCallEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public NetworkCallDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNetworkCallEntity = new EntityInsertionAdapter<NetworkCallEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `network_calls` (`id`,`requestUrl`,`requestMethod`,`requestHeaders`,`requestBody`,`responseCode`,`responseMessage`,`responseHeaders`,`responseBody`,`timestamp`,`durationMs`,`wasMocked`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NetworkCallEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getRequestUrl());
        statement.bindString(3, entity.getRequestMethod());
        statement.bindString(4, entity.getRequestHeaders());
        if (entity.getRequestBody() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getRequestBody());
        }
        statement.bindLong(6, entity.getResponseCode());
        statement.bindString(7, entity.getResponseMessage());
        statement.bindString(8, entity.getResponseHeaders());
        if (entity.getResponseBody() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getResponseBody());
        }
        statement.bindLong(10, entity.getTimestamp());
        statement.bindLong(11, entity.getDurationMs());
        final int _tmp = entity.getWasMocked() ? 1 : 0;
        statement.bindLong(12, _tmp);
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM network_calls";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final NetworkCallEntity entity,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfNetworkCallEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<NetworkCallEntity>> observeAll() {
    final String _sql = "SELECT * FROM network_calls ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"network_calls"}, new Callable<List<NetworkCallEntity>>() {
      @Override
      @NonNull
      public List<NetworkCallEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRequestUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "requestUrl");
          final int _cursorIndexOfRequestMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "requestMethod");
          final int _cursorIndexOfRequestHeaders = CursorUtil.getColumnIndexOrThrow(_cursor, "requestHeaders");
          final int _cursorIndexOfRequestBody = CursorUtil.getColumnIndexOrThrow(_cursor, "requestBody");
          final int _cursorIndexOfResponseCode = CursorUtil.getColumnIndexOrThrow(_cursor, "responseCode");
          final int _cursorIndexOfResponseMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "responseMessage");
          final int _cursorIndexOfResponseHeaders = CursorUtil.getColumnIndexOrThrow(_cursor, "responseHeaders");
          final int _cursorIndexOfResponseBody = CursorUtil.getColumnIndexOrThrow(_cursor, "responseBody");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfWasMocked = CursorUtil.getColumnIndexOrThrow(_cursor, "wasMocked");
          final List<NetworkCallEntity> _result = new ArrayList<NetworkCallEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NetworkCallEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRequestUrl;
            _tmpRequestUrl = _cursor.getString(_cursorIndexOfRequestUrl);
            final String _tmpRequestMethod;
            _tmpRequestMethod = _cursor.getString(_cursorIndexOfRequestMethod);
            final String _tmpRequestHeaders;
            _tmpRequestHeaders = _cursor.getString(_cursorIndexOfRequestHeaders);
            final String _tmpRequestBody;
            if (_cursor.isNull(_cursorIndexOfRequestBody)) {
              _tmpRequestBody = null;
            } else {
              _tmpRequestBody = _cursor.getString(_cursorIndexOfRequestBody);
            }
            final int _tmpResponseCode;
            _tmpResponseCode = _cursor.getInt(_cursorIndexOfResponseCode);
            final String _tmpResponseMessage;
            _tmpResponseMessage = _cursor.getString(_cursorIndexOfResponseMessage);
            final String _tmpResponseHeaders;
            _tmpResponseHeaders = _cursor.getString(_cursorIndexOfResponseHeaders);
            final String _tmpResponseBody;
            if (_cursor.isNull(_cursorIndexOfResponseBody)) {
              _tmpResponseBody = null;
            } else {
              _tmpResponseBody = _cursor.getString(_cursorIndexOfResponseBody);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final boolean _tmpWasMocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasMocked);
            _tmpWasMocked = _tmp != 0;
            _item = new NetworkCallEntity(_tmpId,_tmpRequestUrl,_tmpRequestMethod,_tmpRequestHeaders,_tmpRequestBody,_tmpResponseCode,_tmpResponseMessage,_tmpResponseHeaders,_tmpResponseBody,_tmpTimestamp,_tmpDurationMs,_tmpWasMocked);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<NetworkCallEntity>> observeRecent(final int limit) {
    final String _sql = "SELECT * FROM network_calls ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"network_calls"}, new Callable<List<NetworkCallEntity>>() {
      @Override
      @NonNull
      public List<NetworkCallEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRequestUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "requestUrl");
          final int _cursorIndexOfRequestMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "requestMethod");
          final int _cursorIndexOfRequestHeaders = CursorUtil.getColumnIndexOrThrow(_cursor, "requestHeaders");
          final int _cursorIndexOfRequestBody = CursorUtil.getColumnIndexOrThrow(_cursor, "requestBody");
          final int _cursorIndexOfResponseCode = CursorUtil.getColumnIndexOrThrow(_cursor, "responseCode");
          final int _cursorIndexOfResponseMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "responseMessage");
          final int _cursorIndexOfResponseHeaders = CursorUtil.getColumnIndexOrThrow(_cursor, "responseHeaders");
          final int _cursorIndexOfResponseBody = CursorUtil.getColumnIndexOrThrow(_cursor, "responseBody");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfWasMocked = CursorUtil.getColumnIndexOrThrow(_cursor, "wasMocked");
          final List<NetworkCallEntity> _result = new ArrayList<NetworkCallEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NetworkCallEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRequestUrl;
            _tmpRequestUrl = _cursor.getString(_cursorIndexOfRequestUrl);
            final String _tmpRequestMethod;
            _tmpRequestMethod = _cursor.getString(_cursorIndexOfRequestMethod);
            final String _tmpRequestHeaders;
            _tmpRequestHeaders = _cursor.getString(_cursorIndexOfRequestHeaders);
            final String _tmpRequestBody;
            if (_cursor.isNull(_cursorIndexOfRequestBody)) {
              _tmpRequestBody = null;
            } else {
              _tmpRequestBody = _cursor.getString(_cursorIndexOfRequestBody);
            }
            final int _tmpResponseCode;
            _tmpResponseCode = _cursor.getInt(_cursorIndexOfResponseCode);
            final String _tmpResponseMessage;
            _tmpResponseMessage = _cursor.getString(_cursorIndexOfResponseMessage);
            final String _tmpResponseHeaders;
            _tmpResponseHeaders = _cursor.getString(_cursorIndexOfResponseHeaders);
            final String _tmpResponseBody;
            if (_cursor.isNull(_cursorIndexOfResponseBody)) {
              _tmpResponseBody = null;
            } else {
              _tmpResponseBody = _cursor.getString(_cursorIndexOfResponseBody);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final boolean _tmpWasMocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasMocked);
            _tmpWasMocked = _tmp != 0;
            _item = new NetworkCallEntity(_tmpId,_tmpRequestUrl,_tmpRequestMethod,_tmpRequestHeaders,_tmpRequestBody,_tmpResponseCode,_tmpResponseMessage,_tmpResponseHeaders,_tmpResponseBody,_tmpTimestamp,_tmpDurationMs,_tmpWasMocked);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
