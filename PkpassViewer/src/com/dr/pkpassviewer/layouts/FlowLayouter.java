package com.dr.pkpassviewer.layouts;

import android.view.View;
import android.widget.RelativeLayout;

/**
 * 
 * Assign left/top margins for emulate flow layout. As alternative you can
 * implement own layout with same algorithm. When at row have views > 1, then
 * last child always will be aligned to right side. Also tries place views in
 * row evenly.
 * 
 * @author Verma Rahul
 * 
 */
public class FlowLayouter {

	protected int maxWidth;

	// index of childs inclusive
	protected int rowStartInd;
	protected int rowEndInd;

	protected int rowCurWidth;
	protected int rowHeight;
	protected int rowY;

	public void flow(RelativeLayout layout) {
		int childsCount = layout.getChildCount();

		maxWidth = layout.getWidth() - layout.getPaddingLeft()
				- layout.getPaddingRight();
		rowCurWidth = 0;
		rowY = 0;

		View child;
		int wChild;

		for (int i = 0; i < childsCount; ++i) {

			child = layout.getChildAt(i);
			if (child.getVisibility() == View.GONE) {
				continue;
			}

			wChild = child.getWidth();

			if (rowCurWidth == 0) {
				rowCurWidth = wChild;
				rowStartInd = i;
				rowEndInd = 0;
				if (rowCurWidth > maxWidth) {
					rowEndInd = i;
					onLayoutRow(layout);
				}
			} else if (rowCurWidth + wChild > maxWidth) {
				rowEndInd = i - 1;
				onLayoutRow(layout);
			} else {
				rowCurWidth += wChild;
			}
		}

		rowEndInd = childsCount - 1;
		onLayoutRow(layout);
	}

	protected void onLayoutRow(RelativeLayout layout) {
		RelativeLayout.LayoutParams params;
		View child;

		int dMargin = maxWidth - rowCurWidth;

		if (dMargin < 0) {
			dMargin = 0;
		}
		int rowChildCount = rowEndInd - rowStartInd;
	
		if (rowChildCount > 0) {
			dMargin = dMargin / rowChildCount;
		}

		int left = 0;

		for (int i = rowStartInd; i <= rowEndInd; ++i) {
			child = layout.getChildAt(i);
			if (child.getVisibility() == View.GONE) {
				continue;
			}
			params = (RelativeLayout.LayoutParams) child.getLayoutParams();

			rowHeight = (rowHeight > child.getHeight()) ? rowHeight : child
					.getHeight();

			left += ((i > rowStartInd) ? dMargin : 0);
			params.setMargins(left, rowY, 0, 0);
			left += child.getWidth();
			child.setLayoutParams(params);
		}

		rowCurWidth = 0;
		rowY += rowHeight;
	}

}
