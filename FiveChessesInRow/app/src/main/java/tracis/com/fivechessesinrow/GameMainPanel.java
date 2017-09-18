package tracis.com.fivechessesinrow;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GameMainPanel extends View {

    public static Context context;

    private int mPanelLength;
    private float mLineHeight;
    private final int MAX_LINE = 14;

    public Paint mPaint = new Paint();

    private Bitmap mWhiteChess = null;
    private Bitmap mBlackChess = null;

    private final float chessSizeRatio = 3 * 1.0f / 4;

    private int mOver = 0;

    private Game game;

    public GameMainPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setBackgroundColor(0xaabfbfbf);
        init();
    }

    private void init() {
        context = getContext();

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mWhiteChess = BitmapFactory.decodeResource(getResources(), R.drawable.white_chess);
        mBlackChess = BitmapFactory.decodeResource(getResources(), R.drawable.black_chess);
        game = new Game(MAX_LINE);
    }

    public void restart() {
        game = new Game(MAX_LINE);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mPanelLength = w;
        mLineHeight = mPanelLength * 1.0f / MAX_LINE;

        int chessSize = (int) (mLineHeight * chessSizeRatio);

        mWhiteChess = Bitmap.createScaledBitmap(mWhiteChess, chessSize, chessSize, false);
        mBlackChess = Bitmap.createScaledBitmap(mBlackChess, chessSize, chessSize, false);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawGameTable(canvas);
        drawChesses(canvas);

        String text = game.checkGameStatus();
        if (text != null) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(GameMainPanel.context);
            builder.setMessage(text)
                    .setCancelable(false)
                    .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            restart();
                        }
                    });
            builder.create();
            builder.show();
        }
    }

    private void drawChesses(Canvas canvas) {
        int[][] table = game.getTable();
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                if (table[i][j] == 1) {
                    canvas.drawBitmap(mBlackChess,
                            (float) (mLineHeight * (0.5 + i - 0.5 * chessSizeRatio)),
                            (float) (mLineHeight * (0.5 + j - 0.5 * chessSizeRatio)), null);
                } else if (table[i][j] == 2) {
                    canvas.drawBitmap(mWhiteChess,
                            (float) (mLineHeight * (0.5 + i - 0.5 * chessSizeRatio)),
                            (float) (mLineHeight * (0.5 + j - 0.5 * chessSizeRatio)), null);
                }
            }
        }
    }

    private void drawGameTable(Canvas canvas) {
        int width = mPanelLength;
        float lineHeight = mLineHeight;

        float startPoint = (lineHeight / 2), stopPoint = (width - lineHeight / 2);
        float temp = startPoint;

        for (int i = 0; i < MAX_LINE; i++, temp = (float) ((0.5 + i) * lineHeight)) {
            canvas.drawLine(startPoint, temp, stopPoint, temp, mPaint);
            canvas.drawLine(temp, startPoint, temp, stopPoint, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOver != 0) return false;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            boolean flag = game.onTouchEvent(x, y, mLineHeight);
            if (flag) {
                invalidate();
                return flag;
            }
        }

        return super.onTouchEvent(event);
    }

}
