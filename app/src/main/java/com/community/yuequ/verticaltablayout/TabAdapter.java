package com.community.yuequ.verticaltablayout;


import com.community.yuequ.verticaltablayout.widget.QTabView;

/**
 */
public interface TabAdapter {
    int getCount();

    int getBadge(int position);

    QTabView.TabIcon getIcon(int position);

    QTabView.TabTitle getTitle(int position);

    int getBackground(int position);
}
