package com.sea_monster.dao.query;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import com.sea_monster.dao.AbstractDao;
import com.sea_monster.dao.IQueryDaoAccess;

/**
 * Created by dragonj on 2/19/14.
 */
public class GroupQuery<T> extends AbstractQuery<T> {


    private final static class QueryData<T2> extends AbstractQueryData<T2, GroupQuery<T2>> {

        private QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues) {
            super(dao, sql, initialValues);
        }

        @Override
        protected GroupQuery<T2> createQuery() {
            return new GroupQuery<T2>(this, dao, sql, initialValues.clone());
        }
    }

    static <T2> GroupQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        QueryData<T2> queryData = new QueryData<T2>(dao, sql, toStringArray(initialValues));
        return queryData.forCurrentThread();
    }

    private final QueryData<T> queryData;

    private GroupQuery(QueryData<T> queryData, AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        super(dao, new IQueryDaoAccess.InternalQueryDaoAccess<T>(dao), sql, initialValues);
        this.queryData = queryData;
    }

    public GroupQuery<T> forCurrentThread() {
        return queryData.forCurrentThread(this);
    }

    /**
     * Returns the count (number of results matching the query). Uses SELECT COUNT (*) sematics.
     */

    public <K> List<K> list(QueryResultConsume<K> consume) {
        checkThread();
        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
        List<K> list = new ArrayList<K>();
        if (cursor.moveToFirst()) {
            do {
                list.add(consume.getQueryResult(cursor));
            } while (cursor.moveToNext());
        }
        return list;
    }


    public static interface QueryResultConsume<T3> {
        public T3 getQueryResult(Cursor cursor);
    }
}