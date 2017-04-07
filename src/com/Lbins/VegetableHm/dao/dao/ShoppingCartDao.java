package com.Lbins.VegetableHm.dao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.Lbins.VegetableHm.dao.ShoppingCart;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SHOPPING_CART.
*/
public class ShoppingCartDao extends AbstractDao<ShoppingCart, String> {

    public static final String TABLENAME = "SHOPPING_CART";

    /**
     * Properties of entity ShoppingCart.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Cartid = new Property(0, String.class, "cartid", true, "CARTID");
        public final static Property Goods_id = new Property(1, String.class, "goods_id", false, "GOODS_ID");
        public final static Property Emp_id = new Property(2, String.class, "emp_id", false, "EMP_ID");
        public final static Property Price = new Property(3, String.class, "price", false, "PRICE");
        public final static Property Dateline = new Property(4, String.class, "dateline", false, "DATELINE");
        public final static Property Is_select = new Property(5, String.class, "is_select", false, "IS_SELECT");
    };

    private DaoSession daoSession;


    public ShoppingCartDao(DaoConfig config) {
        super(config);
    }
    
    public ShoppingCartDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SHOPPING_CART' (" + //
                "'CARTID' TEXT PRIMARY KEY NOT NULL ," + // 0: cartid
                "'GOODS_ID' TEXT NOT NULL ," + // 1: goods_id
                "'EMP_ID' TEXT NOT NULL ," + // 2: emp_id
                "'PRICE' TEXT NOT NULL ," + // 3: price
                "'DATELINE' TEXT NOT NULL ," + // 4: dateline
                "'IS_SELECT' TEXT NOT NULL );"); // 5: is_select
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SHOPPING_CART'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ShoppingCart entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getCartid());
        stmt.bindString(2, entity.getGoods_id());
        stmt.bindString(3, entity.getEmp_id());
        stmt.bindString(4, entity.getPrice());
        stmt.bindString(5, entity.getDateline());
        stmt.bindString(6, entity.getIs_select());
    }

    @Override
    protected void attachEntity(ShoppingCart entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ShoppingCart readEntity(Cursor cursor, int offset) {
        ShoppingCart entity = new ShoppingCart( //
            cursor.getString(offset + 0), // cartid
            cursor.getString(offset + 1), // goods_id
            cursor.getString(offset + 2), // emp_id
            cursor.getString(offset + 3), // price
            cursor.getString(offset + 4), // dateline
            cursor.getString(offset + 5) // is_select
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ShoppingCart entity, int offset) {
        entity.setCartid(cursor.getString(offset + 0));
        entity.setGoods_id(cursor.getString(offset + 1));
        entity.setEmp_id(cursor.getString(offset + 2));
        entity.setPrice(cursor.getString(offset + 3));
        entity.setDateline(cursor.getString(offset + 4));
        entity.setIs_select(cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(ShoppingCart entity, long rowId) {
        return entity.getCartid();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(ShoppingCart entity) {
        if(entity != null) {
            return entity.getCartid();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
