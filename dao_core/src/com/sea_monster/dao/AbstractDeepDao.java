package com.sea_monster.dao;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import com.sea_monster.dao.internal.DaoConfig;
import com.sea_monster.dao.query.DeepQueryBuilder;

/**
 * Created by dragonj on 2/20/14.
 */
public abstract class AbstractDeepDao<T, K> extends AbstractDao<T, K> {

    public AbstractDeepDao(DaoConfig config) {
        super(config);
    }

    public AbstractDeepDao(DaoConfig config, AbstractDaoSession daoSession) {
        super(config,daoSession);
    }

    public abstract T loadDeep(Long key) ;

    protected abstract T loadCurrentDeep(Cursor cursor, boolean lock);

    public abstract String getSelectDeep();

    public abstract String getDeepJoinString();

    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<T> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<T> list = new ArrayList<T>(count);

        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }

    protected List<T> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }


    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<T> queryDeep(String where, String... selectionArg) {
        return loadDeepAllAndCloseCursor(getDeepCursor(where, selectionArg));
    }

    public Cursor getDeepCursor(String where, String... selectionArg){
        return  db.rawQuery(getSelectDeep() + where, selectionArg);
    }

    protected T loadDeepCurrent(Cursor cursor, int offset, boolean lock) {
        cursor.move(offset);
        return loadCurrentDeep(cursor,lock);
    }

    public DeepQueryBuilder<T> deepQueryBuilder() {
        return DeepQueryBuilder.internalCreate(this);
    }


    protected T loadDeepUnique(Cursor cursor) {
        boolean available = cursor.moveToFirst();
        if (!available) {
            return null;
        } else if (!cursor.isLast()) {
            throw new DaoException("Expected unique result, but count was " + cursor.getCount());
        }
        return loadDeepCurrent(cursor, 0, true);
    }
    protected T loadDeepUniqueAndCloseCursor(Cursor cursor) {
        try {
            return loadDeepUnique(cursor);
        } finally {
            cursor.close();
        }
    }


}
