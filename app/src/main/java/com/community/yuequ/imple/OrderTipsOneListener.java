package com.community.yuequ.imple;

import com.community.yuequ.modle.OrderTip;

/**
 * Created by Administrator on 2016/6/1.
 */
public interface OrderTipsOneListener {
    void one_confirm(int programId,OrderTip orderTip);
    void one_cancel(int programId,OrderTip orderTip);
}
