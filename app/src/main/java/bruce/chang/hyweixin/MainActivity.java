package bruce.chang.hyweixin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    /**
     * 顶部三个LinearLayout
     */
    private LinearLayout mTabLiaotian;
    private LinearLayout mTabFaxian;
    private LinearLayout mTabTongxunlun;
    /**
     * 顶部的三个TextView
     */
    private TextView mLiaotian;
    private TextView mFaxian;
    private TextView mTongxunlu;

    /**
     * 分别为每个TabIndicator创建一个BadgeView
     */
    private BadgeView mBadgeViewforLiaotian;
    private BadgeView mBadgeViewforFaxian;
    private BadgeView mBadgeViewforTongxunlu;
    /**
     * Tab的那个引导线
     */
    private ImageView mTabLine;
    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initTabLine();

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);

        mTabLiaotian.removeView(mBadgeViewforLiaotian);
        mBadgeViewforLiaotian.setBadgeCount(5);
        mTabLiaotian.addView(mBadgeViewforLiaotian);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e(TAG, "position:" + position + "positionOffset:" + positionOffset + "positionOffsetPixels:" + positionOffsetPixels);
                /**
                 * 利用position和currentIndex判断用户的操作是哪一页往哪一页滑动
                 * 然后改变根据positionOffset动态改变TabLine的leftMargin
                 */
                if (currentIndex == 0 && position == 0)// 0->1
                {
                    LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                    lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                    mTabLine.setLayoutParams(lp);

                } else if (currentIndex == 1 && position == 0) // 1->0
                {
                    LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                    lp.leftMargin = (int) ((positionOffset - 1) * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                    mTabLine.setLayoutParams(lp);

                } else if (currentIndex == 1 && position == 1) // 1->2
                {
                    LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                    lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                    mTabLine.setLayoutParams(lp);
                } else if (currentIndex == 2 && position == 1) // 2->1
                {
                    LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                    lp.leftMargin = (int) ((positionOffset - 1) * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                    mTabLine.setLayoutParams(lp);

                }
            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                resetTextView();
                switch (position) {
                    case 0:
                        /**
                         * 设置消息通知
                         */
                        mBadgeViewforLiaotian.setVisibility(View.VISIBLE);
                        mTabLiaotian.removeView(mBadgeViewforLiaotian);
                        mBadgeViewforLiaotian.setBadgeCount(5);
                        mTabLiaotian.addView(mBadgeViewforLiaotian);
                        mLiaotian.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        break;
                    case 1:
                        /**
                         * 设置消息通知
                         */
                        mBadgeViewforFaxian.setVisibility(View.VISIBLE);
                        mFaxian.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        mTabFaxian.removeView(mBadgeViewforFaxian);
                        mBadgeViewforFaxian.setBadgeCount(15);
                        mTabFaxian.addView(mBadgeViewforFaxian);
                        break;
                    case 2:

                        mTongxunlu.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mTabLiaotian = (LinearLayout) findViewById(R.id.id_tab_liaotian_ly);
        mTabFaxian = (LinearLayout) findViewById(R.id.id_tab_faxian_ly);
        mTabTongxunlun = (LinearLayout) findViewById(R.id.id_tab_tongxunlu_ly);

        mLiaotian = (TextView) findViewById(R.id.id_liaotian);
        mFaxian = (TextView) findViewById(R.id.id_faxian);
        mTongxunlu = (TextView) findViewById(R.id.id_tongxunlu);

        MainTab01 tab01 = new MainTab01();
        MainTab02 tab02 = new MainTab02();
        MainTab03 tab03 = new MainTab03();
        mFragments.add(tab01);
        mFragments.add(tab02);
        mFragments.add(tab03);

        mBadgeViewforFaxian = new BadgeView(this);
        mBadgeViewforLiaotian = new BadgeView(this);
        mBadgeViewforTongxunlu = new BadgeView(this);

    }

    /**
     * 根据屏幕的宽度，初始化引导线的宽度
     */
    private void initTabLine() {
        mTabLine = (ImageView) findViewById(R.id.id_tab_line);
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
        lp.width = screenWidth / 3;
        mTabLine.setLayoutParams(lp);
        Log.e(TAG, "screenWidth：" + screenWidth);
    }

    /**
     * 重置颜色
     */
    protected void resetTextView() {
        mLiaotian.setTextColor(getResources().getColor(R.color.black));
        mFaxian.setTextColor(getResources().getColor(R.color.black));
        mTongxunlu.setTextColor(getResources().getColor(R.color.black));
        mBadgeViewforLiaotian.setVisibility(View.INVISIBLE);
        mBadgeViewforFaxian.setVisibility(View.INVISIBLE);
        mBadgeViewforTongxunlu.setVisibility(View.INVISIBLE);

    }
}
