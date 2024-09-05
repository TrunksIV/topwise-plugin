package com.topwise.plugin.emv.database.table;

import android.content.Context;

import com.topwise.plugin.emv.database.BaseDaoImpl;

import java.util.List;

public class CapkDaoImpl extends BaseDaoImpl<Capk> {
    private static List<Capk> capklist = null;
    public CapkDaoImpl(Context context) {
        super(new MyDBHelper(context),Capk.class);
    }

    /**
     * select capk from database
     * @param rid	the hexstring of rid
     * @param index   index
     * @return
     */
//    public Capk findByRidIndex(String rid,byte index){
//        StringBuffer sb = new StringBuffer("select * from tb_capk where rid='")
//                .append(rid).append("' and rindex='").append(index).append("'");
//        List<Capk> capklist = rawQuery(sb.toString(), null);
//        if(capklist==null||capklist.size()==0){
//            return null;
//        }
//        return capklist.get(0);
//    }

    /**
     * select capk from database
     * @param rid	the hexstring of rid
     * @param index   index
     * @return
     */
    public Capk findByRidIndex(String rid,byte index){
        String ridindex = new StringBuffer(rid).append(Integer.toHexString(index & 0xFF)).toString().toUpperCase();
        StringBuffer sb = new StringBuffer("select * from tb_capk where ridindex='").append(ridindex).append("'");
        List<Capk> capklist = rawQuery(sb.toString(), null);
        if(capklist==null||capklist.size()==0){
            return null;
        }
        return capklist.get(0);
    }

    /**
     * select capk from database
     * @return
     */
    public List<Capk> findAllCapk(){
        if(capklist==null||capklist.size()==0){
            StringBuffer sb = new StringBuffer("select * from tb_capk");
            capklist = rawQuery(sb.toString(), null);
        }
        return capklist;
    }

}
