package com.example.pasturecontrol;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PastureDao_Impl implements PastureDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Pasture> __insertionAdapterOfPasture;

  private final EntityDeletionOrUpdateAdapter<Pasture> __deletionAdapterOfPasture;

  private final EntityDeletionOrUpdateAdapter<Pasture> __updateAdapterOfPasture;

  public PastureDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPasture = new EntityInsertionAdapter<Pasture>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pastures` (`id`,`name`,`area`,`creationDate`,`photoUri`,`videoUri`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Pasture entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        statement.bindDouble(3, entity.getArea());
        if (entity.getCreationDate() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCreationDate());
        }
        if (entity.getPhotoUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPhotoUri());
        }
        if (entity.getVideoUri() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getVideoUri());
        }
      }
    };
    this.__deletionAdapterOfPasture = new EntityDeletionOrUpdateAdapter<Pasture>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `pastures` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Pasture entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfPasture = new EntityDeletionOrUpdateAdapter<Pasture>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `pastures` SET `id` = ?,`name` = ?,`area` = ?,`creationDate` = ?,`photoUri` = ?,`videoUri` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Pasture entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        statement.bindDouble(3, entity.getArea());
        if (entity.getCreationDate() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCreationDate());
        }
        if (entity.getPhotoUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPhotoUri());
        }
        if (entity.getVideoUri() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getVideoUri());
        }
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Pasture pasture, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPasture.insert(pasture);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Pasture pasture, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPasture.handle(pasture);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Pasture pasture, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPasture.handle(pasture);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<Pasture>> $completion) {
    final String _sql = "SELECT * FROM pastures";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Pasture>>() {
      @Override
      @NonNull
      public List<Pasture> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfArea = CursorUtil.getColumnIndexOrThrow(_cursor, "area");
          final int _cursorIndexOfCreationDate = CursorUtil.getColumnIndexOrThrow(_cursor, "creationDate");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUri");
          final int _cursorIndexOfVideoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "videoUri");
          final List<Pasture> _result = new ArrayList<Pasture>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Pasture _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final double _tmpArea;
            _tmpArea = _cursor.getDouble(_cursorIndexOfArea);
            final String _tmpCreationDate;
            if (_cursor.isNull(_cursorIndexOfCreationDate)) {
              _tmpCreationDate = null;
            } else {
              _tmpCreationDate = _cursor.getString(_cursorIndexOfCreationDate);
            }
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            final String _tmpVideoUri;
            if (_cursor.isNull(_cursorIndexOfVideoUri)) {
              _tmpVideoUri = null;
            } else {
              _tmpVideoUri = _cursor.getString(_cursorIndexOfVideoUri);
            }
            _item = new Pasture(_tmpId,_tmpName,_tmpArea,_tmpCreationDate,_tmpPhotoUri,_tmpVideoUri);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
