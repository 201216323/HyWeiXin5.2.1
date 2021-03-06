# HyWeiXin5.2.1
学习鸿洋大神高仿微信5.2.1主界面架构 包含消息通知这篇文章， 然后研究内部ViewPager滑动时候的问题
## 学习鸿洋---高仿微信5.2.1主界面架构 包含消息通知笔记

### 一直都知道BadgeView可以实现App中图标旁边现实消息数量的功能，但自己没怎么使用过。最近看慕课网鸿洋大神的教学视频，里面有提到了这个控件，而且里面利用ViewPager来检测滑动的方向，并随时改变tabline的位置，这两部分自己还是比较陌生的，于是就决定自己动手做一遍。
## **最终的效果图如下所示：**
![image](D:\apic\a.gif)


> 第一：BadgeView的使用

![截图](D:\a.png)

1. 从上图片可以看到，BadgeView是一个继承自TextView的自定义View，所以我们在使用eclipse和Android Studio来进行开发的时候直接以一个自定义View组件放到项目中就可以了，不用添加库工程或者jar包。
2. BadgeView的初始化： BadgeView badView = new BadgeView(this);
3. 给TextView右侧添加badView，首先TextView要位于LinearLayout中，如我在聊天这块的badgeView 就是这样使用的，其中mTabLiaotian是LinearLayout的对象，mBadgeViewforLiaotian是BadgeView的对象，mTabLiaotian是TextView的对象。
4. 经过上面几个步骤就可以给Text View的右侧添加BadgeView，当然还可以设置BadgeView的背景颜色等等，这些在代码中都写得很清楚，就不解释了。 
```
 mTabLiaotian.removeView(mBadgeViewforLiaotian);
        mBadgeViewforLiaotian.setBadgeCount(5);
        mTabLiaotian.addView(mBadgeViewforLiaotian);
```
> 第二：ViewPager滑动的时候指示线跟随手指移动

1. 指示线是一个Image View，其背景是一个.9图，聊天、发现、通讯录三部分平分手机的宽度，所以需要在代码中设置指示线的宽度是屏幕宽度的三分之一，在ViewPager滑动的时候动态的改变该指示线的leftMargin就可以了，

```
DisplayMetrics outMetrics = new DisplayMetrics();
getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
screenWidth = outMetrics.widthPixels;
LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
lp.width = screenWidth / 3;
mTabLine.setLayoutParams(lp);
```
2. ViewPager添加滑动的监听，来研究里面onPageScrolled和onPageSelected方法从而得到动态设置指示线leftMargin的思路。

```
mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e(TAG, "position:" + position + "positionOffset:" + positionOffset + "positionOffsetPixels:" + positionOffsetPixels);
				
            }

            @Override
            public void onPageSelected(int position) {
			currentPosition = position;
                resetTextView();
                switch (position) {
                    case 0:
                      //选中聊天Fragment
                        break;
                    case 1:
                    //选中发现Fragment
                        break;
                    case 2:
                    //选中通讯录Fragment
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
```
3. 观察onPageScrolled中打印的log数据，寻找规律（测试的手机是魅族note3，手机分辨率是1080*1920）。

首先打印手机屏幕宽度看一下数据：
打印出屏幕的宽数据：MainActivity: screenWidth：1080，手机屏幕的宽确实是1080像素。


#### 从第0页到第1页滑动时候onPageScrolled三个参数的变化：
position | positionOffset  |    positionOffset
---|---|---
0 | 0.012037037  |13
0 | 逐渐变大  |逐渐变大
0 | 0.9990741  |1079
从上面数据可以看出：从第0页到第1页滑动时候，position一直为0，positionOffset基本是从0到1，positionOffsetPixels基本也是从0到1080像素（即手机屏幕的宽度）。

#### 从第1页到第0页滑动时候onPageScrolled三个参数的变化：
position | positionOffset  |    positionOffset
---|---|---
0 | 0.99814814  |1078
0 | 逐渐变小  |逐渐变小
0 | 0.0018518518  |1
从上面数据可以看出：从第1页到第0页滑动时候，position一直为0，positionOffset基本是从1到0，positionOffsetPixels基本也是从1080到0像素。


#### 从第1页到第2页滑动时候onPageScrolled三个参数的变化：
position | positionOffset  |    positionOffset
---|---|---
1 | 0.024074078  |26
1 | 逐渐变大  |逐渐变大
1 | 0.9990741  |1079
从上面数据可以看出：从第1页到第2页滑动时候，position一直为1，positionOffset基本是从0到1，positionOffsetPixels基本也是从0到1080像素（即手机屏幕的宽度）。

#### 从第2页到第1页滑动时候onPageScrolled三个参数的变化：
position | positionOffset  |    positionOffset
---|---|---
1 | 0.9333333  |1007
1 | 逐渐变小  |逐渐变小
1 | 0.0018517971  |0
从上面数据可以看出：从第2页到第1页滑动时候，position一直为1，positionOffset基本是从1到0，positionOffsetPixels基本也是从1080到0像素。

综上打印的数据，我们可以得到这样的结论：
当ViewPager里面包含三个Fragment的时候，可以根据两个常量来判断ViewPager的滑动方向，这里需要用到onPageSelected方法中的position，
定义此position=currentPosition,还有onPageScrolled方法中的position，如果currentPosition=0&&position=0此时ViewPager的滑动操作是
从第0页到第1页，如果currentPosition=1&&position=0此时ViewPager的滑动操作是从第1页到第0页，如果currentPosition=1&&position=1此时ViewPager的滑动操作是
从第1页到第2页，如果currentPosition=2&&position=1此时ViewPager的滑动操作是从第2页到第1页，至于positionOffset字段是在左滑的时候是从0到1变化，右滑的时候
是从1到0，positionOffsetPixels字段是在左滑的时候是从0到1080（手机屏幕的宽度像素数）变化，右滑的时候是从1080到0，

4. 根据上面得到的规律，我们就可以在滑动ViewPager的时候来动态的设置指示线的leftMargin了，来实现实时改变指示线位置的功能了。
5.ViewPager滑动监听的完整代码
```
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
```
resetTextView()方法中的代码如下：

```
/**
     * 重置颜色
     */
    protected void resetTextView() {
        mLiaotian.setTextColor(getResources().getColor(R.color.black));//改变字体颜色
        mFaxian.setTextColor(getResources().getColor(R.color.black));
        mTongxunlu.setTextColor(getResources().getColor(R.color.black));
        mBadgeViewforLiaotian.setVisibility(View.INVISIBLE);////隐藏badgeView
        mBadgeViewforFaxian.setVisibility(View.INVISIBLE);
        mBadgeViewforTongxunlu.setVisibility(View.INVISIBLE);

    }
    
```


总结：本片文章为了练习使用BadgeView,主要是研究ViewPager的滑动，利用onPageScrolled方法和onPageSelected方法来实现指示线跟随手指滑动的效果，当然在5.0新特性上面可以直接使用5.0中的控件很方便的来完成这种效果。


程序源代码的下载地址是：[下载源代码](https://github.com/201216323/HyWeiXin5.2.1)

BadgeView的下载链接是： [下载BadgeView](https://github.com/201216323/BadgeView)

鸿洋大神博客文章链接是：[打开链接](http://blog.csdn.net/lmj623565791/article/details/25708045)



