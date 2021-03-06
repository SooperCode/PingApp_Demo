package com.soopercode.pingapp.help;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.soopercode.pingapp.R;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * --> TEMPORARILY TAKING OUT THE HELP PAGES FOR VERSION 2.0
 *
 * Adapted this class from Jake Wharton's ViewPagerIndicator library sample.
 */
public class ViewPageIndicator /* extends View implements ViewPager.OnPageChangeListener */ {

//    private final static String TAG = ViewPageIndicator.class.getSimpleName();
//
//    private ViewPager viewPager;
//    private float radius;
//    private final Paint paintPageFill = new Paint(ANTI_ALIAS_FLAG);
//    private final Paint paintStroke = new Paint(ANTI_ALIAS_FLAG);
//    private final Paint paintFill = new Paint(ANTI_ALIAS_FLAG);
//    private int pageCount;
//    private int currentPage;
//    private int snapPage; // the page we're snapping to
//
//
//    public ViewPageIndicator(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//        Resources res = getResources();
//        paintPageFill.setStyle(Paint.Style.FILL);
//        paintPageFill.setColor(res.getColor(R.color.viewpage_indicator_page_color));
//        paintStroke.setStyle(Paint.Style.STROKE);
//        paintStroke.setColor(res.getColor(R.color.viewpage_indicator_stroke_color));
//        paintStroke.setStrokeWidth(res.getDimension(R.dimen.viewpage_indicator_stroke_width));
//        paintFill.setStyle(Paint.Style.FILL);
//        paintFill.setColor(res.getColor(R.color.viewpage_indicator_fill_color));
//        radius = res.getDimension(R.dimen.viewpage_indicator_radius);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        int widthSize = getWidth();
//        int widthPaddingLeft = getPaddingLeft();
//        int widthPaddingRight = getPaddingRight();
//        int heightPaddingTop = getPaddingTop();
//
//        final float spaceByRadius = radius * 5;
//        final float heightOffset = heightPaddingTop + radius;
//        float widthOffset = widthPaddingLeft + radius * 2.5f;
//        widthOffset += ((widthSize - widthPaddingLeft - widthPaddingRight) / 2.0f)
//                - ((pageCount * spaceByRadius) / 2.0f);
//
//        float pageFillRadius = radius;
//        if (paintStroke.getStrokeWidth() > 0) {
//            pageFillRadius -= paintStroke.getStrokeWidth() / 2.0f;
//        }
//
//        float dX, dY;
//
//        // Draw stroked circles
//        for (int i = 0; i < pageCount; i++) {
//            dX = widthOffset + (i * spaceByRadius);
//            dY = heightOffset;
//
//            // Only paint fill if not completely transparent
//            if (paintPageFill.getAlpha() > 0) {
//                canvas.drawCircle(dX, dY, pageFillRadius, paintPageFill);
//            }
//
//            // Only paint stroke if stroke width was non-zero
//            if (pageFillRadius != radius) {
//                canvas.drawCircle(dX, dY, radius, paintStroke);
//            }
//        }
//
//        // Draw the filled circle according to the current scroll
//        float cx = snapPage * spaceByRadius;
//        dX = widthOffset + cx;
//        dY = heightOffset;
//        canvas.drawCircle(dX, dY, radius, paintFill);
//
//    }
//
//
//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        currentPage = position;
//        invalidate();
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//        currentPage = position;
//        snapPage = position;
//        invalidate();
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//        /* don't need this */
//    }
//
//    /**
//     * Bind this indicator to the ViewPager
//     */
//    public void setViewPager(ViewPager viewPager) {
//        if (this.viewPager == viewPager) {
//            return;
//        }
//        this.viewPager = viewPager;
//        viewPager.addOnPageChangeListener(this);
//        // number of pages doesn't change, so set it here
//        // instead of in onDraw().
//        pageCount = viewPager.getAdapter().getCount();
//        invalidate();
//    }
}
