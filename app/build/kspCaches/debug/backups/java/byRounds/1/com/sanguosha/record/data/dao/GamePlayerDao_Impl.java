package com.sanguosha.record.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sanguosha.record.data.entity.GamePlayer;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class GamePlayerDao_Impl implements GamePlayerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GamePlayer> __insertionAdapterOfGamePlayer;

  private final EntityDeletionOrUpdateAdapter<GamePlayer> __deletionAdapterOfGamePlayer;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByGameId;

  public GamePlayerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGamePlayer = new EntityInsertionAdapter<GamePlayer>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `game_players` (`id`,`gameId`,`playerId`,`heroId`,`identity`,`isWinner`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GamePlayer entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getGameId());
        statement.bindLong(3, entity.getPlayerId());
        statement.bindLong(4, entity.getHeroId());
        statement.bindString(5, entity.getIdentity());
        final int _tmp = entity.isWinner() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__deletionAdapterOfGamePlayer = new EntityDeletionOrUpdateAdapter<GamePlayer>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `game_players` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GamePlayer entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteByGameId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM game_players WHERE gameId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final GamePlayer gamePlayer, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfGamePlayer.insertAndReturnId(gamePlayer);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<GamePlayer> gamePlayers,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGamePlayer.insert(gamePlayers);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final GamePlayer gamePlayer, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfGamePlayer.handle(gamePlayer);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByGameId(final long gameId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByGameId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, gameId);
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
          __preparedStmtOfDeleteByGameId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getGamePlayersByGameId(final long gameId,
      final Continuation<? super List<GamePlayer>> $completion) {
    final String _sql = "SELECT * FROM game_players WHERE gameId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<GamePlayer>>() {
      @Override
      @NonNull
      public List<GamePlayer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfHeroId = CursorUtil.getColumnIndexOrThrow(_cursor, "heroId");
          final int _cursorIndexOfIdentity = CursorUtil.getColumnIndexOrThrow(_cursor, "identity");
          final int _cursorIndexOfIsWinner = CursorUtil.getColumnIndexOrThrow(_cursor, "isWinner");
          final List<GamePlayer> _result = new ArrayList<GamePlayer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GamePlayer _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final long _tmpHeroId;
            _tmpHeroId = _cursor.getLong(_cursorIndexOfHeroId);
            final String _tmpIdentity;
            _tmpIdentity = _cursor.getString(_cursorIndexOfIdentity);
            final boolean _tmpIsWinner;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsWinner);
            _tmpIsWinner = _tmp != 0;
            _item = new GamePlayer(_tmpId,_tmpGameId,_tmpPlayerId,_tmpHeroId,_tmpIdentity,_tmpIsWinner);
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

  @Override
  public Object getGamePlayerInfoByGameId(final long gameId,
      final Continuation<? super List<GamePlayerInfo>> $completion) {
    final String _sql = "\n"
            + "        SELECT gp.playerId, p.name as playerName, h.name as heroName,\n"
            + "               gp.identity, gp.isWinner\n"
            + "        FROM game_players gp\n"
            + "        INNER JOIN players p ON gp.playerId = p.id\n"
            + "        INNER JOIN heroes h ON gp.heroId = h.id\n"
            + "        WHERE gp.gameId = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<GamePlayerInfo>>() {
      @Override
      @NonNull
      public List<GamePlayerInfo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPlayerId = 0;
          final int _cursorIndexOfPlayerName = 1;
          final int _cursorIndexOfHeroName = 2;
          final int _cursorIndexOfIdentity = 3;
          final int _cursorIndexOfIsWinner = 4;
          final List<GamePlayerInfo> _result = new ArrayList<GamePlayerInfo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GamePlayerInfo _item;
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final String _tmpPlayerName;
            _tmpPlayerName = _cursor.getString(_cursorIndexOfPlayerName);
            final String _tmpHeroName;
            _tmpHeroName = _cursor.getString(_cursorIndexOfHeroName);
            final String _tmpIdentity;
            _tmpIdentity = _cursor.getString(_cursorIndexOfIdentity);
            final boolean _tmpIsWinner;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsWinner);
            _tmpIsWinner = _tmp != 0;
            _item = new GamePlayerInfo(_tmpPlayerId,_tmpPlayerName,_tmpHeroName,_tmpIdentity,_tmpIsWinner);
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

  @Override
  public Flow<List<GamePlayer>> getGamePlayersByGameIdFlow(final long gameId) {
    final String _sql = "SELECT * FROM game_players WHERE gameId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"game_players"}, new Callable<List<GamePlayer>>() {
      @Override
      @NonNull
      public List<GamePlayer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfHeroId = CursorUtil.getColumnIndexOrThrow(_cursor, "heroId");
          final int _cursorIndexOfIdentity = CursorUtil.getColumnIndexOrThrow(_cursor, "identity");
          final int _cursorIndexOfIsWinner = CursorUtil.getColumnIndexOrThrow(_cursor, "isWinner");
          final List<GamePlayer> _result = new ArrayList<GamePlayer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GamePlayer _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final long _tmpHeroId;
            _tmpHeroId = _cursor.getLong(_cursorIndexOfHeroId);
            final String _tmpIdentity;
            _tmpIdentity = _cursor.getString(_cursorIndexOfIdentity);
            final boolean _tmpIsWinner;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsWinner);
            _tmpIsWinner = _tmp != 0;
            _item = new GamePlayer(_tmpId,_tmpGameId,_tmpPlayerId,_tmpHeroId,_tmpIdentity,_tmpIsWinner);
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
  public Flow<List<GamePlayer>> getGamePlayersByPlayerId(final long playerId) {
    final String _sql = "SELECT * FROM game_players WHERE playerId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playerId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"game_players"}, new Callable<List<GamePlayer>>() {
      @Override
      @NonNull
      public List<GamePlayer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfHeroId = CursorUtil.getColumnIndexOrThrow(_cursor, "heroId");
          final int _cursorIndexOfIdentity = CursorUtil.getColumnIndexOrThrow(_cursor, "identity");
          final int _cursorIndexOfIsWinner = CursorUtil.getColumnIndexOrThrow(_cursor, "isWinner");
          final List<GamePlayer> _result = new ArrayList<GamePlayer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GamePlayer _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final long _tmpHeroId;
            _tmpHeroId = _cursor.getLong(_cursorIndexOfHeroId);
            final String _tmpIdentity;
            _tmpIdentity = _cursor.getString(_cursorIndexOfIdentity);
            final boolean _tmpIsWinner;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsWinner);
            _tmpIsWinner = _tmp != 0;
            _item = new GamePlayer(_tmpId,_tmpGameId,_tmpPlayerId,_tmpHeroId,_tmpIdentity,_tmpIsWinner);
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
  public Object getPlayerTotalGames(final long playerId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM game_players WHERE playerId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playerId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPlayerWins(final long playerId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM game_players WHERE playerId = ? AND isWinner = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playerId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPlayerHeroStats(final long playerId, final int limit,
      final Continuation<? super List<HeroStats>> $completion) {
    final String _sql = "\n"
            + "        SELECT gp.heroId, h.name as heroName, COUNT(*) as gameCount,\n"
            + "               SUM(CASE WHEN gp.isWinner = 1 THEN 1 ELSE 0 END) as winCount\n"
            + "        FROM game_players gp\n"
            + "        INNER JOIN heroes h ON gp.heroId = h.id\n"
            + "        WHERE gp.playerId = ?\n"
            + "        GROUP BY gp.heroId\n"
            + "        ORDER BY gameCount DESC\n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playerId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HeroStats>>() {
      @Override
      @NonNull
      public List<HeroStats> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfHeroId = 0;
          final int _cursorIndexOfHeroName = 1;
          final int _cursorIndexOfGameCount = 2;
          final int _cursorIndexOfWinCount = 3;
          final List<HeroStats> _result = new ArrayList<HeroStats>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HeroStats _item;
            final long _tmpHeroId;
            _tmpHeroId = _cursor.getLong(_cursorIndexOfHeroId);
            final String _tmpHeroName;
            _tmpHeroName = _cursor.getString(_cursorIndexOfHeroName);
            final int _tmpGameCount;
            _tmpGameCount = _cursor.getInt(_cursorIndexOfGameCount);
            final int _tmpWinCount;
            _tmpWinCount = _cursor.getInt(_cursorIndexOfWinCount);
            _item = new HeroStats(_tmpHeroId,_tmpHeroName,_tmpGameCount,_tmpWinCount);
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

  @Override
  public Object getPlayerIdentityStats(final long playerId,
      final Continuation<? super List<IdentityStats>> $completion) {
    final String _sql = "\n"
            + "        SELECT identity,\n"
            + "               COUNT(*) as gameCount,\n"
            + "               SUM(CASE WHEN isWinner = 1 THEN 1 ELSE 0 END) as winCount\n"
            + "        FROM game_players\n"
            + "        WHERE playerId = ?\n"
            + "        GROUP BY identity\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playerId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IdentityStats>>() {
      @Override
      @NonNull
      public List<IdentityStats> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdentity = 0;
          final int _cursorIndexOfGameCount = 1;
          final int _cursorIndexOfWinCount = 2;
          final List<IdentityStats> _result = new ArrayList<IdentityStats>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IdentityStats _item;
            final String _tmpIdentity;
            _tmpIdentity = _cursor.getString(_cursorIndexOfIdentity);
            final int _tmpGameCount;
            _tmpGameCount = _cursor.getInt(_cursorIndexOfGameCount);
            final int _tmpWinCount;
            _tmpWinCount = _cursor.getInt(_cursorIndexOfWinCount);
            _item = new IdentityStats(_tmpIdentity,_tmpGameCount,_tmpWinCount);
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

  @Override
  public Object getPlayerRecentGames(final long playerId, final int limit,
      final Continuation<? super List<PlayerGameDetail>> $completion) {
    final String _sql = "\n"
            + "        SELECT gp.id as gamePlayerId, gp.gameId, g.datetime, g.location,\n"
            + "               h.name as heroName, gp.identity, gp.isWinner, g.winnerIdentity,\n"
            + "               g.durationSeconds\n"
            + "        FROM game_players gp\n"
            + "        INNER JOIN games g ON gp.gameId = g.id\n"
            + "        INNER JOIN heroes h ON gp.heroId = h.id\n"
            + "        WHERE gp.playerId = ?\n"
            + "        ORDER BY g.datetime DESC\n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playerId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PlayerGameDetail>>() {
      @Override
      @NonNull
      public List<PlayerGameDetail> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfGamePlayerId = 0;
          final int _cursorIndexOfGameId = 1;
          final int _cursorIndexOfDatetime = 2;
          final int _cursorIndexOfLocation = 3;
          final int _cursorIndexOfHeroName = 4;
          final int _cursorIndexOfIdentity = 5;
          final int _cursorIndexOfIsWinner = 6;
          final int _cursorIndexOfWinnerIdentity = 7;
          final int _cursorIndexOfDurationSeconds = 8;
          final List<PlayerGameDetail> _result = new ArrayList<PlayerGameDetail>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlayerGameDetail _item;
            final long _tmpGamePlayerId;
            _tmpGamePlayerId = _cursor.getLong(_cursorIndexOfGamePlayerId);
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpDatetime;
            _tmpDatetime = _cursor.getLong(_cursorIndexOfDatetime);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final String _tmpHeroName;
            _tmpHeroName = _cursor.getString(_cursorIndexOfHeroName);
            final String _tmpIdentity;
            _tmpIdentity = _cursor.getString(_cursorIndexOfIdentity);
            final boolean _tmpIsWinner;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsWinner);
            _tmpIsWinner = _tmp != 0;
            final String _tmpWinnerIdentity;
            _tmpWinnerIdentity = _cursor.getString(_cursorIndexOfWinnerIdentity);
            final long _tmpDurationSeconds;
            _tmpDurationSeconds = _cursor.getLong(_cursorIndexOfDurationSeconds);
            _item = new PlayerGameDetail(_tmpGamePlayerId,_tmpGameId,_tmpDatetime,_tmpLocation,_tmpHeroName,_tmpIdentity,_tmpIsWinner,_tmpWinnerIdentity,_tmpDurationSeconds);
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

  @Override
  public Object getGlobalHeroStats(final int limit,
      final Continuation<? super List<HeroStats>> $completion) {
    final String _sql = "\n"
            + "        SELECT gp.heroId, h.name as heroName, COUNT(*) as gameCount,\n"
            + "               SUM(CASE WHEN gp.isWinner = 1 THEN 1 ELSE 0 END) as winCount\n"
            + "        FROM game_players gp\n"
            + "        INNER JOIN heroes h ON gp.heroId = h.id\n"
            + "        GROUP BY gp.heroId\n"
            + "        ORDER BY gameCount DESC\n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HeroStats>>() {
      @Override
      @NonNull
      public List<HeroStats> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfHeroId = 0;
          final int _cursorIndexOfHeroName = 1;
          final int _cursorIndexOfGameCount = 2;
          final int _cursorIndexOfWinCount = 3;
          final List<HeroStats> _result = new ArrayList<HeroStats>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HeroStats _item;
            final long _tmpHeroId;
            _tmpHeroId = _cursor.getLong(_cursorIndexOfHeroId);
            final String _tmpHeroName;
            _tmpHeroName = _cursor.getString(_cursorIndexOfHeroName);
            final int _tmpGameCount;
            _tmpGameCount = _cursor.getInt(_cursorIndexOfGameCount);
            final int _tmpWinCount;
            _tmpWinCount = _cursor.getInt(_cursorIndexOfWinCount);
            _item = new HeroStats(_tmpHeroId,_tmpHeroName,_tmpGameCount,_tmpWinCount);
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

  @Override
  public Object getGlobalIdentityStats(
      final Continuation<? super List<IdentityStats>> $completion) {
    final String _sql = "\n"
            + "        SELECT identity,\n"
            + "               COUNT(DISTINCT gameId) as gameCount,\n"
            + "               COUNT(DISTINCT CASE WHEN isWinner = 1 THEN gameId END) as winCount\n"
            + "        FROM game_players\n"
            + "        GROUP BY identity\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IdentityStats>>() {
      @Override
      @NonNull
      public List<IdentityStats> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdentity = 0;
          final int _cursorIndexOfGameCount = 1;
          final int _cursorIndexOfWinCount = 2;
          final List<IdentityStats> _result = new ArrayList<IdentityStats>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IdentityStats _item;
            final String _tmpIdentity;
            _tmpIdentity = _cursor.getString(_cursorIndexOfIdentity);
            final int _tmpGameCount;
            _tmpGameCount = _cursor.getInt(_cursorIndexOfGameCount);
            final int _tmpWinCount;
            _tmpWinCount = _cursor.getInt(_cursorIndexOfWinCount);
            _item = new IdentityStats(_tmpIdentity,_tmpGameCount,_tmpWinCount);
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
