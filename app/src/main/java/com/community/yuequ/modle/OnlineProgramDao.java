package com.community.yuequ.modle;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2016/11/9.
 */

public class OnlineProgramDao {
    public int errorCode;
    public String errorMessage;
    public OnlineProgramDaoResult result;

    public class OnlineProgramDaoResult{
        public int livingOrderId;
        public long currTime;
        public LinkedHashMap<String,ArrayList<OnlineProgram>> orderMap;
    }


}
