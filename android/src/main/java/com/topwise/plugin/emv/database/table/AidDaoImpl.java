package com.topwise.plugin.emv.database.table;

import android.content.Context;
import android.util.Log;

import com.topwise.plugin.emv.database.BaseDaoImpl;

import java.util.List;

public class AidDaoImpl extends BaseDaoImpl<Aid> {
    private static List<Aid> aidlist = null;
    public AidDaoImpl(Context context) {
        super(new MyDBHelper(context),Aid.class);
    }

    /**
     * select all aid from database
     * @return
     */
    public List<Aid> findAllAid(){
        if(aidlist==null||aidlist.size()==0){
            StringBuffer sb = new StringBuffer("select * from tb_aid");
            aidlist = rawQuery(sb.toString(), null);
        }
        return aidlist;
    }

    /**
     * select aid from database
     * @param aid	the hexstring of aid
     * @return
     */
    public Aid findByAid(String aid){
        StringBuffer sb = new StringBuffer("select * from tb_aid where aid='")
                .append(aid).append("'");
        List<Aid> aidlist = rawQuery(sb.toString(), null);
        if(aidlist==null||aidlist.size()==0){
            return null;
        }
        return aidlist.get(0);
    }

    /**
     * select aid from database
     * @param aid	the hexstring of aid
     * @return
     */
    public Aid findByAidAndAsi(String aid){
        List<Aid> mList = findAllAid();
        if (mList == null || mList.size() == 0) {
            return null;
        } else {
            for (Aid cAid : mList) {
                Log.d("findByAidAndAsi", "aid.getAid(): " + cAid.getAid());
                if (aid.startsWith(cAid.getAid())) {
                    return cAid;
                }
            }
        }
        return null;
    }

}
