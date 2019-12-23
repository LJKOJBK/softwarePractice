package net.oschina.app.improve.detail.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库
 * Created by haibin on 2017/5/22.
 */
@SuppressWarnings("all")
class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "detail.db";
    private static final int DB_VERSION = 1;

    DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

     DBHelper(Context context, String name) {
        super(context, name, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    private String getTableName(Class<?> cla) {
        Annotation[] annotations = cla.getDeclaredAnnotations();
        if (annotations == null || annotations.length == 0) {
            throw new IllegalStateException("you must use Table annotation for bean");
        }
        String tableName = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof Table)
                tableName = ((Table) annotation).tableName();
        }
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalStateException("you must use Table annotation for bean");
        }
        return tableName;
    }

    public void create(Class<?> cls) {
        String tableName = getTableName(cls);
        create(tableName, cls);
    }

    public void create(String tableName, Class<?> cls) {

        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " ";
        String table = "";
        String primary = "";
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                boolean isAutoincrement = primaryKey.autoincrement();
                String name = primaryKey.column();
                primary = String.format(name + " %s primary key " +
                        (isAutoincrement ? "autoincrement," : ","), getTypeString(field));
            } else if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                String name = column.column();
                table = table + String.format(name + " %s,", getTypeString(field));
            }
        }
        if (TextUtils.isEmpty(table))
            return;
        sql = sql + "(" + primary + table.substring(0, table.length() - 1) + ")";
        getWritableDatabase().execSQL(sql);
    }

    public boolean update(Object obj, String tableName, String where, String[] args) {
        if (!isExist(tableName)) {
            return false;
        }
        Class<?> cls = obj.getClass();
        SQLiteDatabase db ;
        ContentValues values = new ContentValues();
        Field[] fields = cls.getDeclaredFields();
        try {
            db = getWritableDatabase();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);

                    String name = column.column();
                    Object object = field.get(obj);
                    values.put(name, object == null ? "" : object.toString());
                } else if (field.isAnnotationPresent(PrimaryKey.class)) {
                    PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                    boolean isAutoincrement = primaryKey.autoincrement();
                    String name = primaryKey.column();
                    if (!isAutoincrement) {
                        Object object = field.get(obj);
                        values.put(name, object == null ? "" : object.toString());
                    }
                }
            }
            db.update(tableName, values, where, args);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Object obj, String where, String... args) {
        Class<?> cls = obj.getClass();
        String tableName = getTableName(cls);
        return update(obj, tableName, where, args);
    }

    public boolean insert(Object obj, String tableName) {
        if (!isExist(tableName)) {
            return false;
        }
        Class<?> cls = obj.getClass();
        SQLiteDatabase db;
        ContentValues values = new ContentValues();
        Field[] fields = cls.getDeclaredFields();
        try {
            db = getWritableDatabase();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    String name = column.column();
                    Object object = field.get(obj);
                    values.put(name, object == null ? "" : object.toString());
                } else if (field.isAnnotationPresent(PrimaryKey.class)) {
                    PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                    boolean isAutoincrement = primaryKey.autoincrement();
                    String name = primaryKey.column();
                    if (!isAutoincrement) {
                        Object object = field.get(obj);
                        values.put(name, object == null ? "" : object.toString());
                    }
                }
            }
            return db.insert(tableName, "", values) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 事务插入
     *
     * @param lists     数据
     * @param tableName tableName
     * @return  <T>
     */
    private  <T> boolean insertList(List<T> lists, String tableName) {
        if (!isExist(tableName)) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (Object obj : lists) {
                Class<?> cls = obj.getClass();
                ContentValues values = new ContentValues();
                Field[] fields = cls.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);
                        String name = column.column();
                        Object object = field.get(obj);
                        values.put(name, object == null ? "" : object.toString());
                    } else if (field.isAnnotationPresent(PrimaryKey.class)) {
                        PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                        boolean isAutoincrement = primaryKey.autoincrement();
                        String name = primaryKey.column();
                        if (!isAutoincrement) {
                            Object object = field.get(obj);
                            values.put(name, object == null ? "" : object.toString());
                        }
                    }
                }
                db.insert(tableName, "", values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db.isOpen()) {
                db.endTransaction();
            }
        }
        return true;
    }


    public boolean insert(Object obj) {
        Class<?> cls = obj.getClass();
        String tableName = getTableName(cls);
        return isExist(tableName) && insert(obj, tableName);
    }


    public <T> boolean insertTransaction(List<T> list, String tableName) {
        return isExist(tableName) && insertList(list, tableName);
    }

    public long getCount(Class<?> cls) {
        String tableName = getTableName(cls);
        if (!isExist(tableName))
            return -1;
        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            cursor = db.rawQuery(String.format("select count(*) from %s", tableName), null);
            cursor.moveToFirst();
            long count = cursor.getLong(0);
            cursor.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return 0;
    }

    /**
     * 判断数据是否存在
     *
     * @return isDataExist
     */
    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public boolean isDataExist(String tableName, String where) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;
        try {
            String sql = String.format("SELECT * FROM %s %s", tableName, where);
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                return true;// //有城市在数据库已存在，返回true
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return false;
    }

    public boolean clear(String tableName) {
        if (!isExist(tableName)) {
            return false;
        }
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            db.execSQL(String.format("DELETE from '%s'", tableName));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(db!= null && db.isOpen()){
                db.close();
            }
        }
        return false;
    }

    /**
     * 更新字段
     *
     * @param table  表
     * @param column 字段
     * @param value  要更新的值
     * @return 成功或者失败
     */
    public boolean update(String table, String column, Object value, String where) {
        if (!isExist(table)) {
            return false;
        }
        SQLiteDatabase db;
        try {
            db = getReadableDatabase();
            String sql = String.format("UPDATE %s SET %s='%s' %s", table, column, value.toString(), where);
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 新增字段
     *
     * @param tableName  tableName
     * @param columnName columnName
     * @param type       type
     * @return true or false
     */
    private boolean alter(String tableName, String columnName, String type) {
        if (!isExist(tableName)) return false;
        SQLiteDatabase db ;
        try {
            db = getWritableDatabase();
            db.execSQL(String.format("ALTER TABLE %s ADD %s %s", tableName, columnName, type));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 新增字段
     *
     * @param cls cls
     * @return true or false
     */
    public boolean alter(Class<?> cls) {
        String tableName = getTableName(cls);
        if (!isExist(tableName))
            return false;
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                String name = column.column();
                if (!isColumnExist(tableName, name)) {
                    alter(tableName, name, getTypeString(field));
                }
            }
        }
        return false;
    }

    private boolean isColumnExist(String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;
        SQLiteDatabase db ;
        try {
            db = getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0"
                    , null);
            result = cursor != null && cursor.getColumnIndex(columnName) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return result;
    }

    /**
     * 判断数据表是否存在
     *
     * @param tableName tableName
     * @return 判断数据表是否存在
     */
    public boolean isExist(String tableName) {
        if (TextUtils.isEmpty(tableName)) {
            return false;
        }
        boolean exits = false;
        SQLiteDatabase db;
        Cursor cursor = null;
        String sql = "select * from sqlite_master where name=" + "'" + tableName + "'";
        try {
            db = getReadableDatabase();
            cursor = db.rawQuery(sql, null);
            if (cursor.getCount() != 0) {
                exits = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return exits;
    }

    private String getTypeString(Field field) {
        Class<?> type = field.getType();
        if (type.equals(int.class)) {
            return "integer";
        } else if (type.equals(String.class)) {
            return "text";
        } else if (type.equals(long.class)) {
            return "long";
        } else if (type.equals(float.class)) {
            return "feal";
        } else if (type.equals(double.class)) {
            return "feal";
        }
        return "varchar";
    }

    public <T> List<T> get(Class<T> cls) {
        return get(cls, null, null, null, 0, 0);
    }

    public <T> List<T> get(Class<T> cls, String where) {
        return get(cls, where, null, null, 0, 0);
    }


    public <T> List<T> get(Class<T> cls, int limit, int offset) {
        return get(cls, null, null, null, limit, offset);
    }

    public <T> List<T> get(Class<T> cls, String where, String orderColumn, String orderType, int limit, int offset) {
        String tableName = getTableName(cls);
        if (!isExist(tableName))
            return null;
        List<T> list = new ArrayList<>();
        SQLiteDatabase db ;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            String sql = String.format("SELECT * from %s", tableName);
            String whereAre = TextUtils.isEmpty(where) ? null : " " + where;
            String orderBy = TextUtils.isEmpty(orderColumn) ? null : String.format(" ORDER BY %s %s", orderColumn, orderType);
            String limitStr = limit == 0 ? null : String.format(" limit %s offset %s", String.valueOf(limit), String.valueOf(offset));
            StringBuilder sb = new StringBuilder();
            sb.append(sql);
            sb.append(TextUtils.isEmpty(limitStr) ? "" : limitStr);
            sb.append(TextUtils.isEmpty(whereAre) ? "" : whereAre);
            sb.append(TextUtils.isEmpty(orderBy) ? "" : orderBy);

            sql = sb.toString();
            cursor = db.rawQuery(sql, null);
            Field[] fields = cls.getDeclaredFields();
            while (cursor.moveToNext()) {
                T t = cls.newInstance();
                for (Field field : fields) {
                    field.setAccessible(true);
                    String name = "";
                    if (field.isAnnotationPresent(Column.class))
                        name = field.getAnnotation(Column.class).column();
                    else if (field.isAnnotationPresent(PrimaryKey.class))
                        name = field.getAnnotation(PrimaryKey.class).column();
                    if (!TextUtils.isEmpty(name)) {
                        Class<?> type = field.getType();
                        if (type.equals(int.class)) {
                            field.set(t, cursor.getInt(cursor.getColumnIndex(name)));
                        } else if (type.equals(String.class)) {
                            field.set(t, cursor.getString(cursor.getColumnIndex(name)));
                        } else if (type.equals(long.class)) {
                            field.set(t, cursor.getLong(cursor.getColumnIndex(name)));
                        } else if (type.equals(float.class)) {
                            field.set(t, cursor.getFloat(cursor.getColumnIndex(name)));
                        } else if (type.equals(double.class)) {
                            field.set(t, cursor.getDouble(cursor.getColumnIndex(name)));
                        }
                    }
                }
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 删除数据
     *
     * @param cls cls
     * @return 成功或者失败
     */
    public boolean delete(Class<?> cls, String where, String... args) {
        String tableName = getTableName(cls);
        if (!isExist(tableName))
            return false;
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
            int i = db.delete(tableName, where, args);
            return i > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
