package chan.android.game.memoblock;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class PatternView extends View {

    public interface OnMoveListener {

        /**
         * Handler for touch event
         *
         * @param c - The cell being touched
         */
        void onMove(Cell c);
    }

    protected int thickness = 5;

    protected float borderWidth = 1.0f;

    protected int gridLineColor = Color.WHITE;

    protected int backgroundColor = Color.BLACK;

    protected int markerColor = Color.BLUE;

    protected Paint gridLinePaint;

    protected Paint backgroundPaint;

    protected OnMoveListener moveListener;

    protected Cell activeCell = new Cell(-1, -1);

    protected MoveState moveState;

    protected PatternGrid patternGrid;

    private Bitmap checkMarkBitmap;

    private Bitmap crossMarkBitmap;

    protected volatile boolean showPattern = true;

    protected volatile boolean touchable = false;

    protected Paint rectPaint;

    private Rect cachedRect = new Rect(0, 0, 0, 0);

    private Cell cacheCell = new Cell(0, 0);

    private int borderColor;

    private int patternColor;

    private int emptySquareColor;

    private Rect originalBitmapSize = new Rect(0, 0, 256, 256);

    public PatternView(Context context) {
        this(context, null, 0);
        init();
    }

    public PatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PatternView, 0, 0);
        try {
            borderWidth = a.getFloat(R.styleable.PatternView_border_width, 1.0f);
            gridLineColor = a.getColor(R.styleable.PatternView_grid_line_color, Color.WHITE);
            backgroundColor = a.getColor(R.styleable.PatternView_background_color, Color.BLACK);
            markerColor = a.getColor(R.styleable.PatternView_marker_color, Color.BLUE);
        } finally {
            a.recycle();
        }
        init();
    }

    public PatternView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setOnMoveListener(OnMoveListener moveListener) {
        this.moveListener = moveListener;
    }

    private void init() {
        gridLinePaint = new Paint();
        gridLinePaint.setColor(gridLineColor);
        gridLinePaint.setStrokeWidth(borderWidth);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);

        checkMarkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.check_mark_256);
        crossMarkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cross_mark_256);

        rectPaint = new Paint();
        rectPaint.setColor(gridLineColor);
        rectPaint.setStrokeWidth(borderWidth);

        borderColor = Color.parseColor("#66594C");
        emptySquareColor = Color.parseColor("#CCB299");
        patternColor = Color.parseColor("#ffd6dbe1");

        thickness = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, thickness, getResources().getDisplayMetrics());
    }

    public Cell getTouchCell(MotionEvent event) {
        // Get x, y that is relative to our grid
        int x = (int) Math.floor(event.getX());
        int y = (int) Math.floor(event.getY());
        int verticalSpacing = getHeight() / patternGrid.getRow();
        int horizontalSpacing = getWidth() / patternGrid.getColumn();
        return new Cell(y / verticalSpacing, x / horizontalSpacing);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int row = patternGrid.getRow();
        int column = patternGrid.getColumn();
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        int w = width / patternGrid.getColumn();
        int h = height / patternGrid.getRow();
        int x = 0;
        int y = 0;
        for (int r = 0; r < row; ++r) {
            x = 0;
            for (int c = 0; c < column; ++c) {
                cacheCell.r = r;
                cacheCell.c = c;
                cachedRect.set(x, y, x + w, y + h);
                drawRect(canvas, cachedRect, cacheCell);
                x += (w - thickness);
            }
            y += (h - thickness);
        }
    }

    protected void drawRect(Canvas canvas, Rect rect, Cell cell) {
        // Draw border first
        rectPaint.setStrokeWidth(thickness);
        rectPaint.setColor(borderColor);
        canvas.drawRect(rect, rectPaint);

        // Draw inner square
        if (showPattern) {
            if (patternGrid.isMarked(cell.r, cell.c)) {
                rectPaint.setColor(patternColor);
            } else {
                rectPaint.setColor(emptySquareColor);
            }
        } else {
            if (patternGrid.isMarkRemoved(cell.r, cell.c)) {
                rectPaint.setColor(patternColor);
            } else {
                rectPaint.setColor(emptySquareColor);
            }
        }

        rect.set(rect.left + thickness, rect.top + thickness, rect.right - thickness, rect.bottom - thickness);
        canvas.drawRect(rect, rectPaint);

        if (cell.equals(activeCell)) {
            int scaleWidth = rect.right - rect.left;
            int scaleHeight = rect.bottom - rect.top;
            if (moveState == MoveState.LOST) {
                crossMarkBitmap = Bitmap.createScaledBitmap(crossMarkBitmap, scaleWidth, scaleHeight, true);
                canvas.drawBitmap(crossMarkBitmap, rect.left, rect.top, null);
            } else if (moveState == MoveState.WON) {
                checkMarkBitmap = Bitmap.createScaledBitmap(checkMarkBitmap, scaleWidth, scaleHeight, true);
                canvas.drawBitmap(checkMarkBitmap, rect.left, rect.top, null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                synchronized (this) {
                    if (touchable) {
                        onActionDown(event);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_OUTSIDE:
                break;
        }


        return true;
    }

    protected void onActionDown(MotionEvent event) {
        Cell c = getTouchCell(event);
        // Deliver move coordinate to parent controller
        if (moveListener != null) {
            moveListener.onMove(c);
        }
    }

    public void setPatternGrid(PatternGrid grid) {
        patternGrid = grid;
    }

    public void setActiveCell(int r, int c, MoveState state) {
        activeCell.r = r;
        activeCell.c = c;
        moveState = state;
    }

    public Cell getActiveCell() {
        return activeCell;
    }

    public MoveState getMoveState() {
        return moveState;
    }

    public synchronized boolean isTouchable() {
        return touchable;
    }

    public synchronized boolean isShowPattern() {
        return showPattern;
    }

    public synchronized void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    public synchronized void setShowPattern(boolean showPattern) {
        this.showPattern = showPattern;
    }
}
